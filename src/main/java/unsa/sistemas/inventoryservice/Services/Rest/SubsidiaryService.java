package unsa.sistemas.inventoryservice.Services.Rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import unsa.sistemas.inventoryservice.DTOs.SubsidiaryDTO;
import unsa.sistemas.inventoryservice.Models.Subsidiary;
import unsa.sistemas.inventoryservice.Repositories.SubsidiaryRepository;


@Service
@AllArgsConstructor
public class SubsidiaryService {
    private final SubsidiaryRepository subsidiaryRepository;

    public Mono<Subsidiary> createSubsidiary(SubsidiaryDTO dto) {
        return Mono.fromCallable(() -> {
            Subsidiary subsidiary = new Subsidiary();
            subsidiary.setName(dto.getName());
            subsidiary.setDescription(dto.getDescription());
            subsidiary.setDirection(dto.getDirection());
            return subsidiaryRepository.save(subsidiary);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Page<Subsidiary>> getAllSubsidiaries(int pageNumber, int size, String text) {
        return Mono.fromCallable(() -> {
            Pageable pageable = PageRequest.of(pageNumber, size);
            return subsidiaryRepository.findByNameContainingIgnoreCase(text, pageable);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Subsidiary> getSubsidiaryById(Long id) {
        return Mono.fromCallable(() -> subsidiaryRepository.findById(id).orElse(null))
                   .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Subsidiary> updateSubsidiary(Long id, SubsidiaryDTO dto) {
        return Mono.fromCallable(() ->
            subsidiaryRepository.findById(id)
                .map(subsidiary -> {
                    subsidiary.setName(dto.getName());
                    subsidiary.setDescription(dto.getDescription());
                    subsidiary.setDirection(dto.getDirection());
                    return subsidiaryRepository.save(subsidiary);
                }).orElse(null)
        ).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteSubsidiary(Long id) {
        return Mono.fromRunnable(() -> subsidiaryRepository.deleteById(id))
                   .subscribeOn(Schedulers.boundedElastic())
                   .then();
    }
}
