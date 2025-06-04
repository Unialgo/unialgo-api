package com.ua.unialgo.user.repository;

import com.ua.unialgo.user.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * SOLID - Liskov Substitution Principle (LSP):
 * A UserRepository estende a CrudRepository, o que garante que qualquer implementação da
 * UserRepository possa substituir sua classe pai sem comprometer a funcionalidade.
 */
public interface UserRepository extends CrudRepository<User, Long> {
    
    Optional<User> findByKeycloakId(String keycloakId);
    
    Optional<User> findByEmail(String email);
}
