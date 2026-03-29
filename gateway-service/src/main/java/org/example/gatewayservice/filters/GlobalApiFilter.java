package org.example.gatewayservice.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class GlobalApiFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. SECURITY: Provera API ključa
        String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-KEY");
        if (apiKey == null || !apiKey.equals("studentska-tajna-2026")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 2. TRACING: Generisanje Trace ID-a
        String traceId = UUID.randomUUID().toString();

        // Mutiramo request da prosledimo Trace ID mikroservisima
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(builder -> builder.header("X-Trace-Id", traceId))
                .build();

        System.out.println("[GATEWAY] Dolazni zahtev na: " + exchange.getRequest().getPath() + " | TraceID: " + traceId);

        return chain.filter(mutatedExchange).then(Mono.fromRunnable(() -> {
            // POST-FILTER LOGIKA: Logujemo status odgovor
            System.out.println("[GATEWAY] Odgovor poslat sa statusom: " + exchange.getResponse().getStatusCode());
        }));
    }

    @Override
    public int getOrder() { return -1; }
}
