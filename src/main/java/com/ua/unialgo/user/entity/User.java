package com.ua.unialgo.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Entity
@Getter
@AllArgsConstructor
public class User {

    @Id
    private String id; // Keycloak's UUID

    private String username;

    protected User() {}

    @Override
    public String toString() {
        return String.format(
                "User[id='%s', username='%s']",
                id, username);
    }
}
