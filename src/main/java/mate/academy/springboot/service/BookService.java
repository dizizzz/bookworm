package mate.academy.springboot.service;

import java.util.List;
import mate.academy.springboot.dto.book.BookDto;
import mate.academy.springboot.dto.book.BookSearchParameters;
import mate.academy.springboot.dto.book.CreateBookRequestDto;
import mate.academy.springboot.model.Book;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll(String username, Pageable pageable);

    BookDto getBookById(Long id);

    BookDto updateBookById(Long id, CreateBookRequestDto requestDto);

    void deleteById(Long id);

    List<BookDto> search(BookSearchParameters params);

    List<Book> findAllByCategoriesId(Long id);
}
