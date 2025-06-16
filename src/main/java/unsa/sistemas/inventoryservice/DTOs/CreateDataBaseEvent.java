package unsa.sistemas.inventoryservice.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDataBaseEvent {
    private String encryptedPayload;
}