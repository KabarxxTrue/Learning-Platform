package com.community_programmers.learning_platform.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtFilter implements WebFilter, Ordered {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION))
            return Mono.error(new RuntimeException("Authorization header missing"));

        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
            return Mono.error(new RuntimeException("Invalid Authorization header"));

        String token = authorizationHeader.substring(7);

        try {
            Claims claims = Jwts.parser().setSigningKey(jwtSecret.getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            exchange.getRequest().mutate().header("userId", String.valueOf(claims.get("userId")))
                    .build();
        } catch (SignatureException e) {
            return Mono.error(new RuntimeException("Invalid JWT"));
        } catch (Exception e) {
            return Mono.error(new RuntimeException("JWT Validation error"));
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
