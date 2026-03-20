package leyden.samples.inventory.repository;

import org.springframework.data.repository.CrudRepository;
import leyden.samples.inventory.model.StockMovement;

import java.util.List;

public interface StockMovementRepository extends CrudRepository<StockMovement, String> {

    List<StockMovement> findByProductId(String productId);
}
