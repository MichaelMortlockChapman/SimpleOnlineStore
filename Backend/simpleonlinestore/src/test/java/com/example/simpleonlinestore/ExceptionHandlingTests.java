package com.example.simpleonlinestore;

import static com.example.simpleonlinestore.AuthTests.createSignupJSON;
import java.time.Instant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.simpleonlinestore.controllers.HelloController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class ExceptionHandlingTests {

  @MockBean
  HelloController helloController;

  private String cookie;

  @BeforeEach
  public void start(@Autowired WebTestClient webClient) {
    String username = ""+Instant.now().toEpochMilli();
    cookie = webClient
      .post().uri("/v1/auth/signup/customer")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createSignupJSON(username)) 
      .exchange()
      .expectStatus().isCreated()
      .returnResult(HttpHeaders.class)
      .getResponseHeaders().getFirst(HttpHeaders.SET_COOKIE);
  }

  @AfterEach
  public void end(@Autowired WebTestClient webClient) {
    webClient
      .delete().uri("/v1/auth/delete")
      .header("Cookie", cookie)
      .exchange()
      .expectStatus().isOk();
  }

  @Test
  void badRouteIs404ForUser(@Autowired WebTestClient webClient) {
    webClient
      .get().uri("/v1/badRoute")
      .header("Cookie", cookie)
      .exchange()
      .expectStatus()
      .isEqualTo(404);
  }

  @Test
  void badRouteIs403ForNonUser(@Autowired WebTestClient webClient) {
    webClient
      .get().uri("/v1/badRoute")
      .exchange()
      .expectStatus()
      .isEqualTo(403);
  }

  @Test
  void serverSends500onInternalError(@Autowired WebTestClient webClient) {
    Mockito.when(helloController.hello()).thenThrow(new RuntimeException());

    webClient
      .get().uri("/v1/hello")
      .header("Cookie", cookie)
      .exchange()
      .expectStatus()
      .isEqualTo(500);
  }

  @Test
  void badAuthHeaderThrows401(@Autowired WebTestClient webClient) {
    webClient
      .post().uri("/v1/auth/signin")
      .header("Authorization", "hi")
      .exchange()
      .expectStatus().isEqualTo(401);
  }

  @Test
  void incorrectAuthHeaderThrows401(@Autowired WebTestClient webClient) {
    webClient
      .post().uri("/v1/auth/signin")
      .header("Authorization", "Basic 111122222")
      .exchange()
      .expectStatus().isEqualTo(401);
  }
}
