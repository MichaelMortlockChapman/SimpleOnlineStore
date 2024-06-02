package com.example.simpleonlinestore.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.simpleonlinestore.database.customer.Customer;
import com.example.simpleonlinestore.database.customer.CustomerRepository;
import com.example.simpleonlinestore.database.users.UserRepository;
import com.example.simpleonlinestore.security.UserRole;
import com.example.simpleonlinestore.security.filters.cookies.CookieGenerator;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class CustomerController {

  @Autowired
  private final CustomerRepository customerRepository;

  @Autowired
  private UserRepository userRepository;

  public CustomerController(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  @GetMapping("/v1/customer/details")
  @Secured({UserRole.ROLE_USER})
  public ResponseEntity<String> GetUserCustomerDetails(
    @CookieValue(CookieGenerator.COOKIE_LOGIN) String loginCookieValue
  ) throws ResponseStatusException {
    Optional<Customer> customer = customerRepository.findById(userRepository.findInfoIdFromLogin(loginCookieValue));
    return ResponseEntity.ok(customer.get().toJSON());
  }
}
