package com.gwatcho.orderservice.config;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.client.RestClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;


@Configuration
@Slf4j
public class ProductServiceRestClientConfig {

    @Bean
    public RestClient productServiceRestClient(@Value("${services.product-service.url}")
            String productServiceUrl) {
        log.info(
                "Creating ProductService RestClient with base URL: {}",
                productServiceUrl
        );
        return RestClient.builder()
                .baseUrl(productServiceUrl)
                .requestInterceptor((request, body, execution) -> {

                    Authentication authentication =
                            SecurityContextHolder.getContext().getAuthentication();

                    if (authentication instanceof JwtAuthenticationToken jwtAuth) {
                        request.getHeaders().setBearerAuth(
                                jwtAuth.getToken().getTokenValue()
                        );
                    } else {
                        log.warn("No authenticated JWT available for Product Service request");
                    }
                    log.info(
                            "PRODUCT REQUEST: {} {}",
                            request.getMethod(),
                            request.getURI()
                    );
                    var response = execution.execute(request, body);
                    log.info(
                            "PRODUCT RESPONSE: {} {}",
                            response.getStatusCode(),
                            request.getURI()
                    );

                    return response;
                })
                .build();
    }
}