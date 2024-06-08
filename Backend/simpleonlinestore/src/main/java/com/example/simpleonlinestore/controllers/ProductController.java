package com.example.simpleonlinestore.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.example.simpleonlinestore.database.product.Product;
import com.example.simpleonlinestore.database.product.ProductRepository;
import com.example.simpleonlinestore.security.UserRole;

import java.util.Iterator;
import java.util.Optional;

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

  private final ProductRepository productRepository;

  public ProductController(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

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

  public Optional<ResponseEntity<String>> checkProductExists(Integer productId) {
    Optional<ResponseEntity<String>> result = Optional.empty();
    if (!productRepository.findById(productId).isPresent()) {
      result = Optional.of(new ResponseEntity<>("Unknown ProductId", HttpStatus.BAD_REQUEST));
    }
    return result;
  }

  @GetMapping("/v1/product/get/{productId}")
  public ResponseEntity<String> findProduct(@PathVariable int productId) {
    Optional<ResponseEntity<String>> invalidatingReponse = checkProductExists(productId);
    if (invalidatingReponse.isPresent()) {
      return invalidatingReponse.get();
    }
    return ResponseEntity.ok(this.productRepository.findById(productId).get().toJSON());
  }

  public record OrderUpdateRequest(
    Integer id, String name, 
    String description, Integer units, 
    Long price
  ) {};

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

  @PostMapping("/v1/product/add")
  @Secured({UserRole.ROLE_ADMIN})
  public ResponseEntity<String> addProduct(@RequestBody ProductRequest productRequest) {
    Product product = new Product(productRequest.name(), productRequest.description(), productRequest.units(), productRequest.price());
    this.productRepository.save(product);
    return new ResponseEntity<String>("{ productId: \"" + product.getId() +"\"}", HttpStatus.CREATED);
  }

  @DeleteMapping("/v1/product/delete")
  @Secured({UserRole.ROLE_ADMIN})
  public ResponseEntity<String> deleteProduct(@RequestBody Integer productId) {
    Optional<ResponseEntity<String>> invalidatingReponse = checkProductExists(productId);
    if (invalidatingReponse.isPresent()) {
      return invalidatingReponse.get();
    }
    this.productRepository.deleteById(productId);
    return ResponseEntity.ok("Done");
  }
}
