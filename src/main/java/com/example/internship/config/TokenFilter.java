package com.example.internship.config;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class TokenFilter extends OncePerRequestFilter {

    private final Jwttokens jwttokens;
    private final UserDetailsService userDetailsService;

    @Autowired
    public TokenFilter(Jwttokens jwttokens, UserDetailsService userDetailsService) {
        this.jwttokens = jwttokens;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = null;
        String email = null;
        UserDetails userDetails = null;
        UsernamePasswordAuthenticationToken auth = null;

        try {
            String headerAuth = request.getHeader("Authorization");
            if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
                jwt = headerAuth.substring(7); // Извлечение токена
                System.out.println("JWT Token: " + jwt); // Логируем токен
            }

            if (jwt != null) {
                try {
                    email = jwttokens.getNameFromJwt(jwt); // Получаем email из токена
                    System.out.println("Extracted email from JWT: " + email); // Логируем email
                } catch (ExpiredJwtException e) {
                    System.out.println("Token expired: " + e.getMessage()); // Логируем ошибку истекшего токена
                }

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    userDetails = userDetailsService.loadUserByUsername(email);
                    System.out.println("Loading user by email: " + email); // Логируем загрузку пользователя
                    auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth); // Устанавливаем аутентификацию в контекст
                }
            }
        } catch (Exception e) {
            e.printStackTrace();  // Логируем все ошибки
        }
        filterChain.doFilter(request, response);  // Продолжаем фильтрацию
    }
}
