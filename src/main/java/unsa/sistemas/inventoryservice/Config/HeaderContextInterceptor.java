package unsa.sistemas.inventoryservice.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import unsa.sistemas.inventoryservice.Config.Context.OrgContext;
import unsa.sistemas.inventoryservice.Config.Context.UserContext;
import unsa.sistemas.inventoryservice.Config.Context.UserContextHolder;
import unsa.sistemas.inventoryservice.Config.MultiTenantImpl.DataSourceBasedMultiTenantConnectionProviderImpl;
import unsa.sistemas.inventoryservice.Models.Role;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class HeaderContextInterceptor implements HandlerInterceptor {
    private final DataSourceBasedMultiTenantConnectionProviderImpl dataSourceBasedMultiTenantConnectionProviderImpl;

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws IOException {
        String path = request.getRequestURI();

        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") || path.startsWith("/swagger-resources")) {
            return true;
        }

        String username = request.getHeader("X-User-Name");
        String role = request.getHeader("X-User-Role");
        String orgCode = request.getHeader("X-Org-Code");

        if (username == null || role == null || orgCode == null || !dataSourceBasedMultiTenantConnectionProviderImpl.getDataSources().containsKey(orgCode) || Role.valueOf(role) == Role.ROLE_PRINCIPAL_USER) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Forbidden: Missing or invalid authentication");
            response.getWriter().flush();
            return false;
        }

        OrgContext.setOrgCode(orgCode);
        UserContext context = new UserContext(username, role);
        UserContextHolder.set(context);

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        UserContextHolder.clear();
        OrgContext.clear();
    }
}
