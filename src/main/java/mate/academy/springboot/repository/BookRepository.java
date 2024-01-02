package mate.academy.springboot.repository;

import java.util.List;
import mate.academy.springboot.model.Book;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
