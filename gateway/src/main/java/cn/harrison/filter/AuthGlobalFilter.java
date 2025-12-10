package cn.harrison.filter;

import cn.harrison.utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(AuthGlobalFilter.class);

    @Autowired
    private JwtUtils jwtUtils;

    // Whitelist
    private static final List<String> WHITELIST = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/reset-password",
            "/api/auth/send-code",
            "/api/ai/chat/stream"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        log.info("Incoming request: {} {}", request.getMethod(), path);

        // Check whitelist
        for (String allowPath : WHITELIST) {
            if (path.startsWith(allowPath)) {
                return chain.filter(exchange);
            }
        }

        // Get token
        String token = request.getHeaders().getFirst("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token == null || !jwtUtils.validateToken(token)) {
            log.warn("Unauthorized access to {}", path);
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        // Optional: Pass user info to downstream
        // Claims claims = jwtUtils.getClaims(token);
        // ServerHttpRequest mutatedRequest = request.mutate()
        //         .header("X-User-Id", claims.get("userId").toString())
        //         .build();
        // return chain.filter(exchange.mutate().request(mutatedRequest).build());

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
