package mate.academy.springboot.repository.book;

import java.util.List;
import mate.academy.springboot.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @Query("FROM Book b INNER JOIN FETCH b.categories c WHERE c.id = :category_id")
    List<Book> findAllByCategoriesId(@Param("category_id") Long categoryId);
}
