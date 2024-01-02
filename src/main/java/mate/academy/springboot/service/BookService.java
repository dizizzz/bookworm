package mate.academy.springboot.service;

import java.util.List;
import mate.academy.springboot.dto.BookDto;
import mate.academy.springboot.dto.CreateBookRequestDto;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto getBookById(Long id);
}
