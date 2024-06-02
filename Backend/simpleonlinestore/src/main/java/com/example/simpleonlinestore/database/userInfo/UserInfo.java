package com.example.simpleonlinestore.database.userInfo;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_info")
public class UserInfo {

  @Id
  @Column(name="user_id")
  private UUID userId;

  @Column(name="info_id")
  private UUID infoId;

  // Hibernate expects entities to have a no-arg constructor,
  @SuppressWarnings("unused")
  private UserInfo () {}

  public UserInfo(UUID userId, UUID infoId) {
    this.userId = userId;
    this.infoId = infoId;
  }

  public UUID getUserId() {
    return userId;
  }

  public UUID getInfoId() {
    return infoId;
  }
}
