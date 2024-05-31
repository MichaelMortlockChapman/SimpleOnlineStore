package com.example.simpleonlinestore.database.sessions;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionRepository {
   @Autowired
  private SessionCrudRepository sessionRepo;


  public void deleteAllUserSessions(String login) {
    sessionRepo.findAll().forEach(session -> {
      if (session.getUserLogin().equals(login)) {
        sessionRepo.delete(session);
      }
    });
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
