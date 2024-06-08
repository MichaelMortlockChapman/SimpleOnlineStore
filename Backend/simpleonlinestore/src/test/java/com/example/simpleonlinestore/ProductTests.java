package com.example.simpleonlinestore;

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
import com.example.simpleonlinestore.controllers.ProductController.OrderUpdateRequest;
import com.example.simpleonlinestore.database.product.Product;
import com.example.simpleonlinestore.security.SecurityConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(SecurityConfig.class)
public class ProductTests {

  @Autowired
  private ProductController productController;

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
  @SuppressWarnings("null")
  @WithMockUser(roles = "ADMIN")
  void addProductAndGetAll(@Autowired WebTestClient webClient) {
    // create products, get their IDs
    ResponseEntity<String> reponse = productController.addProduct(new Product("PS3", "Video Game Console", 5, (long)1000000));
    ResponseEntity<String> reponse2 = productController.addProduct(new Product("PS4", "Video Game Console", 5, (long)5000000));
    assertTrue(reponse2.getBody() != null && reponse.getBody() != null);
    Integer ps3ProductId = Integer.parseInt(reponse.getBody().replace("{ productId: \"", "").replace("\"}", ""));
    Integer ps4ProductId = Integer.parseInt(reponse2.getBody().replace("{ productId: \"", "").replace("\"}", ""));

    // test get all
    webClient
      .get().uri("/v1/product/get/all")
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .consumeWith(body -> {
        String bodyString = new String(body.getResponseBodyContent());
        assertTrue(bodyString.matches("(?i).*\"name\":\"" + "PS3" + "\"(.*)"));
        assertTrue(bodyString.matches("(?i).*\"productId\":\"" + ps3ProductId + "\"(.*)"));
        assertTrue(bodyString.matches("(?i).*\"name\":\"" + "PS4" + "\"(.*)"));
        assertTrue(bodyString.matches("(?i).*\"productId\":\"" + ps4ProductId + "\"(.*)"));
      });

    productController.deleteProduct(ps3ProductId);
    productController.deleteProduct(ps4ProductId);
  }

  @Test
  @SuppressWarnings("null")
  @WithMockUser(roles = "ADMIN")
  void updateProductWithValidDataWorks(@Autowired WebTestClient webClient) {
    // create product
    ResponseEntity<String> reponse = productController.addProduct(new Product("PS3", "Video Game Console", 5, (long)1000000));
    assertTrue(reponse.getBody() != null);
    Integer productId = Integer.parseInt(reponse.getBody().replace("{ productId: \"", "").replace("\"}", ""));
    // update
    productController.updateProduct(new OrderUpdateRequest(productId, "PS5", "Modern Video Game Console", 1, (long)2000000));
    // check update worked
    webClient
      .get().uri("/v1/product/get/" + productId)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .consumeWith(body -> {
        String bodyString = new String(body.getResponseBodyContent());
        assertTrue(bodyString.matches("(?i).*\"name\":\"" + "PS5" + "\"(.*)"));
        assertTrue(bodyString.matches("(?i).*\"description\":\"" + "Modern Video Game Console" + "\"(.*)"));
        assertTrue(bodyString.matches("(?i).*\"units\":\"" + "1" + "\"(.*)"));
        assertTrue(bodyString.matches("(?i).*\"price\":\"" + "2000000" + "\"(.*)"));
        assertTrue(bodyString.matches("(?i).*\"productId\":\"" + productId + "\"(.*)"));
      });
    // clean up
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
