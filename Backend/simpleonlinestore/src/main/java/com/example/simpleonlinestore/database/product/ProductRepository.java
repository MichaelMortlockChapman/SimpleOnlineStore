package com.example.simpleonlinestore.database.product;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import jakarta.transaction.Transactional;

public interface ProductRepository extends CrudRepository<Product, Integer> {

  @Transactional
  @Modifying
  @Query("UPDATE Product SET name = ?2, description = ?3, units = ?4, price = ?5 WHERE id = ?1")
  void updateProductDetails(
    Integer id, String name, String description, Integer units, Long price
  );
}