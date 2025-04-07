package com.ua.unialgo.user.repository;

import com.ua.unialgo.user.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> { }
