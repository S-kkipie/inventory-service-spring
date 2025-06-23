package unsa.sistemas.inventoryservice.Models;


import jakarta.persistence.*;
import lombok.*;
import unsa.sistemas.inventoryservice.Models.Keys.StoredProductKey;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoredProduct {
    @EmbeddedId
    StoredProductKey  id;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @MapsId("warehouseId")
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    private Long stock;
}
