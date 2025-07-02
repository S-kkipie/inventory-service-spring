package unsa.sistemas.inventoryservice.Config;


import lombok.Getter;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Properties;


@Configuration
@PropertySource("classpath:hibernate.properties")
@Getter
public class HibernateProperties {
    private final String username;
    private final String password;
    private final String driverClass;
    private final String defaultUrl;

    public HibernateProperties(JdbcConnectionDetails connectionDetails) {
        this.username = connectionDetails.getUsername();
        this.password = connectionDetails.getPassword();
        this.driverClass = connectionDetails.getDriverClassName();
        this.defaultUrl = connectionDetails.getJdbcUrl();
    }

    @Value("${dialect}")
    private String dialect;

    @Value("${hbm2ddl.auto}")
    private String hbm2ddlAuto;

    @Value("${show_sql}")
    private String showSql;

    @Value("${format_sql}")
    private String formatSql;

    public String getDefaultTenantUrl() {
        return this.defaultUrl;
    }


    public Properties getTenantProperties() {
        Properties props = new Properties();
        props.setProperty(AvailableSettings.JAKARTA_JDBC_URL, getDefaultTenantUrl());
        return getProperties(props);
    }

    private Properties getProperties(Properties props) {
        props.setProperty(AvailableSettings.JAKARTA_JDBC_USER, getUsername());
        props.setProperty(AvailableSettings.JAKARTA_JDBC_PASSWORD, getPassword());
        return getPrivateProperties(props);
    }

    private Properties getPrivateProperties(Properties props) {
        props.setProperty(AvailableSettings.JAKARTA_JDBC_DRIVER, getDriverClass());
        props.setProperty(AvailableSettings.DIALECT, getDialect());
        props.setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, getHbm2ddlAuto());
        props.setProperty(AvailableSettings.SHOW_SQL, getShowSql());
        props.setProperty(AvailableSettings.FORMAT_SQL, getFormatSql());
        return props;
    }

    public Properties getCommonProperties() {
        Properties props = new Properties();
        return getPrivateProperties(props);
    }
}