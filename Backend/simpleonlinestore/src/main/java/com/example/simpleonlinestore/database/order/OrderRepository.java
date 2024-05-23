package com.example.simpleonlinestore.database.order;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, UUID> {}
