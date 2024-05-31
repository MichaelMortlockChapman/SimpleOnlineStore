package com.example.simpleonlinestore.security.filters.cookies;

import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;

@Service
public class CookieGenerator {

  public static final String COOKE_NAME = "auth_cookie";
  public static final String COOKIE_LOGIN = "login";

  @Value("${backend.secret}")
  private String secret;

  public Cookie generateToken(String login) {
    Calendar expires = Calendar.getInstance();
    expires.add(Calendar.MONTH, 1);
    String token = expires.toInstant().toEpochMilli() + ":" + UUID.randomUUID().toString();
    Cookie cookie = new Cookie(COOKE_NAME, token);
    cookie.setAttribute(COOKIE_LOGIN, login);
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setMaxAge((int)ChronoUnit.MONTHS.getDuration().toSeconds());

    return cookie;
  }

  public Cookie invalidateCookie(String login) {
    Cookie cookie = new Cookie(COOKE_NAME, null); //saves bandwidth and still works
    cookie.setAttribute(COOKIE_LOGIN, login);
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setMaxAge(0);

    return cookie;
  }
}
