package unsa.sistemas.inventoryservice.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unsa.sistemas.inventoryservice.DTOs.WarehouseDTO;
import unsa.sistemas.inventoryservice.Models.Warehouse;
import unsa.sistemas.inventoryservice.Services.WarehouseService;

@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseService warehouseService;

    @Operation(summary = "Create a new warehouse")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Warehouse created successfully", content = @Content(schema = @Schema(implementation = Warehouse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Warehouse> createWarehouse(@RequestBody WarehouseDTO dto) {
        Warehouse warehouse = warehouseService.createWarehouse(dto);
        return new ResponseEntity<>(warehouse, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all warehouses")
    @ApiResponse(responseCode = "200", description = "List of warehouses", content = @Content(schema = @Schema(implementation = Warehouse.class)))
    @GetMapping
    public ResponseEntity<Page<Warehouse>> getAllWarehouses(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(warehouseService.getAllWarehouses(page));
    }

    @Operation(summary = "Get a warehouse by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Warehouse found", content = @Content(schema = @Schema(implementation = Warehouse.class))),
        @ApiResponse(responseCode = "404", description = "Warehouse not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Warehouse> getWarehouseById(@PathVariable Long id) {
        return warehouseService.getWarehouseById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Update a warehouse by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Warehouse updated", content = @Content(schema = @Schema(implementation = Warehouse.class))),
        @ApiResponse(responseCode = "404", description = "Warehouse not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Warehouse> updateWarehouse(@PathVariable Long id, @RequestBody WarehouseDTO dto) {
        return warehouseService.updateWarehouse(id, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Delete a warehouse by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Warehouse deleted", content = @Content),
        @ApiResponse(responseCode = "404", description = "Warehouse not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
        if (warehouseService.getWarehouseById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }
}

