package dev.playerblair.kuro.auth;

import dev.playerblair.kuro.auth.model.Token;
import dev.playerblair.kuro.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Token> login(@Valid @RequestBody AuthRequest request) {
        Token response = authService.login(request);
        log.info("Successful login, Token generated.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody AuthRequest request) {
        authService.signup(request);
        log.info("Created User with username:{}", request.username());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
