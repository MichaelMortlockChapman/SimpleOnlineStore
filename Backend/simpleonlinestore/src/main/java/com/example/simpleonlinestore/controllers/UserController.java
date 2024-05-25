package com.example.simpleonlinestore.controllers;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

import com.example.simpleonlinestore.database.users.User;
import com.example.simpleonlinestore.database.users.UserRepository;
import com.example.simpleonlinestore.security.UserRole;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class UserController {

  @Value("${backend.secret}")
  private String secret;
  
  @Autowired
  private UserRepository userRepository;

  // allows access to spring's encoders
  private final PasswordEncoder passwordEncoder;

  public UserController() {
    passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  // simple record for login info
  public record LoginRequest(String login, String password) {}

  // pattern for valid emails
  private static Pattern emailRegex = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

  /**
   * Simple signin route to authenticate users
   * @param loginRequest record of singin infomation (email, password)
   * @return ResponseEntity with status code
   */
  @PostMapping("/v1/auth/signin")
  public ResponseEntity<String> signin(@RequestBody LoginRequest loginRequest) {
    Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequest.login(), loginRequest.password(), userRepository.findByLogin(loginRequest.login()).getAuthorities());
    if (authentication.isAuthenticated()) {
      return ResponseEntity.ok("signed in"); 
    } else {
      return ResponseEntity.badRequest().build();
    }
  }  
  
  /**
   * Simple signup route to create users 
   *  returns BAD_REQUEST status if email already in use, email is invalid, and or password in less than 8 chars
   * @param loginRequest record of signup infomation (email, password)
   * @return ResponseEntity with status code
   */
  @PostMapping("/v1/auth/signup")
  public ResponseEntity<String> signup(@RequestBody LoginRequest loginRequest) {
    if (userRepository.findByLogin(loginRequest.login()) != null) {
      return new ResponseEntity<String>("Login already in use", HttpStatus.BAD_REQUEST);
    } else if (!emailRegex.matcher(loginRequest.login()).find()) {
      return new ResponseEntity<String>("Invalid email", HttpStatus.BAD_REQUEST);
    } else if (loginRequest.password.length() < 8) {
      return new ResponseEntity<String>("Password less than 8 characters", HttpStatus.BAD_REQUEST);
    }

    String encodedPassword = passwordEncoder.encode(loginRequest.password + secret);
    userRepository.save(new User(loginRequest.login, encodedPassword, UserRole.ROLE_USER));
    return new ResponseEntity<String>("User signed up", HttpStatus.CREATED);
  }
  
}
