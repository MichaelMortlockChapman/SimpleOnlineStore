package com.example.simpleonlinestore.security.filters.logins;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginAuthenticationFilter extends OncePerRequestFilter {

  @Value("${backend.secret}")
  private String secret;

  // need lazy or otherwise a circular dependency occurs
  @Lazy
  @Autowired
  private AuthenticationManager authenticationManager;

  // filter for login details, authenticates if valid
  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    // help from https://stackoverflow.com/questions/16000517/how-to-get-password-from-http-basic-authentication
    String authHeader = request.getHeader("Authorization");
    if(authHeader != null && authHeader.startsWith("Basic ")){
      String[] values = new String[2];
      try {
        String base64Credentials = authHeader.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        values = credentials.split(":", 2); // credentials = username:password
      } catch (Exception e) {
        throw new BadCredentialsException("Bad Auth Header"); 
      }

      SecurityContext sc = SecurityContextHolder.getContext();
      UsernamePasswordAuthenticationToken loginAuthToken = new UsernamePasswordAuthenticationToken(values[0], values[1]);
      Authentication auth = authenticationManager.authenticate(loginAuthToken);
      sc.setAuthentication(auth);
      filterChain.doFilter(request, response);
      return;
    }

    throw new BadCredentialsException("Missing login details"); 
  }
}
