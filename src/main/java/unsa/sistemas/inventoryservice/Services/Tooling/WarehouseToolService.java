package unsa.sistemas.inventoryservice.Services.Tooling;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import unsa.sistemas.inventoryservice.DTOs.WarehouseDTO;
import unsa.sistemas.inventoryservice.Models.Subsidiary;
import unsa.sistemas.inventoryservice.Models.Warehouse;
import unsa.sistemas.inventoryservice.Repositories.SubsidiaryRepository;
import unsa.sistemas.inventoryservice.Repositories.WarehouseRepository;

import java.util.Optional;

@Slf4j
@Validated
@RequiredArgsConstructor
@Service
public class WarehouseToolService {

    private final WarehouseRepository warehouseRepository;
    private final SubsidiaryRepository subsidiaryRepository;

    /* --------------------------------------------------------------------- */
    /* ------------------------  CREATE  ----------------------------------- */
    /* --------------------------------------------------------------------- */

    @Tool(description = "Crea un nuevo almacén en la base de datos. | Create a new warehouse in the database.")
    public Warehouse createWarehouse(
            @ToolParam(description = "Nombre del almacén que se desea registrar. | Name of the warehouse to be created.")
            @NotBlank(message = "El nombre del almacén es obligatorio.") String name,

            @ToolParam(description = "Dirección física del almacén. | Physical address of the warehouse.")
            @NotBlank(message = "La dirección del almacén es obligatoria.") String direction,

            @ToolParam(description = "ID de la subsidiaria a la que pertenece el almacén | ID of the subsidiary the warehouse belongs to.")
            @NotNull(message = "Debe proporcionar el ID de la subsidiaria.") Long subsidiaryId
    ) {
        Subsidiary subsidiary = subsidiaryRepository.findById(subsidiaryId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Subsidiaria con ID " + subsidiaryId + " no encontrada."));

        Warehouse warehouse = Warehouse.builder()
                .name(name)
                .direction(direction)
                .subsidiary(subsidiary)
                .build();

        log.info("Creating warehouse: {}", warehouse);
        return warehouseRepository.save(warehouse);
    }

    @Tool(description = "Busca almacenes por nombre con paginación. | Search warehouses by name with pagination.")
    public Page<Warehouse> findWarehouses(
            @ToolParam(description = "Número de página para la consulta (por defecto es 0). | Page number (default 0).")
            @Positive(message = "El número de página debe ser 0 o positivo.") int pageNumber,

            @ToolParam(description = "Tamaño de la página de resultados (por defecto es 10). | Page size (default 10).")
            @Positive(message = "El tamaño de página debe ser mayor que 0.") int size,

            @ToolParam(description = "Texto para buscar coincidencias en los nombres de almacenes. | Text to search for matching warehouse names.", required = false)
            String text
    ) {
        Pageable pageable = PageRequest.of(pageNumber, size);
        log.info("Finding warehouses with text '{}' page {} size {}", text, pageNumber, size);

        return (text == null || text.isBlank())
                ? warehouseRepository.findAll(pageable)
                : warehouseRepository.findByNameContainingIgnoreCase(text, pageable);
    }

    @Tool(description = "Obtiene un almacén usando su ID único. | Retrieve a warehouse using its unique ID.")
    public Optional<Warehouse> getWarehouseById(
            @ToolParam(description = "ID del almacén que se desea consultar. | ID of the warehouse to retrieve.")
            @NotNull(message = "El ID del almacén es obligatorio.") Long id
    ) {
        log.info("Finding warehouse with id {}", id);
        return warehouseRepository.findById(id);
    }

    @Tool(description = "Actualiza un almacén existente usando su ID y los nuevos datos. | Update an existing warehouse by ID.")
    public Warehouse updateWarehouse(
            @ToolParam(description = "ID del almacén que se va a actualizar. | ID of the warehouse to update.")
            @NotNull(message = "El ID del almacén es obligatorio.") Long id,

            @ToolParam(description = "DTO con los nuevos datos (nombre, dirección, subsidiaria). | DTO with updated data.")
            @NotNull(message = "El cuerpo de datos (DTO) no puede ser nulo.") WarehouseDTO dto
    ) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Almacén con ID " + id + " no encontrado."));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            warehouse.setName(dto.getName());
        }
        if (dto.getDirection() != null && !dto.getDirection().isBlank()) {
            warehouse.setDirection(dto.getDirection());
        }

        if (dto.getSubsidiaryId() != null) {
            Subsidiary subsidiary = subsidiaryRepository.findById(dto.getSubsidiaryId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Subsidiaria con ID " + dto.getSubsidiaryId() + " no encontrada."));
            warehouse.setSubsidiary(subsidiary);
        }

        log.info("Updating warehouse with id {}: {}", id, warehouse);
        return warehouseRepository.save(warehouse);
    }

    @Tool(description = "Elimina un almacén por su ID. Esta acción es irreversible. | Delete a warehouse by its ID. This action is irreversible.")
    public void deleteWarehouse(
            @ToolParam(description = "ID del almacén que se desea eliminar. | ID of the warehouse to delete.")
            @NotNull(message = "El ID del almacén es obligatorio.") Long id
    ) {
        if (!warehouseRepository.existsById(id)) {
            throw new IllegalArgumentException("Almacén con ID " + id + " no encontrado.");
        }
        log.info("Deleting warehouse with id {}", id);
        warehouseRepository.deleteById(id);
    }
}
