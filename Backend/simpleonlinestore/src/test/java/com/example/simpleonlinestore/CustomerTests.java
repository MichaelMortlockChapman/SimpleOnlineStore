package com.example.simpleonlinestore;

import java.time.Instant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.example.simpleonlinestore.AuthTests.USER_ADDRESS;
import static com.example.simpleonlinestore.AuthTests.USER_CITY;
import static com.example.simpleonlinestore.AuthTests.USER_COUNTRY;
import static com.example.simpleonlinestore.AuthTests.USER_NAME;
import static com.example.simpleonlinestore.AuthTests.USER_POSTALCODE;
import static com.example.simpleonlinestore.AuthTests.createSignupJSON;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class CustomerTests {

  private String cookie;

  @BeforeEach
  public void start(@Autowired WebTestClient webClient) {
    cookie = webClient
      .post().uri("/v1/auth/signup/customer")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createSignupJSON(""+Instant.now().toEpochMilli())) 
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
  void getCustomerDetails(@Autowired WebTestClient webClient) {
    webClient
      .get().uri("/v1/customer/details")
      .header("Cookie", cookie)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .consumeWith(body -> {
        String bodyString = new String(body.getResponseBodyContent());
        assertTrue(bodyString.matches("(?i).*\"name\":\"" + USER_NAME + "\"(.*)"));
        assertTrue(bodyString.matches("(?i).*\"address\":\"" + USER_ADDRESS + "\"(.*)"));
        assertTrue(bodyString.matches("(?i).*\"city\":\"" + USER_CITY + "\"(.*)"));
        assertTrue(bodyString.matches("(?i).*\"postalCode\":\"" + USER_POSTALCODE + "\"(.*)"));
        assertTrue(bodyString.matches("(?i).*\"country\":\"" + USER_COUNTRY + "\"(.*)"));
      });
  }
}
