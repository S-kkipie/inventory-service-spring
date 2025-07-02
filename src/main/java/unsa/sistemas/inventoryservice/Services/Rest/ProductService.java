package unsa.sistemas.inventoryservice.Services.Rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import unsa.sistemas.inventoryservice.DTOs.ProductDTO;
import unsa.sistemas.inventoryservice.Models.Product;
import unsa.sistemas.inventoryservice.Repositories.ProductRepository;


@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Mono<Product> createProduct(ProductDTO dto) {
        return Mono.fromCallable(() -> {
            Product product = new Product();
            product.setName(dto.getName());
            product.setDescription(dto.getDescription());
            product.setPrice(dto.getPrice());
            product.setImageUrl(dto.getImageUrl());
            return productRepository.save(product);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Page<Product>> getAllProducts(int pageNumber, int size, String text) {
        return Mono.fromCallable(() -> {
            Pageable pageable = PageRequest.of(pageNumber, size);
            return productRepository.findByNameContainingIgnoreCase(text, pageable);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Product> getProductById(Long id) {
        return Mono.fromCallable(() -> productRepository.findById(id).orElse(null))
                   .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Product> updateProduct(Long id, ProductDTO dto) {
        return Mono.fromCallable(() -> {
            Product product = productRepository.findById(id).orElse(null);
            if (product == null) return null;
            product.setName(dto.getName());
            product.setDescription(dto.getDescription());
            product.setPrice(dto.getPrice());
            product.setImageUrl(dto.getImageUrl());
            return productRepository.save(product);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteProduct(Long id) {
        return Mono.fromRunnable(() -> productRepository.findById(id).ifPresent(product -> productRepository.deleteById(product.getId()))).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
