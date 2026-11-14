package com.micro.gatewayserver.filters;

import com.micro.gatewayserver.controller.FallbackController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
@Order(1)
@Component
public class RequestLoggingFilter implements GlobalFilter {


    private static final Logger log  = LoggerFactory.getLogger(RequestLoggingFilter.class);
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTimeInMillis = System.currentTimeMillis();
        log.info("Routing request {} {}", exchange.getRequest().getMethod(), exchange.getRequest().getURI());
        return chain.filter(exchange).doFinally(signalType -> {
            long duration = System.currentTimeMillis()-startTimeInMillis;
            log.info("Completed request {} {} with status {} in {} ms", exchange.getRequest().getMethod(),
                    exchange.getRequest().getURI(), exchange.getResponse().getStatusCode(), duration);
        });
    }
}
