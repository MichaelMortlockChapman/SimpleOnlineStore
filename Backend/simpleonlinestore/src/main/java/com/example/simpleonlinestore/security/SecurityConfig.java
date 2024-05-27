package com.example.simpleonlinestore.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
  private TokenAuthenticationFilter tokenAuthenticationFilter;

  @Autowired
  private LoginAuthenticationFilter loginAuthenticationFilter;

  @Bean
  PasswordEncoder passwordEncoder() {
      return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  // custom filter for auth routes to permit them without authorization (also without cors and csrf )
  @Bean
  @Order(0)
  SecurityFilterChain authSignupSecurityFilterChain(HttpSecurity http) throws Exception {
    return http
            .securityMatcher("/v1/auth/signup")
            .authorizeHttpRequests((authorize) -> authorize.anyRequest().permitAll())
            .cors(cors -> cors.disable())
            .csrf(csrf -> csrf.disable())
            .securityContext(context -> context.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .build();
  }

  @Bean
  @Order(1)
  SecurityFilterChain authSigninSecurityFilterChain(HttpSecurity http) throws Exception {
    return http
            .securityMatcher("/v1/auth/signin")
            .authorizeHttpRequests((requests) -> requests.anyRequest().authenticated())
            // .cors(cors -> cors.disable())
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(loginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exp -> exp.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))) //need to config correctly atm just sends 401 
            .build();
  }

  @Bean
  @Order(2)
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests((requests) -> requests.anyRequest().authenticated())
        // .cors(cors -> cors.disable())
        // .csrf(csrf -> csrf.disable())
        .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(exp -> exp.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))) //need to config correctly atm just sends 401 
        .build();
  }
}
