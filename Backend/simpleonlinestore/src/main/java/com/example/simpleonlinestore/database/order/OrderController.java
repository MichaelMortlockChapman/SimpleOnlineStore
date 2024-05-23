package com.example.simpleonlinestore.database.order;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
public class OrderController {
  
  private final OrderRepository orderRepository;

  public OrderController(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  } 

  @GetMapping("/orders")
  public Iterable<Order> findAllOrders() {
      return this.orderRepository.findAll();
  }
  
  @PostMapping("/orders")
  public Order postOrder(@RequestBody Order order) {
      return this.orderRepository.save(order);
  }
}
