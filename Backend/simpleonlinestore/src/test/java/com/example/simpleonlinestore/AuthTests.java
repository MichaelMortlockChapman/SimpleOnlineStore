package com.example.simpleonlinestore;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@PropertySource("classpath:/testing.properties")
public class AuthTests {

  @Value("${super.email}")
  private String adminEmail;

  @Value("${super.password}")
  private String adminPassword;
  
  public static String getAuthMsgUsername(String username, String password) {
    byte[] bytes = Base64.getEncoder().encode((username + ":" +password).getBytes());
    return "Basic " + new String(bytes);
  }

  public static String getAuthMsg(String username) {
    return getAuthMsgUsername(username, "password");
  }

  public static String wrapString(String str) {
    return "\"" + str + "\"";
  }

  public static final String USER_NAME = "Adam Jones";
  public static final String USER_ADDRESS = "26 1st Street";
  public static final String USER_CITY = "Metropolis";
  public static final String USER_POSTALCODE = "1000";
  public static final String USER_COUNTRY = "USA";

  public static String createSignupJSONAll(String email, String password, 
    String name, String address, String city, String postalCode, String country
  ) {
    return "{"
      + wrapString("login") + ":" + wrapString(email) + ","
      + wrapString("password") + ":" + wrapString("password") + ","
      + wrapString("name") + ":" + wrapString(USER_NAME) + ","
      + wrapString("address") + ":" + wrapString(USER_ADDRESS) + ","
      + wrapString("city") + ":" + wrapString(USER_CITY) + ","
      + wrapString("postalCode") + ":" + wrapString(USER_POSTALCODE) + ","
      + wrapString("country") + ":" + wrapString(USER_COUNTRY)
      + "}";
  }

  public static String createSignupJSON(String username) {
    return createSignupJSONAll(username + "@test.org", "password", 
      USER_NAME, USER_ADDRESS, USER_CITY, USER_POSTALCODE, USER_COUNTRY);
  }

