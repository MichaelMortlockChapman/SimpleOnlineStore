package com.example.simpleonlinestore.database.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.simpleonlinestore.database.sessions.SessionRepository;

@Service
public class UserRepository {

  @Autowired
  private UserCrudRepository userRepo;

  @Autowired
  private SessionRepository sessionRepository;

  // public User findByLoginUser(String login) {
  //   for (User user : userRepo.findAll()) {
  //     if (user.getLogin().equals(login)) {
  //       return user;
  //     }
  //   }
  //   return null;
  // }

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

  public void updateAssociationsFromDelete(User user) {
    sessionRepository.deleteAllUserSessions(user.getLogin());
  }
  
  public void deleteById(User user) {
    updateAssociationsFromDelete(user);
    userRepo.deleteById(user.getId());
  }

  // returns boolean in case login doesn't exist
  public Boolean deleteByLogin(String login) {
    for (User user : userRepo.findAll()) {
      if (user.getLogin().equals(login)) {
        deleteById(user);
        return true;
      }
    }
    return false;
  }
}
