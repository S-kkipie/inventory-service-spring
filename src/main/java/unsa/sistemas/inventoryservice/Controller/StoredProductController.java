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
import reactor.core.publisher.Mono;
import unsa.sistemas.inventoryservice.Config.Context.UserContext;
import unsa.sistemas.inventoryservice.DTOs.StoredProductDTO;
import unsa.sistemas.inventoryservice.Models.Role;
import unsa.sistemas.inventoryservice.Models.StoredProduct;
import unsa.sistemas.inventoryservice.Services.Rest.StoredProductService;
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
    public Mono<ResponseEntity<ResponseWrapper<StoredProduct>>> createStoredProduct(@RequestBody Mono<StoredProductDTO> body) {
        return Mono.deferContextual(ctx -> {
            UserContext user = ctx.get(UserContext.KEY);

            if (!user.getRole().equals(Role.ROLE_EMPLOYEE.name())) {
                return Mono.just(ResponseHandler.generateResponse("Unauthorized access", HttpStatus.FORBIDDEN, null));
            }

            return body.flatMap(dto ->
                    storedProductService.createStoredProduct(dto)
                            .map(storedProduct -> ResponseHandler.generateResponse("Created successfully", HttpStatus.CREATED, storedProduct))
            );
        });
    }

    @Operation(summary = "Get all stored products (all stock entries)", parameters = {
            @Parameter(name = "page", description = "Page number for pagination", example = "0"),
            @Parameter(name = "size", description = "Size of page", example = "10"),
            @Parameter(name = "search", description = "Text for search in product name", example = "laptop")
    })
    @ApiResponse(responseCode = "200", description = "List of stored products", content = @Content(schema = @Schema(implementation = StoredProduct.class)))
    @GetMapping
    public Mono<ResponseEntity<ResponseWrapper<Page<StoredProduct>>>> getAllStoredProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search) {
        return storedProductService.getAllStoredProducts(page, size, search).map(storedProducts -> ResponseHandler.generateResponse("Stored products fetched successfully", HttpStatus.OK, storedProducts));
    }

    @Operation(summary = "Get a stored product by product and warehouse IDs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stored product found", content = @Content(schema = @Schema(implementation = StoredProduct.class))),
            @ApiResponse(responseCode = "404", description = "Stored product not found", content = @Content)
    })
    @GetMapping("/product/{productId}/warehouse/{warehouseId}")
    public Mono<ResponseEntity<ResponseWrapper<StoredProduct>>> getStoredProduct(@PathVariable Long productId, @PathVariable Long warehouseId) {
        return storedProductService.getStoredProduct(productId, warehouseId)
                .map(sp -> {
                    if (sp == null) {
                        return ResponseHandler.generateResponse("Stored product not found", HttpStatus.NOT_FOUND, null);
                    }
                    return ResponseHandler.generateResponse("Stored product found", HttpStatus.OK, sp);
                });
    }

    @Operation(summary = "Update stock for a stored product entry")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stored product updated", content = @Content(schema = @Schema(implementation = StoredProduct.class))),
            @ApiResponse(responseCode = "404", description = "Stored product not found", content = @Content)
    })
    @PutMapping
    public Mono<ResponseEntity<ResponseWrapper<StoredProduct>>> updateStoredProduct(@RequestBody StoredProductDTO dto) {
        return Mono.deferContextual(ctx -> {
            UserContext uc = ctx.get(UserContext.KEY);
            if (!uc.getRole().equals(Role.ROLE_EMPLOYEE.name())) {
                return Mono.just(ResponseHandler.generateResponse("Stored product not found", HttpStatus.NOT_FOUND, null));
            }

            return storedProductService.updateStoredProduct(dto)
                    .map(sp -> {
                        if (sp == null) {
                            return ResponseHandler.generateResponse("Stored product not found", HttpStatus.NOT_FOUND, null);
                        }
                        return ResponseHandler.generateResponse("Stored product updated", HttpStatus.OK, sp);
                    });
        });
    }

    @Operation(summary = "Delete a stored product entry by product and warehouse IDs")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Stored product deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Stored product not found", content = @Content)
    })
    @DeleteMapping("/product/{productId}/warehouse/{warehouseId}")
    public Mono<ResponseEntity<ResponseWrapper<Object>>> deleteStoredProduct(@PathVariable Long productId, @PathVariable Long warehouseId) {
        return Mono.deferContextual(ctx -> {
            UserContext uc = ctx.get(UserContext.KEY);
            if (!uc.getRole().equals(Role.ROLE_EMPLOYEE.name())) {
                return Mono.just(ResponseHandler.generateResponse("Stored product not found", HttpStatus.NOT_FOUND, null));
            }
            return storedProductService.deleteStoredProduct(productId, warehouseId).thenReturn(ResponseHandler.generateResponse("Stored product deleted", HttpStatus.NO_CONTENT, null));
        });
    }
}
