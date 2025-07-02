package unsa.sistemas.inventoryservice.Services.Tooling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import unsa.sistemas.inventoryservice.DTOs.StoredProductDTO;
import unsa.sistemas.inventoryservice.Models.StoredProduct;
import unsa.sistemas.inventoryservice.Repositories.StoredProductRepository;
import unsa.sistemas.inventoryservice.Repositories.ProductRepository;
import unsa.sistemas.inventoryservice.Repositories.WarehouseRepository;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoredProductToolService {
    private final StoredProductRepository storedProductRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    @Tool(description = "Create a new stored product entry (stock in warehouse).")
    public StoredProduct createStoredProduct(
            @ToolParam(description = "Product ID") Long productId,
            @ToolParam(description = "Warehouse ID") Long warehouseId,
            @ToolParam(description = "Stock quantity") Long stock
    ) {
        StoredProduct storedProduct = StoredProduct.builder()
                .product(productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found")))
                .warehouse(warehouseRepository.findById(warehouseId).orElseThrow(() -> new IllegalArgumentException("Warehouse not found")))
                .stock(stock)
                .build();
        log.info("Creating stored product: {}", storedProduct);
        return storedProductRepository.save(storedProduct);
    }

    @Tool(description = "Find stored products by product name, pageNumber and size.")
    public Page<StoredProduct> findStoredProducts(
            @ToolParam(description = "Page number, default is 0", required = false) int pageNumber,
            @ToolParam(description = "Page size, default is 10", required = false) int size,
            @ToolParam(description = "Text to search in product name", required = false) String text
    ) {
        Pageable pageable = PageRequest.of(pageNumber, size);
        log.info("Finding stored products with text {} page {} size {}", text, pageNumber, size);
        return storedProductRepository.findByProductNameContainingIgnoreCase(text, pageable);
    }

    @Tool(description = "Get a stored product by product and warehouse IDs.")
    public Optional<StoredProduct> getStoredProduct(Long productId, Long warehouseId) {
        log.info("Finding stored product for productId {} and warehouseId {}", productId, warehouseId);
        return storedProductRepository.findByProductIdAndWarehouseId(productId, warehouseId);
    }

    @Tool(description = "Update a stored product entry.")
    public StoredProduct updateStoredProduct(Long productId, Long warehouseId, Long stock) {
        StoredProduct storedProduct = storedProductRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Stored product not found"));
        storedProduct.setStock(stock);
        log.info("Updating stored product: {}", storedProduct);
        return storedProductRepository.save(storedProduct);
    }

    @Tool(description = "Delete a stored product by product and warehouse IDs.")
    public void deleteStoredProduct(Long productId, Long warehouseId) {
        log.info("Deleting stored product for productId {} and warehouseId {}", productId, warehouseId);
        storedProductRepository.deleteByProductIdAndWarehouseId(productId, warehouseId);
    }
}

