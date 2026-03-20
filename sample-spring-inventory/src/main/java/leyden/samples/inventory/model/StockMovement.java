package leyden.samples.inventory.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash("stock_movements")
public class StockMovement implements Serializable {

    @Id
    private String id;

    @Indexed
    private String productId;

    private MovementType type;

    private Integer quantity;

    private String reason;

    private LocalDateTime movedAt;

    public enum MovementType {
        IN, OUT, ADJUSTMENT
    }

    public StockMovement() {}

    public StockMovement(String productId, MovementType type, Integer quantity, String reason) {
        this.productId = productId;
        this.type = type;
        this.quantity = quantity;
        this.reason = reason;
        this.movedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public MovementType getType() { return type; }
    public void setType(MovementType type) { this.type = type; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getMovedAt() { return movedAt; }
    public void setMovedAt(LocalDateTime movedAt) { this.movedAt = movedAt; }
}
