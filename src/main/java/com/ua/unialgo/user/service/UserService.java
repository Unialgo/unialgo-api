package com.ua.unialgo.user.service;

import com.ua.unialgo.user.entity.User;
import com.ua.unialgo.user.repository.UserRepository;
import com.ua.unialgo.user.strategies.RoleStrategyFactory;
import org.springframework.stereotype.Service;

/**
 * SOLID - Single Responsibility Principle (SRP):
 * A UserService é responsável apenas por gerenciar a lógica de negócios relacionada aos usuários.
 * Outras responsabilidades, como persistência de dados, são delegadas a repositórios específicos,
 * como UserRepository, TeacherRepository e StudentRepository.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleStrategyFactory roleStrategyFactory;

    public UserService(UserRepository userRepository, RoleStrategyFactory roleStrategyFactory) {
        this.userRepository = userRepository;
        this.roleStrategyFactory = roleStrategyFactory;
    }

    public void syncUser(String id, String username, String role) {
        User user = new User(id, username);

        userRepository.findById(id).orElseGet(() -> userRepository.save(user));

        roleStrategyFactory.getStrategy(role).handleRole(user);
    }
}
