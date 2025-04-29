package com.ua.unialgo.user.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ua.unialgo.user.dto.LoginRequestDto;
import com.ua.unialgo.user.dto.SignupRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {
    private final UserService userService;

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.client-uuid}")
    private String clientUuid;
    @Value("${keycloak.client-id}")
    private String clientId;
    @Value("${keycloak.client-secret}")
    private String clientSecret;
    @Value("${keycloak.admin-client-id}")
    private String adminClientId;
    @Value("${keycloak.admin-client-secret}")
    private String adminClientSecret;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public ResponseEntity<?> loginAndSyncUser(LoginRequestDto loginRequest) {
        try {
            ResponseEntity<Map> response = generateToken(loginRequest.username(), loginRequest.password());

            String accessToken = (String) response.getBody().get("access_token");
            DecodedJWT jwt = JWT.decode(accessToken);

            String id = jwt.getSubject();
            String username = jwt.getClaim("preferred_username").asString();
//          TODO: Retrieve role

            userService.syncUser(id, username, "");

            return ResponseEntity.ok(response.getBody());

        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    public ResponseEntity<?> signupAndSyncUser(SignupRequestDto signupRequest) {
        try {
            String accessToken = this.getAdminAccessToken();

            String userId = this.createUserInKeycloak(signupRequest.username(), signupRequest.password(), accessToken);

            this.assignRoleToUser(userId, signupRequest.role(), accessToken);

            userService.syncUser(userId, signupRequest.username(), signupRequest.role());

            return ResponseEntity.ok(userId);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    private ResponseEntity<Map> generateToken(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("username", username);
        form.add("password", password);

        HttpEntity<?> entity = new HttpEntity<>(form, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token",
                HttpMethod.POST,
                entity,
                Map.class
        );

        return response;
    }

    private String getAdminAccessToken() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

        form.add("grant_type", "client_credentials");
        form.add("client_id", adminClientId);
        form.add("client_secret", adminClientSecret);

        HttpEntity<?> entity = new HttpEntity<>(form, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                keycloakUrl + "/realms/master/protocol/openid-connect/token",
                HttpMethod.POST,
                entity,
                Map.class
        );

        return (String) response.getBody().get("access_token");

        } catch (HttpClientErrorException e) {
            throw new HttpClientErrorException(e.getStatusCode(), e.getResponseBodyAsString());
        }
    }

    private String createUserInKeycloak(String username, String password, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("enabled", true);

        Map<String, Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("value", password);
        credentials.put("temporary", false);

        user.put("credentials", List.of(credentials));

        HttpEntity<?> entity = new HttpEntity<>(user, headers);

        restTemplate.postForEntity(
                keycloakUrl + "/admin/realms/" + realm + "/users",
                entity,
                Void.class
        );

        ResponseEntity<List> response = restTemplate.exchange(
        keycloakUrl + "/admin/realms/" + realm + "/users?username=" + username,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            List.class
        );

        Map<String, Object> createdUser = (Map<String, Object>) response.getBody().get(0);

        return (String) createdUser.get("id");
    }

    private void assignRoleToUser(String userId, String role, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String roleId = "";

        if (role.equals("STUDENT")) {
            roleId = "e453a600-634f-4085-961b-7948f7bbf149";
        } else if (role.equals("TEACHER")) {
            roleId = "8c09e5f9-d4f7-4599-9061-eac3891d8852";
        }

        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("name", role);
        roleMap.put("id", roleId);

        HttpEntity<?> entity = new HttpEntity<>(List.of(roleMap), headers);

        restTemplate.postForEntity(
        keycloakUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/clients/" + clientUuid,
            entity,
            Void.class
        );
    }
}
