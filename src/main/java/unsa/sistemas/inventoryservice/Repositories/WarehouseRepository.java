package unsa.sistemas.inventoryservice.Repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unsa.sistemas.inventoryservice.Models.Warehouse;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Page<Warehouse> findByNameContainingIgnoreCase(String name, Pageable page);
}
