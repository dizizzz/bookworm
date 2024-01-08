package mate.academy.springboot.service;

import java.util.List;
import mate.academy.springboot.dto.BookDto;
import mate.academy.springboot.dto.BookSearchParameters;
import mate.academy.springboot.dto.CreateBookRequestDto;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll(Pageable pageable);

    BookDto getBookById(Long id);

    BookDto updateBookById(Long id, CreateBookRequestDto requestDto);

    void deleteById(Long id);

    List<BookDto> search(BookSearchParameters params);
}
