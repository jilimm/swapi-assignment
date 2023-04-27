package com.assignment.swapi.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class ExternalWebClients {


    @Value("${swapi.url}")
    private String swapiUrl;

    @Bean(name = "swapiWebClient")
    public WebClient swapiWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(swapiUrl)
                .build();
    }
}
