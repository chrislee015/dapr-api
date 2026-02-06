package com.example.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfiguration {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;
    
    @Value("${gateway.cors.allowed-origins:https://localhost:3000,https://localhost:8080}")
    private List<String> allowedOrigins;
    
    @Value("${gateway.auth.success-redirect-url:${gateway.base-url:https://localhost:8080}/}")
    private String successRedirectUrl;

    @Bean
    SecurityFilterChain security(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter()))
                );

        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthConverter() {
        JwtGrantedAuthoritiesConverter rolesConverter =
                new JwtGrantedAuthoritiesConverter();

        rolesConverter.setAuthorityPrefix("ROLE_");
        rolesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(rolesConverter);
        return converter;
    }



//    @Bean
//    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//        return http
//            .authorizeExchange(exchanges -> exchanges
//                .pathMatchers("/auth/**", "/error", "/actuator/health").permitAll()
//                .pathMatchers("/.well-known/**").permitAll()
//                .anyExchange().authenticated()
//            )
//            .oauth2Login(oauth2 -> oauth2
//                .authenticationSuccessHandler(
//                    new RedirectServerAuthenticationSuccessHandler(successRedirectUrl)
//                )
//            )
//            .oauth2ResourceServer(oauth2 -> oauth2
//                .jwt(jwt -> jwt.jwtDecoder(jwtDecoder()))
//            )
//            .csrf(ServerHttpSecurity.CsrfSpec::disable)
//            .cors(cors -> cors.configurationSource(this::corsConfiguration))
//            .requiresChannel(channel ->
//                channel.requestMatchers(ServerWebExchangeMatchers.anyExchange())
//                       .requiresSecure()
//            )
//            .build();
//    }

    private CorsConfiguration corsConfiguration(ServerWebExchange exchange) {
        var corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);
        corsConfig.setAllowedOrigins(allowedOrigins);
        corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setMaxAge(3600L);
        return corsConfig;
    }

//    @Bean
//    public ReactiveJwtDecoder jwtDecoder() {
//        if (issuerUri == null || issuerUri.trim().isEmpty()) {
//            throw new IllegalArgumentException("JWT issuer URI must be configured");
//        }
//        try {
//            return ReactiveJwtDecoders.fromIssuerLocation(issuerUri);
//        } catch (Exception e) {
//            log.error("Failed to create JWT decoder for issuer: {}", issuerUri, e);
//            throw new IllegalStateException("Cannot initialize JWT decoder", e);
//        }
//    }
}
