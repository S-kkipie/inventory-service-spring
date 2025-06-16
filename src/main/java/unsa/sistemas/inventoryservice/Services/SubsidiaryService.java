package unsa.sistemas.inventoryservice.Services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import unsa.sistemas.inventoryservice.DTOs.SubsidiaryDTO;
import unsa.sistemas.inventoryservice.Models.Subsidiary;
import unsa.sistemas.inventoryservice.Repositories.SubsidiaryRepository;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SubsidiaryService {
    private final SubsidiaryRepository subsidiaryRepository;

    public Subsidiary createSubsidiary(SubsidiaryDTO dto) {
        Subsidiary subsidiary = new Subsidiary();
        subsidiary.setName(dto.getName());
        subsidiary.setDescription(dto.getDescription());
        subsidiary.setDirection(dto.getDirection());
        return subsidiaryRepository.save(subsidiary);
    }

    public List<Subsidiary> getAllSubsidiaries() {
        return subsidiaryRepository.findAll();
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

