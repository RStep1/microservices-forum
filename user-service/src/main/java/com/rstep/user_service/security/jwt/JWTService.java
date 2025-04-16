package com.rstep.user_service.security.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rstep.user_service.model.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JWTService {
    private SecretKey secretKey;
    private long expiration;

    public JWTService(@Value("${jwt.secret}") String secretKey, 
                        @Value("${jwt.expiration}") Long expiration) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.expiration = expiration;
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        return buildToken(claims, user.getUsername());
    }

    private String buildToken(Map<String, Object> extraClaims, String username) {
        return Jwts.builder()
            .claims(extraClaims)
            .subject(username)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(secretKey, Jwts.SIG.HS256)
            .header().type("JWT").and()
            .compact();
    }

    public boolean validateJwtToken(String jwt) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getExpiration()
                .after(new Date());
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    public String getUsernameFromJwtToken(String jwt) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }
}
