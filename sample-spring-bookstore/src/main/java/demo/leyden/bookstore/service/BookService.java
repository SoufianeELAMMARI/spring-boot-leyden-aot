package demo.leyden.bookstore.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import demo.leyden.bookstore.model.Book;
import demo.leyden.bookstore.repository.BookRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll() {
        log.info("Fetching all books");
        return bookRepository.findAll();
    }

    public Optional<Book> findById(Long id) {
        log.info("Fetching book id={}", id);
        return bookRepository.findById(id);
    }

    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    public List<Book> findByGenre(String genre) {
        return bookRepository.findByGenre(genre);
    }

    public List<Book> findInStock() {
        return bookRepository.findByStockGreaterThan(0);
    }

    public Book save(Book book) {
        log.info("Saving book: {}", book.getTitle());
        return bookRepository.save(book);
    }

    public Optional<Book> update(Long id, Book updated) {
        return bookRepository.findById(id).map(existing -> {
            existing.setTitle(updated.getTitle());
            existing.setAuthor(updated.getAuthor());
            existing.setPrice(updated.getPrice());
            existing.setGenre(updated.getGenre());
            existing.setStock(updated.getStock());
            return bookRepository.save(existing);
        });
    }

    public boolean delete(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
