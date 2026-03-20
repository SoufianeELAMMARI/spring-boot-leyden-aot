package leyden.samples.inventory.repository;

import org.springframework.data.repository.CrudRepository;
import leyden.samples.inventory.model.Product;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, String> {

    List<Product> findByCategory(String category);

    List<Product> findByName(String name);
}
