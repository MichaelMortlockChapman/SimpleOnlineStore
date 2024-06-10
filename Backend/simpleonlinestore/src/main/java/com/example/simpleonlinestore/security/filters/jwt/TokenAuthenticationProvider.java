package com.example.simpleonlinestore.security.filters.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Service;

import com.example.simpleonlinestore.database.users.UserRepository;

@Service
public class TokenAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private JwtService jwtService;

  @Autowired
  private UserRepository userRepository;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String token  = authentication.getCredentials().toString();
    if (jwtService.validateToken(token)) {
      String login = jwtService.extractUserName(token);
      UserDetails user = userRepository.findByLogin(login);
      return new CustomTokenAuthentication(login, token, user.getAuthorities());
    }

    throw new BadJwtException("Bad token");
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(BearerTokenAuthenticationToken.class);
  }
}
