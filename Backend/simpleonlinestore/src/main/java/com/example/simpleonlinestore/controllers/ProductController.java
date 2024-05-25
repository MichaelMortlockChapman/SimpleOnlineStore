package com.example.simpleonlinestore.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.example.simpleonlinestore.database.product.Product;
import com.example.simpleonlinestore.database.product.ProductRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class ProductController {

  private final ProductRepository productRepository;

  public ProductController(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @GetMapping("/v1/products")
  public Iterable<Product> findAllProducts() {
      return this.productRepository.findAll();
  }

  @PostMapping("/v1/products")
  public Product postProduct(@RequestBody Product product) {
      return this.productRepository.save(product);
  }
  
}
