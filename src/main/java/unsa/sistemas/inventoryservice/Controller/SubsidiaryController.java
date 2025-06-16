package unsa.sistemas.inventoryservice.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unsa.sistemas.inventoryservice.DTOs.SubsidiaryDTO;
import unsa.sistemas.inventoryservice.Models.Subsidiary;
import unsa.sistemas.inventoryservice.Services.SubsidiaryService;
import java.util.List;

@RestController
@RequestMapping("/api/subsidiaries")
@RequiredArgsConstructor
public class SubsidiaryController {
    private final SubsidiaryService subsidiaryService;

    @Operation(summary = "Create a new subsidiary")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Subsidiary created successfully", content = @Content(schema = @Schema(implementation = Subsidiary.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Subsidiary> createSubsidiary(@RequestBody SubsidiaryDTO dto) {
        Subsidiary subsidiary = subsidiaryService.createSubsidiary(dto);
        return new ResponseEntity<>(subsidiary, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all subsidiaries")
    @ApiResponse(responseCode = "200", description = "List of subsidiaries", content = @Content(schema = @Schema(implementation = Subsidiary.class)))
    @GetMapping
    public ResponseEntity<List<Subsidiary>> getAllSubsidiaries() {
        return ResponseEntity.ok(subsidiaryService.getAllSubsidiaries());
    }

    @Operation(summary = "Get a subsidiary by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Subsidiary found", content = @Content(schema = @Schema(implementation = Subsidiary.class))),
        @ApiResponse(responseCode = "404", description = "Subsidiary not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Subsidiary> getSubsidiaryById(@PathVariable Long id) {
        return subsidiaryService.getSubsidiaryById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Update a subsidiary by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Subsidiary updated", content = @Content(schema = @Schema(implementation = Subsidiary.class))),
        @ApiResponse(responseCode = "404", description = "Subsidiary not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Subsidiary> updateSubsidiary(@PathVariable Long id, @RequestBody SubsidiaryDTO dto) {
        return subsidiaryService.updateSubsidiary(id, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Delete a subsidiary by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Subsidiary deleted", content = @Content),
        @ApiResponse(responseCode = "404", description = "Subsidiary not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubsidiary(@PathVariable Long id) {
        if (subsidiaryService.getSubsidiaryById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        subsidiaryService.deleteSubsidiary(id);
        return ResponseEntity.noContent().build();
    }
}

