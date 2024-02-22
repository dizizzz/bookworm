package mate.academy.springboot.repository.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import mate.academy.springboot.model.Order;
import mate.academy.springboot.model.OrderItem;
import mate.academy.springboot.model.Status;
import mate.academy.springboot.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {
        "classpath:database/users/add-users.sql",
        "classpath:database/orders/add-orders.sql",
        "classpath:database/books/add-books.sql",
        "classpath:database/orders/add-order-items.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/orders/remove-order-items.sql",
        "classpath:database/books/remove-books.sql",
        "classpath:database/orders/remove-orders.sql",
        "classpath:database/users/remove-users.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class OrderItemRepositoryTest {
    @Autowired
    private OrderItemRepository orderItemRepository;

    private User user;
    private Order order;

    @Test
    @DisplayName("Find all order items by correct order")
    void findAllByOrder_CorrectOrder_ReturnOrderItems() {
        user = new User();
        user.setId(1L);
        user.setEmail("sam.email.com");
        user.setPassword("123456789");
        user.setFirstName("Sam");
        user.setLastName("Smith");
        user.setShippingAddress("123 Main St, City, Country");

        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setStatus(Status.NEW);
        order.setTotal(BigDecimal.valueOf(121));

        List<OrderItem> actual = orderItemRepository.findAllByOrder(order);

        Assertions.assertEquals(1, actual.size());
    }

    @Test
    @DisplayName("Find all order items by incorrect order")
    void findAllByOrder_IncorrectOrder_ReturnEmptyList() {
        user = new User();
        user.setId(1L);
        user.setEmail("sam.email.com");
        user.setPassword("123456789");
        user.setFirstName("Sam");
        user.setLastName("Smith");
        user.setShippingAddress("123 Main St, City, Country");

        order = new Order();
        order.setId(2L);
        order.setUser(user);
        order.setStatus(Status.NEW);
        order.setTotal(BigDecimal.valueOf(121));
        List<OrderItem> actual = orderItemRepository.findAllByOrder(order);

        Assertions.assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("Find all order items by correct id and order id")
    void findByIdAndOrderId_CorrectIdAndOrderId_ReturnOrderItems() {
        Long itemId = 1L;
        Long orderId = 1L;
        Optional<OrderItem> actual = orderItemRepository.findByIdAndOrderId(itemId, orderId);

        Assertions.assertTrue(actual.isPresent());
        Assertions.assertEquals(orderId, actual.get().getOrder().getId());
    }

    @Test
    @DisplayName("Find all order items by incorrect id and order id")
    void findByIdAndOrderId_CorrectIdAndOrderId_ReturnEmptyOptional() {
        Long itemId = 100L;
        Long orderId = 100L;
        Optional<OrderItem> actual = orderItemRepository.findByIdAndOrderId(itemId, orderId);

        Assertions.assertFalse(actual.isPresent());
    }
}
