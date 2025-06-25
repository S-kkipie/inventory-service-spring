package unsa.sistemas.inventoryservice.Services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import unsa.sistemas.inventoryservice.Config.AppProperties;
import unsa.sistemas.inventoryservice.DTOs.SubsidiaryDTO;
import unsa.sistemas.inventoryservice.Models.Subsidiary;
import unsa.sistemas.inventoryservice.Repositories.SubsidiaryRepository;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SubsidiaryService {
    private final SubsidiaryRepository subsidiaryRepository;
    private final AppProperties appProperties;
    public Subsidiary createSubsidiary(SubsidiaryDTO dto) {
        Subsidiary subsidiary = new Subsidiary();
        subsidiary.setName(dto.getName());
        subsidiary.setDescription(dto.getDescription());
        subsidiary.setDirection(dto.getDirection());
        return subsidiaryRepository.save(subsidiary);
    }

    public Page<Subsidiary> getAllSubsidiaries(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, appProperties.getPageSize());
        return subsidiaryRepository.findAll(pageable);
    }

    public Optional<Subsidiary> getSubsidiaryById(Long id) {
        return subsidiaryRepository.findById(id);
    }

    public Optional<Subsidiary> updateSubsidiary(Long id, SubsidiaryDTO dto) {
        return subsidiaryRepository.findById(id).map(subsidiary -> {
            subsidiary.setName(dto.getName());
            subsidiary.setDescription(dto.getDescription());
            subsidiary.setDirection(dto.getDirection());
            return subsidiaryRepository.save(subsidiary);
        });
    }

    public void deleteSubsidiary(Long id) {
        subsidiaryRepository.deleteById(id);
    }
}

