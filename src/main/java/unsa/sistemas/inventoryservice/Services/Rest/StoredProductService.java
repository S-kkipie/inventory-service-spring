package unsa.sistemas.inventoryservice.Services.Rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import unsa.sistemas.inventoryservice.DTOs.StoredProductDTO;
import unsa.sistemas.inventoryservice.Models.StoredProduct;
import unsa.sistemas.inventoryservice.Repositories.StoredProductRepository;
import unsa.sistemas.inventoryservice.Repositories.ProductRepository;
import unsa.sistemas.inventoryservice.Repositories.WarehouseRepository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@AllArgsConstructor
public class StoredProductService {
    private final StoredProductRepository storedProductRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public Mono<StoredProduct> createStoredProduct(StoredProductDTO dto) {
        return Mono.fromCallable(() -> {
                    StoredProduct storedProduct = StoredProduct.builder()
                            .product(productRepository.findById(dto.getProductId())
                                    .orElseThrow(() -> new IllegalArgumentException("Product not found")))
                            .warehouse(warehouseRepository.findById(dto.getWarehouseId())
                                    .orElseThrow(() -> new IllegalArgumentException("Warehouse not found")))
                            .stock(dto.getStock())
                            .build();
                    return storedProductRepository.save(storedProduct);
                }
        ).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Page<StoredProduct>> getAllStoredProducts(int pageNumber, int size, String text) {
        return Mono.fromCallable(() -> {
            Pageable pageable = PageRequest.of(pageNumber, size);
            return storedProductRepository.findByProductNameContainingIgnoreCase(text, pageable);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<StoredProduct> getStoredProduct(Long productId, Long warehouseId) {
        return Mono.fromCallable(() -> storedProductRepository.findByProductIdAndWarehouseId(productId, warehouseId).orElse(null))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<StoredProduct> updateStoredProduct(StoredProductDTO dto) {
        return Mono.fromCallable(() ->
                storedProductRepository.findByProductIdAndWarehouseId(dto.getProductId(), dto.getWarehouseId())
                        .map(storedProduct -> {
                            storedProduct.setStock(dto.getStock());
                            return storedProductRepository.save(storedProduct);
                        }).orElse(null)
        ).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteStoredProduct(Long productId, Long warehouseId) {
        return Mono.fromRunnable(() -> storedProductRepository.deleteByProductIdAndWarehouseId(productId, warehouseId))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
