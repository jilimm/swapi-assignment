package com.assignment.swapi.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Configuration
@Slf4j
public class ExternalWebClients  {

    @Value("${swapi.url}")
    private String swapiUrl;

    @Bean(name = "swapiWebClient")
    public WebClient swapiWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(swapiUrl)
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(logRequest());
                    exchangeFilterFunctions.add(logResponse());
                })
                .build();
    }

    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("INFO: {} \t {} {} ",clientRequest.logPrefix(),clientRequest.method(),clientRequest.url());
            return Mono.just(clientRequest);
        });
    }


    private static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("INFO: {} \t ResponseStatus {} ",clientResponse.logPrefix(),clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }
}

