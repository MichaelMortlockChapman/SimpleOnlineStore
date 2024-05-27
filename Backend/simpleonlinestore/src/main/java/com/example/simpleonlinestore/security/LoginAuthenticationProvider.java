package com.example.simpleonlinestore.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.simpleonlinestore.database.users.UserRepository;

@Service
public class LoginAuthenticationProvider implements AuthenticationProvider {

  @Value("${backend.secret}")
  private String secret;

  @Autowired
  private PasswordEncoder passwordEncoder;

  private final UserRepository userRepository;

  public LoginAuthenticationProvider(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    final String name = authentication.getName();
    final String password = authentication.getCredentials().toString();

    UserDetails userDetails = userRepository.findByLogin(name);
    if (userDetails != null && passwordEncoder.matches(password + secret, userDetails.getPassword())) {
      return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), password + secret, userDetails.getAuthorities());
    }
    authentication.setAuthenticated(false);
    return authentication;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
  
}
