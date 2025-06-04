package com.ua.unialgo.user.service;

import com.ua.unialgo.user.entity.User;
import com.ua.unialgo.user.repository.UserRepository;
import com.ua.unialgo.user.strategies.RoleStrategyFactory;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.security.Principal;

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

    public void syncUser(String keycloakId, String email, String role) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> {
                    User newUser = new User(keycloakId, email);
                    return userRepository.save(newUser);
                });

        // Update email if changed
        if (!email.equals(user.getEmail())) {
            user.setEmail(email);
            userRepository.save(user);
        }

        roleStrategyFactory.getStrategy(role).handleRole(user);
    }

    /**
     * Gets the current user ID from the Principal.
     * 
     * @param principal The authenticated user principal
     * @return The user ID
     */
    public Long getCurrentUserId(Principal principal) {
        if (principal instanceof Authentication auth && auth.getPrincipal() instanceof Jwt jwt) {
            String keycloakId = jwt.getSubject();
            User user = userRepository.findByKeycloakId(keycloakId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with Keycloak ID: " + keycloakId));
            return user.getId();
        }
        throw new IllegalArgumentException("Invalid principal type");
    }

    /**
     * Finds user by ID.
     * 
     * @param userId The user ID
     * @return The user entity
     * @throws EntityNotFoundException if user not found
     */
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    }

    /**
     * Finds user by Keycloak ID.
     * 
     * @param keycloakId The Keycloak ID
     * @return The user entity
     * @throws EntityNotFoundException if user not found
     */
    public User findByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with Keycloak ID: " + keycloakId));
    }

    /**
     * Checks if the current user has a specific role.
     * 
     * @param principal The authenticated user principal
     * @param role The role to check
     * @return true if user has the role
     */
    public boolean hasRole(Principal principal, String role) {
        if (principal instanceof Authentication auth) {
            return auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(authority -> authority.equals("ROLE_" + role));
        }
        return false;
    }
}
