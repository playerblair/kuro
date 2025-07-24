package dev.playerblair.kuro.auth.service;

import dev.playerblair.kuro.auth.AuthRequest;
import dev.playerblair.kuro.auth.UserRepository;
import dev.playerblair.kuro.auth.exception.UserAlreadyExistsException;
import dev.playerblair.kuro.auth.model.Token;
import dev.playerblair.kuro.auth.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public Token login(AuthRequest request) {
        log.debug("Authenticating User with username:{}", request.username());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        log.debug("User authenticated. Generating token...");
        return tokenService.generateToken(authentication);
    }

    public void signup(AuthRequest request) {
        log.debug("Creating User with username:{}", request.username());

        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException(request.username());
        }

        User saved = userRepository.save(
                User.builder()
                    .username(request.username())
                    .password(passwordEncoder.encode(request.password()))
                    .role("USER")
                    .build()
        );

        log.debug("Created User(ID:{}).", saved.getId());
    }
}
