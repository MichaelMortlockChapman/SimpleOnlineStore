package com.example.simpleonlinestore;

import java.time.Instant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.simpleonlinestore.security.SecurityConfig;

import static com.example.simpleonlinestore.AuthTests.createSignupJSON;
import static com.example.simpleonlinestore.AuthTests.getAuthMsgUsername;
import static com.example.simpleonlinestore.ProductTests.createProductJSON;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(SecurityConfig.class)
@PropertySource("classpath:/testing.properties")
public class OrderTests {

  @Value("${super.email}")
  private String adminEmail;

  @Value("${super.password}")
  private String adminPassword;

  private String email;
  private String cookie;
  private String adminCookie;
  private Integer productId;

  @BeforeEach
  public void start(@Autowired WebTestClient webClient) {
    email = Instant.now().toEpochMilli() + "@test.org";
    cookie = webClient
      .post().uri("/v1/auth/signup/customer")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createSignupJSON(email)) 
      .exchange()
      .expectStatus().isCreated()
      .returnResult(HttpHeaders.class)
      .getResponseHeaders().getFirst(HttpHeaders.SET_COOKIE);

    adminCookie = webClient
      .post().uri("/v1/auth/signin")
      .header("Authorization", getAuthMsgUsername(adminEmail, adminPassword))
      .exchange()
      .expectStatus().isOk()
      .returnResult(HttpHeaders.class)
      .getResponseHeaders().getFirst(HttpHeaders.SET_COOKIE);

    byte[] bodyRaw = webClient
      .post().uri("/v1/product/add")
      .header("Cookie", adminCookie)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createProductJSON()) 
      .exchange()
      .expectStatus().isCreated()
      .returnResult(HttpHeaders.class)
      .getResponseBodyContent();
    String bodyStr = new String(bodyRaw);
    productId = Integer.parseInt(bodyStr.replace("{ productId: \"", "").replace("\"}", ""));
  }

  @AfterEach
  public void end(@Autowired WebTestClient webClient) {
    webClient
      .delete().uri("/v1/auth/delete")
      .header("Cookie", cookie)
      .exchange()
      .expectStatus().isOk();

    webClient
      .method(HttpMethod.DELETE).uri("/v1/product/delete")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(productId) 
      .header("Cookie", adminCookie)
      .exchange()
      .expectStatus().isOk();

    webClient
      .post().uri("/v1/auth/signout")
      .header("Cookie", adminCookie)
      .exchange()
      .expectStatus().isOk();
  }

  @Test
  public void smokeTest(@Autowired WebTestClient webClient) {
    assertTrue(true);
  }
}
