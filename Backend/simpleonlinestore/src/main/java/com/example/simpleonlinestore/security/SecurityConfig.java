package com.example.simpleonlinestore.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
  securedEnabled = true
)
@ComponentScan("com.example.simpleonlinestore")
public class SecurityConfig {

  @Autowired
  private CustomAuthenticationProvider authenticationProvider;

  @Bean
  AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder.authenticationProvider(authenticationProvider);
    return authenticationManagerBuilder.build();
  }

  // custom filter for auth routes to permit them without authorization (also without cors and csrf )
  @Bean
  @Order(0)
  SecurityFilterChain authSecurityFilterChain(HttpSecurity http) throws Exception {
    return http
            .securityMatcher("/v1/auth/**")
            .authorizeHttpRequests((authorize) -> authorize.anyRequest().permitAll())
            .cors(cors -> cors.disable())
            .csrf(csrf -> csrf.disable())
            // .requestCache(cache -> cache.disable())
            .securityContext(context -> context.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .build();
  }

  @Bean
  @Order(1)
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests((requests) -> requests.anyRequest().authenticated())
        // .cors(cors -> cors.disable())
        // .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .httpBasic(withDefaults())
        .build();
  }
}
