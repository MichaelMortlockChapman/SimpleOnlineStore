package com.example.simpleonlinestore.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.example.simpleonlinestore.database.order.OrderRepository;
import com.example.simpleonlinestore.database.product.Product;
import com.example.simpleonlinestore.database.product.ProductRepository;
import com.example.simpleonlinestore.security.UserRole;

import java.util.Iterator;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class ProductController {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private OrderRepository orderRepository;

  /**
   * Gets JSON list obj of all products
   * @return JSON list obj string
   */
  @GetMapping("/v1/product/get/all")
  public ResponseEntity<String> findAllProducts() {
    String productListJSON = "[";
    Iterator<Product> productList = this.productRepository.findAll().iterator();
    while (productList.hasNext()) {
      Product product = productList.next();
      productListJSON += product.toJSON();
      if (productList.hasNext()) {
        productListJSON += ",";
      }
    }
    productListJSON += "]";

    return ResponseEntity.ok(productListJSON);
  }

  /**
   * Helper func to validate productId
   * @param productId
   * @return Optional ResponseEntity<String>, empty if valid otherwise holds problem details
   */
  public Optional<ResponseEntity<String>> checkProductExists(Integer productId) {
    Optional<ResponseEntity<String>> result = Optional.empty();
    if (!productRepository.findById(productId).isPresent()) {
      result = Optional.of(new ResponseEntity<>("Unknown ProductId", HttpStatus.BAD_REQUEST));
    }
    return result;
  }

  /**
   * Gets JSON obj of given productId
   * @param productId
   * @return JSON obj of product or problem
   */
  @GetMapping("/v1/product/get/{productId}")
  public ResponseEntity<String> findProduct(@PathVariable int productId) {
    Optional<ResponseEntity<String>> invalidatingReponse = checkProductExists(productId);
    if (invalidatingReponse.isPresent()) {
      return invalidatingReponse.get();
    }
    return ResponseEntity.ok(productRepository.findById(productId).get().toJSON());
  }

  public record OrderUpdateRequest(
    Integer id, String name, 
    String description, Integer units, 
    Long price
  ) {};
  /**
   * Updates a product's details
   * @param product updated details record of product
   * @return Done msg
   */
  @PutMapping("/v1/product/update")
  @Secured({UserRole.ROLE_ADMIN})
  public ResponseEntity<String> updateProduct(@RequestBody OrderUpdateRequest product) {
      productRepository.updateProductDetails(
        product.id(), product.name(), 
        product.description(), product.units(), product.price()
      );

      return ResponseEntity.ok("Done");
  }
  
  public record ProductRequest(String name, String description, Integer units, Long price) {}
  /**
   * creates and saves a new product
   * @param productRequest details of product to create
   * @return productId of created product
   */
  @PostMapping("/v1/product/add")
  @Secured({UserRole.ROLE_ADMIN})
  public ResponseEntity<String> addProduct(@RequestBody ProductRequest productRequest) {
    Product product = new Product(productRequest.name(), productRequest.description(), productRequest.units(), productRequest.price());
    productRepository.save(product);
    return new ResponseEntity<String>("{ productId: \"" + product.getId() +"\"}", HttpStatus.CREATED);
  }

  /**
   * Deletes a product from DB and nulls orders which refrenced it
   * @param productId
   * @return Done or error msg
   */
  @DeleteMapping("/v1/product/delete")
  @Secured({UserRole.ROLE_ADMIN})
  public ResponseEntity<String> deleteProduct(@RequestBody Integer productId) {
    Optional<ResponseEntity<String>> invalidatingReponse = checkProductExists(productId);
    if (invalidatingReponse.isPresent()) {
      return invalidatingReponse.get();
    }
    orderRepository.removeDeletedProduct(productId);
    productRepository.deleteById(productId);
    return ResponseEntity.ok("Done");
  }
}
