package demo.leyden.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import demo.leyden.bookstore.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByAuthor(String author);

    List<Book> findByGenre(String genre);

    List<Book> findByStockGreaterThan(Integer stock);
}
