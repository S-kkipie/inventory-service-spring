package unsa.sistemas.inventoryservice.Services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.springframework.stereotype.Service;
import unsa.sistemas.inventoryservice.Config.HibernateProperties;
import unsa.sistemas.inventoryservice.Models.Keys.StoredProductKey;
import unsa.sistemas.inventoryservice.Models.Product;
import unsa.sistemas.inventoryservice.Models.StoredProduct;
import unsa.sistemas.inventoryservice.Models.Subsidiary;
import unsa.sistemas.inventoryservice.Models.Warehouse;

import javax.sql.DataSource;
import java.util.EnumSet;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchemaService {
    private final HibernateProperties hibernateProperties;

    public void createSchemaForTenant(DataSource dataSource, String pwd) {
        try (var connection = dataSource.getConnection()) {
            String url = connection.getMetaData().getURL();

            log.debug("JDBC Driver: {}", hibernateProperties.getDriverClass());
            log.debug("JDBC URL: {}", url);
            log.debug("JDBC User: {}", connection.getMetaData().getUserName());

            Map<String, Object> settings = Map.of(
                    AvailableSettings.JAKARTA_JDBC_URL, url,
                    AvailableSettings.JAKARTA_JDBC_PASSWORD, pwd,
                    AvailableSettings.JAKARTA_JDBC_USER, connection.getMetaData().getUserName()
            );

            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .applySettings(settings)
                    .applySettings(hibernateProperties.getCommonProperties())
                    .build();

            try {
                Metadata metadata = new MetadataSources(registry)
                        .addAnnotatedClass(Product.class)
                        .addAnnotatedClass(StoredProduct.class)
                        .addAnnotatedClass(Subsidiary.class)
                        .addAnnotatedClass(Warehouse.class)
                        .addAnnotatedClass(StoredProductKey.class)
                        .buildMetadata();

                var export = new SchemaExport();
                export.setHaltOnError(true);
                export.setFormat(true);
                export.execute(EnumSet.of(TargetType.DATABASE),
                        SchemaExport.Action.CREATE, metadata);
            } finally {
                StandardServiceRegistryBuilder.destroy(registry);
            }
        } catch (Exception e) {
            log.error("Error creating schema: {}", e.getMessage(), e);
            throw new RuntimeException("An error occurred creating the database schema", e);
        }
    }

}