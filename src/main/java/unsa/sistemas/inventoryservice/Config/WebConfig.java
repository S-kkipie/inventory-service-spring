package unsa.sistemas.inventoryservice.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import unsa.sistemas.inventoryservice.Config.MultiTenantImpl.DataSourceBasedMultiTenantConnectionProviderImpl;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final DataSourceBasedMultiTenantConnectionProviderImpl data;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HeaderContextInterceptor(data));
    }
}
