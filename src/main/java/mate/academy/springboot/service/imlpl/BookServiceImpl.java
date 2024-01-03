package mate.academy.springboot.service.imlpl;

import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import mate.academy.springboot.dto.BookDto;
import mate.academy.springboot.dto.CreateBookRequestDto;
import mate.academy.springboot.exception.EntityNotFoundException;
import mate.academy.springboot.mapper.BookMapper;
import mate.academy.springboot.model.Book;
import mate.academy.springboot.repository.BookRepository;
import mate.academy.springboot.service.BookService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        book.setIsbn(String.format("%010d", new Random().nextInt(1000000000)));
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                    .map(bookMapper::toDto)
                    .toList();
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                    () -> new EntityNotFoundException("Can`t find book by id " + id));
        return bookMapper.toDto(book);
    }

    @Override
    public BookDto updateBookById(Long id, CreateBookRequestDto requestDto) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id " + id));

        Book updatedBook = bookMapper.toModel(requestDto);

        updatedBook.setId(id);

        return bookMapper.toDto(bookRepository.save(updatedBook));
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
}
