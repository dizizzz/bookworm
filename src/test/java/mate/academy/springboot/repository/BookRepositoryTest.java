package mate.academy.springboot.repository;

import java.util.List;
import mate.academy.springboot.model.Book;
import mate.academy.springboot.repository.book.BookRepository;
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
        "classpath:database/categories/add-categories.sql",
        "classpath:database/books/add-books.sql",
        "classpath:database/books/add-value-to-books-categories-table.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/categories/remove-categories.sql",
        "classpath:database/books/remove-books.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Find all books by categories id")
    void findAllByCategoriesId_CorrectId_ReturnBookList() {
        Long categoryId = 1L;
        List<Book> actual = bookRepository.findAllByCategoriesId(categoryId);
        Assertions.assertEquals(1, actual.size());
    }

    @Test
    @DisplayName("Find all books by categories id")
    void findAllByCategoriesId_IncorrectId_ReturnEmptyList() {
        Long categoryId = 6L;
        List<Book> actual = bookRepository.findAllByCategoriesId(categoryId);
        Assertions.assertTrue(actual.isEmpty());
    }
}
