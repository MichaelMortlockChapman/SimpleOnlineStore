package com.example.simpleonlinestore.database.product;

import com.example.simpleonlinestore.database.IJson;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product implements IJson {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name="product_id")
  private Integer id;

  @Column(name="product_name")
  private String name;

  @Column(name="product_description")
  private String description;

  @Column(name="units")
  private Integer units;

  @Column(name="price")
  private Long price;

  // Hibernate expects entities to have a no-arg constructor,
  @SuppressWarnings("unused")
  private Product () {}

  public Product(String productName, String productDescription, Integer units, Long price) {
    this.name = productName;
    this.description = productDescription;
    this.units = units;
    this.price = price;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Integer getUnits() {
    return units;
  }

  public Long getPrice() {
    return price;
  }

  @Override
  public String toJSON() {
    return "{"
      + "\"productId\"" + ":" + "\"" + getId() + "\"" + ","
      + "\"name\"" + ":" + "\"" + getName() + "\"" + ","
      + "\"description\"" + ":" + "\"" + getDescription() + "\"" + ","
      + "\"units\"" + ":" + "\"" + getUnits() + "\"" + ","
      + "\"price\"" + ":" + "\"" + getPrice() + "\""
      + "}";
  }
}
