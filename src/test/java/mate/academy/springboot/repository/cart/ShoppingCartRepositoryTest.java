package mate.academy.springboot.repository.cart;

import java.util.Optional;
import mate.academy.springboot.model.ShoppingCart;
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
        "classpath:database/shopping-carts/add-shopping-carts.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/shopping-carts/remove-shopping-carts.sql",
        "classpath:database/users/remove-users.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("Find the shopping cart by correct user id")
    void findByUserId_CorrectId_ReturnShoppingCart() {
        Long userId = 1L;
        Optional<ShoppingCart> actual = shoppingCartRepository.findByUserId(userId);

        Assertions.assertTrue(actual.isPresent());
        Assertions.assertEquals(userId, actual.get().getUser().getId());
    }

    @Test
    @DisplayName("Find the shopping cart by incorrect user id")
    void findByUserId_IncorrectId_ReturnEmptyOptional() {
        Long userId = 100L;
        Optional<ShoppingCart> actual = shoppingCartRepository.findByUserId(userId);

        Assertions.assertFalse(actual.isPresent());
    }

}
