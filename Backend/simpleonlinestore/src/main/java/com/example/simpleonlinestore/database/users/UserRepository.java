package com.example.simpleonlinestore.database.users;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.simpleonlinestore.database.customer.CustomerRepository;
import com.example.simpleonlinestore.database.sessions.SessionRepository;
import com.example.simpleonlinestore.database.userInfo.UserInfo;
import com.example.simpleonlinestore.database.userInfo.UserInfoRepository;
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
  private UserInfoRepository userInfoRepository;

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

  public void updateAssociationsFromDelete(User user) throws ResponseStatusException {
    sessionRepository.deleteAllUserSessions(user.getLogin());
    CrudRepository<?, UUID> relatedRepo = getRelatedRepo(user.getRole());
    userInfoRepository.findById(user.getId()).ifPresent(userInfo -> relatedRepo.deleteById(userInfo.getInfoId()));
  }

  public UUID findInfoIdFromLogin (String login) throws ResponseStatusException {
    User user = findByLoginUser(login);
    Optional<UserInfo> userInfo = userInfoRepository.findById(user.getId());
    if (userInfo.isPresent()) {
      return userInfo.get().getInfoId();
    }
    throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Missing User Info");
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
