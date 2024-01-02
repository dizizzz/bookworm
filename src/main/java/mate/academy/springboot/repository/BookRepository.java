package mate.academy.springboot.repository;

import java.util.List;
import java.util.Optional;
import mate.academy.springboot.model.Book;

public interface BookRepository {
    Book save(Book book);

    Optional<Book> findBookById(Long id);

    List<Book> findAll();
}
