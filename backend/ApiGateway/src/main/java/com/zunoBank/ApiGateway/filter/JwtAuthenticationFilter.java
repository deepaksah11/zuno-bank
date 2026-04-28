package com.zunoBank.ApiGateway.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Value("${jwt.secretKey}")
    private String secretKey;

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            // ✅ ALLOW PREFLIGHT (MOST IMPORTANT FIX)
            if (exchange.getRequest().getMethod().name().equals("OPTIONS")) {
                return chain.filter(exchange);
            }

            String path = exchange.getRequest().getURI().getPath();

            if (path.contains("/api/v1/auth")) {
                return chain.filter(exchange);
            }

            String token = null;

            // 🔥 1. Try cookie first
            if (exchange.getRequest().getCookies().getFirst("token") != null) {
                token = exchange.getRequest()
                        .getCookies()
                        .getFirst("token")
                        .getValue();
            }

            // 🔁 2. Fallback to Authorization header (optional)
            if (token == null) {
                String authHeader = exchange.getRequest()
                        .getHeaders()
                        .getFirst(HttpHeaders.AUTHORIZATION);

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }
            }

            // ❌ If still null → unauthorized
            if (token == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            try {
//                String token = authHeader.substring(7);

                SecretKey key = Keys.hmacShaKeyFor(
                        secretKey.getBytes(StandardCharsets.UTF_8)
                );

                Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token);

                return chain.filter(exchange);

            } catch (Exception e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    public static class Config {}
}