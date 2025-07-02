package unsa.sistemas.inventoryservice.Services.Tooling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import unsa.sistemas.inventoryservice.DTOs.ProductDTO;
import unsa.sistemas.inventoryservice.Models.Product;
import unsa.sistemas.inventoryservice.Repositories.ProductRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductToolService {
    private final ProductRepository productRepository;

    @Tool(description = "Create a new product with the given details.")
    public Product createProduct(@ToolParam(description = "Name of the product") String name,
                                 @ToolParam(description = "Description of the product") String description,
                                 @ToolParam(description = "Price of the product") Long price,
                                 @ToolParam(description = "Image URL of the product") String imageUrl) {
        Product product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .imageUrl(imageUrl)
                .createdAt(LocalDateTime.now())
                .build();
        log.info("Creating product with details {}", product);
        return productRepository.save(product);
    }

    @Tool(description = "Find products matching the given text, pageNumber and size.")
    public Page<Product> findProducts(
            @ToolParam(description = "Page number, default is 0", required = false) int pageNumber,
            @ToolParam(description = "Page size, default is 10", required = false) int size,
            @ToolParam(description = "Text to search in product names", required = false) String text
    ) {
        log.info("Finding products with text {} and pageNumber {} and size {}", text, pageNumber, size);
        Pageable pageable = PageRequest.of(pageNumber, size);
        return productRepository.findByNameContainingIgnoreCase(text, pageable);
    }

    @Tool(description = "Get a product by the given id")
    public Optional<Product> getProductById(Long id) {
        log.info("Finding product with id {}", id);
        return productRepository.findById(id);
    }

    @Tool(description = "Update a product by the given id and the given DTO")
    public Product updateProduct(Long id, ProductDTO dto) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setImageUrl(dto.getImageUrl());
        log.info("Updating product with id {} and details {}", id, product);
        return productRepository.save(product);
    }

    @Tool(description = "Delete a product by te given id")
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        productRepository.deleteById(product.getId());
        log.info("Deleting product with id {}", id);
    }
}
