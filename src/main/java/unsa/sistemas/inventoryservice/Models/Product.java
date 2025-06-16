package unsa.sistemas.inventoryservice.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Product {
    @Id
    private Long id;

    private String name;
    private String description;
    private Long price;
    private String imageUrl;

    private Boolean enabled;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}