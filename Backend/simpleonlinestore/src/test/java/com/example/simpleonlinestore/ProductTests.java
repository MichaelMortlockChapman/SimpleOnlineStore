package com.example.simpleonlinestore;

import static com.example.simpleonlinestore.AuthTests.wrapString;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.simpleonlinestore.controllers.ProductController;
import com.example.simpleonlinestore.database.product.Product;
import com.example.simpleonlinestore.security.SecurityConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(SecurityConfig.class)
public class ProductTests {

  @Autowired
  private ProductController productController;

  public static String createProductJSON() {
    return "{"
      + wrapString("productId") + ":" + wrapString("1") + ","
      + wrapString("name") + ":" + wrapString("PS3") + ","
      + wrapString("description") + ":" + wrapString("Video Game Console") + ","
      + wrapString("units") + ":" + wrapString("5") + ","
      + wrapString("price") + ":" + wrapString("100000")
      + "}";
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void addProductAndGetProduct(@Autowired WebTestClient webClient) {
    ResponseEntity<String> reponse = productController.addProduct(new Product("PS3", "Video Game Console", 5, (long)1000000));
    assertTrue(reponse.getStatusCode().equals(HttpStatusCode.valueOf(201)));
    assertTrue(reponse.getBody() != null);
    @SuppressWarnings("null")
    int productId = Integer.parseInt(reponse.getBody().replace("{ productId: \"", "").replace("\"}", ""));

    webClient
      .get().uri("/v1/product/get/" + productId)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .consumeWith(body -> {
        String bodyString = new String(body.getResponseBodyContent());
        assertTrue(bodyString.matches("(?i).*\"name\":\"" + "PS3" + "\"(.*)"));
        assertTrue(bodyString.matches("(?i).*\"productId\":\"" + productId + "\"(.*)"));
      });

    productController.deleteProduct(productId);

    webClient
      .get().uri("/v1/product/get/" + productId)
      .exchange()
      .expectStatus().isBadRequest();
  }
  
  @Test
  @WithMockUser(roles = "ADMIN")
  void addProductAndGetAll(@Autowired WebTestClient webClient) {
    ResponseEntity<String> reponse = productController.addProduct(new Product("PS3", "Video Game Console", 5, (long)1000000));
    assertTrue(reponse.getStatusCode().equals(HttpStatusCode.valueOf(201)));
    assertTrue(reponse.getBody() != null);
    @SuppressWarnings("null")
    int productId = Integer.parseInt(reponse.getBody().replace("{ productId: \"", "").replace("\"}", ""));

    webClient
      .get().uri("/v1/product/get/all")
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .consumeWith(body -> {
        String bodyString = new String(body.getResponseBodyContent());
        assertTrue(bodyString.matches("(?i).*\"name\":\"" + "PS3" + "\"(.*)"));
        assertTrue(bodyString.matches("(?i).*\"productId\":\"" + productId + "\"(.*)"));
      });

    productController.deleteProduct(productId);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void errorOnBadProductId(@Autowired WebTestClient webClient) {
    ResponseEntity<String> reponse = productController.deleteProduct(-1);
    assertTrue(reponse.getStatusCode().equals(HttpStatusCode.valueOf(400)));
  }

  @Test
  @WithMockUser(roles = "USER")
  void errorIfUserUsesAddProduct(@Autowired WebTestClient webClient) {
    try {
      productController.addProduct(new Product("PS3", "Video Game Console", 5, (long)1000000));
      assertTrue(false);
    } catch (Exception e) { // cannot check e is instanceOf AccessDeniedException as addProduct doesn't directly throw it
      assertTrue(e.getMessage().equals("Access Denied"));
    }
  }

  @Test
  @WithMockUser(roles = "USER")
  void errorIfUserUsesDeleteProduct(@Autowired WebTestClient webClient) {
    try {
      productController.deleteProduct(1);
      assertTrue(false);
    } catch (Exception e) { // cannot check e is instanceOf AccessDeniedException as addProduct doesn't directly throw it
      assertTrue(e.getMessage().equals("Access Denied"));
    }
  }
}
