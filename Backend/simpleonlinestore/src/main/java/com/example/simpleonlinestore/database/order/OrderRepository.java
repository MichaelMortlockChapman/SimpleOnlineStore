package com.example.simpleonlinestore.database.order;

import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface OrderRepository extends CrudRepository<Order, UUID> {

  @Transactional
  @Modifying
  @Query("UPDATE Order SET status = ?2 WHERE id = ?1")
  void updateOrderStatus(UUID orderId, String status);

  @Transactional
  @Modifying
  @Query("UPDATE Order SET quantity = ?3, address = ?4, city = ?5, postalCode = ?6, country = ?7  WHERE id = ?1 AND customer.id = ?2")
  void updateOrder(
    UUID orderId, UUID customerId, Integer quantity, String address, 
    String city, Integer postalCode, String country
  );

  @Query("SELECT o FROM Order o WHERE o.id = ?1")
  Iterable<Order> getAllOrders(UUID customerId);

  @Query("SELECT o FROM Order o WHERE o.id = ?1 AND o.status <> '"+OrderStatuses.COMPLETE+"' AND o.status <> '"+OrderStatuses.CANCELED+"'")
  Iterable<Order> getAllActiveOrders(UUID customerId);
}
