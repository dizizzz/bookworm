package mate.academy.springboot.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.springboot.dto.book.BookDto;
import mate.academy.springboot.dto.book.CreateBookRequestDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/add-books.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/categories/add-categories.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/add-value-to-books-categories-table.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/remove-books-categories.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/categories/remove-categories.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/remove-books.sql")
            );
        }
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get all books")
    void getAll_GivenBooks_ShouldReturnAllBooks() throws Exception {
        mockMvc.perform(
                        get("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Title 1")))
                .andExpect(jsonPath("$[0].author", is("Author 1")))
                .andExpect(jsonPath("$[0].price", is(111)))
                .andExpect(jsonPath("$[0].description", is("Description 1")))
                .andExpect(jsonPath("$[0].coverImage", is("image1.jpg")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Title 2")))
                .andExpect(jsonPath("$[1].author", is("Author 2")))
                .andExpect(jsonPath("$[1].price", is(222)))
                .andExpect(jsonPath("$[1].description", is("Description 2")))
                .andExpect(jsonPath("$[1].coverImage", is("image2.jpg")));
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get book by ID")
    void getBookById_GivenId_ShouldReturnBook() throws Exception {
        BookDto expected = new BookDto()
                .setId(1L).setTitle("Title 1").setAuthor("Author 1")
                .setPrice(BigDecimal.valueOf(111)).setDescription("Description 1")
                .setCoverImage("image1.jpg")
                .setCategoryIds(List.of(1L));

        mockMvc.perform(
                        get("/books/{id}", expected.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Title 1")))
                .andExpect(jsonPath("$.author", is("Author 1")))
                .andExpect(jsonPath("$.price", is(111)))
                .andExpect(jsonPath("$.description", is("Description 1")))
                .andExpect(jsonPath("$.coverImage", is("image1.jpg")));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update book by ID")
    void updateBook_GivenId_ShouldReturnBook() throws Exception {
        Long updateId = 1L;
        CreateBookRequestDto updateBook = new CreateBookRequestDto()
                .setTitle("Title 1").setAuthor("Author 1")
                .setPrice(BigDecimal.valueOf(111)).setDescription("Description 1")
                .setIsbn("1207199818861")
                .setCoverImage("image1.jpg")
                .setCategoryIds(List.of(1L));

        mockMvc.perform(
                        put("/books/{id}", updateId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateBook))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(updateBook.getTitle())));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a new book")
    @Sql(
            scripts = "classpath:database/books/remove-books-by-title.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void createBook_ValidRequestDto_Success() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Title")
                .setAuthor("Author")
                .setIsbn("1207199818869")
                .setPrice(BigDecimal.valueOf(123))
                .setDescription("Description")
                .setCoverImage("image.jpg")
                .setCategoryIds(List.of(1L));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is(requestDto.getTitle())))
                .andExpect(jsonPath("$.author", is(requestDto.getAuthor())))
                .andExpect(jsonPath("$.price", is(requestDto.getPrice().intValue())))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.coverImage", is(requestDto.getCoverImage())));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete a book")
    @Sql(
            scripts = "classpath:database/books/add-book-to-delete.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void delete_ValidBookId_Success() throws Exception {
        mockMvc.perform(
                        delete("/books/{id}", 3L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }
}
