package com.example.simpleonlinestore.security.filters.cookies;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class CookieAuthenticationToken extends AbstractAuthenticationToken {

private final String login;
  private final String secret;

  public CookieAuthenticationToken(String login, String secret) {
    super(null);
    this.login = login;
    this.secret = secret;
  }

  public CookieAuthenticationToken(String login, String secret, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.login = login;
    this.secret = secret;
    this.setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return secret;
  }

  @Override
  public Object getPrincipal() {
    return login;
  }
}
