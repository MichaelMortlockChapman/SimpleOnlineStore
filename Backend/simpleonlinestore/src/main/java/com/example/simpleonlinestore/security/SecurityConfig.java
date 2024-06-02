package com.example.simpleonlinestore.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.simpleonlinestore.security.filters.ExceptionHandlerFilter;
import com.example.simpleonlinestore.security.filters.cookies.CookieAuthenticationFilter;
import com.example.simpleonlinestore.security.filters.jwt.TokenAuthenticationFilter;
import com.example.simpleonlinestore.security.filters.logins.LoginAuthenticationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
  securedEnabled = true
)
@ComponentScan("com.example.simpleonlinestore")
public class SecurityConfig {

  @Autowired
  private CookieAuthenticationFilter cookieAuthenticationFilter;

  @Autowired
  private LoginAuthenticationFilter loginAuthenticationFilter;

  @Autowired
  private ExceptionHandlerFilter exceptionHandlerFilter;

  @Bean
  PasswordEncoder passwordEncoder() {
      return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  // need to disable registration of filters otherwise auto added to filter chains
  @Bean
  FilterRegistrationBean<LoginAuthenticationFilter> registrationLoginFilter(LoginAuthenticationFilter filter) {
    FilterRegistrationBean<LoginAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
    registration.setEnabled(false);
    return registration;
  }
  @Bean
  FilterRegistrationBean<TokenAuthenticationFilter> registrationTokenFilter(TokenAuthenticationFilter filter) {
    FilterRegistrationBean<TokenAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
    registration.setEnabled(false);
    return registration;
  }

  @Bean
  FilterRegistrationBean<ExceptionHandlerFilter> registrationExceptionFilter(ExceptionHandlerFilter filter) {
    FilterRegistrationBean<ExceptionHandlerFilter> registration = new FilterRegistrationBean<>(filter);
    registration.setEnabled(false);
    return registration;
  }

  @Bean
  FilterRegistrationBean<CookieAuthenticationFilter> customerCookieFilter(CookieAuthenticationFilter filter) {
    FilterRegistrationBean<CookieAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
    registration.setEnabled(false);
    return registration;
  }

  // custom filter for auth routes to permit them without authorization (also without cors and csrf )
  @Bean
  @Order(0)
  SecurityFilterChain authSignupSecurityFilterChain(HttpSecurity http) throws Exception {
    return http
            .securityMatcher("/v1/auth/signup/**", "/v1/auth/signout")
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
            .cors(cors -> cors.disable())
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(loginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(exceptionHandlerFilter, LoginAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .build();
  }

  @Bean
  @Order(2)
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests((requests) -> requests.requestMatchers(new AntPathRequestMatcher("/v1/auth/**")).permitAll().anyRequest().authenticated())
        // .cors(cors -> cors.disable())
        .csrf(csrf -> csrf.disable())
        .addFilterBefore(cookieAuthenticationFilter,  UsernamePasswordAuthenticationFilter.class)
        // .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(exceptionHandlerFilter, CookieAuthenticationFilter.class)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .build();
  }
}
