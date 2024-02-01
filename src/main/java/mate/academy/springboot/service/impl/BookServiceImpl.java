package mate.academy.springboot.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.springboot.dto.book.BookDto;
import mate.academy.springboot.dto.book.BookSearchParameters;
import mate.academy.springboot.dto.book.CreateBookRequestDto;
import mate.academy.springboot.exception.EntityNotFoundException;
import mate.academy.springboot.mapper.BookMapper;
import mate.academy.springboot.model.Book;
import mate.academy.springboot.model.Category;
import mate.academy.springboot.repository.book.BookRepository;
import mate.academy.springboot.repository.book.BookSpecificationBuilder;
import mate.academy.springboot.repository.category.CategoryRepository;
import mate.academy.springboot.service.BookService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;
    private final CategoryRepository categoryRepository;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll(String email, Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
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
        book.setPrice(requestDto.getPrice());
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setIsbn(requestDto.getIsbn());
        book.setCoverImage(requestDto.getCoverImage());
        book.setDescription(requestDto.getDescription());
        Set<Category> collect = requestDto.getCategoryIds().stream()
                .map(categoryRepository::findById)
                .map(Optional::orElseThrow)
                .collect(Collectors.toSet());
        book.setCategories(collect);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> search(BookSearchParameters params) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(params);
        return bookRepository.findAll(bookSpecification)
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<Book> findAllByCategoriesId(Long id) {
        return bookRepository.findAllByCategoriesId(id);
    }

}
