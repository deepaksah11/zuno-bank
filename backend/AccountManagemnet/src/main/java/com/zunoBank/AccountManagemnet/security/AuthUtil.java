package com.zunoBank.AccountManagemnet.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class AuthUtil {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(
                jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
        // returns employeeId — e.g. "EMP-2026-0003"
    }

    public String getRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
        // returns e.g. "BRANCH_MANAGER", "RELATIONSHIP_OFFICER"
    }

    public String getBranchCodeFromToken(String token) {
        return getClaims(token).get("branchCode", String.class);
        // bonus — you can get branchCode directly from JWT too
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}