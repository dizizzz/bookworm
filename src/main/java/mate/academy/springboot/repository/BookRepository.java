package mate.academy.springboot.repository;

import java.math.BigDecimal;
import mate.academy.springboot.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("UPDATE Book b SET b.title = :title,"
            + " b.author = :author,"
            + " b.isbn = :isbn,"
            + " b.price = :price,"
            + " b.description = :description,"
            + " b.coverImage = :coverImage WHERE b.id = :id")
    void updateBook(Long id,
                    String title,
                    String author,
                    String isbn,
                    BigDecimal price,
                    String description,
                    String coverImage);
}

