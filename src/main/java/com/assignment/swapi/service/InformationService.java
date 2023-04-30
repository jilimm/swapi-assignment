package com.assignment.swapi.service;

import com.assignment.swapi.models.response.InformationResponse;
import com.assignment.swapi.models.response.ResponseStarship;
import com.assignment.swapi.utils.RegexUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class InformationService {
    // deafult values
    public static final ResponseStarship DEFAULT_RESPONSE_STARSHIP = new ResponseStarship();
    public static final Long DEFAULT_CREW_NUMBER = 0L;
    public static final Boolean DEFAULT_LEIA_ON_ALDERAAN = false;
    private static final NumberFormat US_NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);
    @Value("${swapi.url.path}")
    private String swapiPath;
    @Value("${swapi.url.people}")
    private String peopleResource;
    @Value("${swapi.url.starships}")
    private String starshipsResource;
    @Value("${swapi.url.planets}")
    private String planetsResource;
    @Value("${swapi.url.people.princess-leia.id}")
    private int leiaId;
    @Value("${swapi.url.people.darth-vader.id}")
    private int darthVaderId;
    @Value("${swapi.url.planets.alderaan.id}")
    private int alderaanId;
    @Value("${swapi.url.starships.death-star.id}")
    private int deathStarId;
    @Autowired
    @Qualifier("swapiWebClient")
    private WebClient webClient;
    @Autowired
    private RegexUtils regexUtils;

    public Mono<InformationResponse> getInformation() {

        Mono<ResponseStarship> responseStarship = getStarshipUrlOfDarthVader()
                .flatMap(this::getStarShipInformationFromUrl)
                .defaultIfEmpty(DEFAULT_RESPONSE_STARSHIP);
        Mono<Long> crewNumber = getCrewOnDeathStar()
                .defaultIfEmpty(DEFAULT_CREW_NUMBER);
        Mono<Boolean> isLeiaOnAlderaan = isLeiaOnAlderaan()
                .defaultIfEmpty(DEFAULT_LEIA_ON_ALDERAAN);


        return
                Mono.zip(responseStarship, crewNumber, isLeiaOnAlderaan)
                        .map(data ->
                                new InformationResponse(data.getT1(), data.getT2(), data.getT3()));
    }


    public Mono<String> getStarshipUrlOfDarthVader() {
        // get darth vader information
        // get starship url --> ping the starship???
        log.info("---- getting starship of darth vader-----");

        Mono<String> darthVaderStarShipUrl = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(swapiPath)
                        .build(peopleResource, darthVaderId))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.empty())
                .bodyToMono(JsonNode.class)
                .map(jsonNode -> Optional.ofNullable(jsonNode.get("starships"))
                        .filter(JsonNode::isArray)
                        .filter(array -> array.size() > 0)
                        .map(array -> array.get(0))
                        .map(JsonNode::asText)
                )
                .flatMap(Mono::justOrEmpty)
                .filter(StringUtils::isNotBlank);

        return darthVaderStarShipUrl;

    }

    public Mono<ResponseStarship> getStarShipInformationFromUrl(String urlPath) {
        log.info("--- getting starship information from url: " + urlPath);

        log.info("Starship URL: " + urlPath);
        Integer starShipId = regexUtils.extractStarShipIdFromUrl(urlPath);

        if (starShipId == null) {
            return Mono.empty(); // starship id is null / not valid
        }

        Mono<ResponseStarship> starshipInformation = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(swapiPath)
                        .build(starshipsResource, starShipId)
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.empty())
                .bodyToMono(ResponseStarship.class);


        return starshipInformation;
    }

    public Mono<Long> getCrewOnDeathStar() {

        log.info("---- getting crew on death star -----");

        Mono<Long> crew = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(swapiPath)
                        .build(starshipsResource, deathStarId))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.empty())
                .bodyToMono(JsonNode.class)
                .map(jsonNode -> Optional.ofNullable(jsonNode)
                        .map(i -> i.get("crew"))
                        .filter(JsonNode::isTextual)
                        .map(JsonNode::asText)
                        .filter(StringUtils::isNotBlank)
                        .map(crewNumberString -> {
                            try {
                                return US_NUMBER_FORMAT.parse(crewNumberString);
                            } catch (ParseException e) {
                                log.error("Invalid crew number: {}", crewNumberString);
                                return null;
                            }
                        }))
                .flatMap(Mono::justOrEmpty)
                .map(Number::longValue)
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return Mono.empty();
                });

        return crew;

    }

    public Mono<Boolean> isLeiaOnAlderaan() {

        log.info("---- checking if leia on alderaan -----");

        // get alderaan information
        Flux<Integer> alderaanInformation = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(swapiPath)
                        .build(planetsResource, alderaanId))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.empty())
                .bodyToMono(JsonNode.class)
                .map(jsonNode ->
                        Optional.ofNullable(jsonNode)
                                .map(i -> i.get("residents"))
                                .filter(JsonNode::isArray)
                                .filter(array -> array.size() > 0))
                .flatMap(Mono::justOrEmpty)
                .flatMapIterable(arrayNode -> arrayNode)
                .filter(JsonNode::isTextual)
                .map(JsonNode::asText)
                // TODO: add client ID in log?
                .log()
                .map(regexUtils::extractPeopleIdFromUrl)
                .filter(Objects::nonNull);

        boolean isNotEmpty = Boolean.TRUE.equals(alderaanInformation
                .hasElements()
                .block());

        if (isNotEmpty) {
            return alderaanInformation.hasElement(leiaId);
        } else {
            return Mono.empty();
        }

    }


}
