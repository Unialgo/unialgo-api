package com.ua.unialgo.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String keycloakId; // Keycloak's UUID

    @Column(unique = true, nullable = false)
    private String email;

    private String username;

    public User(String keycloakId, String email) {
        this.keycloakId = keycloakId;
        this.email = email;
        this.username = email; // Default username to email
    }
}
