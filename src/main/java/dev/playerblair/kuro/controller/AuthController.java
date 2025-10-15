package dev.playerblair.kuro.controller;

import dev.playerblair.kuro.dto.SimpleResponse;
import dev.playerblair.kuro.dto.Token;
import dev.playerblair.kuro.request.LoginRequest;
import dev.playerblair.kuro.request.SignupRequest;
import dev.playerblair.kuro.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SimpleResponse> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        SimpleResponse response = new SimpleResponse("Profile created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Token> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
