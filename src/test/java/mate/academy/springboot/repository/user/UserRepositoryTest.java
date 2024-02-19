package mate.academy.springboot.repository.user;

import java.util.Optional;
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
        "classpath:database/users/add-users.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/users/remove-users.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Verify the user by correct email")
    void existsByEmail_CorrectEmail_ReturnTrue() {
        String email = "sam@email.com";
        boolean actual = userRepository.existsByEmail(email);

        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("Verify the user by incorrect email")
    void existsByEmail_IncorrectEmail_ReturnFalse() {
        String email = "sam@email";
        boolean actual = userRepository.existsByEmail(email);

        Assertions.assertFalse(actual);
    }

    @Test
    @DisplayName("Find the user by correct email")
    void findByEmail_CorrectEmail_ReturnUser() {
        String email = "sam@email.com";
        Optional<User> actual = userRepository.findByEmail(email);

        Assertions.assertTrue(actual.isPresent());
        Assertions.assertEquals(email, actual.get().getEmail());
    }

    @Test
    @DisplayName("Find the user by incorrect email")
    void findByEmail_IncorrectEmail_ReturnEmptyOptional() {
        String email = "sam@email";
        Optional<User> actual = userRepository.findByEmail(email);

        Assertions.assertFalse(actual.isPresent());
    }
}
