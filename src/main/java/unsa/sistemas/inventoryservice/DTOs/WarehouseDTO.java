package unsa.sistemas.inventoryservice.DTOs;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WarehouseDTO {
    private String name;
    private String direction;
    private Long subsidiaryId;
}

