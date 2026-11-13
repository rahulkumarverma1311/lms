package com.micro.gatewayserver;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}
	@Bean
	public RouteLocator deeRavBankRouteConfig(RouteLocatorBuilder routeLocatorBuilder){
		return routeLocatorBuilder.routes()
				.route(p-> p
						.path("/deerav/accounts/**")
						.filters(f->f.rewritePath("/deerav/accounts/(?<segment>.*)","/${segment}")
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
								.circuitBreaker(config-> config
										.setName("accountsCircuitBreaker")
										.setFallbackUri("forward:/contactSupport"))
						)
						.uri("lb://ACCOUNTS"))
				.route(p-> p
						.path("/deerav/loans/**")
						.filters(f->f.rewritePath("/deerav/loans/(?<segment>.*)","/${segment}")
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
								.retry(retryConfig -> retryConfig.setRetries(3)
										.setMethods(HttpMethod.GET)
										.setBackoff(Duration.ofMillis(100),Duration.ofMillis(1000),2,true))
						)
						.uri("lb://LOANS"))
				.route(p-> p
						.path("/deerav/cards/**")
						.filters(f->f.rewritePath("/deerav/cards/(?<segment>.*)","/${segment}")
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
								.requestRateLimiter(config-> config
										.setRateLimiter(redisRateLimiter())
										.setKeyResolver(userKeyResolver()))
						)
						.uri("lb://CARDS")).build();
	}


	@Bean
	public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
		return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
				.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
				.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4)).build()).build());
	}
	@Bean
	public RedisRateLimiter redisRateLimiter(){
		return new RedisRateLimiter(1,1,1);
	}

	@Bean
	KeyResolver userKeyResolver() {
		return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("user"))
				.defaultIfEmpty("anonymous");
	}
}
//
//
//package com.micro.gatewayserver;
//
//import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
//import io.github.resilience4j.timelimiter.TimeLimiterConfig;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
//import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
//import org.springframework.cloud.client.circuitbreaker.Customizer;
//import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
//import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.HttpMethod;
//import reactor.core.publisher.Mono;
//
//import java.time.Duration;
//
//@SpringBootApplication
//public class GatewayserverApplication {
//
//	public static void main(String[] args) {
//		SpringApplication.run(GatewayserverApplication.class, args);
//	}
//
//	@Bean
//	public RouteLocator deeRavBankRouteConfig(RouteLocatorBuilder builder,
//											  RedisRateLimiter rateLimiter,
//											  KeyResolver keyResolver) {
//		return builder.routes()
//				.route(p -> p.path("/deerav/accounts/**")
//						.filters(f -> resilient(f, "accounts", rateLimiter, keyResolver))
//						.uri("lb://ACCOUNTS"))
//				.route(p -> p.path("/deerav/loans/**")
//						.filters(f -> resilient(f, "loans", rateLimiter, keyResolver))
//						.uri("lb://LOANS"))
//				.route(p -> p.path("/deerav/cards/**")
//						.filters(f -> resilient(f, "cards", rateLimiter, keyResolver))
//						.uri("lb://CARDS"))
//				.build();
//	}
//
//	/** Applies rewrite + rate limiter + retry + circuit breaker to a route. */
//	private GatewayFilterSpec resilient(GatewayFilterSpec f, String service,
//										RedisRateLimiter rateLimiter, KeyResolver keyResolver) {
//		return f.rewritePath("/deerav/" + service + "/(?<segment>.*)", "/${segment}")
//				// 1. Rate limiter (outermost)
//				.requestRateLimiter(c -> c
//						.setRateLimiter(rateLimiter)
//						.setKeyResolver(keyResolver))
//				// 2. Retry (GET only, so non-idempotent POST/PUT are never repeated)
//				.retry(r -> r
//						.setRetries(3)
//						.setMethods(HttpMethod.GET)
//						.setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
//				// 3. Circuit breaker (innermost)
//				.circuitBreaker(c -> c
//						.setName(service + "CircuitBreaker")
//						.setFallbackUri("forward:/contactSupport"));
//	}
//
//	@Bean
//	public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
//		return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
//				.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
//				.timeLimiterConfig(TimeLimiterConfig.custom()
//						.timeoutDuration(Duration.ofSeconds(4)).build())
//				.build());
//	}
//
//	@Bean
//	public RedisRateLimiter redisRateLimiter() {
//		// replenishRate, burstCapacity, requestedTokens
//		return new RedisRateLimiter(10, 20, 1);
//	}
//
//	@Bean
//	public KeyResolver userKeyResolver() {
//		return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("user"))
//				.defaultIfEmpty("anonymous");
//	}
//}