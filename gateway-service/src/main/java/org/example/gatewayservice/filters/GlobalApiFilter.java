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
        String path = exchange.getRequest().getURI().getPath();

        if (path.contains("/v3/api-docs") || path.contains("/swagger-ui") || path.contains("/webjars")) {
            return chain.filter(exchange);
        }

        // 2. SECURITY (PRE-FILTER): Provera API ključa
        String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-KEY");
        if (apiKey == null || !apiKey.equals("studentska-tajna-2026")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        //if (path.contains("/restaurants")) {
        //    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN); // 403
        //    return exchange.getResponse().setComplete();
        //}

        // TRACING: Generisanje Trace ID-a (Ručno, uz Micrometer koji radi automatski)
        String traceId = UUID.randomUUID().toString();

        // Mutiramo request da prosledimo Trace ID mikroservisima kroz zaglavlje
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(builder -> builder.header("X-Trace-Id", traceId))
                .build();

        System.out.println("[GATEWAY PRE] Putanja: " + path + " | TraceID: " + traceId);

        // 4. POST-FILTER: Izvršava se pomoću .then() nakon što mikroservis odgovori
        return chain.filter(mutatedExchange).then(Mono.fromRunnable(() -> {
            System.out.println("[GATEWAY POST] Odgovor za: " + path +
                    " | Status: " + exchange.getResponse().getStatusCode() +
                    " | TraceID: " + traceId);
        }));
    }

    /*
    Sigurnosni filteri i filteri za praćenje
    uvek moraju imati najveći prioritet (najmanji order),
    kako bismo loše zahteve odbacili na samom ulazu,
    pre nego što sistem potroši resurse na njihovu dalju obradu
    */
    @Override
    public int getOrder() {
        //Visok prioritet da bi ovo bio prvi filter u lancu
        return -1;
    }
}