package com.example.simpleonlinestore.database.sessions;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SessionCrudRepository extends CrudRepository<Session, String> {

  @Modifying
  @Query("UPDATE Session SET login = ?2 WHERE login = ?1")
  void updateSessions(String oldLogin, String newlogin);

  @Modifying
  @Query("DELETE Session WHERE login = ?1")
  void deleteSessions(String login);
}