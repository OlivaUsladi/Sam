package com.example.backend.security;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;

@Slf4j
@Service
public class EmailCipherService {

    private static final String HMAC_ALG = "HmacSHA256";
    private static final String AES_ALG  = "AES";
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LEN_BITS = 128;
    private static final int GCM_IV_LEN_BYTES = 12;

    private final SecureRandom random = new SecureRandom();

    private final String hmacKeyB64;
    private final String aesKeyB64;

    private SecretKeySpec hmacKey;
    private SecretKeySpec aesKey;

    public EmailCipherService(
            @Value("${app.security.email.hmac-key}") String hmacKeyB64,
            @Value("${app.security.email.aes-key}") String aesKeyB64
    ) {
        this.hmacKeyB64 = hmacKeyB64;
        this.aesKeyB64 = aesKeyB64;
    }

    @PostConstruct
    void init() {
        byte[] hmac = Base64.getDecoder().decode(hmacKeyB64);
        byte[] aes  = Base64.getDecoder().decode(aesKeyB64);
        if (hmac.length < 32) {
            throw new IllegalStateException("EMAIL_HMAC_KEY must be ≥ 32 bytes after base64 decode");
        }
        if (aes.length != 32) {
            throw new IllegalStateException("EMAIL_AES_KEY must be exactly 32 bytes (AES-256) after base64 decode");
        }
        this.hmacKey = new SecretKeySpec(hmac, HMAC_ALG);
        this.aesKey  = new SecretKeySpec(aes, AES_ALG);
        log.info("EmailCipherService initialized (HMAC-SHA-256 + AES-256-GCM)");
    }

    public String normalize(String email) {
        if (email == null) return "";
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public String hash(String email) {
        try {
            String normalized = normalize(email);
            Mac mac = Mac.getInstance(HMAC_ALG);
            mac.init(hmacKey);
            byte[] raw = mac.doFinal(normalized.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(raw);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to HMAC email", e);
        }
    }

    public String encrypt(String email) {
        try {
            byte[] iv = new byte[GCM_IV_LEN_BYTES];
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_LEN_BITS, iv));

            byte[] cipherText = cipher.doFinal(email.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to AES-GCM encrypt email", e);
        }
    }

    public String decrypt(String encryptedB64) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedB64);
            byte[] iv = new byte[GCM_IV_LEN_BYTES];
            byte[] cipherText = new byte[combined.length - GCM_IV_LEN_BYTES];
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LEN_BYTES);
            System.arraycopy(combined, GCM_IV_LEN_BYTES, cipherText, 0, cipherText.length);

            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_LEN_BITS, iv));
            byte[] plain = cipher.doFinal(cipherText);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to AES-GCM decrypt email", e);
        }
    }
}
