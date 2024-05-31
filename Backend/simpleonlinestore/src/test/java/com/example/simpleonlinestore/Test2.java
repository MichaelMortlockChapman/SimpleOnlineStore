package com.example.simpleonlinestore;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.config.web.server.ServerHttpSecurity.HeaderSpec;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class Test2 {
  
  private String base64Login = "Basic dGhpbmdAdGhpbmcuY29tOnRoaW5nIG5hbWU=";

  @Test
  void exampleTest(@Autowired WebTestClient webClient) {
    
    FluxExchangeResult<Object> reponse = webClient
      .post().uri("/v1/auth/signin")
      .header("Authorization", base64Login)
      .exchange()
      .expectStatus().isOk()
      .expectCookie().exists("auth_cookie")
      .returnResult(Object.class);

    MultiValueMap<String, ResponseCookie> responseCookies = reponse.getResponseCookies();
    MultiValueMap<String, String> myCookies = new LinkedMultiValueMap<String, String>();
    for (String key : responseCookies.keySet()) {
      String cookieValue = responseCookies.get(key).get(0).getValue();
      myCookies.add(key, cookieValue);
    }
    System.out.println(responseCookies.toString());
    
    webClient
      .get().uri("/v1/hello/user").cookies(cookies -> cookies.addAll(myCookies))
      .exchange()
      // .expectStatus().isOk()
      .expectBody()
      .consumeWith(body -> 
        System.out.println(new String(body.getResponseBody()))
        // assertTrue(Base64.getEncoder().encodeToString(body.getResponseBody()).equals("Hello, user!"))
      );
  }
}
