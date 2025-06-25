package unsa.sistemas.inventoryservice.Repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unsa.sistemas.inventoryservice.Models.Keys.StoredProductKey;
import unsa.sistemas.inventoryservice.Models.StoredProduct;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoredProductRepository extends JpaRepository<StoredProduct, StoredProductKey> {
    List<StoredProduct> findByWarehouse_Id(Long warehouseId);
    List<StoredProduct> findByProduct_Id(Long productId);
    Optional<StoredProduct> findByProductIdAndWarehouseId(Long productId, Long warehouseId);
    void deleteByProductIdAndWarehouseId(Long productId, Long warehouseId);

    @Query("SELECT sp FROM StoredProduct sp WHERE LOWER(sp.product.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<StoredProduct> findByProductNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
}