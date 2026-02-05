package com.example.gateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> 
    implements Ordered {

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.debug("Processing authentication for request: {}", exchange.getRequest().getPath());
            
            return ReactiveSecurityContextHolder.getContext()
                .cast(org.springframework.security.core.context.SecurityContext.class)
                .map(securityContext -> securityContext.getAuthentication())
                .cast(JwtAuthenticationToken.class)
                .flatMap(authentication -> {
                    Jwt jwt = authentication.getToken();
                    
                    // Extract claims and roles
                    Map<String, Object> claims = jwt.getClaims();
                    List<String> roles = extractRoles(jwt);
                    
                    log.info("User authenticated: {}", jwt.getSubject());
                    log.debug("User claims: {}", claims);
                    log.debug("User roles: {}", roles);
                    
                    // Add claims and roles to request headers
                    var mutatedRequest = exchange.getRequest().mutate()
                        .header("X-User-Subject", jwt.getSubject())
                        .header("X-User-Claims", claims.toString())
                        .header("X-User-Roles", String.join(",", roles))
                        .header("X-User-Email", jwt.getClaimAsString("email"))
                        .header("X-User-Name", jwt.getClaimAsString("name"))
                        .build();
                    
                    var mutatedExchange = exchange.mutate().request(mutatedRequest).build();
                    return chain.filter(mutatedExchange);
                })
                .onErrorResume(error -> {
                    log.error("Authentication failed: {}", error.getMessage());
                    return Mono.error(new RuntimeException("Authentication required"));
                });
        };
    }

    @SuppressWarnings("unchecked")
    private List<String> extractRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            return (List<String>) realmAccess.get("roles");
        }
        
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess != null) {
            // Extract roles from resource access
            return resourceAccess.values().stream()
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .filter(resource -> resource.containsKey("roles"))
                .flatMap(resource -> ((List<String>) resource.get("roles")).stream())
                .toList();
        }
        
        return List.of();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    public static class Config {
        // Configuration properties if needed
    }
}
