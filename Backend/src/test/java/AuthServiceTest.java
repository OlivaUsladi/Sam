import com.example.backend.dto.*;
import com.example.backend.entity.PasswordResetTokenEntity;
import com.example.backend.entity.RefreshTokenEntity;
import com.example.backend.entity.UserEntity;
import com.example.backend.repository.PasswordResetTokenRepository;
import com.example.backend.repository.RefreshTokenRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.EmailCipherService;
import com.example.backend.security.JwtService;
import com.example.backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock private EmailCipherService emailCipher;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "currentConsentVersion", "1.0");
        ReflectionTestUtils.setField(authService, "passwordResetTtlMinutes", 15L);
    }

    private UserEntity sampleUser() {
        return UserEntity.builder()
                .id(1)
                .name("Тест")
                .emailHash("hash")
                .emailEncrypted("enc")
                .passwordHash("$bcrypt$")
                .build();
    }

    // ----- REGISTER -----

    @Test
    void register_withWrongConsentVersion_throws400() {
        RegisterRequestDto req = new RegisterRequestDto("Тест", "t@t.ru", "Password1", "0.0");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.register(req));
        assertTrue(ex.getMessage().contains("пользовательского соглашения"));
    }

    @Test
    void register_whenEmailExists_throws409() {
        RegisterRequestDto req = new RegisterRequestDto("Тест", "t@t.ru", "Password1", "1.0");
        when(emailCipher.hash("t@t.ru")).thenReturn("hash");
        when(userRepository.existsByEmailHash("hash")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.register(req));
        assertTrue(ex.getMessage().contains("уже существует"));
    }

    @Test
    void register_success_issuesTokens() {
        RegisterRequestDto req = new RegisterRequestDto("Тест", "t@t.ru", "Password1", "1.0");
        when(emailCipher.hash("t@t.ru")).thenReturn("hash");
        when(userRepository.existsByEmailHash("hash")).thenReturn(false);
        when(emailCipher.normalize("t@t.ru")).thenReturn("t@t.ru");
        when(emailCipher.encrypt("t@t.ru")).thenReturn("enc");
        when(passwordEncoder.encode("Password1")).thenReturn("hashedPwd");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> {
            UserEntity u = inv.getArgument(0); u.setId(1); return u;
        });
        when(jwtService.generateAccessToken(1)).thenReturn("access");
        when(jwtService.generateRefreshToken()).thenReturn("refresh");
        when(jwtService.hashRefreshToken("refresh")).thenReturn("rh");
        when(jwtService.refreshExpiry()).thenReturn(Instant.now().plusSeconds(3600));
        when(jwtService.getAccessTtlSeconds()).thenReturn(900L);
        when(emailCipher.decrypt("enc")).thenReturn("t@t.ru");

        AuthResponseDto resp = authService.register(req);

        assertEquals("access", resp.accessToken());
        assertEquals("refresh", resp.refreshToken());
        verify(userRepository).save(any(UserEntity.class));
        verify(refreshTokenRepository).save(any(RefreshTokenEntity.class));
    }

    // ----- LOGIN -----

    @Test
    void login_whenUserNotFound_throws401() {
        LoginRequestDto req = new LoginRequestDto("t@t.ru", "Password1");
        when(emailCipher.hash("t@t.ru")).thenReturn("hash");
        when(userRepository.findByEmailHash("hash")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.login(req));
        assertTrue(ex.getMessage().contains("Неверный email или пароль"));
    }

    @Test
    void login_whenPasswordWrong_throws401() {
        LoginRequestDto req = new LoginRequestDto("t@t.ru", "wrong");
        when(emailCipher.hash("t@t.ru")).thenReturn("hash");
        when(userRepository.findByEmailHash("hash")).thenReturn(Optional.of(sampleUser()));
        when(passwordEncoder.matches("wrong", "$bcrypt$")).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.login(req));
        assertTrue(ex.getMessage().contains("Неверный email или пароль"));
    }

    @Test
    void login_success_returnsTokens() {
        LoginRequestDto req = new LoginRequestDto("t@t.ru", "Password1");
        when(emailCipher.hash("t@t.ru")).thenReturn("hash");
        when(userRepository.findByEmailHash("hash")).thenReturn(Optional.of(sampleUser()));
        when(passwordEncoder.matches("Password1", "$bcrypt$")).thenReturn(true);
        when(jwtService.generateAccessToken(1)).thenReturn("access");
        when(jwtService.generateRefreshToken()).thenReturn("refresh");
        when(jwtService.hashRefreshToken("refresh")).thenReturn("rh");
        when(jwtService.refreshExpiry()).thenReturn(Instant.now().plusSeconds(3600));
        when(jwtService.getAccessTtlSeconds()).thenReturn(900L);
        when(emailCipher.decrypt("enc")).thenReturn("t@t.ru");

        AuthResponseDto resp = authService.login(req);

        assertEquals("access", resp.accessToken());
        verify(refreshTokenRepository).save(any(RefreshTokenEntity.class));
    }

    // ----- REFRESH -----

    @Test
    void refresh_whenTokenNotFound_throws401() {
        RefreshRequestDto req = new RefreshRequestDto("rtok");
        when(jwtService.hashRefreshToken("rtok")).thenReturn("rh");
        when(refreshTokenRepository.findByTokenHash("rh")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.refresh(req));
        assertTrue(ex.getMessage().contains("Refresh-токен"));
    }

    @Test
    void refresh_whenTokenRevoked_throws401() {
        RefreshRequestDto req = new RefreshRequestDto("rtok");
        RefreshTokenEntity t = RefreshTokenEntity.builder()
                .id(1).userId(1).tokenHash("rh")
                .expiresAt(LocalDateTime.now().plusMinutes(1))
                .revoked(true).build();
        when(jwtService.hashRefreshToken("rtok")).thenReturn("rh");
        when(refreshTokenRepository.findByTokenHash("rh")).thenReturn(Optional.of(t));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.refresh(req));
        assertTrue(ex.getMessage().contains("отозван"));
    }

    @Test
    void refresh_whenTokenExpired_throws401() {
        RefreshRequestDto req = new RefreshRequestDto("rtok");
        RefreshTokenEntity t = RefreshTokenEntity.builder()
                .id(1).userId(1).tokenHash("rh")
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .revoked(false).build();
        when(jwtService.hashRefreshToken("rtok")).thenReturn("rh");
        when(refreshTokenRepository.findByTokenHash("rh")).thenReturn(Optional.of(t));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.refresh(req));
        assertTrue(ex.getMessage().contains("истёк"));
    }

    @Test
    void refresh_success_rotatesToken() {
        RefreshRequestDto req = new RefreshRequestDto("rtok");
        RefreshTokenEntity t = RefreshTokenEntity.builder()
                .id(1).userId(1).tokenHash("rh")
                .expiresAt(LocalDateTime.now().plusMinutes(60))
                .revoked(false).build();
        when(jwtService.hashRefreshToken("rtok")).thenReturn("rh");
        when(refreshTokenRepository.findByTokenHash("rh")).thenReturn(Optional.of(t));
        when(userRepository.findById(1)).thenReturn(Optional.of(sampleUser()));
        when(jwtService.generateAccessToken(1)).thenReturn("access2");
        when(jwtService.generateRefreshToken()).thenReturn("refresh2");
        when(jwtService.hashRefreshToken("refresh2")).thenReturn("rh2");
        when(jwtService.refreshExpiry()).thenReturn(Instant.now().plusSeconds(3600));
        when(jwtService.getAccessTtlSeconds()).thenReturn(900L);
        when(emailCipher.decrypt("enc")).thenReturn("t@t.ru");

        AuthResponseDto resp = authService.refresh(req);

        assertTrue(t.getRevoked());
        verify(refreshTokenRepository, times(2)).save(any(RefreshTokenEntity.class));
        assertEquals("access2", resp.accessToken());
    }

    // ----- LOGOUT -----

    @Test
    void logout_withEmptyToken_doesNothing() {
        authService.logout("");
        authService.logout(null);
        verifyNoInteractions(refreshTokenRepository);
    }

    @Test
    void logout_revokesToken() {
        RefreshTokenEntity t = RefreshTokenEntity.builder().id(1).userId(1)
                .tokenHash("rh").revoked(false)
                .expiresAt(LocalDateTime.now().plusDays(1)).build();
        when(jwtService.hashRefreshToken("rtok")).thenReturn("rh");
        when(refreshTokenRepository.findByTokenHash("rh")).thenReturn(Optional.of(t));

        authService.logout("rtok");

        assertTrue(t.getRevoked());
        verify(refreshTokenRepository).save(t);
    }

    // ----- FORGOT PASSWORD -----

    @Test
    void forgotPassword_whenUserNotFound_returnsGenericMessage() {
        when(emailCipher.hash("t@t.ru")).thenReturn("hash");
        when(userRepository.findByEmailHash("hash")).thenReturn(Optional.empty());

        ForgotPasswordResponseDto resp = authService.forgotPassword("t@t.ru");

        assertNull(resp.resetToken());
        assertTrue(resp.message().contains("Если такой email"));
        verify(passwordResetTokenRepository, never()).save(any());
    }

    @Test
    void forgotPassword_whenUserFound_issuesToken() {
        when(emailCipher.hash("t@t.ru")).thenReturn("hash");
        when(userRepository.findByEmailHash("hash")).thenReturn(Optional.of(sampleUser()));
        when(jwtService.generateRefreshToken()).thenReturn("rawtok");
        when(jwtService.hashRefreshToken("rawtok")).thenReturn("rh");

        ForgotPasswordResponseDto resp = authService.forgotPassword("t@t.ru");

        assertEquals("rawtok", resp.resetToken());
        verify(passwordResetTokenRepository).save(any(PasswordResetTokenEntity.class));
    }

    // ----- RESET PASSWORD -----

    @Test
    void resetPassword_invalidToken_throws401() {
        ResetPasswordRequestDto req = new ResetPasswordRequestDto("bad", "NewPass1");
        when(jwtService.hashRefreshToken("bad")).thenReturn("rh");
        when(passwordResetTokenRepository.findByTokenHash("rh")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.resetPassword(req));
        assertTrue(ex.getMessage().contains("Неверный код"));
    }

    @Test
    void resetPassword_usedToken_throws401() {
        ResetPasswordRequestDto req = new ResetPasswordRequestDto("tok", "NewPass1");
        PasswordResetTokenEntity t = PasswordResetTokenEntity.builder()
                .id(1).userId(1).tokenHash("rh")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(true).build();
        when(jwtService.hashRefreshToken("tok")).thenReturn("rh");
        when(passwordResetTokenRepository.findByTokenHash("rh")).thenReturn(Optional.of(t));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.resetPassword(req));
        assertTrue(ex.getMessage().contains("использован"));
    }

    @Test
    void resetPassword_expiredToken_throws401() {
        ResetPasswordRequestDto req = new ResetPasswordRequestDto("tok", "NewPass1");
        PasswordResetTokenEntity t = PasswordResetTokenEntity.builder()
                .id(1).userId(1).tokenHash("rh")
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .used(false).build();
        when(jwtService.hashRefreshToken("tok")).thenReturn("rh");
        when(passwordResetTokenRepository.findByTokenHash("rh")).thenReturn(Optional.of(t));

        assertThrows(ResponseStatusException.class, () -> authService.resetPassword(req));
    }

    @Test
    void resetPassword_success_updatesAndRevokes() {
        ResetPasswordRequestDto req = new ResetPasswordRequestDto("tok", "NewPass1");
        PasswordResetTokenEntity t = PasswordResetTokenEntity.builder()
                .id(1).userId(1).tokenHash("rh")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(false).build();
        when(jwtService.hashRefreshToken("tok")).thenReturn("rh");
        when(passwordResetTokenRepository.findByTokenHash("rh")).thenReturn(Optional.of(t));
        when(userRepository.findById(1)).thenReturn(Optional.of(sampleUser()));
        when(passwordEncoder.encode("NewPass1")).thenReturn("newHash");

        authService.resetPassword(req);

        verify(userRepository).save(any(UserEntity.class));
        verify(passwordResetTokenRepository).invalidateAllForUser(1);
        verify(refreshTokenRepository).revokeAllForUser(1);
    }

    // ----- LOAD ME -----

    @Test
    void loadMe_whenNotFound_throws401() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> authService.loadMe(1));
    }

    @Test
    void loadMe_success_returnsDto() {
        when(userRepository.findById(1)).thenReturn(Optional.of(sampleUser()));
        when(emailCipher.decrypt("enc")).thenReturn("t@t.ru");

        MeResponseDto me = authService.loadMe(1);

        assertEquals(1, me.id());
        assertEquals("Тест", me.name());
        assertEquals("t@t.ru", me.email());
    }
}
