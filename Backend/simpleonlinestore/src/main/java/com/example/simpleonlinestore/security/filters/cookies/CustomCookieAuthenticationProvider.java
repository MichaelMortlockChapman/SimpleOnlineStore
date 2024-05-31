package com.example.simpleonlinestore.security.filters.cookies;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.simpleonlinestore.database.sessions.Session;
import com.example.simpleonlinestore.database.sessions.SessionRepository;
import com.example.simpleonlinestore.database.users.UserRepository;

@Service
public class CustomCookieAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private SessionRepository sessionRepository;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String login = authentication.getPrincipal().toString();
    UserDetails user = userRepository.findByLogin(login);
    String token = authentication.getCredentials().toString();

    //check present and login is correct
    Optional<Session> session = sessionRepository.findById(token);
    if (session.isPresent() && session.get().getUserLogin().equals(login)) {
      Date expires = new Date(Long.parseLong(token.split(":")[0]));
      Date now = new Date();
      if (now.compareTo(expires) < 0) {
        return new CookieAuthenticationToken(login, token, user.getAuthorities());
      } else {
        throw new BadCredentialsException("Expired Token");
      }
    } else {
      throw new BadCredentialsException("Bad Token");
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(CookieAuthenticationToken.class);
  }
}
