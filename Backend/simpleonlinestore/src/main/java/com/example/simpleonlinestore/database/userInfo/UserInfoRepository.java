package com.example.simpleonlinestore.database.userInfo;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface UserInfoRepository extends CrudRepository<UserInfo, UUID> {};
