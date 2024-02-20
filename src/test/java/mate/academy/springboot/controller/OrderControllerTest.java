package mate.academy.springboot.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.springboot.dto.order.OrderItemResponseDto;
import mate.academy.springboot.dto.order.OrderRequestDto;
import mate.academy.springboot.dto.order.OrderResponseDto;
import mate.academy.springboot.model.Role;
import mate.academy.springboot.model.RoleName;
import mate.academy.springboot.model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerTest {
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
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/orders/add-orders.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/orders/add-order-items.sql")
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
                    new ClassPathResource("database/orders/remove-order-items.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/orders/remove-orders.sql")
            );
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
    @DisplayName("Create a new order")
    void addOrder_ValidRequestDto_Success() throws Exception {
        OrderItemResponseDto orderItemResponseDto = new OrderItemResponseDto()
                .setId(1L)
                .setQuantity(2);

        OrderRequestDto requestDto = new OrderRequestDto()
                .setShoppingAddress("New address");

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        when(authenticationManager.authenticate(authentication)).thenReturn(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                        post("/orders")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        OrderResponseDto expected = new OrderResponseDto()
                .setId(1L)
                .setUserId(user.getId())
                .setStatus("NEW")
                .setOrderDate(LocalDateTime.of(2024,2, 18, 17, 24, 1))
                .setTotal(BigDecimal.valueOf(222))
                .setOrderItems(Set.of(orderItemResponseDto));

        OrderResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), OrderResponseDto.class
        );

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual,"id");
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get all orders")
    void getOrders_ShouldReturnAllOrders() throws Exception {
        OrderItemResponseDto orderItemResponseDto = new OrderItemResponseDto()
                .setId(1L)
                .setQuantity(2);

        List<OrderResponseDto> expected = new ArrayList<>();
        expected.add(new OrderResponseDto()
                .setId(1L)
                .setUserId(user.getId())
                .setOrderItems(Set.of(orderItemResponseDto))
                .setOrderDate(LocalDateTime.of(2024,2, 18, 17, 24, 1))
                .setTotal(BigDecimal.valueOf(121))
                .setStatus("NEW"));

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        when(authenticationManager.authenticate(authentication)).thenReturn(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jsonRequest = objectMapper.writeValueAsString(user);
        MvcResult result = mockMvc.perform(
                        get("/orders")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        OrderResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), OrderResponseDto[].class
        );
        Assertions.assertEquals(1,actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get all order items")
    void getOrderItems_GivenId_ShouldReturnOrderItem() throws Exception {
        List<OrderItemResponseDto> expected = new ArrayList<>();
        expected.add(new OrderItemResponseDto()
                .setId(1L)
                .setQuantity(2));

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        when(authenticationManager.authenticate(authentication)).thenReturn(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jsonRequest = objectMapper.writeValueAsString(user);
        MvcResult result = mockMvc.perform(
                        get("/orders/{orderId}/items", 1L)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        OrderItemResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), OrderItemResponseDto[].class
        );

        Assertions.assertEquals(1,actual.length);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get the order item by id")
    void getOrderItemById_GivenId_ShouldReturnOrderItem() throws Exception {
        OrderItemResponseDto expected = new OrderItemResponseDto()
                .setId(1L)
                .setQuantity(2);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        when(authenticationManager.authenticate(authentication)).thenReturn(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jsonRequest = objectMapper.writeValueAsString(user);
        MvcResult result = mockMvc.perform(
                        get("/orders/{orderId}/items/{itemId}", 1L, 1L)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        OrderItemResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), OrderItemResponseDto.class
        );

        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }
}
