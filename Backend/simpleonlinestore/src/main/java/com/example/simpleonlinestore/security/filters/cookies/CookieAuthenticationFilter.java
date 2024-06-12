package com.example.simpleonlinestore.security.filters.cookies;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieAuthenticationFilter extends OncePerRequestFilter {

  @Lazy
  @Autowired
  private AuthenticationManager authenticationManager;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    Cookie secretCookie = null;
    Cookie loginCookie = null;
    if (request.getCookies() == null) {
      throw new BadCredentialsException("Missing Cookie");
    }
    for (Cookie cookie : request.getCookies()) {
      if (cookie.getName().equals(CookieGenerator.COOKE_NAME)) {
        secretCookie = cookie;
      } else if (cookie.getName().equals("login")) {
        loginCookie = cookie;
      }
    }

    if (secretCookie != null && loginCookie != null) {
      SecurityContext sc = SecurityContextHolder.getContext();
      CookieAuthenticationToken cookieAuthToken = new CookieAuthenticationToken(loginCookie.getValue(), secretCookie.getValue());
      Authentication auth = authenticationManager.authenticate(cookieAuthToken);
      sc.setAuthentication(auth);
      filterChain.doFilter(request, response);
      return;
    }

    throw new BadCredentialsException("Missing Cookie");
  }

}