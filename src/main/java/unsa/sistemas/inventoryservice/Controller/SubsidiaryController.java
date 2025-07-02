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
import reactor.core.scheduler.Schedulers;
import unsa.sistemas.inventoryservice.Config.Context.UserContext;
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
    public Mono<ResponseEntity<ResponseWrapper<Object>>> createSubsidiary(@RequestBody SubsidiaryDTO body) {
        return Mono.deferContextual(ctx -> {
            UserContext context = ctx.get(UserContext.KEY);
            Role role = Role.valueOf(context.getRole());

            if (role != Role.ROLE_ADMIN && role != Role.ROLE_PRINCIPAL_ADMIN) {
                return Mono.just(ResponseHandler.generateResponse("Unauthorized access", HttpStatus.FORBIDDEN, null));
            }

            return subsidiaryService.createSubsidiary(body)
                    .subscribeOn(Schedulers.boundedElastic())
                    .map(sub -> ResponseHandler.generateResponse(
                            "Created successfully", HttpStatus.CREATED, sub));

        });
    }

    @Operation(summary = "Get all subsidiaries", parameters = {
            @Parameter(name = "page", description = "Page number for pagination", example = "0"),
            @Parameter(name = "size", description = "Size of page", example = "10"),
            @Parameter(name = "search", description = "Text for search in name", example = "Sucursal Lima")
    })
    @ApiResponse(responseCode = "200", description = "List of subsidiaries", content = @Content(schema = @Schema(implementation = Subsidiary.class)))
    @GetMapping
    public Mono<ResponseEntity<ResponseWrapper<Page<Subsidiary>>>> getAllSubsidiaries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search) {
        return subsidiaryService.getAllSubsidiaries(page, size, search)
                .map(result -> ResponseHandler.generateResponse("Subsidiaries fetched successfully", HttpStatus.OK, result));
    }

    @Operation(summary = "Get a subsidiary by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subsidiary found", content = @Content(schema = @Schema(implementation = Subsidiary.class))),
            @ApiResponse(responseCode = "404", description = "Subsidiary not found", content = @Content)
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ResponseWrapper<Subsidiary>>> getSubsidiaryById(@PathVariable Long id) {
        return subsidiaryService.getSubsidiaryById(id)
                .map(sub -> {
                            if (sub == null) {
                                return ResponseHandler.generateResponse("Subsidiary not found", HttpStatus.NOT_FOUND, null);
                            }
                            return ResponseHandler.generateResponse("Subsidiary found", HttpStatus.OK, sub);
                        }
                );
    }

    @Operation(summary = "Update a subsidiary by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subsidiary updated", content = @Content(schema = @Schema(implementation = Subsidiary.class))),
            @ApiResponse(responseCode = "404", description = "Subsidiary not found", content = @Content)
    })
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ResponseWrapper<Subsidiary>>> updateSubsidiary(@PathVariable Long id, @RequestBody Mono<SubsidiaryDTO> body) {
        return Mono.deferContextual(ctx -> {
            UserContext context = ctx.get(UserContext.KEY);
            Role role = Role.valueOf(context.getRole());
            if (role != Role.ROLE_ADMIN) {
                return Mono.just(ResponseHandler.generateResponse("Unauthorized access", HttpStatus.FORBIDDEN, null));
            }
            return body.flatMap(dto ->
                    subsidiaryService.updateSubsidiary(id, dto)
                            .map(sub -> {
                                if (sub == null) {
                                    return ResponseHandler.generateResponse("Subsidiary not found", HttpStatus.NOT_FOUND, null);
                                }
                                return ResponseHandler.generateResponse("Subsidiary found", HttpStatus.OK, sub);
                            })
            );
        });
    }

    @Operation(summary = "Delete a subsidiary by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Subsidiary deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Subsidiary not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ResponseWrapper<Object>>> deleteSubsidiary(@PathVariable Long id) {
        return Mono.deferContextual(ctx -> {
            UserContext uc = ctx.get(UserContext.KEY);
            Role role = Role.valueOf(uc.getRole());

            if (role != Role.ROLE_ADMIN) {
                return Mono.just(ResponseHandler.generateResponse(
                        "Unauthorized access", HttpStatus.FORBIDDEN, null));
            }

            return subsidiaryService.deleteSubsidiary(id)
                    .then(Mono.just(ResponseHandler.generateResponse(
                            "Subsidiary deleted successfully",
                            HttpStatus.OK, null)));
        });
    }
}
