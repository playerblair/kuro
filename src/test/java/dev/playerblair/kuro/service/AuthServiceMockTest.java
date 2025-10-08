package dev.playerblair.kuro.service;

import dev.playerblair.kuro.dto.Token;
import dev.playerblair.kuro.exception.UserAlreadyExistsException;
import dev.playerblair.kuro.model.User;
import dev.playerblair.kuro.repository.UserRepository;
import dev.playerblair.kuro.request.LoginRequest;
import dev.playerblair.kuro.request.SignupRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceMockTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private SignupRequest testSignupRequest;
    private LoginRequest testLoginRequest;
    private User mockUser;

    @BeforeEach
    public void setup() {
        testSignupRequest = new SignupRequest(
                "user",
                "pass"
        );
        testLoginRequest = new LoginRequest(
                "user",
                "pass"
        );

        mockUser = mock(User.class);
    }

    @Test
    public void whenSignupIsCalled_shouldCreateUser() {
        // given
        when(userRepository.existsByUsername(testSignupRequest.username())).thenReturn(false);
        when(encoder.encode(testSignupRequest.password())).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // when
        authService.signup(testSignupRequest);

        // then
        verify(userRepository).existsByUsername(testSignupRequest.username());
        verify(encoder).encode(testSignupRequest.password());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void whenSignupIsCalled_givenUserAlreadyExists_shouldThrowException() {
        // given
        when(userRepository.existsByUsername(testSignupRequest.username())).thenReturn(true);

        // when & then
        assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.signup(testSignupRequest)
        );
        verify(userRepository).existsByUsername(testSignupRequest.username());
        verify(encoder, never()).encode(testSignupRequest.password());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void whenLoginIsCalled_givenValidLoginRequest_shouldGenerateToken() {
        // given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(tokenService.generateToken(any(Authentication.class))).thenReturn(mock(Token.class));

        // when
        Token token = authService.login(testLoginRequest);

        // then
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService).generateToken(any(Authentication.class));
    }

    @Test
    public void whenLoginIsCalled_givenBadCredentials_throwException() {
        // given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(BadCredentialsException.class);

        // when & then
        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(testLoginRequest)
        );
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, never()).generateToken(any(Authentication.class));
    }
}
