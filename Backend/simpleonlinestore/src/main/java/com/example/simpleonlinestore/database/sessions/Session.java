package com.example.simpleonlinestore.database.sessions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sessions")
public class Session {

  @Id
  @Column(name="session_id")
  private String id;

  @Column(name="login")
  private String login;

  // Hibernate expects entities to have a no-arg constructor,
  @SuppressWarnings("unused")
  private Session () {}

  public Session(String id, String login) {
    this.id = id;
    this.login = login;
  }

  public String getId() {
    return id;
  }

  public String getUserLogin() {
    return login;
  }
}
