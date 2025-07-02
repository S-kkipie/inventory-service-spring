package unsa.sistemas.inventoryservice.Tools;

import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import unsa.sistemas.inventoryservice.Services.Tooling.ProductToolService;
import unsa.sistemas.inventoryservice.Services.Tooling.StoredProductToolService;
import unsa.sistemas.inventoryservice.Services.Tooling.SubsidiaryToolService;
import unsa.sistemas.inventoryservice.Services.Tooling.WarehouseToolService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class Tools {

    @Bean
    public List<ToolCallback> allTools(
            ProductToolService productToolService,
            StoredProductToolService storedProductToolService,
            SubsidiaryToolService subsidiaryToolService,
            WarehouseToolService warehouseToolService
    ) {
        return Stream.of(
                        ToolCallbacks.from(productToolService),
                        ToolCallbacks.from(storedProductToolService),
                        ToolCallbacks.from(subsidiaryToolService),
                        ToolCallbacks.from(warehouseToolService)
                )
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());
    }
}