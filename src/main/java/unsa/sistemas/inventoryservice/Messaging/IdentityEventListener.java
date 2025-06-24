package unsa.sistemas.inventoryservice.Messaging;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;
import unsa.sistemas.inventoryservice.Config.HibernateProperties;
import unsa.sistemas.inventoryservice.Config.MultiTenantImpl.DataSourceBasedMultiTenantConnectionProviderImpl;
import unsa.sistemas.inventoryservice.DTOs.CreateDataBaseEvent;
import unsa.sistemas.inventoryservice.Services.SchemaService;
import unsa.sistemas.inventoryservice.Utils.EncryptionUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class IdentityEventListener {
    private final HibernateProperties hibernateProperties;
    private final SchemaService schemaService;
    private final DataSourceBasedMultiTenantConnectionProviderImpl connectionProvider;
    private final EncryptionUtil encryptionUtil;

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void handleNewDatabase(CreateDataBaseEvent event) throws Exception {
        String decryptedJson = encryptionUtil.decrypt(event.getEncryptedPayload());
        //The database always is orgCode+_inventory_db
        Map<String, String> dbInfo = new ObjectMapper().readValue(decryptedJson, new TypeReference<>() {
        });

        log.debug(dbInfo.toString());

        String baseUrl = dbInfo.get("url");
        String username = dbInfo.get("username");
        String password = dbInfo.get("password");
        String code = dbInfo.get("orgCode");

        String dbName = code + "_inventory_db";

        String url = baseUrl + "/" + dbName;

        if (connectionProvider.getDataSources().containsKey(code)) {
            log.warn("DataSource for tenant {} already exists, skipping creation.", code);
            return;
        }

        try {
            DataSource newDataSource = DataSourceBuilder.create()
                    .url(url)
                    .username(username)
                    .password(password)
                    .driverClassName(hibernateProperties.getDriverClass())
                    .build();

            try (Connection conn = newDataSource.getConnection()) {
                if (conn.isValid(5)) {
                    log.info("Successfully connected to tenant database: {}", code);
                } else {
                    throw new SQLException("Invalid connection for tenant: " + code);
                }
            }


            try {
                boolean physicalExists = dbInfo.get("physicalExists").equals("true");
                if (physicalExists) {
                    log.info("Physical database for tenant {} exists, skipping schema creation.", code);
                }
            } catch (Exception e) {
                log.warn("Database does not have physicalExists field, assuming it does not exist");
                log.info("Physical database for tenant {} does not exist, proceeding with schema creation.", code);
                schemaService.createSchemaForTenant(newDataSource, password);
            }

            log.info("Creating DataSource for tenant: {}, URL: {}, Username: {}, pwd: {}", code, url, username, password);
            connectionProvider.addDataSource(code, newDataSource);

        } catch (Exception e) {
            log.error("Failed to create or verify DataSource for tenant {}: {}", code, e.getMessage(), e);
            throw new RuntimeException("Could not establish connection to the tenant database: " + code, e);
        }


        log.info("Message received, database: {} registered for company {}", dbName, code);
    }
}

