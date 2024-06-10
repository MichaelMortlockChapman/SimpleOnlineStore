package com.example.simpleonlinestore;

import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

import com.example.simpleonlinestore.database.order.OrderStatuses;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.example.simpleonlinestore.AuthTests.createSignupJSONWithEmail;
import static com.example.simpleonlinestore.AuthTests.getAuthMsgUsername;
import static com.example.simpleonlinestore.AuthTests.wrapString;
import static com.example.simpleonlinestore.ProductTests.createProductJSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@PropertySource("classpath:/testing.properties")
public class OrderTests {

  ///////////////////////////////////////////////
  // SETUP & HELPERS
  ///////////////////////////////////////////////

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
      .bodyValue(createSignupJSONWithEmail(email)) 
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

  // 'JSON' OBJECTS

  public String createAdminOrderRequestAll(
    String login, String productId, String quantity, 
    String address, String city, String postalCode, String country 
  ) {
    return "{"
      + "\"login\":" + wrapString(login) + ","
      + "\"productId\":" + wrapString(productId) + ","
      + "\"quantity\":" + wrapString(quantity) + ","
      + "\"address\":" + wrapString(address) + ","
      + "\"city\":" + wrapString(city) + ","
      + "\"postalCode\":" + wrapString(postalCode) + ","
      + "\"country\":" + wrapString(country)
      + "}";
  }

  public String createAdminOrderRequest() {
    return createAdminOrderRequestAll(
      email, productId.toString(), "5", 
      "27 2nd Ave.", "Gotham", "1000", "USA"
    );
  }

  public String createOrderRequestAll(
    String productId, String quantity, String address, 
    String city, String postalCode, String country 
  ) {
    return "{"
      + "\"productId\":" + wrapString(productId) + ","
      + "\"quantity\":" + wrapString(quantity) + ","
      + "\"address\":" + wrapString(address) + ","
      + "\"city\":" + wrapString(city) + ","
      + "\"postalCode\":" + wrapString(postalCode) + ","
      + "\"country\":" + wrapString(country)
      + "}";
  }

  public String createOrderRequest() {
    return createOrderRequestAll(
      productId.toString(), "5", "27 2nd Ave.", 
      "Gotham", "1000", "USA"
    );
  }

  public String createCustomerOrderUpdateRequest(String orderId) {
    return "{"
      + "\"orderId\":" + wrapString(orderId) + ","
      + "\"quantity\":" + wrapString("10") + ","
      + "\"address\":" + wrapString("3 3rd Street") + ","
      + "\"city\":" + wrapString("London") + ","
      + "\"postalCode\":" + wrapString("2500") + ","
      + "\"country\":" + wrapString("UK")
      + "}";
  }

  public String createAdminOrderUpdateRequest(String orderId, String login) {
    return "{"
      + "\"orderId\":" + wrapString(orderId) + ","
      + "\"customerLogin\":" + wrapString(login) + ","
      + "\"quantity\":" + wrapString("10") + ","
      + "\"address\":" + wrapString("3 3rd Street") + ","
      + "\"city\":" + wrapString("London") + ","
      + "\"postalCode\":" + wrapString("2500") + ","
      + "\"country\":" + wrapString("UK")
      + "}";
  }

  public String createAdminOrderUpdateRequest(String orderId) {
    return createAdminOrderUpdateRequest(orderId, email);
  }

  // ACTIONS

  private String getOrderIdFromResponse(byte[] body) {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root;
    String orderId = null;
    try {
      root = mapper.readTree(new String(body));
      orderId = root.get("orderId").asText();
    } catch (Exception e) {
      assertTrue(false);
    }
    return orderId;
  }

  public ResponseSpec doAction(WebTestClient webClient, HttpMethod httpMethod, String uri, String reqCookie, String bodyVal) {
    return webClient
      .method(httpMethod)
      .uri(uri)
      .header("Cookie", reqCookie)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(bodyVal)
      .exchange();
  }

