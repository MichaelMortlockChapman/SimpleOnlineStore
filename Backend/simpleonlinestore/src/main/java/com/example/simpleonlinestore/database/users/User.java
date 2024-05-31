package com.example.simpleonlinestore.database.users;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.simpleonlinestore.security.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User implements UserDetails {
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name="user_id")
  private UUID id;

  @Column(name="username")
  private String login;

  @Column(name="password")
  private String password;

  @Column(name="role")
  private String role;

  // Hibernate expects entities to have a no-arg constructor,
  @SuppressWarnings("unused")
  private User () {}

  public User(String login, String password, String role) {
    this.login = login;
    this.password = password;
    this.role = role;
  }

  public UUID getId() {
    return id;
  }

  public String getLogin() {
    return login;
  }

  public String getRole() {
    return role;
  }

  protected static final Collection<? extends GrantedAuthority> adminAuthorities = List.of(new SimpleGrantedAuthority(UserRole.ROLE_ADMIN), new SimpleGrantedAuthority(UserRole.ROLE_USER));
  protected static final Collection<? extends GrantedAuthority> userAuthorities = List.of(new SimpleGrantedAuthority(UserRole.ROLE_USER));
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    if (this.role == UserRole.ROLE_ADMIN) {
      return adminAuthorities;
    } else {
      return userAuthorities; 
    }
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return login;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }  
}
