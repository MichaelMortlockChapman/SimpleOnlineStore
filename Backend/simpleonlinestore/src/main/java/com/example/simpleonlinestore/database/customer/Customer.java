package com.example.simpleonlinestore.database.customer;

import java.util.UUID;

import com.example.simpleonlinestore.database.IJson;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer implements IJson {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name="customer_id")
  private UUID id;

  @Column(name="customer_name")
  private String name;

  @Column(name="customer_address")
  private String address;

  @Column(name="customer_city")
  private String city;

  @Column(name="customer_postal_code")
  private Integer postalCode;

  @Column(name="customer_country")
  private String country;

  // Hibernate expects entities to have a no-arg constructor,
  @SuppressWarnings("unused")
  private Customer () {}

  public Customer(String name, String address,
      String city, Integer postalCode, String country) {
    this.name = name;
    this.address = address;
    this.city = city;
    this.postalCode = postalCode;
    this.country = country;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
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

  @Override
  public String toJSON() {
    return "{"
      + "\"name\"" + ":" + "\"" + getName() + "\"" + ","
      + "\"address\"" + ":" + "\"" + getAddress() + "\"" + ","
      + "\"city\"" + ":" + "\"" + getCity() + "\"" + ","
      + "\"postalCode\"" + ":" + "\"" + getPostalCode() + "\"" + ","
      + "\"country\"" + ":" + "\"" + getCountry() + "\""
      + "}";
  }
}
