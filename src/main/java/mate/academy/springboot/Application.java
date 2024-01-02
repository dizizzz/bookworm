package mate.academy.springboot;

import java.math.BigDecimal;
import mate.academy.springboot.model.Book;
import mate.academy.springboot.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setTitle("The Body in the Library");
            book.setAuthor("Agatha Christie");
            book.setIsbn("978-1-234567-89-0");
            book.setPrice(BigDecimal.valueOf(350));
            book.setDescription("The novel concerns the murders of two girls.");
            book.setCoverImage("CoverImage.jpg");
            bookService.save(book);
            System.out.println(bookService.findAll());
        };
    }
}
