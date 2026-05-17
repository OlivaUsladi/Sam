package com.example.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HexFormat;

@Slf4j
@Service
public class JwtService {

    private final String secretB64;
    private final long accessTtlMinutes;
    private final long refreshTtlDays;

    private SecretKey signingKey;
    private final SecureRandom random = new SecureRandom();

    public JwtService(
            @Value("${app.security.jwt.secret}") String secretB64,
            @Value("${app.security.jwt.access-ttl-minutes:15}") long accessTtlMinutes,
            @Value("${app.security.jwt.refresh-ttl-days:30}") long refreshTtlDays
    ) {
        this.secretB64 = secretB64;
        this.accessTtlMinutes = accessTtlMinutes;
        this.refreshTtlDays = refreshTtlDays;
    }

    @PostConstruct
    void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secretB64);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET must be ≥ 32 bytes after base64 decode (HS256)");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        log.info("JwtService initialized (HS256, accessTTL={}min, refreshTTL={}d)", accessTtlMinutes, refreshTtlDays);
    }


    public String generateAccessToken(Integer userId) {
        Instant now = Instant.now();
        Instant exp = now.plus(Duration.ofMinutes(accessTtlMinutes));
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public Integer parseUserId(String accessToken) {
        try {
            Jws<Claims> parsed = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(accessToken);
            return Integer.parseInt(parsed.getPayload().getSubject());
        } catch (Exception e) {
            return null;
        }
    }

    public String generateRefreshToken() {
        byte[] buf = new byte[32];
        random.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

    public String hashRefreshToken(String rawToken) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] raw = md.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(raw);
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    public Instant refreshExpiry() {
        return Instant.now().plus(Duration.ofDays(refreshTtlDays));
    }

    public long getAccessTtlSeconds() {
        return accessTtlMinutes * 60L;
    }
}
