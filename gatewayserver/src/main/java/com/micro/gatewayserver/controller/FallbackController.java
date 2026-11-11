package com.micro.gatewayserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;

import org.springframework.web.server.ServerWebExchange;

import java.util.concurrent.TimeoutException;

@RestController
public class FallbackController {
    @RequestMapping("/contactSupport")
    public Mono<ResponseEntity<String>> contactSupport(ServerWebExchange exchange) {
        Throwable ex = exchange.getAttribute(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        for (Throwable t = ex; t != null; t = t.getCause()) {
            if (t instanceof TimeoutException) {
                status = HttpStatus.GATEWAY_TIMEOUT;           // 504 after the time limit
                break;
            }
            if (t instanceof CallNotPermittedException) {
                status = HttpStatus.SERVICE_UNAVAILABLE;       // 503, circuit is open
                break;
            }
        }

        return Mono.just(ResponseEntity.status(status)
                .body("An error occurred please contact to support team"));
    }
}
