package com.example.simpleonlinestore.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class CustomTokenAuthentication extends AbstractAuthenticationToken {

  private final String login;
  private final String token;

  public CustomTokenAuthentication(String login, String token, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.login = login;
    this.token = token;
    this.setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return token;
  }

  @Override
  public Object getPrincipal() {
    return login;
  }

}