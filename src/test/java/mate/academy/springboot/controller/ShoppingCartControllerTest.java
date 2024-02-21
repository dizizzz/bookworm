package mate.academy.springboot.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.springboot.dto.cart.CartItemRequestDto;
import mate.academy.springboot.dto.cart.CartItemRequestUpdateDto;
import mate.academy.springboot.dto.cart.ShoppingCartResponseDto;
import mate.academy.springboot.model.Role;
import mate.academy.springboot.model.RoleName;
import mate.academy.springboot.model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthenticationManager authenticationManager;

    private Role roleUser;
    private User user;

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
                    new ClassPathResource("database/books/add-books.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/users/add-users.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/shopping-carts/add-shopping-carts.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/shopping-carts/add-cart-items.sql")
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
                    new ClassPathResource("database/shopping-carts/remove-cart-items.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/shopping-carts/remove-shopping-carts.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/users/remove-users.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/remove-books.sql")
            );
        }
    }

    @BeforeEach
    public void setup() {
        roleUser = new Role();
        roleUser.setId(1L);
        roleUser.setName(RoleName.USER);

        user = new User();
        user.setId(1L);
        user.setEmail("sam@email.com");
        user.setPassword("123456789");
        user.setFirstName("Sam");
        user.setLastName("Smith");
        user.setShippingAddress("123 Main St, City, Country");
        user.setRoles(Set.of(roleUser));
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Add the book to shopping cart")
    void addBookToCart_ValidRequestDto_Success() throws Exception {
        CartItemRequestDto requestDto = new CartItemRequestDto()
                .setBookId(1L).setQuantity(2);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        when(authenticationManager.authenticate(authentication)).thenReturn(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        System.out.println(jsonRequest);
        mockMvc.perform(
                        post("/cart")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", is(user.getId().intValue())))
                .andExpect(jsonPath("$.cartItems").exists());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get shopping cart")
    void getShoppingCart_ShouldReturnShoppingCart() throws Exception {
        CartItemRequestDto requestDto = new CartItemRequestDto()
                .setBookId(1L).setQuantity(2);

        ShoppingCartResponseDto expected = new ShoppingCartResponseDto()
                .setUserId(1L).setCartItems(Set.of(requestDto));

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        when(authenticationManager.authenticate(authentication)).thenReturn(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jsonRequest = objectMapper.writeValueAsString(expected);
        mockMvc.perform(
                        get("/cart")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(expected.getUserId().intValue())))
                .andExpect(jsonPath("$.cartItems[0].quantity", is(requestDto.getQuantity())));
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Update quantity of a book in the shopping cart")
    void updateCartItem_GivenId_ShouldReturnShoppingCart() throws Exception {
        CartItemRequestUpdateDto updateDto = new CartItemRequestUpdateDto();
        updateDto.setQuantity(4);

        Long cartItemId = 1L;

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        when(authenticationManager.authenticate(authentication)).thenReturn(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jsonRequest = objectMapper.writeValueAsString(updateDto);
        mockMvc.perform(
                        put("/cart/cart-items/{cartItemId}", cartItemId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userId", is(user.getId().intValue())))
                .andExpect(jsonPath("$.cartItems[0].quantity", is(updateDto.getQuantity())));
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Delete a shopping cart")
    @Sql(
            scripts = "classpath:database/shopping-carts/add-shopping-cart-to-delete.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void deleteBookById_ValidBookId_Success() throws Exception {
        mockMvc.perform(
                        delete("/cart/cart-items/{cartItemId}", 3L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }
}
