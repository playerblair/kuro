package dev.playerblair.kuro.service;

import dev.playerblair.kuro.dto.Token;
import dev.playerblair.kuro.exception.UserAlreadyExistsException;
import dev.playerblair.kuro.model.User;
import dev.playerblair.kuro.repository.UserRepository;
import dev.playerblair.kuro.request.LoginRequest;
import dev.playerblair.kuro.request.SignupRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, TokenService tokenService, PasswordEncoder encoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
    }

    public void signup(SignupRequest request) {
        log.debug("Creating User with username:{}", request.username());
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException(request.username());
        }
        User user = userRepository.save(new User(null, request.username(), encoder.encode(request.password())));
        log.info("Created {}", user);
    }

    public Token login(LoginRequest request) {
        log.debug("Authenticating {}...", request.username());
        Authentication authentication = authenticationManager.authenticate
                (new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        Token token = tokenService.generateToken(authentication);
        log.info("User authenticated, Token generated");
        return token;
    }
}
