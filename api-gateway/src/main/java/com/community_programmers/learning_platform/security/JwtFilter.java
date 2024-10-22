package com.community_programmers.learning_platform.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Config> {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public JwtFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (request.getURI().getPath().matches("/api/auth/register|/api/auth/login")) {
                return chain.filter(exchange);
            }

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION))
                return Mono.error(new RuntimeException("Authorization header missing"));

            String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
                return Mono.error(new RuntimeException("Invalid Authorization header"));

            String token = authorizationHeader.substring(7);

            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(jwtSecret.getBytes())
                        .parseClaimsJws(token)
                        .getBody();

                exchange = exchange.mutate()
                        .request(request.mutate().header("userId", String.valueOf(claims.get("userId"))).build())
                        .build();

            } catch (SignatureException e) {
                return Mono.error(new RuntimeException("Invalid JWT"));
            } catch (Exception e) {
                return Mono.error(new RuntimeException("JWT Validation error"));
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
    }
}
