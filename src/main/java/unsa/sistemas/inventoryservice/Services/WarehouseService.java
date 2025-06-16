package unsa.sistemas.inventoryservice.Services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import unsa.sistemas.inventoryservice.DTOs.WarehouseDTO;
import unsa.sistemas.inventoryservice.Models.Warehouse;
import unsa.sistemas.inventoryservice.Repositories.WarehouseRepository;
import unsa.sistemas.inventoryservice.Models.Subsidiary;
import unsa.sistemas.inventoryservice.Repositories.SubsidiaryRepository;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final SubsidiaryRepository subsidiaryRepository;

    public Warehouse createWarehouse(WarehouseDTO dto) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(dto.getName());
        warehouse.setDirection(dto.getDirection());
        if (dto.getSubsidiaryId() != null) {
            subsidiaryRepository.findById(dto.getSubsidiaryId()).ifPresent(warehouse::setSubsidiary);
        }
        return warehouseRepository.save(warehouse);
    }

    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    public Optional<Warehouse> getWarehouseById(Long id) {
        return warehouseRepository.findById(id);
    }

    public Optional<Warehouse> updateWarehouse(Long id, WarehouseDTO dto) {
        return warehouseRepository.findById(id).map(warehouse -> {
            warehouse.setName(dto.getName());
            warehouse.setDirection(dto.getDirection());
            if (dto.getSubsidiaryId() != null) {
                subsidiaryRepository.findById(dto.getSubsidiaryId()).ifPresent(warehouse::setSubsidiary);
            }
            return warehouseRepository.save(warehouse);
        });
    }

    public void deleteWarehouse(Long id) {
        warehouseRepository.deleteById(id);
    }
}

