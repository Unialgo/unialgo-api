package com.ua.unialgo.user.controller;

import com.ua.unialgo.user.dto.LoginRequestDto;
import com.ua.unialgo.user.dto.SignupRequestDto;
import com.ua.unialgo.user.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(path = "/public/users")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(path = "/login")
    ResponseEntity<?> login(@RequestBody LoginRequestDto body) {
        return authService.loginAndSyncUser(body);
    }

    @PostMapping(path = "/signup")
    ResponseEntity<?> signup(@RequestBody SignupRequestDto body) {
        return authService.signupAndSyncUser(body);
    }
}
