package unsa.sistemas.inventoryservice.Config.MultiTenantImpl;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;
import unsa.sistemas.inventoryservice.Config.Context.OrgContext;

@Slf4j
@Component
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver<String> {
    @Override
    public String resolveCurrentTenantIdentifier() {
        String orgCode = OrgContext.getOrg();
        log.info("Resolving tenant identifier: {}", orgCode);
        return (orgCode != null) ? orgCode : "default";
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}