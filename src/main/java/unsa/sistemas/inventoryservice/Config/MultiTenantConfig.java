package unsa.sistemas.inventoryservice.Config;

import lombok.AllArgsConstructor;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import unsa.sistemas.inventoryservice.Config.MultiTenantImpl.CurrentTenantIdentifierResolverImpl;
import unsa.sistemas.inventoryservice.Config.MultiTenantImpl.DataSourceBasedMultiTenantConnectionProviderImpl;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;

@Configuration
@AllArgsConstructor
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories(
        basePackages = "unsa.sistemas.inventoryservice.Repositories",
        entityManagerFactoryRef = "tenantEntityManager",
        transactionManagerRef = "tenantTransactionManager"
)
public class MultiTenantConfig {
    private HibernateProperties hibernateProperties;

    @Bean(name = "tenantEntityManager")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSourceBasedMultiTenantConnectionProviderImpl connectionProvider,
            CurrentTenantIdentifierResolverImpl tenantResolver
    ) {
        Properties properties = new Properties();
        properties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
        properties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantResolver);

        properties.putAll(hibernateProperties.getTenantProperties());

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setPackagesToScan("unsa.sistemas.inventoryservice.Models");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emf.setJpaProperties(properties);
        return emf;
    }

    @Bean(name = "tenantTransactionManager")
    public JpaTransactionManager tenantTransactionManager(
           @Qualifier("tenantEntityManager") LocalContainerEntityManagerFactoryBean tenantEntityManager) {
        return new JpaTransactionManager(Objects.requireNonNull(tenantEntityManager.getObject()));
    }

    @Bean(name = "tenantDefaultDataSource")
    public DataSource tenantDataSource() {
        return DataSourceBuilder.create()
                .url(hibernateProperties.getTenantUrl())
                .username(hibernateProperties.getUsername())
                .password(hibernateProperties.getPassword())
                .driverClassName(hibernateProperties.getDriverClass())
                .build();
    }

}
