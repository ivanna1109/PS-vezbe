package org.example.gatewayservice.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

//Mozemo umesto GlobalFiltera imati i Custom definisane
// Pre- i post-filtere
@Component
public class LoggingPostFilter extends AbstractGatewayFilterFactory<LoggingPostFilter.Config> {

    public LoggingPostFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            long startTime = System.currentTimeMillis();

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;

                System.out.println("Zahtev na putanji: " + exchange.getRequest().getURI() +
                        " je trajao: " + duration + "ms");

                exchange.getResponse().getHeaders().add("X-Response-Time", String.valueOf(duration));
            }));
        };
    }

    public static class Config { }
}