package com.ua.unialgo.user.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ua.unialgo.user.entity.User;
import com.ua.unialgo.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void syncUser(DecodedJWT jwt) {
        String id = jwt.getSubject();
        String username = jwt.getClaim("preferred_username").asString();

        userRepository.findById(id).orElseGet(() -> {
            User user = new User(id, username);

            return userRepository.save(user);
        });
    }
}
