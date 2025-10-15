package dev.playerblair.kuro.controller;

import dev.playerblair.kuro.config.TestSecurityConfig;
import dev.playerblair.kuro.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
public class UserControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private Authentication mockAuthentication;

    @BeforeEach
    public void setup() {
        mockAuthentication = mock(Authentication.class);
    }

    @Test
    @WithMockUser
    public void whenDeleteUserIsCalled_shouldReturn200() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/user")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("User deleted successfully")));
    }

    @Test
    public void whenDeleteUserIsCalled_givenUnauthorisedUser_shouldReturn403() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void whenDeleterUserIsCalled_givenUsernameNotFound_shouldReturn404() throws Exception {
        // given
        doThrow(new UsernameNotFoundException("User not found with username: testuser"))
                .when(userService)
                .deleteUser(any(Authentication.class));

        // when & then
        mockMvc.perform(delete("/api/user"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("User not found with username: testuser")));
    }
}
