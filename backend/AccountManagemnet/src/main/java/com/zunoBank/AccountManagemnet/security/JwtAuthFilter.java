package com.zunoBank.AccountManagemnet.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthUtil authUtil;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            log.info("Incoming Request: {}", request.getRequestURI());

            final String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = header.split("Bearer ")[1];

            // extract employeeId and role directly from JWT
            // no DB call needed — token already verified by auth-service
            String employeeId = authUtil.getUsernameFromToken(token);
            String role = authUtil.getRoleFromToken(token);

            if (employeeId != null
                    && SecurityContextHolder.getContext()
                    .getAuthentication() == null) {

                // build UserDetails from JWT claims — no DB call needed
                org.springframework.security.core.userdetails.User userDetails =
                        new org.springframework.security.core.userdetails.User(
                                employeeId,
                                "",
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,                    // ← UserDetails not String
                                null,
                                userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            handlerExceptionResolver.resolveException(
                    request, response, null, ex);
        }
    }
}