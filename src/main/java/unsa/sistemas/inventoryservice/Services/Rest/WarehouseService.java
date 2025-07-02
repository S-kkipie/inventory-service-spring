package unsa.sistemas.inventoryservice.Services.Rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import unsa.sistemas.inventoryservice.DTOs.WarehouseDTO;
import unsa.sistemas.inventoryservice.Models.Warehouse;
import unsa.sistemas.inventoryservice.Repositories.WarehouseRepository;
import unsa.sistemas.inventoryservice.Repositories.SubsidiaryRepository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@AllArgsConstructor
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final SubsidiaryRepository subsidiaryRepository;

    public Mono<Warehouse> createWarehouse(WarehouseDTO dto) {
        return Mono.fromCallable(() -> {
            Warehouse warehouse = new Warehouse();
            warehouse.setName(dto.getName());
            warehouse.setDirection(dto.getDirection());
            if (dto.getSubsidiaryId() != null) {
                subsidiaryRepository.findById(dto.getSubsidiaryId()).ifPresent(warehouse::setSubsidiary);
            }
            return warehouseRepository.save(warehouse);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Page<Warehouse>> getAllWarehouses(int pageNumber, int size, String text) {
        return Mono.fromCallable(() -> {
            Pageable pageable = PageRequest.of(pageNumber, size);
            return warehouseRepository.findByNameContainingIgnoreCase(text, pageable);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Warehouse> getWarehouseById(Long id) {
        return Mono.fromCallable(() -> warehouseRepository.findById(id).orElse(null))
                   .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Warehouse> updateWarehouse(Long id, WarehouseDTO dto) {
        return Mono.fromCallable(() ->
            warehouseRepository.findById(id).map(warehouse -> {
                warehouse.setName(dto.getName());
                warehouse.setDirection(dto.getDirection());
                if (dto.getSubsidiaryId() != null) {
                    subsidiaryRepository.findById(dto.getSubsidiaryId()).ifPresent(warehouse::setSubsidiary);
                }
                return warehouseRepository.save(warehouse);
            }).orElse(null)
        ).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteWarehouse(Long id) {
        return Mono.fromRunnable(() -> warehouseRepository.deleteById(id))
                   .subscribeOn(Schedulers.boundedElastic())
                   .then();
    }
}
