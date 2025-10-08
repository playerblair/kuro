package dev.playerblair.kuro.service;

import dev.playerblair.kuro.model.User;
import dev.playerblair.kuro.repository.UserRepository;
import dev.playerblair.kuro.util.AuthenticationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceMockTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationHelper authenticationHelper;

    @InjectMocks
    private UserService userService;

    private Authentication mockAuthentication;
    private User mockUser;

    @BeforeEach
    public void setup() {
        mockAuthentication = mock(Authentication.class);
        mockUser = mock(User.class);
    }

    @Test
    public void whenDeleteUserIsCalled_shouldDeleteUser() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenReturn(mockUser);

        // when
        userService.deleteUser(mockAuthentication);

        // then
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(userRepository).delete(mockUser);
    }

    @Test
    public void whenDeleteUserIsCalled_givenUsernameNotFound_shouldThrowException() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenThrow(UsernameNotFoundException.class);

        // when & then
        assertThrows(
                UsernameNotFoundException.class,
                () -> userService.deleteUser(mockAuthentication)
        );
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(userRepository, never()).delete(mockUser);
    }
}
