package com.example.simpleonlinestore.security;

import javax.naming.AuthenticationException;

public class UnknownUserAuthenticationException extends AuthenticationException {
  public UnknownUserAuthenticationException(String msg) {
    super(msg);
  }
}
