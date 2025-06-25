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
import unsa.sistemas.inventoryservice.DTOs.SubsidiaryDTO;
import unsa.sistemas.inventoryservice.Models.Role;
import unsa.sistemas.inventoryservice.Models.Subsidiary;
import unsa.sistemas.inventoryservice.Services.Rest.SubsidiaryService;
import unsa.sistemas.inventoryservice.Utils.ResponseHandler;
import unsa.sistemas.inventoryservice.Utils.ResponseWrapper;

@RestController
@RequestMapping("/subsidiaries")
@RequiredArgsConstructor
public class SubsidiaryController {
    private final SubsidiaryService subsidiaryService;

    @Operation(summary = "Create a new subsidiary")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Subsidiary created successfully", content = @Content(schema = @Schema(implementation = Subsidiary.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ResponseWrapper<Object>> createSubsidiary(@RequestBody SubsidiaryDTO dto) {
        UserContext context = UserContextHolder.get();
        Role role = Role.valueOf(context.getRole());

        if (role != Role.ROLE_PRINCIPAL_ADMIN) {
            return ResponseHandler.generateResponse("Unauthorized access", HttpStatus.FORBIDDEN, null);
        }
        Subsidiary subsidiary = subsidiaryService.createSubsidiary(dto);
        return ResponseHandler.generateResponse("Created successfully", HttpStatus.CREATED, subsidiary);

    }

    @Operation(summary = "Get all subsidiaries", parameters = {
            @Parameter(name = "page", description = "Page number for pagination", example = "0"),
            @Parameter(name = "size", description = "Size of page", example = "10"),
            @Parameter(name = "search", description = "Text for search in name", example = "Sucursal Lima")
    })
    @ApiResponse(responseCode = "200", description = "List of subsidiaries", content = @Content(schema = @Schema(implementation = Subsidiary.class)))
    @GetMapping
    public ResponseEntity<ResponseWrapper<Page<Subsidiary>>> getAllSubsidiaries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search) {
        return ResponseHandler.generateResponse("Subsidiaries fetched successfully", HttpStatus.OK, subsidiaryService.getAllSubsidiaries(page, size, search));
    }

    @Operation(summary = "Get a subsidiary by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Subsidiary found", content = @Content(schema = @Schema(implementation = Subsidiary.class))),
        @ApiResponse(responseCode = "404", description = "Subsidiary not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Subsidiary>> getSubsidiaryById(@PathVariable Long id) {
        return subsidiaryService.getSubsidiaryById(id)
                .map(subsidiary -> ResponseHandler.generateResponse("Subsidiary found", HttpStatus.OK, subsidiary))
                .orElseGet(() -> ResponseHandler.generateResponse("Subsidiary not found", HttpStatus.NOT_FOUND, null));
    }

    @Operation(summary = "Update a subsidiary by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Subsidiary updated", content = @Content(schema = @Schema(implementation = Subsidiary.class))),
        @ApiResponse(responseCode = "404", description = "Subsidiary not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Subsidiary>> updateSubsidiary(@PathVariable Long id, @RequestBody SubsidiaryDTO dto) {
        return subsidiaryService.updateSubsidiary(id, dto)
                .map(subsidiary -> ResponseHandler.generateResponse("Subsidiary updated", HttpStatus.OK, subsidiary))
                .orElseGet(() -> ResponseHandler.generateResponse("Subsidiary not found", HttpStatus.NOT_FOUND, null));
    }

    @Operation(summary = "Delete a subsidiary by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Subsidiary deleted", content = @Content),
        @ApiResponse(responseCode = "404", description = "Subsidiary not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Object>> deleteSubsidiary(@PathVariable Long id) {
        if (subsidiaryService.getSubsidiaryById(id).isEmpty()) {
            return ResponseHandler.generateResponse("Subsidiary not found", HttpStatus.NOT_FOUND, null);
        }
        subsidiaryService.deleteSubsidiary(id);
        return ResponseHandler.generateResponse("Subsidiary deleted", HttpStatus.NO_CONTENT, null);
    }
}
