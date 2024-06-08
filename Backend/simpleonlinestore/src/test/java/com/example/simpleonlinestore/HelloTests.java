package com.example.simpleonlinestore;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.simpleonlinestore.controllers.HelloController;
import com.example.simpleonlinestore.security.SecurityConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(SecurityConfig.class)
public class HelloTests {

  @Autowired
  private HelloController helloController;

  @Test
  @WithMockUser(roles = "ADMIN")
  void basicHelloWithAdmin(@Autowired WebTestClient webClient) {
    assertTrue(helloController.hello().equals("Hello, World!"));
  }

  @Test
  @WithMockUser(roles = "USER")
  void basicHelloWithUser(@Autowired WebTestClient webClient) {
    assertTrue(helloController.hello().equals("Hello, World!"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void adminHelloWithAdmin(@Autowired WebTestClient webClient) {
    assertTrue(helloController.helloAdmin().equals("Hello, admin!"));
  }

  @Test
  @WithMockUser(roles = "USER")
  void userHelloWithUser(@Autowired WebTestClient webClient) {
    assertTrue(helloController.helloUser().equals("Hello, user!"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void userHelloWithAdmin(@Autowired WebTestClient webClient) {
    assertTrue(helloController.helloUser().equals("Hello, user!"));
  }

  @Test
  @WithMockUser(roles = "USER")
  void adminHelloWithUser(@Autowired WebTestClient webClient) {
    assertThrows(AccessDeniedException.class, () -> helloController.helloAdmin());
  }
}
