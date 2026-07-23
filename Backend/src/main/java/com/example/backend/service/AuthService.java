package com.example.backend.service;

import com.example.backend.dto.AuthResponseDto;
import com.example.backend.dto.ForgotPasswordResponseDto;
import com.example.backend.dto.LoginRequestDto;
import com.example.backend.dto.MeResponseDto;
import com.example.backend.dto.RefreshRequestDto;
import com.example.backend.dto.RegisterRequestDto;
import com.example.backend.dto.ResetPasswordRequestDto;
import com.example.backend.entity.PasswordResetTokenEntity;
import com.example.backend.entity.RefreshTokenEntity;
import com.example.backend.entity.UserEntity;
import com.example.backend.repository.PasswordResetTokenRepository;
import com.example.backend.repository.RefreshTokenRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.EmailCipherService;
import com.example.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;


    //register - хэшируем/шифруем email, BCrypt-им пароль, фиксируем
    //согласие на ПД, выписываем пару (access, refresh)
    //login - ищем по emailHash, верифицируем пароль BCrypt, отдаём пару

     //forgotPassword - выдаём короткий reset-токен
     //resetPassword  - проверяем и применяем новый пароль, revoke всех refresh-токенов (forced logout)

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailCipherService emailCipher;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.security.consent.current-version:1.0}")
    private String currentConsentVersion;

    @Value("${app.security.password-reset.ttl-minutes:15}")
    private long passwordResetTtlMinutes;


    @Transactional
    public AuthResponseDto register(RegisterRequestDto req) {
        if (!currentConsentVersion.equals(req.acceptedTermsVersion())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Нужно принять текущую версию пользовательского соглашения (" + currentConsentVersion + ")");
        }

        String emailHash = emailCipher.hash(req.email());
        if (userRepository.existsByEmailHash(emailHash)) {
            // 409 - пользователь с таким email уже есть.
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь с таким email уже существует");
        }

        UserEntity user = UserEntity.builder()
                .name(req.name())
                .emailHash(emailHash)
                .emailEncrypted(emailCipher.encrypt(emailCipher.normalize(req.email())))
                .passwordHash(passwordEncoder.encode(req.password()))
                .consentAcceptedAt(LocalDateTime.now())
                .consentVersion(currentConsentVersion)
                .build();

        UserEntity saved = userRepository.save(user);
        log.info("User registered: id={}", saved.getId());

        return issueTokens(saved);
    }


    @Transactional
    public AuthResponseDto login(LoginRequestDto req) {
        String emailHash = emailCipher.hash(req.email());
        UserEntity user = userRepository.findByEmailHash(emailHash)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Неверный email или пароль"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный email или пароль");
        }

        return issueTokens(user);
    }


    @Transactional
    public AuthResponseDto refresh(RefreshRequestDto req) {
        String tokenHash = jwtService.hashRefreshToken(req.refreshToken());
        RefreshTokenEntity token = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh-токен недействителен"));

        if (Boolean.TRUE.equals(token.getRevoked())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh-токен отозван");
        }
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh-токен истёк");
        }

        UserEntity user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Пользователь не найден"));

        token.setRevoked(true);
        refreshTokenRepository.save(token);
        return issueTokens(user);
    }


    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) return;
        String tokenHash = jwtService.hashRefreshToken(refreshToken);
        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(t -> {
            t.setRevoked(true);
            refreshTokenRepository.save(t);
        });
    }


    @Transactional
    public ForgotPasswordResponseDto forgotPassword(String email) {
        String emailHash = emailCipher.hash(email);

        UserEntity user = userRepository.findByEmailHash(emailHash).orElse(null);
        if (user == null) {
            log.info("forgotPassword: user not found");
            return new ForgotPasswordResponseDto(
                    "Если такой email зарегистрирован, на него отправлена ссылка для смены пароля.",
                    null);
        }

        String rawToken = jwtService.generateRefreshToken();
        String tokenHash = jwtService.hashRefreshToken(rawToken);
        LocalDateTime expiresAt = LocalDateTime.now().plus(Duration.ofMinutes(passwordResetTtlMinutes));

        PasswordResetTokenEntity entity = PasswordResetTokenEntity.builder()
                .userId(user.getId())
                .tokenHash(tokenHash)
                .expiresAt(expiresAt)
                .build();
        passwordResetTokenRepository.save(entity);

        String decryptedEmail = emailCipher.decrypt(user.getEmailEncrypted());
        emailService.sendPasswordResetEmail(decryptedEmail, rawToken);

        log.info("password reset issued for userId={}", user.getId());
        return new ForgotPasswordResponseDto(
                "Если такой email зарегистрирован, на него отправлена ссылка для смены пароля.",
                null);
    }


    @Transactional
    public void resetPassword(ResetPasswordRequestDto req) {
        String tokenHash = jwtService.hashRefreshToken(req.resetToken());
        PasswordResetTokenEntity token = passwordResetTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный код"));

        if (Boolean.TRUE.equals(token.getUsed())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Код уже был использован");
        }
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Срок действия кода истёк");
        }

        UserEntity user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Пользователь не найден"));

        user.setPasswordHash(passwordEncoder.encode(req.newPassword()));
        userRepository.save(user);

        passwordResetTokenRepository.invalidateAllForUser(user.getId());

        refreshTokenRepository.revokeAllForUser(user.getId());
        log.info("Password reset done for userId={}", user.getId());
    }


    public MeResponseDto loadMe(Integer userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Пользователь не найден"));
        return new MeResponseDto(user.getId(), user.getName(), emailCipher.decrypt(user.getEmailEncrypted()));
    }


    private AuthResponseDto issueTokens(UserEntity user) {
        String access = jwtService.generateAccessToken(user.getId());
        String rawRefresh = jwtService.generateRefreshToken();
        Instant refreshExp = jwtService.refreshExpiry();

        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .userId(user.getId())
                .tokenHash(jwtService.hashRefreshToken(rawRefresh))
                .expiresAt(LocalDateTime.ofInstant(refreshExp, ZoneId.systemDefault()))
                .build();
        refreshTokenRepository.save(entity);

        MeResponseDto me = new MeResponseDto(user.getId(), user.getName(), emailCipher.decrypt(user.getEmailEncrypted()));
        return new AuthResponseDto(access, rawRefresh, "Bearer", jwtService.getAccessTtlSeconds(), me);
    }
}
