package com.example.simpleonlinestore;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class AuthTests {
  
  private String getAuthMsgUsername(String username, String password) {
    byte[] bytes = Base64.getEncoder().encode((username + "@test.org:" +password).getBytes());
    return "Basic " + new String(bytes);
  }

  private String getAuthMsg(String username) {
    return getAuthMsgUsername(username, "password");
  }

  @Test 
  void SignupAndDelete(@Autowired WebTestClient webClient) {
    String set_cookie = webClient
      .post().uri("/v1/auth/signup")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("{ \"login\": \"" + Instant.now().toEpochMilli() + "@test.org\", \"password\": \"password\"}")
      .exchange()
      .expectStatus().isCreated()
      .returnResult(HttpHeaders.class)
      .getResponseHeaders().getFirst(HttpHeaders.SET_COOKIE);
    assertNotNull(set_cookie);

    webClient
      .delete().uri("/v1/auth/delete")
      .header("Cookie", set_cookie)
      .exchange()
      .expectStatus().isOk();

    webClient
      .delete().uri("/v1/auth/delete")
      .header("Cookie", set_cookie)
      .exchange()
      .expectStatus().isUnauthorized();
  }

  @Test
  void SignInAndOutTest(@Autowired WebTestClient webClient) {
    String username = "" + Instant.now().toEpochMilli();

    // signup
    String set_cookie = webClient
      .post().uri("/v1/auth/signup")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("{ \"login\": \"" + username + "@test.org\", \"password\": \"password\"}")
      .exchange()
      .expectStatus().isCreated()
      .returnResult(HttpHeaders.class)
      .getResponseHeaders().getFirst(HttpHeaders.SET_COOKIE);
    assertNotNull(set_cookie);

    // show valid user 
    webClient
      .get().uri("/v1/hello/user")
      .header("Cookie", set_cookie)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .consumeWith(body ->
        assertTrue("Hello, user!".equals(new String(body.getResponseBodyContent())))
      );

    // check signout valid
    webClient
      .post().uri("v1/auth/signout")
      .header("Cookie", set_cookie)
      .exchange()
      .expectStatus().isOk();

    // sign into account
    set_cookie = webClient
      .post().uri("/v1/auth/signin")
      .header("Authorization", getAuthMsg(username))
      .exchange()
      .expectStatus().isOk()
      .returnResult(HttpHeaders.class)
      .getResponseHeaders().getFirst(HttpHeaders.SET_COOKIE);
    assertNotNull(set_cookie);

    // check cookie token is valid
    webClient
      .get().uri("/v1/hello/user")
      .header("Cookie", set_cookie)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .consumeWith(body ->
        assertTrue("Hello, user!".equals(new String(body.getResponseBodyContent())))
      );

    // clean up (delete user)
    webClient
      .delete().uri("/v1/auth/delete")
      .header("Cookie", set_cookie)
      .exchange()
      .expectStatus().isOk();
  }

  @Test
  void updateUserLoginEmail(@Autowired WebTestClient webClient) {
    String username = "" + Instant.now().toEpochMilli();

    // signup
    String set_cookie = webClient
      .post().uri("/v1/auth/signup")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("{ \"login\": \"" + username + "@test.org\", \"password\": \"password\"}")
      .exchange()
      .expectStatus().isCreated()
      .returnResult(HttpHeaders.class)
      .getResponseHeaders().getFirst(HttpHeaders.SET_COOKIE);

    // update email
    String cookie2 = webClient
      .put().uri("/v1/auth/user/update")
      .contentType(MediaType.APPLICATION_JSON)
      .header("Cookie", set_cookie)
      .bodyValue("{ \"login\": \"" + username + "a" + "@test.org\", \"password\": \"password\"}")
      .exchange()
      .expectStatus().isOk()
      .returnResult(HttpHeaders.class)
      .getResponseHeaders().getFirst(HttpHeaders.SET_COOKIE);

    // check old cookie works
    assertNotNull(set_cookie);
    webClient
      .get().uri("/v1/hello/user")
      .header("Cookie", set_cookie.split("login=")[0] + "login=" + username + "a" + "@test.org")
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .consumeWith(body ->
        assertTrue("Hello, user!".equals(new String(body.getResponseBodyContent())))
      );

    // check new cookie works
    webClient
      .get().uri("/v1/hello/user")
      .header("Cookie", cookie2)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .consumeWith(body ->
        assertTrue("Hello, user!".equals(new String(body.getResponseBodyContent())))
      );

    // try sign with new email expect ok
    set_cookie = webClient
      .post().uri("/v1/auth/signin")
      .header("Authorization", getAuthMsg(username + "a"))
      .exchange()
      .expectStatus().isOk()
      .returnResult(HttpHeaders.class)
      .getResponseHeaders().getFirst(HttpHeaders.SET_COOKIE);

    // try sign with old email expect unauthorized
    webClient
      .post().uri("/v1/auth/signin")
      .header("Authorization", getAuthMsg(username))
      .exchange()
      .expectStatus().isUnauthorized();

    // clean up (delete user)
    webClient
      .delete().uri("/v1/auth/delete")
      .header("Cookie", set_cookie)
      .exchange()
      .expectStatus().isOk();    
  }

  @Test
  void updateUserLoginPassword(@Autowired WebTestClient webClient) {
    webClient =webClient.mutate().responseTimeout(Duration.ofSeconds(100000)).build();
    String username = "" + Instant.now().toEpochMilli();

    // signup
    String cookie1 = webClient
      .post().uri("/v1/auth/signup")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("{ \"login\": \"" + username + "@test.org\", \"password\": \"password\"}")
      .exchange()
      .expectStatus().isCreated()
      .returnResult(HttpHeaders.class)
      .getResponseHeaders().getFirst(HttpHeaders.SET_COOKIE);

    // update email
    String newPassword = "password2";
    String cookie2 = webClient
      .put().uri("/v1/auth/user/update")
      .contentType(MediaType.APPLICATION_JSON)
      .header("Cookie", cookie1)
      .bodyValue("{ \"login\": \"" + username + "@test.org\", \"password\": \"" + newPassword + "\"}")
      .exchange()
      .expectStatus().isOk()
      .returnResult(HttpHeaders.class)
      .getResponseHeaders().getFirst(HttpHeaders.SET_COOKIE);

    // check old cookie doesn't work
    webClient
      .get().uri("/v1/hello/user")
      .header("Cookie", cookie1)
      .exchange()
      .expectStatus().isUnauthorized();

    // check new cookie doesn't work
    webClient
      .get().uri("/v1/hello/user")
      .header("Cookie", cookie2)
      .exchange()
      .expectStatus().isUnauthorized();
    
    // try sign with new password
    String cookeie3 = webClient
      .post().uri("/v1/auth/signin")
      .header("Authorization", getAuthMsgUsername(username, newPassword))
      .exchange()
      .expectStatus().isOk()
      .returnResult(HttpHeaders.class)
      .getResponseHeaders().getFirst(HttpHeaders.SET_COOKIE);

    // clean up (delete user)
    webClient
      .delete().uri("/v1/auth/delete")
      .header("Cookie", cookeie3)
      .exchange()
      .expectStatus().isOk();   
  }
}
