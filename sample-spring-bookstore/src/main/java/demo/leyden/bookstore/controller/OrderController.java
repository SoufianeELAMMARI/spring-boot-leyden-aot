package demo.leyden.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import demo.leyden.bookstore.model.Order;
import demo.leyden.bookstore.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Order management")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "Get all orders")
    public List<Order> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<Order> findById(@PathVariable Long id) {
        return orderService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerName}")
    @Operation(summary = "Get orders by customer name")
    public List<Order> findByCustomer(@PathVariable String customerName) {
        return orderService.findByCustomer(customerName);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Place a new order")
    public ResponseEntity<?> placeOrder(@Valid @RequestBody Order order) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(order));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<Order> updateStatus(@PathVariable Long id,
                                               @RequestParam Order.OrderStatus status) {
        return orderService.updateStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel an order")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        return orderService.cancel(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