  public ResponseSpec doAction(WebTestClient webClient, HttpMethod httpMethod, String uri, String reqCookie) {
    return webClient
      .method(httpMethod)
      .uri(uri)
      .header("Cookie", reqCookie)
      .exchange();
  }

  private JsonNode getRoot(String bodyStr) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readTree(new String(bodyStr));
    } catch (Exception e) {
      assertTrue(false);
    }
    assertTrue(false);
    return null;
  }

  void updateOrderStatus(WebTestClient webClient, String orderId, String status) {
    doAction(webClient, HttpMethod.PUT, "/v1/order/admin/update/status", adminCookie, "{\"orderId\":"+wrapString(orderId)+",\"orderStatus\":"+wrapString(status)+"}");
  }

  void checkOrderExpected(WebTestClient webClient, String orderId, String status, String address, String quantity) {
    doAction(webClient, HttpMethod.GET, "/v1/order/get/" + orderId, cookie)
      .expectStatus().isOk()
      .expectBody()
      .consumeWith(body -> {
        String bodyString = new String(body.getResponseBodyContent());
        assertTrue(bodyString.matches("(?i).*\"status\":\"" + status + "\"(.*)"));
        assertTrue(bodyString.matches("(?i).*\"address\":\"" + address + "\"(.*)"));
        assertTrue(bodyString.matches("(?i).*\"quantity\":\"" + quantity + "\"(.*)"));
      });
  }

  String createSimpleOrder(WebTestClient webClient) {
    byte[] bodyRaw = doAction(webClient, HttpMethod.POST, "/v1/order/create/simple", cookie, "{ \"productId\":"+wrapString(productId.toString())+", \"quantity\":7}")
      .expectStatus().isCreated()
      .returnResult(HttpHeaders.class)
      .getResponseBodyContent();
    return getOrderIdFromResponse(bodyRaw);
  }

  ///////////////////////////////////////////////
  // TESTS
  ///////////////////////////////////////////////

  @Test
  void canCreateSimpleAndGet(@Autowired WebTestClient webClient) {
    byte[] bodyRaw = doAction(webClient, HttpMethod.POST, "/v1/order/create/simple", cookie, "{ \"productId\":"+wrapString(productId.toString())+", \"quantity\":7}")
      .expectStatus().isCreated()
      .returnResult(HttpHeaders.class)
      .getResponseBodyContent();
    String orderId = getOrderIdFromResponse(bodyRaw);

    checkOrderExpected(webClient, orderId, OrderStatuses.ORDERED, AuthTests.USER_ADDRESS, "7");
  }

  @Test
  void createSimpleBadRequest(@Autowired WebTestClient webClient) {
    doAction(webClient, HttpMethod.POST, "/v1/order/create/simple", cookie, "{ \"productId\":"+wrapString(productId.toString())+", \"quantity\":\"hi\"}")
      .expectStatus().isBadRequest();
  }

  @Test
  void canCreate(@Autowired WebTestClient webClient) {
    byte[] bodyRaw =  doAction(webClient, HttpMethod.POST, "/v1/order/create", cookie, createOrderRequest())
      .expectStatus().isCreated()
      .returnResult(HttpHeaders.class)
      .getResponseBodyContent();
    String orderId = getOrderIdFromResponse(bodyRaw);

    checkOrderExpected(webClient, orderId, OrderStatuses.ORDERED, "27 2nd Ave.", "5");
  }

  @Test
  void canAdminCreate(@Autowired WebTestClient webClient) {
    byte[] bodyRaw = doAction(webClient, HttpMethod.POST, "/v1/order/admin/create", adminCookie, createAdminOrderRequest())
      .expectStatus().isCreated()
      .returnResult(HttpHeaders.class)
      .getResponseBodyContent();
    String orderId = getOrderIdFromResponse(bodyRaw);

    checkOrderExpected(webClient, orderId, OrderStatuses.ORDERED, "27 2nd Ave.", "5");
  }

  @Test
  void createSimpleBadOrderIdIsInvalid(@Autowired WebTestClient webClient) {
    String badOrderRequest = "{ \"productId\":"+wrapString("1")+", \"quantity\":7}";
    doAction(webClient, HttpMethod.POST, "/v1/order/create/simple", cookie, badOrderRequest)
      .expectStatus().isBadRequest();
  }

  @Test
  void createBadOrderIdIsInvalid(@Autowired WebTestClient webClient) {
    String badOrderRequest = createOrderRequestAll(
      "1", "5", "27 2nd Ave.", "Gotham", "1000", "USA"
    );
    doAction(webClient, HttpMethod.POST, "/v1/order/create", cookie, badOrderRequest)
      .expectStatus().isBadRequest();
  }

  @Test
  void adminCreateBadOrderIdIsInvalid(@Autowired WebTestClient webClient) {
    String badOrderRequest = createAdminOrderRequestAll(
      email, "1", "5", "27 2nd Ave.", "Gotham", "1000", "USA"
    );
    doAction(webClient, HttpMethod.POST, "/v1/order/admin/create", adminCookie, badOrderRequest)
      .expectStatus().isBadRequest();
  }

  @Test
  void adminCreateBadEmailIsInvalid(@Autowired WebTestClient webClient) {
    String badOrderRequest = createAdminOrderRequestAll(
      "2@a.com", "1", "5", "27 2nd Ave.", "Gotham", "1000", "USA"
    );
    doAction(webClient, HttpMethod.POST, "/v1/order/admin/create", adminCookie, badOrderRequest)
      .expectStatus().isBadRequest();
  }

  @Test
  void canChangeStatus(@Autowired WebTestClient webClient) {
    String orderId = createSimpleOrder(webClient);
    
    doAction(webClient, HttpMethod.PUT, "/v1/order/admin/update/status", adminCookie, 
      "{\"orderId\":"+wrapString(orderId)+",\"orderStatus\":"+wrapString(OrderStatuses.statuses.COMPLETE.name())+"}")
      .expectStatus().isOk();

    checkOrderExpected(webClient, orderId, OrderStatuses.COMPLETE, AuthTests.USER_ADDRESS, "7");
  }

  @Test
  void canCustomerUpdate(@Autowired WebTestClient webClient) {
    String orderId = createSimpleOrder(webClient);
    
    doAction(webClient, HttpMethod.PUT, "/v1/order/update", cookie, createCustomerOrderUpdateRequest(orderId.toString()))
      .expectStatus().isOk();

    checkOrderExpected(webClient, orderId, OrderStatuses.ORDERED, "3 3rd Street", "10");
  }

  @Test
  void customerUpdateUnderwayOrderIsInvalid(@Autowired WebTestClient webClient) {
    String orderId = createSimpleOrder(webClient);

    updateOrderStatus(webClient, orderId, OrderStatuses.statuses.SHIPPED.name());

    doAction(webClient, HttpMethod.PUT, "/v1/order/update", cookie, createCustomerOrderUpdateRequest(orderId.toString()))
      .expectStatus().isBadRequest();
  }

  @Test
  void canAdminUpdate(@Autowired WebTestClient webClient) {
    String orderId = createSimpleOrder(webClient);

    doAction(webClient, HttpMethod.PUT, "/v1/order/admin/update", adminCookie, createAdminOrderUpdateRequest(orderId.toString()))
      .expectStatus().isOk();

    checkOrderExpected(webClient, orderId, OrderStatuses.ORDERED, "3 3rd Street", "10");
  }

  @Test
  void canAdminUpdateUnderwayOrder(@Autowired WebTestClient webClient) {
    String orderId = createSimpleOrder(webClient);

    updateOrderStatus(webClient, orderId, OrderStatuses.statuses.SHIPPED.name());

    doAction(webClient, HttpMethod.PUT, "/v1/order/admin/update", adminCookie, createAdminOrderUpdateRequest(orderId.toString()))
      .expectStatus().isOk();

    checkOrderExpected(webClient, orderId, OrderStatuses.SHIPPED, "3 3rd Street", "10");
  }

  @Test
  void canGetActive(@Autowired WebTestClient webClient) {
    String[] orderIds = new String[3];

    orderIds[0] = createSimpleOrder(webClient);
    orderIds[1] = createSimpleOrder(webClient);
    orderIds[2] = createSimpleOrder(webClient);

    updateOrderStatus(webClient, orderIds[0], OrderStatuses.statuses.CANCELED.name());
    updateOrderStatus(webClient, orderIds[2], OrderStatuses.statuses.COMPLETE.name());

    
    byte[] ordersBodyRaw = doAction(webClient, HttpMethod.GET, "/v1/order/get/active", cookie)
      .expectStatus().isOk()
      .returnResult(HttpHeaders.class)
      .getResponseBodyContent();

    JsonNode root = getRoot(new String(ordersBodyRaw));
    Iterator<JsonNode> iter = root.elements();
    Integer count = 0;
    while (iter.hasNext()) {
      JsonNode node = iter.next();
      node.get("orderId").asText().equals(orderIds[1]);
      count++;
    }
    assertEquals(count, 1);
  }

  @Test
  void canGetAll(@Autowired WebTestClient webClient) {
    String[] orderIds = new String[2];

    orderIds[0] = createSimpleOrder(webClient);
    orderIds[1] = createSimpleOrder(webClient);

    updateOrderStatus(webClient, orderIds[0], OrderStatuses.statuses.COMPLETE.name());

    byte[] ordersBodyRaw = webClient
      .get().uri("/v1/order/get/all")
      .header("Cookie", cookie)
      .exchange()
      .expectStatus().isOk()
      .returnResult(HttpHeaders.class)
      .getResponseBodyContent();

    JsonNode root = getRoot(new String(ordersBodyRaw));
    Iterator<JsonNode> iter = root.elements();
    Integer count = 0;
    String[] statuses = {OrderStatuses.statuses.COMPLETE.name(), OrderStatuses.statuses.ORDERED.name()};
    while (iter.hasNext()) {
      JsonNode node = iter.next();
      node.get("orderId").asText().equals(orderIds[count]);
      node.get("status").asText().equals(statuses[count]);
      count++;
    }
    assertEquals(count, 2);
  }

  // technically flaky tests but chances of uuid existing already astronomically low 
  //    could mock repo to make sure it doesn't exist but since this is 'simple' project not bothering 
  @Test
  void getOrderWithBadOrderIdIsInvalid(@Autowired WebTestClient webClient) {
    webClient = webClient.mutate().responseTimeout(Duration.ofSeconds(1000000)).build();
    doAction(webClient, HttpMethod.GET, "/v1/order/get/" + "8340b3ef-195f-4a64-94e0-6352207e45b3", cookie)
      .expectStatus().isBadRequest();
  }

  @Test
  void adminUpdateWithBadOrderIdIsInvalid(@Autowired WebTestClient webClient) {
    doAction(webClient, HttpMethod.PUT, "/v1/order/admin/update", adminCookie, createAdminOrderUpdateRequest("8340b3ef-195f-4a64-94e0-6352207e45b3")) 
      .expectStatus().isBadRequest();
  }

  @Test
  void adminUpdateWithBadLoginIsInvalid(@Autowired WebTestClient webClient) {
    String orderId = createSimpleOrder(webClient);

    doAction(webClient, HttpMethod.PUT, "/v1/order/admin/update", adminCookie, createAdminOrderUpdateRequest(orderId.toString(), "8340b3ef-195f-4a64-94e0-6352207e45b3"))
      .expectStatus().isBadRequest();
  }

  @Test
  void customerUpdateBadOrderIdIsInvalid(@Autowired WebTestClient webClient) {
    doAction(webClient, HttpMethod.PUT, "/v1/order/update", cookie, createCustomerOrderUpdateRequest("8340b3ef-195f-4a64-94e0-6352207e45b3"))
      .expectStatus().isBadRequest();
  }
}