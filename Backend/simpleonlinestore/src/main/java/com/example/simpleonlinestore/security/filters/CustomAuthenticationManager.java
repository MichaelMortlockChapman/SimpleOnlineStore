package com.example.simpleonlinestore.security.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import com.example.simpleonlinestore.security.filters.logins.LoginAuthenticationProvider;
import com.example.simpleonlinestore.security.filters.tokens.TokenAuthenticationProvider;


// Configuration that allows filters to use their providers
@Configuration
public class CustomAuthenticationManager {
  
  @Autowired
  private LoginAuthenticationProvider loginAuthenticationProvider;
 
  @Autowired
  private TokenAuthenticationProvider tokenAuthenticationProvider;

  // need lazy or otherwise a circular dependency occurs
  @Lazy
  @Bean
  AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    return new ProviderManager(loginAuthenticationProvider, tokenAuthenticationProvider);
  }
}
