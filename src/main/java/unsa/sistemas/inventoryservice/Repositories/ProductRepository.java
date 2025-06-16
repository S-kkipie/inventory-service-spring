package unsa.sistemas.inventoryservice.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unsa.sistemas.inventoryservice.Models.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
