package leyden.samples.inventory.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import leyden.samples.inventory.model.Product;
import leyden.samples.inventory.model.StockMovement;
import leyden.samples.inventory.repository.ProductRepository;
import leyden.samples.inventory.repository.StockMovementRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;

    public ProductService(ProductRepository productRepository,
                          StockMovementRepository stockMovementRepository) {
        this.productRepository = productRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    public List<Product> findAll() {
        log.info("Fetching all products from Redis");
        List<Product> products = new ArrayList<>();
        productRepository.findAll().forEach(products::add);
        return products;
    }

    public Optional<Product> findById(String id) {
        return productRepository.findById(id);
    }

    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public Product save(Product product) {
        if (product.getId() == null || product.getId().isBlank()) {
            product.setId(UUID.randomUUID().toString());
        }
        log.info("Saving product id={} name={}", product.getId(), product.getName());
        Product saved = productRepository.save(product);

        StockMovement movement = new StockMovement(
                saved.getId(),
                StockMovement.MovementType.IN,
                saved.getQuantity(),
                "Initial stock"
        );
        movement.setId(UUID.randomUUID().toString());
        stockMovementRepository.save(movement);

        return saved;
    }

    public Optional<Product> update(String id, Product updated) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setCategory(updated.getCategory());
            existing.setPrice(updated.getPrice());
            existing.setDescription(updated.getDescription());
            return productRepository.save(existing);
        });
    }

    public Optional<Product> adjustStock(String id, int delta, String reason) {
        return productRepository.findById(id).map(product -> {
            int newQty = product.getQuantity() + delta;
            if (newQty < 0) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName());
            }
            product.setQuantity(newQty);
            productRepository.save(product);

            StockMovement movement = new StockMovement(
                    id,
                    delta > 0 ? StockMovement.MovementType.IN : StockMovement.MovementType.OUT,
                    Math.abs(delta),
                    reason
            );
            movement.setId(UUID.randomUUID().toString());
            stockMovementRepository.save(movement);

            log.info("Stock adjusted for product={} delta={} newQty={}", id, delta, newQty);
            return product;
        });
    }

    public List<StockMovement> getMovements(String productId) {
        return stockMovementRepository.findByProductId(productId);
    }

    public boolean delete(String id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
