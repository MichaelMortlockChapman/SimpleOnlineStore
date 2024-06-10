package com.example.simpleonlinestore.database.users;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.simpleonlinestore.database.customer.CustomerRepository;
import com.example.simpleonlinestore.database.order.OrderRepository;
import com.example.simpleonlinestore.database.sessions.SessionRepository;
import com.example.simpleonlinestore.security.UserRole;

@Service
public class UserRepository {

  @Autowired
  private UserCrudRepository userRepo;

  @Autowired
  private SessionRepository sessionRepository;

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private OrderRepository orderRepository;

  private User findByLoginUser(String login) {
    for (User user : userRepo.findAll()) {
      if (user.getLogin().equals(login)) {
        return user;
      }
    }
    return null;
  }

  public UserDetails findByLogin(String login) {
    for (User user : userRepo.findAll()) {
      if (user.getLogin().equals(login)) {
        return (UserDetails)user;
      }
    }
    return null;
  }

  /**
   * updates user username and password 
   * @param login 
   * @param password assumes password already encrypted
   */
  @Transactional
  public void UpdateUserCreds(String oldLogin, String login, String password) {
    userRepo.updateUserCreds(oldLogin, login, password);
  }

  public User save(User user) {
    return userRepo.save(user);
  }

  public void updateAssociationsFromUpdate(String oldLogin, String newLogin) {
    sessionRepository.updateAllUserSessions(oldLogin, newLogin);
  }

  public CrudRepository<?,UUID> getRelatedRepo(String role) throws ResponseStatusException {
    switch (role) {
      case UserRole.ROLE_USER:
        return customerRepository;
      case UserRole.ROLE_ADMIN:
        return null;
      default:
        throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Bad User Role");
    }
  }

  // expects login to have roleId
  public UUID getRoleIdFromLogin(String login) {
    if (existsRoleIdFromLogin(login) == false) {
      throw new IllegalArgumentException("Login has no associated roldId");
    }
    return findByLoginUser(login).getRoleId();
  }

  public Boolean existsRoleIdFromLogin(String login) {
    User user = findByLoginUser(login);
    if (user == null) {
      return false;
    }
    return user.getRoleId() != null;
  }

  public void updateAssociationsFromDelete(User user) throws ResponseStatusException {
    sessionRepository.deleteAllUserSessions(user.getLogin());
    
    CrudRepository<?, UUID> relatedRepo = getRelatedRepo(user.getRole());
    if (user.getRole().equals(UserRole.ROLE_USER)) {
      UUID customerId = user.getRoleId();
      orderRepository.removeDeletedCustomer(customerId);
      customerRepository.deleteById(customerId);
    }
    if (relatedRepo != null) {
      relatedRepo.deleteById(user.getRoleId());
    }
  }
  
  public void deleteById(User user) throws ResponseStatusException {
    updateAssociationsFromDelete(user);
    userRepo.deleteById(user.getId());
  }

  // returns boolean in case login doesn't exist
  public Boolean deleteByLogin(String login) throws ResponseStatusException {
    for (User user : userRepo.findAll()) {
      if (user.getLogin().equals(login)) {
        deleteById(user);
        return true;
      }
    }
    return false;
  }
}
