package com.example.simpleonlinestore.database.sessions;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionRepository {
   @Autowired
  private SessionCrudRepository sessionRepo;

  @Transactional
  public void updateAllUserSessions(String oldLogin, String newLogin) {
    sessionRepo.updateSessions(oldLogin, newLogin);
  }

  @Transactional
  public void deleteAllUserSessions(String login) {
    sessionRepo.deleteSessions(login);
  }

  public void deleteById(String id) {
    sessionRepo.deleteById(id);
  }

  public Session save(Session session) {
    return sessionRepo.save(session);
  }

  public Optional<Session> findById(String id) {
    return sessionRepo.findById(id);
  }
}
