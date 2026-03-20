package demo.leyden.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import demo.leyden.bookstore.model.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerName(String customerName);

    List<Order> findByBookId(Long bookId);

    List<Order> findByStatus(Order.OrderStatus status);
}
