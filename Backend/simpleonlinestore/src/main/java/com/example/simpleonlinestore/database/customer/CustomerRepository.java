package com.example.simpleonlinestore.database.customer;

import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import jakarta.transaction.Transactional;

public interface CustomerRepository extends CrudRepository<Customer, UUID> {
  
  @Transactional
  @Modifying
  @Query("UPDATE Customer SET name = ?2, address = ?3, city = ?4, postalCode = ?5, country = ?6 WHERE id = ?1")
  void updateCustomerDetails(
    UUID id,
    String name, String address, 
    String city, Integer postalCode, 
    String country
  );
}