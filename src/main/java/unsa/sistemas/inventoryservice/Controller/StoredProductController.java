package unsa.sistemas.inventoryservice.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unsa.sistemas.inventoryservice.Config.Context.UserContext;
import unsa.sistemas.inventoryservice.Config.Context.UserContextHolder;
import unsa.sistemas.inventoryservice.DTOs.StoredProductDTO;
import unsa.sistemas.inventoryservice.Models.Role;
import unsa.sistemas.inventoryservice.Models.StoredProduct;
import unsa.sistemas.inventoryservice.Services.StoredProductService;
import unsa.sistemas.inventoryservice.Utils.ResponseHandler;
import unsa.sistemas.inventoryservice.Utils.ResponseWrapper;

@RestController
@RequestMapping("/stored-products")
@RequiredArgsConstructor
public class StoredProductController {
    private final StoredProductService storedProductService;

    @Operation(summary = "Create a new stored product entry (stock in warehouse)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Stored product created successfully", content = @Content(schema = @Schema(implementation = StoredProduct.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ResponseWrapper<StoredProduct>> createStoredProduct(@RequestBody StoredProductDTO dto) {
        UserContext user = UserContextHolder.get();

        if (!user.getRole().equals(Role.ROLE_EMPLOYEE.name())) {
            return ResponseHandler.generateResponse("Unauthorized access", HttpStatus.FORBIDDEN, null);
        }

        StoredProduct storedProduct = storedProductService.createStoredProduct(dto);
        return ResponseHandler.generateResponse("Created successfully", HttpStatus.CREATED, storedProduct);
    }

    @Operation(summary = "Get all stored products (all stock entries)", parameters = {
            @Parameter(name = "page", description = "Page number for pagination", example = "0"),
            @Parameter(name = "size", description = "Size of page", example = "10"),
            @Parameter(name = "search", description = "Text for search in product name", example = "laptop")
    })
    @ApiResponse(responseCode = "200", description = "List of stored products", content = @Content(schema = @Schema(implementation = StoredProduct.class)))
    @GetMapping
    public ResponseEntity<Page<StoredProduct>> getAllStoredProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search) {
        return ResponseEntity.ok(storedProductService.getAllStoredProducts(page, size, search));
    }

    @Operation(summary = "Get a stored product by product and warehouse IDs")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stored product found", content = @Content(schema = @Schema(implementation = StoredProduct.class))),
        @ApiResponse(responseCode = "404", description = "Stored product not found", content = @Content)
    })
    @GetMapping("/product/{productId}/warehouse/{warehouseId}")
    public ResponseEntity<StoredProduct> getStoredProduct(@PathVariable Long productId, @PathVariable Long warehouseId) {
        return storedProductService.getStoredProduct(productId, warehouseId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Update stock for a stored product entry")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stored product updated", content = @Content(schema = @Schema(implementation = StoredProduct.class))),
        @ApiResponse(responseCode = "404", description = "Stored product not found", content = @Content)
    })
    @PutMapping
    public ResponseEntity<StoredProduct> updateStoredProduct(@RequestBody StoredProductDTO dto) {
        return storedProductService.updateStoredProduct(dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Delete a stored product entry by product and warehouse IDs")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Stored product deleted", content = @Content),
        @ApiResponse(responseCode = "404", description = "Stored product not found", content = @Content)
    })
    @DeleteMapping("/product/{productId}/warehouse/{warehouseId}")
    public ResponseEntity<Void> deleteStoredProduct(@PathVariable Long productId, @PathVariable Long warehouseId) {
        if (storedProductService.getStoredProduct(productId, warehouseId).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        storedProductService.deleteStoredProduct(productId, warehouseId);
        return ResponseEntity.noContent().build();
    }
}
