package unsa.sistemas.inventoryservice;

import io.micrometer.context.ContextRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import reactor.core.publisher.Hooks;
import unsa.sistemas.inventoryservice.Config.Context.OrgContext;

@SpringBootApplication
@EnableDiscoveryClient
public class InventoryServiceApplication {

    public static void main(String[] args) {
        Hooks.enableAutomaticContextPropagation();
        SpringApplication.run(InventoryServiceApplication.class, args);
        ContextRegistry.getInstance()
                .registerThreadLocalAccessor("ORG",
                        OrgContext::getOrg,
                        OrgContext::setOrg,
                        OrgContext::clear);

    }

}
