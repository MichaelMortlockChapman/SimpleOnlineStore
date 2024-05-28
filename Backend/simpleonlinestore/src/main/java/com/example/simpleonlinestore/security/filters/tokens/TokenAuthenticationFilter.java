package com.example.simpleonlinestore.security.filters.tokens;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

  // need lazy or otherwise a circular dependency occurs
  @Lazy
  @Autowired
  private AuthenticationManager authenticationManager;

  // filter for token (Jwt), authenticates if valid
  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
      throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication() == null){
          String token = authHeader.substring(7);
          SecurityContext sc = SecurityContextHolder.getContext();
          BearerTokenAuthenticationToken tokenAuth = new BearerTokenAuthenticationToken(token);
          Authentication auth = authenticationManager.authenticate(tokenAuth);
          sc.setAuthentication(auth);
          filterChain.doFilter(request, response);
          return;
        }

        throw new BadCredentialsException("Missing token");
  }
}
