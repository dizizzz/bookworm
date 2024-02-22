package mate.academy.springboot.repository.order;

import java.util.List;
import java.util.Optional;
import mate.academy.springboot.model.Order;
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
        "classpath:database/orders/add-orders.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/orders/remove-orders.sql",
        "classpath:database/users/remove-users.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("Find all order by correct id and user id")
    void findByIdAndUserId_CorrectId_ReturnShoppingCart() {
        Long orderId = 1L;
        Long userId = 1L;
        Optional<Order> actual = orderRepository.findByIdAndUserId(userId, orderId);

        Assertions.assertTrue(actual.isPresent());
        Assertions.assertEquals(userId, actual.get().getUser().getId());
    }

    @Test
    @DisplayName("Find all order by incorrect id and user id")
    void findByIdAndUserId_IncorrectId_ReturnEmptyOptional() {
        Long orderId = 100L;
        Long userId = 100L;
        Optional<Order> actual = orderRepository.findByIdAndUserId(userId, orderId);

        Assertions.assertFalse(actual.isPresent());
    }

    @Test
    @DisplayName("Find all order by correct user id")
    void findAllByUserId_CorrectId_ReturnShoppingCart() {
        Long userId = 1L;
        List<Order> actual = orderRepository.findAllByUserId(userId);

        Assertions.assertEquals(1, actual.size());
    }

    @Test
    @DisplayName("Find all order by incorrect user id")
    void findAllByUserId_IncorrectId_ReturnEmptyList() {
        Long userId = 100L;
        List<Order> actual = orderRepository.findAllByUserId(userId);

        Assertions.assertTrue(actual.isEmpty());
    }
}
