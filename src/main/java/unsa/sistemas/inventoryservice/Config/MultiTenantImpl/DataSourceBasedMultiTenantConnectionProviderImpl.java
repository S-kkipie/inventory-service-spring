package unsa.sistemas.inventoryservice.Config.MultiTenantImpl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter
@Component
public class DataSourceBasedMultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl<String> {
    private final DataSource defaultDataSource;
    private final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

    public DataSourceBasedMultiTenantConnectionProviderImpl(
            @Qualifier("tenantDefaultDataSource") DataSource defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
        dataSources.put("default", defaultDataSource);
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return defaultDataSource;
    }

    public void addDataSource(String orgCode, DataSource dataSource) throws DuplicateKeyException {
        if (!dataSources.containsKey(orgCode)) {
            dataSources.put(orgCode, dataSource);
            log.debug("Adding database connection for orgCode: {}", orgCode);
            return;
        }
        log.debug("Code already registered: {}", orgCode);
        throw new DuplicateKeyException(orgCode);
    }

    @Override
    protected DataSource selectDataSource(String orgCode) {
        log.debug("Selecting data source: {}", orgCode);
        if (!dataSources.containsKey(orgCode)) {
            throw new RuntimeException("Organization Code " + orgCode + " not found");
        }
        return dataSources.get(orgCode);
    }

}
