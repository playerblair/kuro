package dev.playerblair.kuro.controller;

import dev.playerblair.kuro.config.TestSecurityConfig;
import dev.playerblair.kuro.dto.Token;
import dev.playerblair.kuro.exception.UserAlreadyExistsException;
import dev.playerblair.kuro.request.LoginRequest;
import dev.playerblair.kuro.request.SignupRequest;
import dev.playerblair.kuro.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureJsonTesters
@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
public class AuthControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private JacksonTester<SignupRequest> jsonSignupRequest;

    @Autowired
    private JacksonTester<LoginRequest> jsonLoginRequest;

    @Autowired
    private JacksonTester<Token> jsonToken;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    private Token token;

    @BeforeEach
    public void setup() {
        signupRequest = new SignupRequest(
                "user",
                "Password1"
        );
        loginRequest = new LoginRequest(
                "user",
                "pass"
        );
        token = new Token("generated_token_value");
    }

    @Test
    public void whenSignupIsCalled_shouldReturn201() throws Exception {
        // when & then
        mockMvc.perform(post("/api/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSignupRequest.write(signupRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Profile created successfully")))
        ;
    }

    @Test
    public void whenSignupIsCalled_givenNoRequest_shouldReturn400() throws Exception {
        // when & then
        mockMvc.perform(post("/api/auth/signup")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("The request body could not be parsed. Please check your JSON syntax and field types.")));
    }

    @Test
    public void whenSignupIsCalled_givenInvalidRequest_shouldReturn400() throws Exception {
        // given
        signupRequest = new SignupRequest(
                "us",
                "password"
        );

        // when & then
        mockMvc.perform(post("/api/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSignupRequest.write(signupRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Username must be at least 3 characters")))
                .andExpect(content().string(containsString("Password must be at least 8 characters with at least 1 uppercase character, lowercase character, and digit")))
        ;
    }

    @Test
    public void whenSignupIsCalled_givenUserAlreadyExists_shouldReturn400() throws Exception {
        // given
        doThrow(new UserAlreadyExistsException(signupRequest.username())).when(authService).signup(signupRequest);

        // when & then
        mockMvc.perform(post("/api/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSignupRequest.write(signupRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("User already exists with username: " + signupRequest.username())));
    }

    @Test
    public void whenLoginIsCalled_shouldReturn200() throws Exception {
        // given
        when(authService.login(loginRequest)).thenReturn(token);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLoginRequest.write(loginRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonToken.write(token).getJson()));
    }

    @Test
    public void whenLoginIsCalled_givenNoRequest_shouldReturn400() throws Exception {
        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("The request body could not be parsed. Please check your JSON syntax and field types.")));
    }

    @Test
    public void whenLoginIsCalled_givenInvalidRequest_shouldReturn401() throws Exception {
        // given
        when(authService.login(loginRequest)).thenThrow(BadCredentialsException.class);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLoginRequest.write(loginRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Invalid username or password")));
    }
}
