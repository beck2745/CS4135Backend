package com.skillswap.gateway.filter;

import com.skillswap.gateway.support.GatewayJwtSupport;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.UUID;

/**
 * Adds {@code X-Correlation-Id} for observability and strips/rebuilds trusted identity headers from JWT
 * (defence in depth with downstream services).
 */
@Component
public class CorrelationAndIdentityGatewayFilter implements GlobalFilter, Ordered {

    private final SecretKey jwtKey;

    public CorrelationAndIdentityGatewayFilter(@Value("${jwt.secret}") String jwtSecret) {
        this.jwtKey = GatewayJwtSupport.signingKey(jwtSecret);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String correlation = request.getHeaders().getFirst("X-Correlation-Id");
        if (correlation == null || correlation.isBlank()) {
            correlation = UUID.randomUUID().toString();
        }
        final String correlationId = correlation;

        ServerHttpRequest.Builder builder =
                request.mutate().header("X-Correlation-Id", correlationId);

        builder.headers(
                headers -> {
                    headers.remove("X-Authenticated-User-Id");
                    headers.remove("X-Authenticated-Role");
                });

        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            try {
                String token = authorization.substring(7);
                Claims claims = GatewayJwtSupport.parseClaims(token, jwtKey);
                Number userId = claims.get("userId", Number.class);
                String role = claims.get("role", String.class);
                if (userId != null) {
                    builder.header("X-Authenticated-User-Id", userId.toString());
                }
                if (role != null) {
                    builder.header("X-Authenticated-Role", role);
                }
            } catch (RuntimeException ignored) {
                // Invalid or expired token: do not inject identity headers; resource servers may return 401/403.
            }
        }

        return chain.filter(exchange.mutate().request(builder.build()).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
