package com.programming.techie.apigateway.config;

import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.web.servlet.function.RequestPredicates.path;
import static org.springframework.web.servlet.function.RouterFunctions.route;

/**
 * Gateway configuration for routing requests to microservices.
 * Implements routers for product, order, and inventory services.
 */
@Configuration
public class GatewayConfig {

	@Bean
	public RouterFunction<ServerResponse> productServiceRouter() {
		return GatewayRouterFunctions.route("product-service")
				.route(path("/api/products/**"), HandlerFunctions.http("lb://product-service"))
				.build();
	}

	@Bean
	public RouterFunction<ServerResponse> orderServiceRouter() {
		return GatewayRouterFunctions.route("order-service")
				.route(path("/api/orders/**"), HandlerFunctions.http("lb://order-service"))
				.build();
	}

	@Bean
	public RouterFunction<ServerResponse> inventoryServiceRouter() {
		return GatewayRouterFunctions.route("inventory-service")
				.route(path("/api/inventory/**"), HandlerFunctions.http("lb://inventory-service"))
				.build();
	}

	@Bean
	public RouterFunction<ServerResponse> gatewayRouter(
			RouterFunction<ServerResponse> productServiceRouter,
			RouterFunction<ServerResponse> orderServiceRouter,
			RouterFunction<ServerResponse> inventoryServiceRouter) {
		return route()
				.add(productServiceRouter)
				.add(orderServiceRouter)
				.add(inventoryServiceRouter)
				.build();
	}

}
