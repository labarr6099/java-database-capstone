package com.project.back_end.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Service
public class TokenService {

    // ---------------------------------------------------------
    // SECRET KEY (must be at least 256 bits for HS256)
    // ---------------------------------------------------------
    private static final String SECRET = "mySuperSecretKeyForJwtTokenGeneration1234567890";

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Base64.getEncoder().encode(SECRET.getBytes()));
    }

    // ---------------------------------------------------------
    // GENERATE TOKEN
    // ---------------------------------------------------------
    public String generateToken(Long id, String identifier, String role) {

        return Jwts.builder()
                .subject(identifier)
                .claims(Map.of(
                        "id", id,
                        "role", role
                ))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ---------------------------------------------------------
    // EXTRACT CLAIMS
    // ---------------------------------------------------------
    private Claims extractAllClaims(String token) {

        Jws<Claims> jws = Jwts.parser()
                .verifyWith(getSigningKey())   // ✔ SecretKey is valid
                .build()
                .parseSignedClaims(token);

        return jws.getPayload();
    }

    // ---------------------------------------------------------
    // EXTRACT USER ID
    // ---------------------------------------------------------
    public Long extractId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("id", Long.class);
    }

    // ---------------------------------------------------------
    // EXTRACT IDENTIFIER (email/username)
    // ---------------------------------------------------------
    public String extractIdentifier(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    // ---------------------------------------------------------
    // EXTRACT ROLE
    // ---------------------------------------------------------
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    // ---------------------------------------------------------
    // VALIDATE TOKEN (ROLE CHECK)
    // ---------------------------------------------------------
    public boolean validateToken(String token, String expectedRole) {
        try {
            Claims claims = extractAllClaims(token);

            String role = claims.get("role", String.class);
            Date expiration = claims.getExpiration();

            if (!expectedRole.equals(role)) {
                return false;
            }

            return expiration.after(new Date());

        } catch (Exception e) {
            return false;
        }
    }
}
