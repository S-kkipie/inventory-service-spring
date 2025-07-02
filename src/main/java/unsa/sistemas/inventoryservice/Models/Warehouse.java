package unsa.sistemas.inventoryservice.Models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String direction;


    @ManyToOne
    @JoinColumn(name = "subsidiary_id", nullable = false)
    private Subsidiary subsidiary;

    private Boolean enabled;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
