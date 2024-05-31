package com.example.simpleonlinestore.controllers;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

import com.example.simpleonlinestore.database.sessions.Session;
import com.example.simpleonlinestore.database.sessions.SessionRepository;
import com.example.simpleonlinestore.database.users.User;
import com.example.simpleonlinestore.database.users.UserRepository;
import com.example.simpleonlinestore.security.UserRole;
import com.example.simpleonlinestore.security.filters.cookies.CookieGenerator;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
public class UserController {

  @Value("${backend.secret}")
  private String secret;
  
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CookieGenerator cookieGenerator;

  @Autowired
  private SessionRepository sessionRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  // simple record for login info
  public record LoginRequest(String login, String password) {}

  // pattern for valid emails
  private static Pattern emailRegex = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

  /**
   * Simple signin route to authenticate users. Using Authorization header to pass thru login info as it is needed by loginFilter (needed to get here)
   *  and would be redundant to pass login info in both header and body
   * @param authHeader with basic auth deatails password and email
   * @return ResponseEntity with status code
   */
  @PostMapping("/v1/auth/signin")
  public ResponseEntity<String> signin(@RequestHeader("Authorization") String authHeader, HttpServletResponse response) {
    String base64Credentials = authHeader.substring("Basic".length()).trim();
    byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
    String credentials = new String(credDecoded, StandardCharsets.UTF_8);
    String[] values = credentials.split(":", 2);
    LoginRequest loginRequest = new LoginRequest(values[0], values[1]);

    Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequest.login(), loginRequest.password(), userRepository.findByLogin(loginRequest.login()).getAuthorities());
    if (authentication.isAuthenticated()) {
      Cookie c = cookieGenerator.generateToken(loginRequest.login());
      sessionRepository.save(new Session(c.getValue(), loginRequest.login()));
      response.addCookie(c);
      return ResponseEntity.ok().build(); 
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
  
  @PostMapping("/v1/auth/signout")
  public ResponseEntity<String> postMethodName(HttpServletRequest request, HttpServletResponse response,
      @CookieValue(CookieGenerator.COOKIE_LOGIN) String loginCookieValue, 
      @CookieValue(CookieGenerator.COOKE_NAME) String authCookieValue) {
    SecurityContextHolder.clearContext();

    response.addCookie(cookieGenerator.invalidateCookie(loginCookieValue));
    sessionRepository.deleteById(authCookieValue);

    return ResponseEntity.ok("Done");
  }
  
}
