package com.zunoBank.Authentication.security;

import com.zunoBank.Authentication.entity.StaffUser;
import com.zunoBank.Authentication.repository.StaffUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final StaffUserRepository staffUserRepository;
    private final AuthUtil authUtil;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return "OPTIONS".equalsIgnoreCase(request.getMethod()) ||
                path.equals("/api/v1/auth/staff/login") ||
                path.equals("/api/v1/auth/staff/logout");
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            log.info("Incoming Request: {}", request.getRequestURI());

            String token = null;
            String path = request.getRequestURI();


            // ✅ 1. Get token from cookies
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("token".equals(cookie.getName())) {
                        token = cookie.getValue();
                        log.info("TOKEN FROM COOKIE: {}", token);
                        break;
                    }
                }
            }

            // 🔁 Optional fallback (for testing with Postman)
            if (token == null) {
                final String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }
            }

            // ❌ No token → continue
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // ✅ Extract username
            String username = authUtil.getUsernameFromToken(token);

            // ✅ Authenticate user
            if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                StaffUser user = staffUserRepository
                        .findByEmployeeId(username)
                        .orElseThrow();

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);

        }catch (Exception ex) {
            log.error("JWT Error: {}", ex.getMessage());

            if (ex instanceof io.jsonwebtoken.ExpiredJwtException) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"Token expired\"}");
                return;
            }

            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }
}
