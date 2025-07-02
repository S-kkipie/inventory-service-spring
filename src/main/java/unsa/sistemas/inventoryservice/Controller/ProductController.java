package unsa.sistemas.inventoryservice.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import unsa.sistemas.inventoryservice.Config.Context.UserContext;
import unsa.sistemas.inventoryservice.DTOs.ProductDTO;
import unsa.sistemas.inventoryservice.Models.Product;
import unsa.sistemas.inventoryservice.Models.Role;
import unsa.sistemas.inventoryservice.Services.Rest.ProductService;
import unsa.sistemas.inventoryservice.Utils.ResponseHandler;
import unsa.sistemas.inventoryservice.Utils.ResponseWrapper;


@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "Create a new product")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created successfully", content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content)
    })
    @PostMapping
    public Mono<ResponseEntity<ResponseWrapper<Product>>> createProduct(@RequestBody Mono<ProductDTO> body) {
        return Mono.deferContextual(ctx -> {
            UserContext user = ctx.get(UserContext.KEY);

            if (!user.getRole().equals(Role.ROLE_EMPLOYEE.name())) {
                return Mono.just(ResponseHandler.generateResponse("Unauthorized access", HttpStatus.FORBIDDEN, null));
            }

            return body.flatMap(dto -> productService.createProduct(dto)
                    .map(product -> ResponseHandler.generateResponse("Created successfully", HttpStatus.CREATED, product)));
        });
    }

    @Operation(summary = "Get all products", parameters = {
            @Parameter(name = "page", description = "Page number for pagination", in = ParameterIn.QUERY, example = "0"),
            @Parameter(name = "size", description = "Size of page", in = ParameterIn.QUERY, example = "10"),
            @Parameter(name = "search", description = "Text for search in name", in = ParameterIn.QUERY, example = "laptop")
    })
    @ApiResponse(responseCode = "200", description = "List of products", content = @Content(schema = @Schema(implementation = Product.class)))
    @GetMapping
    public Mono<ResponseEntity<ResponseWrapper<Page<Product>>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search) {
        return productService.getAllProducts(page, size, search)
                .map(products -> ResponseHandler.generateResponse("Products fetched successfully", HttpStatus.OK, products));
    }


    @Operation(summary = "Get a product by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product found", content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ResponseWrapper<Product>>> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(product -> ResponseHandler.generateResponse("Product found", HttpStatus.OK, product))
                .defaultIfEmpty(ResponseHandler.generateResponse("Product not found", HttpStatus.NOT_FOUND, null));
    }

    @Operation(summary = "Update a product by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product updated", content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ResponseWrapper<Product>>> updateProduct(@PathVariable Long id, @RequestBody ProductDTO dto) {
        return Mono.deferContextual(ctx -> {
            UserContext user = ctx.get(UserContext.KEY);

            if (!user.getRole().equals(Role.ROLE_EMPLOYEE.name())) {
                return Mono.just(ResponseHandler.generateResponse("Unauthorized access", HttpStatus.FORBIDDEN, null));
            }

            return productService.updateProduct(id, dto)
                    .map(product -> {
                        if (product == null)
                            return ResponseHandler.generateResponse("Product not found", HttpStatus.NOT_FOUND, null);
                        return ResponseHandler.generateResponse("Product updated successfully", HttpStatus.OK, product);
                    });
        });
    }

    @Operation(summary = "Delete a product by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Product deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ResponseWrapper<Object>>> deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id)
                .thenReturn(ResponseHandler.generateResponse("Product deleted successfully", HttpStatus.NO_CONTENT, null));
    }
}
