package com.example.simpleonlinestore.database.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.simpleonlinestore.database.sessions.SessionRepository;

@Service
public class UserRepository {

  @Autowired
  private UserCrudRepository userRepo;

  @Autowired
  private SessionRepository sessionRepository;

  public UserDetails findByLogin(String login) {
    for (User user : userRepo.findAll()) {
      if (user.getLogin().equals(login)) {
        return (UserDetails)user;
      }
    }
    return null;
  }

  public User save(User user) {
    return userRepo.save(user);
  }

  public void updateAssociations(User user) {
    sessionRepository.deleteAllUserSessions(user.getLogin());
  }
  
  public void deleteById(User user) {
    updateAssociations(user);
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
