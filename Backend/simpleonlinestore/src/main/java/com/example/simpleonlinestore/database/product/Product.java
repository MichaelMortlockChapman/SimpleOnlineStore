package com.example.simpleonlinestore.database.product;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID product_id;

  private String product_name;

  private String product_description;

  private Integer units;

  private Long price;

  // Hibernate expects entities to have a no-arg constructor,
  @SuppressWarnings("unused")
  private Product () {}

  public Product(UUID productID, String productName, String productDescription, Integer units, Long price) {
    this.product_id = productID;
    this.product_name = productName;
    this.product_description = productDescription;
    this.units = units;
    this.price = price;
  }

  public UUID getProduct_id() {
    return product_id;
  }

  public String getProduct_name() {
    return product_name;
  }

  public String getProduct_description() {
    return product_description;
  }

  public Integer getUnits() {
    return units;
  }

  public Long getPrice() {
    return price;
  }
}
