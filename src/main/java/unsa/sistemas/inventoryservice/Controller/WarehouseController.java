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
import unsa.sistemas.inventoryservice.DTOs.WarehouseDTO;
import unsa.sistemas.inventoryservice.Models.Warehouse;
import unsa.sistemas.inventoryservice.Services.Rest.WarehouseService;
import unsa.sistemas.inventoryservice.Utils.ResponseHandler;
import unsa.sistemas.inventoryservice.Utils.ResponseWrapper;

import javax.validation.Valid;

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
    public ResponseEntity<ResponseWrapper<Warehouse>> createWarehouse(@Valid @RequestBody WarehouseDTO dto) {
        return ResponseHandler.generateResponse("Warehouse created successfully", HttpStatus.CREATED, warehouseService.createWarehouse(dto));
    }

    @Operation(summary = "Find warehouses or get all", parameters = {
            @Parameter(name = "page", description = "Page number for pagination", in = ParameterIn.QUERY, example = "1"),
            @Parameter(name = "size", description = "Size of page", in = ParameterIn.QUERY, example = "10"),
            @Parameter(name = "text", description = "Text for search in name", in = ParameterIn.QUERY, example = "pepito")
    })
    @ApiResponse(responseCode = "200", description = "List of warehouses", content = @Content(schema = @Schema(implementation = Warehouse.class)))
    @GetMapping
    public ResponseEntity<ResponseWrapper<Page<Warehouse>>> getAllWarehouses(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "") String search) {
        return ResponseHandler.generateResponse("Warehouses fetched successfully", HttpStatus.OK, warehouseService.getAllWarehouses(page, size, search));
    }

    @Operation(summary = "Get a warehouse by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Warehouse found", content = @Content(schema = @Schema(implementation = Warehouse.class))),
            @ApiResponse(responseCode = "404", description = "Warehouse not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Warehouse>> getWarehouseById(@PathVariable Long id) {
        return warehouseService.getWarehouseById(id)
                .map(warehouse -> ResponseHandler.generateResponse("Warehouse found", HttpStatus.OK, warehouse))
                .orElseGet(() -> ResponseHandler.generateResponse("Warehouse not found", HttpStatus.NOT_FOUND, null));
    }

    @Operation(summary = "Update a warehouse by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Warehouse updated", content = @Content(schema = @Schema(implementation = Warehouse.class))),
            @ApiResponse(responseCode = "404", description = "Warehouse not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Warehouse>> updateWarehouse(@PathVariable Long id, @RequestBody WarehouseDTO dto) {
        return warehouseService.updateWarehouse(id, dto)
                .map(warehouse -> ResponseHandler.generateResponse("Warehouse updated", HttpStatus.OK, warehouse))
                .orElseGet(() -> ResponseHandler.generateResponse("Warehouse not found", HttpStatus.NOT_FOUND, null));
    }

    @Operation(summary = "Delete a warehouse by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Warehouse deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Warehouse not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Object>> deleteWarehouse(@PathVariable Long id) {
        if (warehouseService.getWarehouseById(id).isEmpty()) {
            return ResponseHandler.generateResponse("Warehouse not found", HttpStatus.NOT_FOUND, null);
        }
        warehouseService.deleteWarehouse(id);
        return ResponseHandler.generateResponse("Warehouse deleted", HttpStatus.NO_CONTENT, null);
    }
}
