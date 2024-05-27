package com.example.simpleonlinestore.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class CustomAuthenticationManager {
  
  @Autowired
  private LoginAuthenticationProvider loginAuthenticationProvider;

  @Autowired
  private TokenAuthenticationProvider tokenAuthenticationProvider;

  @Lazy
  @Bean
  AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder.authenticationProvider(loginAuthenticationProvider);
    authenticationManagerBuilder.authenticationProvider(tokenAuthenticationProvider);
    return authenticationManagerBuilder.build();
  }
}
