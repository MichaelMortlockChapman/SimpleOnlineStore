package com.example.simpleonlinestore.database.product;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, UUID> {}