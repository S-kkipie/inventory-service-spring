package unsa.sistemas.inventoryservice.Services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import unsa.sistemas.inventoryservice.DTOs.StoredProductDTO;
import unsa.sistemas.inventoryservice.Models.StoredProduct;
import unsa.sistemas.inventoryservice.Models.Product;
import unsa.sistemas.inventoryservice.Models.Warehouse;
import unsa.sistemas.inventoryservice.Repositories.StoredProductRepository;
import unsa.sistemas.inventoryservice.Repositories.ProductRepository;
import unsa.sistemas.inventoryservice.Repositories.WarehouseRepository;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StoredProductService {
    private final StoredProductRepository storedProductRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public StoredProduct createStoredProduct(StoredProductDTO dto) {
        StoredProduct storedProduct = new StoredProduct();
        productRepository.findById(dto.getProductId()).ifPresent(storedProduct::setProduct);
        warehouseRepository.findById(dto.getWarehouseId()).ifPresent(storedProduct::setWarehouse);
        storedProduct.setStock(dto.getStock());
        return storedProductRepository.save(storedProduct);
    }

    public List<StoredProduct> getAllStoredProducts() {
        return storedProductRepository.findAll();
    }

    public Optional<StoredProduct> getStoredProduct(Long productId, Long warehouseId) {
        return storedProductRepository.findByProductIdAndWarehouseId(productId, warehouseId);
    }

    public Optional<StoredProduct> updateStoredProduct(StoredProductDTO dto) {
        return storedProductRepository.findByProductIdAndWarehouseId(dto.getProductId(), dto.getWarehouseId())
            .map(storedProduct -> {
                storedProduct.setStock(dto.getStock());
                return storedProductRepository.save(storedProduct);
            });
    }

    public void deleteStoredProduct(Long productId, Long warehouseId) {
        storedProductRepository.deleteByProductIdAndWarehouseId(productId, warehouseId);
    }
}

