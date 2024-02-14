package mate.academy.springboot.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mate.academy.springboot.dto.book.BookDto;
import mate.academy.springboot.dto.book.BookSearchParameters;
import mate.academy.springboot.dto.book.CreateBookRequestDto;
import mate.academy.springboot.exception.EntityNotFoundException;
import mate.academy.springboot.mapper.BookMapper;
import mate.academy.springboot.model.Book;
import mate.academy.springboot.model.Category;
import mate.academy.springboot.repository.book.BookRepository;
import mate.academy.springboot.service.impl.BookServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookDto bookDto;
    private CreateBookRequestDto requestDto;
    private Category category;
    private BookSearchParameters searchParameters;

    @BeforeEach
    public void setup() {
        Long bookId = 1L;
        book = new Book();
        book.setId(bookId);
        book.setTitle("Title");
        book.setAuthor("Author");
        book.setPrice(BigDecimal.valueOf(123));
        book.setIsbn("1248752418855");
        book.setDescription("Description");
        book.setCoverImage("image.jpg");

        bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());

        category = new Category();
        category.setId(1L);
        category.setName("Category");
        category.setDescription("Description");
    }

    @Test
    @DisplayName("Verify the correct book was saved")
    public void saveBook_WithValidValue_ReturnBookDto() {
        requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Title");
        requestDto.setAuthor("Author");
        requestDto.setPrice(BigDecimal.valueOf(123));
        requestDto.setIsbn("1248752418855");
        requestDto.setDescription("Description");
        requestDto.setCoverImage("image.jpg");

        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        when(bookMapper.toModel(requestDto)).thenReturn(book);

        BookDto actual = bookService.save(requestDto);

        assertNotNull(actual);
        assertEquals(book.getId(), actual.getId());
    }

    @Test
    @DisplayName("""
            Given available books, retrieve the available list of book
            """)
    void findAll_WithValidEmailAndPageable_ReturnBookList() {
        String email = "email@test.com";
        Pageable pageable = PageRequest.of(0, 10, Sort.by("title"));

        Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 1);

        when(bookRepository.findAll(pageable)).thenReturn((bookPage));
        List<BookDto> bookList = bookService.findAll(email, pageable);
        Assertions.assertEquals(1, bookList.size());
    }

    @Test
    @DisplayName("Verify the correct book was returned when book exists")
    public void getBook_WithValidBookId_ShouldReturnValidBook() {
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        BookDto actual = bookService.getBookById(book.getId());
        Assertions.assertEquals(book.getId(), actual.getId());
    }

    @Test
    @DisplayName("Given incorrect id, check if returns exception")
    public void getBook_WithNotExistingBookId_ShouldThrowException() {
        Long bookId = 100L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.getBookById(bookId)
        );

        String expected = "Can`t find book by id " + bookId;
        String actual = exception.getMessage();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("")
    void deleteById_VerifyDeletion() {
        Long bookId = 1L;
        bookService.deleteById(bookId);
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    void findAllByCategoriesId_WithValidBook_ReturnBookList() {
        Long categoryId = 1L;
        Category category = new Category();
        category.setName("Category");
        category.setId(categoryId);
        category.setDescription("Description");
        book.setCategories(Set.of(category));

        List<Book> books = new ArrayList<>();
        books.add(new Book());
        when(bookRepository.findAllByCategoriesId(categoryId)).thenReturn(books);

        List<Book> bookList = bookService.findAllByCategoriesId(categoryId);

        Assertions.assertEquals(books.size(), bookList.size());
    }
}
