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

  // don't remove order when customer is deleted but set to null as ref would point to non-existing customer
  @Transactional
  @Modifying
  @Query("UPDATE Order SET customer = null WHERE customer.id = ?1")
  void removeDeletedCustomer(UUID customerId);

  @Transactional
  @Modifying
  @Query("UPDATE Order SET product = null WHERE product.id = ?1")
  void removeDeletedProduct(Integer productId);

  @Transactional
  @Modifying
  @Query("UPDATE Order SET quantity = ?3, address = ?4, city = ?5, postalCode = ?6, country = ?7  WHERE id = ?1 AND customer.id = ?2")
  void updateOrder(
    UUID orderId, UUID customerId, Integer quantity, String address, 
    String city, Integer postalCode, String country
  );

  @Query("SELECT o FROM Order o WHERE o.customer.id = ?1")
  Iterable<Order> getAllOrders(UUID customerId);

  @Query("SELECT o FROM Order o WHERE o.customer.id = ?1 AND (o.status != 'COMPLETE' AND o.status != 'CANCELED')")
  Iterable<Order> getAllActiveOrders(UUID customerId);
}
