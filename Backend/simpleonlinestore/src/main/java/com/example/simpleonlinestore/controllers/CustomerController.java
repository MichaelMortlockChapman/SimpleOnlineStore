package com.example.simpleonlinestore.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.example.simpleonlinestore.database.customer.Customer;
import com.example.simpleonlinestore.database.customer.CustomerRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class CustomerController {

  private final CustomerRepository customerRepository;

  public CustomerController(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  @GetMapping("/customers")
  public Iterable<Customer> findAllCustomers() {
      return this.customerRepository.findAll();
  }

  @PostMapping("/customers")
  public Customer postCustomer(@RequestBody Customer customer) {
      return this.customerRepository.save(customer);
  }
}
