package unsa.sistemas.inventoryservice.Config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import unsa.sistemas.inventoryservice.Config.Context.UserContext;
import unsa.sistemas.inventoryservice.Config.MultiTenantImpl.DataSourceBasedMultiTenantConnectionProviderImpl;
import unsa.sistemas.inventoryservice.Models.Role;


@Component
@RequiredArgsConstructor
public class TenantHeaderWebFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(TenantHeaderWebFilter.class);
    private final DataSourceBasedMultiTenantConnectionProviderImpl provider;

    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange,
                             @NonNull WebFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")) {
            return chain.filter(exchange);
        }

        var h = exchange.getRequest().getHeaders();
        String u = h.getFirst("X-User-Name");
        String r = h.getFirst("X-User-Role");
        String o = h.getFirst("X-Org-Code");

        log.info("User: {}, Role: {}, Org: {}", u, r, o);
        log.info("dataSources: {}", provider.getDataSources().keySet());
        boolean invalid =
                u == null || r == null || o == null
                        || !provider.getDataSources().containsKey(o)
                        || Role.valueOf(r) == Role.ROLE_PRINCIPAL_USER;

        if (invalid) {
            ServerHttpResponse resp = exchange.getResponse();

            resp.setStatusCode(HttpStatus.FORBIDDEN);
            return resp.setComplete();
        }

        return chain.filter(exchange)
                .contextWrite(ctx -> ctx
                        .put(UserContext.KEY, new UserContext(u, r))
                        .put("ORG", o))
                        .contextCapture();
    }
}