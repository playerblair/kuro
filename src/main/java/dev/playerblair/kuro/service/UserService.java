package dev.playerblair.kuro.service;

import dev.playerblair.kuro.model.User;
import dev.playerblair.kuro.repository.UserRepository;
import dev.playerblair.kuro.util.AuthenticationHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationHelper helper;

    public UserService(UserRepository userRepository, AuthenticationHelper helper) {
        this.userRepository = userRepository;
        this.helper = helper;
    }

    public void deleteUser(Authentication authentication) {
        User user = helper.getCurrentUser(authentication);
        log.debug("Deleting {}", user);
        userRepository.delete(user);
        log.info("Deleted {}", user);
    }
}
