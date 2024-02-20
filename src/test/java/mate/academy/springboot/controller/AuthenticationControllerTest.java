package mate.academy.springboot.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.springboot.dto.user.UserLoginRequestDto;
import mate.academy.springboot.dto.user.UserLoginResponseDto;
import mate.academy.springboot.dto.user.UserRegistrationRequestDto;
import mate.academy.springboot.dto.user.UserResponseDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthenticationManager authenticationManager;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/users/add-users.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/users/remove-users.sql")
            );
        }
    }

    @Test
    @DisplayName("Login the user")
    void login_ReturnToken() throws Exception {
        UserLoginRequestDto requestDto =
                new UserLoginRequestDto("sam@email.com", "123456789");

        Authentication authentication
                = new UsernamePasswordAuthenticationToken(
                        requestDto.email(), requestDto.password()
        );

        when(authenticationManager.authenticate(authentication)).thenReturn(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                        post("/auth/login")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        UserLoginResponseDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                UserLoginResponseDto.class);

        assertNotNull(actual);
    }

    @Test
    @DisplayName("Register the user")
    void register_Successes() throws Exception {
        UserRegistrationRequestDto userRequestDto = new UserRegistrationRequestDto()
                .setEmail("user@gmail.com")
                .setPassword("123456789")
                .setRepeatPassword("123456789")
                .setFirstName("User")
                .setLastName("Registration")
                .setShippingAddress("Address");

        UserResponseDto expected = new UserResponseDto()
                .setId(3L)
                .setEmail(userRequestDto.getEmail())
                .setFirstName(userRequestDto.getFirstName())
                .setLastName(userRequestDto.getLastName())
                .setShippingAddress(userRequestDto.getShippingAddress());

        String jsonRequest = objectMapper.writeValueAsString(userRequestDto);
        MvcResult result = mockMvc.perform(
                        post("/auth/registration")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserResponseDto.class);

        assertNotNull(actual);
        assertEquals(expected.getEmail(), actual.getEmail());
    }
}
