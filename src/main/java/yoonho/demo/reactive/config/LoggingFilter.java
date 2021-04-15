package yoonho.demo.reactive.config;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        long startTime = System.currentTimeMillis();
        log.info("filter start {}", startTime);
        return chain.filter(exchange).doFinally(signalType -> {
            long totalTime = System.currentTimeMillis() - startTime;
            exchange.getAttributes().put("totalTime", totalTime);
            log.info("totalTime : {}", totalTime);
        });
    }
}
