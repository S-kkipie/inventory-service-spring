package unsa.sistemas.inventoryservice.Services.Tooling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import unsa.sistemas.inventoryservice.DTOs.SubsidiaryDTO;
import unsa.sistemas.inventoryservice.Models.Subsidiary;
import unsa.sistemas.inventoryservice.Repositories.SubsidiaryRepository;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubsidiaryToolService {
    private final SubsidiaryRepository subsidiaryRepository;

    @Tool(description = "Create a new subsidiary.")
    public Subsidiary createSubsidiary(
            @ToolParam(description = "Name of the subsidiary") String name,
            @ToolParam(description = "Description of the subsidiary") String description,
            @ToolParam(description = "Direction of the subsidiary") String direction
    ) {
        Subsidiary subsidiary = Subsidiary.builder()
                .name(name)
                .description(description)
                .direction(direction)
                .build();
        log.info("Creating subsidiary: {}", subsidiary);
        return subsidiaryRepository.save(subsidiary);
    }

    @Tool(description = "Find subsidiaries by name, pageNumber and size.")
    public Page<Subsidiary> findSubsidiaries(
            @ToolParam(description = "Page number, default is 0", required = false) int pageNumber,
            @ToolParam(description = "Page size, default is 10", required = false) int size,
            @ToolParam(description = "Text to search in subsidiary name", required = false) String text
    ) {
        Pageable pageable = PageRequest.of(pageNumber, size);
        log.info("Finding subsidiaries with text {} page {} size {}", text, pageNumber, size);
        return subsidiaryRepository.findByNameContainingIgnoreCase(text, pageable);
    }

    @Tool(description = "Get a subsidiary by ID.")
    public Optional<Subsidiary> getSubsidiaryById(Long id) {
        log.info("Finding subsidiary with id {}", id);
        return subsidiaryRepository.findById(id);
    }

    @Tool(description = "Update a subsidiary by ID and DTO.")
    public Subsidiary updateSubsidiary(Long id, SubsidiaryDTO dto) {
        Subsidiary subsidiary = subsidiaryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Subsidiary not found"));
        subsidiary.setName(dto.getName());
        subsidiary.setDescription(dto.getDescription());
        subsidiary.setDirection(dto.getDirection());
        log.info("Updating subsidiary with id {}: {}", id, subsidiary);
        return subsidiaryRepository.save(subsidiary);
    }

    @Tool(description = "Delete a subsidiary by ID.")
    public void deleteSubsidiary(Long id) {
        log.info("Deleting subsidiary with id {}", id);
        subsidiaryRepository.deleteById(id);
    }
}

