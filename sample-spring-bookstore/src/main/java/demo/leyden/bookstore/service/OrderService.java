package demo.leyden.bookstore.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import demo.leyden.bookstore.model.Book;
import demo.leyden.bookstore.model.Order;
import demo.leyden.bookstore.repository.BookRepository;
import demo.leyden.bookstore.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;

    public OrderService(OrderRepository orderRepository, BookRepository bookRepository) {
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> findByCustomer(String customerName) {
        return orderRepository.findByCustomerName(customerName);
    }

    public Order placeOrder(Order order) {
        log.info("Placing order for bookId={} qty={}", order.getBookId(), order.getQuantity());
        Book book = bookRepository.findById(order.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + order.getBookId()));

        if (book.getStock() < order.getQuantity()) {
            throw new IllegalStateException("Insufficient stock for book: " + book.getTitle());
        }

        order.setTotalPrice(book.getPrice() * order.getQuantity());
        book.setStock(book.getStock() - order.getQuantity());
        bookRepository.save(book);

        return orderRepository.save(order);
    }

    public Optional<Order> updateStatus(Long id, Order.OrderStatus status) {
        return orderRepository.findById(id).map(order -> {
            order.setStatus(status);
            return orderRepository.save(order);
        });
    }

    public boolean cancel(Long id) {
        return orderRepository.findById(id).map(order -> {
            order.setStatus(Order.OrderStatus.CANCELLED);
            orderRepository.save(order);
            return true;
        }).orElse(false);
    }
}