  @Test 
  void SignupAndDelete(@Autowired WebTestClient webClient) {
    String set_cookie = webClient
      .post().uri("/v1/auth/signup/customer")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createSignupJSON(""+Instant.now().toEpochMilli())) 
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
      .post().uri("/v1/auth/signup/customer")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createSignupJSON(""+Instant.now().toEpochMilli())) 
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
      .header("Authorization", getAuthMsg(username + "@test.org"))
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
  void cannotUseSameLogin(@Autowired WebTestClient webClient) {
    String username = "" + Instant.now().toEpochMilli();

    // signup
    String set_cookie = webClient
      .post().uri("/v1/auth/signup/customer")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createSignupJSON(username)) 
      .exchange()
      .expectStatus().isCreated()
      .returnResult(HttpHeaders.class)
      .getResponseHeaders().getFirst(HttpHeaders.SET_COOKIE);
    
    // try signing up with same email
    webClient
      .post().uri("/v1/auth/signup/customer")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createSignupJSON(username)) 
      .exchange()
      .expectStatus().isBadRequest();

    // clean up (delete user)
    webClient
      .delete().uri("/v1/auth/delete")
      .header("Cookie", set_cookie)
      .exchange()
      .expectStatus().isOk();
  }

  @Test
  void cannotUseBadEmail(@Autowired WebTestClient webClient) {
    // try signing up with bad email
    webClient
      .post().uri("/v1/auth/signup/customer")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createSignupJSONAll("bad#", "password", 
        USER_NAME, USER_ADDRESS, USER_CITY, USER_POSTALCODE, USER_COUNTRY)) 
      .exchange()
      .expectStatus().isBadRequest();
  }

  @Test
  void cannotUse7CharPassword(@Autowired WebTestClient webClient) {
    // try signing up with password under 8 chars
    webClient
      .post().uri("/v1/auth/signup/customer")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createSignupJSONAll("A@a.a", "passwor", 
        USER_NAME, USER_ADDRESS, USER_CITY, USER_POSTALCODE, USER_COUNTRY)) 
      .exchange()
      .expectStatus().isBadRequest();
  }


  @Test
  void updateUserLoginEmail(@Autowired WebTestClient webClient) {
    String username = "" + Instant.now().toEpochMilli();

    // signup
    String set_cookie = webClient
      .post().uri("/v1/auth/signup/customer")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createSignupJSON(""+Instant.now().toEpochMilli())) 
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
      .header("Authorization", getAuthMsg(username + "a" + "@test.org"))
      .exchange()
      .expectStatus().isOk()
      .returnResult(HttpHeaders.class)
      .getResponseHeaders().getFirst(HttpHeaders.SET_COOKIE);

    // try sign with old email expect unauthorized
    webClient
      .post().uri("/v1/auth/signin")
      .header("Authorization", getAuthMsg(username + "@test.org"))
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
    String username = "" + Instant.now().toEpochMilli();

    // signup
    String cookie1 = webClient
      .post().uri("/v1/auth/signup/customer")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createSignupJSON(username)) 
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
      .bodyValue("{ \"login\": \"" + username + "a" + "@test.org\", \"password\": \"" + newPassword + "\"}")
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
      .header("Authorization", getAuthMsgUsername(username + "a" + "@test.org", newPassword))
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

  @Test
  void updateWithBadDeatilsIsInvalid(@Autowired WebTestClient webClient) {
    String username = "" + Instant.now().toEpochMilli();

    // signup
    String cookie1 = webClient
      .post().uri("/v1/auth/signup/customer")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createSignupJSON(username)) 
      .exchange()
      .expectStatus().isCreated()
      .returnResult(HttpHeaders.class)
      .getResponseHeaders().getFirst(HttpHeaders.SET_COOKIE);

    // update email
    String newPassword = "passwo";
    webClient
      .put().uri("/v1/auth/user/update")
      .contentType(MediaType.APPLICATION_JSON)
      .header("Cookie", cookie1)
      .bodyValue("{ \"login\": \"" + username + "a" + "@test.org\", \"password\": \"" + newPassword + "\"}")
      .exchange()
      .expectStatus().isBadRequest();

    // clean up (delete user)
    webClient
      .delete().uri("/v1/auth/delete")
      .header("Cookie", cookie1)
      .exchange()
      .expectStatus().isOk();  
  }

  @Test
  void signoutAllValid(@Autowired WebTestClient webClient) {
    String username = "" + Instant.now().toEpochMilli();

    // signup
    String cookie1 = webClient
      .post().uri("/v1/auth/signup/customer")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(createSignupJSON(username)) 
      .exchange()
      .expectStatus().isCreated()
      .returnResult(HttpHeaders.class)
      .getResponseHeaders().getFirst(HttpHeaders.SET_COOKIE);

    // sign into account
    String cookie2 = webClient
      .post().uri("/v1/auth/signin")
      .header("Authorization", getAuthMsg(username + "@test.org"))
      .exchange()
      .expectStatus().isOk()
      .returnResult(HttpHeaders.class)
      .getResponseHeaders().getFirst(HttpHeaders.SET_COOKIE);

    //sign out all
    webClient
      .post().uri("/v1/auth/signout/all")
      .header("Cookie", cookie2)
      .exchange()
      .expectStatus().isOk();

    //check cookie 2 is invalid
    webClient
      .get().uri("/v1/hello/user")
      .header("Cookie", cookie2)
      .exchange()
      .expectStatus().isUnauthorized();
    
    //check cookie 1 is invalid
    webClient
      .get().uri("/v1/hello/user")
      .header("Cookie", cookie1)
      .exchange()
      .expectStatus().isUnauthorized();
      
    // clean up (delete user)
    String cookie3 = webClient
      .post().uri("/v1/auth/signin")
      .header("Authorization", getAuthMsg(username + "@test.org"))
      .exchange()
      .expectStatus().isOk()
      .returnResult(HttpHeaders.class)
      .getResponseHeaders().getFirst(HttpHeaders.SET_COOKIE);
    webClient
      .delete().uri("/v1/auth/delete")
      .header("Cookie", cookie3)
      .exchange()
      .expectStatus().isOk();   
  }

  @Test
  void adminSiginValid(@Autowired WebTestClient webClient) {
    String cookie = webClient
      .post().uri("/v1/auth/signin")
      .header("Authorization", getAuthMsgUsername(adminEmail, adminPassword))
      .exchange()
      .expectStatus().isOk()
      .returnResult(HttpHeaders.class)
      .getResponseHeaders().getFirst(HttpHeaders.SET_COOKIE);

    webClient
      .get().uri("/v1/hello/admin")
      .header("Cookie", cookie)
      .exchange()
      .expectStatus().isOk();

    webClient
      .post().uri("/v1/auth/signout")
      .header("Cookie", cookie)
      .exchange()
      .expectStatus().isOk();
  }
}
