package com.example.simpleonlinestore.database.users;

import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserCrudRepository extends CrudRepository<User, UUID> {
  
  @Modifying
  @Query("UPDATE User SET login = ?2, password = ?3 WHERE login = ?1")
  void updateUserCreds(String oldLogin, String login, String password);
}