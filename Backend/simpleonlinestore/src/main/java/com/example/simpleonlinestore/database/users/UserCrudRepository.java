package com.example.simpleonlinestore.database.users;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface UserCrudRepository extends CrudRepository<User, UUID> {}