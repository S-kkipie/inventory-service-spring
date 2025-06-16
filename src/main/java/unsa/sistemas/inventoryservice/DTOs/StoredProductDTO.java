package unsa.sistemas.inventoryservice.DTOs;

import lombok.Data;

@Data
public class StoredProductDTO {
    private Long productId;
    private Long warehouseId;
    private Long stock;
}

