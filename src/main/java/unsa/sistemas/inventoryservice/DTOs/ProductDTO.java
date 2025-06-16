package unsa.sistemas.inventoryservice.DTOs;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductDTO {
    private String name;
    private String description;
    private Long price;
    private String imageUrl;
}

