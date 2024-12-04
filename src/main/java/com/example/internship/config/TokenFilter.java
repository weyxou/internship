package com.example.internship.config;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration

public class TokenFilter extends OncePerRequestFilter {

    private Jwttokens jwttokens;
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = null;
        String username = null;
        UserDetails userDetails = null;

        UsernamePasswordAuthenticationToken auth = null;

        try {
            String headerAuth = request.getHeader("Auth");
            if (headerAuth !=null && headerAuth.startsWith("Bearer")){
                jwt = headerAuth.substring(7);
            }

            if (jwt != null){
                try {
                    username =jwttokens.getNameFromJwt(jwt);
                } catch (ExpiredJwtException e){}

                if (userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null){
                    userDetails = userDetailsService.loadUserByUsername(username);
                    auth = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }

        } catch (Exception e){}
        filterChain.doFilter(request, response);
    }
}
