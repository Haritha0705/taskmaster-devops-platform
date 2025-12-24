package com.taskmaster.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final JwtBlacklistService jwtBlacklistService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        log.debug("[JwtAuthFilter] Request to {}", path);

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("[JwtAuthFilter] No Authorization header or not Bearer for {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        log.debug("[JwtAuthFilter] Found Bearer token (masked) for {}", path);

        // BLOCK blacklisted tokens
        try {
            if (jwtBlacklistService.isBlacklisted(token)) {
                log.warn("[JwtAuthFilter] Token is blacklisted for request {}", path);
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token is blacklisted");
                return;
            }
        } catch (Exception ex) {
            log.warn("[JwtAuthFilter] Error checking blacklist: {}", ex.toString());
            // continue — treat as not blacklisted (fallback behaviour)
        }

        String username;
        try {
            username = jwtTokenProvider.extractUsername(token);
            log.debug("[JwtAuthFilter] Extracted username='{}' from token for {}", username, path);
        } catch (Exception e) {
            log.debug("[JwtAuthFilter] Failed to extract username from token: {}", e.toString());
            filterChain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.debug("[JwtAuthFilter] No authentication present in context, will load user details for {}", username);

            UserDetails userDetails;
            try {
                userDetails = userDetailsService.loadUserByUsername(username);
            } catch (Exception e) {
                log.warn("[JwtAuthFilter] Failed to load user by username {}: {}", username, e.toString());
                filterChain.doFilter(request, response);
                return;
            }

            boolean valid = false;
            try {
                valid = jwtTokenProvider.isTokenValid(token, userDetails);
            } catch (Exception ex) {
                log.warn("[JwtAuthFilter] Error validating token for {}: {}", username, ex.toString());
            }

            if (valid) {
                log.debug("[JwtAuthFilter] Token valid for {}; setting authentication", username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("[JwtAuthFilter] Authentication set with authorities {} for {}", userDetails.getAuthorities(), username);
            } else {
                log.debug("[JwtAuthFilter] Token invalid or expired for {}", username);
            }
        } else {
            log.debug("[JwtAuthFilter] Username is null or authentication already present for {}", path);
        }

        filterChain.doFilter(request, response);
    }
}
