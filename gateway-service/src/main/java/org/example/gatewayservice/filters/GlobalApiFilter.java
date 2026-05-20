package org.example.gatewayservice.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GlobalApiFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(GlobalApiFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 1. Preskačemo Swagger i API dokumentaciju
        if (path.contains("/v3/api-docs") || path.contains("/swagger-ui") || path.contains("/webjars")) {
            return chain.filter(exchange);
        }

        // 2. SECURITY: Provera API ključa
        String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-KEY");
        if (apiKey == null || !apiKey.equals("studentska-tajna-2026")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 3. PRE-FILTER LOGOVANJE: Koristimo reaktivni .defer() da obezbedimo da Micrometer inicijalizuje Trace ID na ovoj niti
        return Mono.defer(() -> {
                    log.info("[GATEWAY PRE] Prošao API ključ za putanju: {}", path);

                    // Nastavljamo lanac filtera
                    return chain.filter(exchange);
                })
                // 4. POST-FILTER LOGOVANJE: Umesto Mono.fromRunnable, koristimo doOnSuccess / doOnError
                .doOnSuccess(v -> {
                    log.info("[GATEWAY POST] Odgovor za: {} | Status: {}",
                            path, exchange.getResponse().getStatusCode());
                })
                .doOnError(e -> {
                    log.error("[GATEWAY ERROR] Greška na putanji: {} | Poruka: {}", path, e.getMessage());
                });
    }

    @Override
    public int getOrder() {
        return -1;
    }
}