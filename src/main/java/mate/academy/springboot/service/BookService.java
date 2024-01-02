package mate.academy.springboot.service;

import java.util.List;
import mate.academy.springboot.model.Book;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
