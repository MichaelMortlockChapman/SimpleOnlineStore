package com.example.simpleonlinestore.database.order;

import java.util.UUID;

import com.example.simpleonlinestore.database.IJson;
import com.example.simpleonlinestore.database.customer.Customer;
import com.example.simpleonlinestore.database.product.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order implements IJson {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name="order_id")
  private UUID id;

  @ManyToOne
  @JoinColumn(name="product_id")
  private Product product;

  @ManyToOne
  @JoinColumn(name="customer_id")
  private Customer customer;

  @Column(name="quantity")
  private Integer quantity;

  @Column(name="delivery_address")
  private String address;

  @Column(name="delivery_city")
  private String city;

  @Column(name="delivery_postal_code")
  private Integer postalCode;

  @Column(name="delivery_country")
  private String country;

  @Column(name="order_status")
  private String status;

  // Hibernate expects entities to have a no-arg constructor,
  @SuppressWarnings("unused")
  private Order () {}

  public Order(Product product, Customer customer, Integer quantity, String address, String city,
      Integer postalCode, String country, String status) {
    this.product = product;
    this.customer = customer;
    this.quantity = quantity;
    this.address = address;
    this.city = city;
    this.postalCode = postalCode;
    this.country = country;
    this.status = status;
  }

  public UUID getId() {
    return id;
  }

  public Product getProduct() {
    return product;
  }

  public Customer getCustomer() {
    return customer;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public String getAddress() {
    return address;
  }

  public String getCity() {
    return city;
  }

  public Integer getPostalCode() {
    return postalCode;
  }

  public String getCountry() {
    return country;
  }

  public String getStatus() {
    return status;
  }

  @Override
  public String toJSON() {
    return "{"
      + "\"orderId\"" + ":" + "\"" + getId() + "\"" + ","
      + "\"productId\"" + ":" + "\"" + product.getId() + "\"" + ","
      + "\"customerId\"" + ":" + "\"" + customer.getId() + "\"" + ","
      + "\"quantity\"" + ":" + "\"" + getQuantity() + "\"" + ","
      + "\"address\"" + ":" + "\"" + getAddress() + "\"" + ","
      + "\"city\"" + ":" + "\"" + getCity() + "\"" + ","
      + "\"postalCode\"" + ":" + "\"" + getPostalCode() + "\"" + ","
      + "\"country\"" + ":" + "\"" + getCountry() + "\"" + ","
      + "\"status\"" + ":" + "\"" + getStatus() + "\""
      + "}";
  }
}
