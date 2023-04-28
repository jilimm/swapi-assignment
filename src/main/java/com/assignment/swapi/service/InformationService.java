package com.assignment.swapi.service;

import com.assignment.swapi.models.response.InformationResponse;
import com.assignment.swapi.models.response.ResponseStarship;
import com.assignment.swapi.utils.RegexUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
import java.util.stream.StreamSupport;

@Service
public class InformationService {
    //https://medium.com/@knoldus/spring-boot-combining-mono-s-358b83b7485a
    // https://stackoverflow.com/a/48183459
    // https://stackoverflow.com/questions/60850570/execute-three-mono-in-parallel-as-soon-they-are-created-wait-for-all-to-finish
// https://www.baeldung.com/spring-webclient-simultaneous-calls & https://copyprogramming.com/howto/zip-three-different-mono-of-different-type

    // parallel: https://www.amitph.com/spring-webclient-concurrent-calls/
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

    private static final NumberFormat US_NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);


    public Mono<InformationResponse> getInformation() {

        // TODO: webflux can be done in parallel???
        
        String  starShipUrl = getStarshipUrlOfDarthVader();
        Mono<ResponseStarship> responseStarship = getStarShipInformationFromUrl(starShipUrl);
        // ensure null starship gives empty json
        // https://stackoverflow.com/questions/44837846/spring-boot-return-a-empty-json-instead-of-empty-body-when-returned-object-is-n
        Mono<Long> crewNumber = getCrewOnDeathStar();
        Mono<Boolean> isLeiaOnAlderaan = isLeiaOnAlderaan();


        // https://stackoverflow.com/questions/50203875/how-to-use-spring-webclient-to-make-multiple-calls-simultaneously




        Mono<InformationResponse> informationResponseMono =
                Mono.zip(responseStarship, crewNumber, isLeiaOnAlderaan).map(data->{

            return new InformationResponse(data.getT1(), data.getT2(), data.getT3());
        });

        return informationResponseMono;

    }


    private String getStarshipUrlOfDarthVader() {
        // get darth vader information
        // get starship url --> ping the starship???
        System.out.println("---- getting starship of darth vader-----");

        JsonNode darthVaderInformation = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(swapiPath)
                        .build(peopleResource, darthVaderId))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> Mono.empty())
                .bodyToMono(JsonNode.class)
                .block();

        if (darthVaderInformation != null) {
            System.out.println(darthVaderInformation.toPrettyString());
        }

        String starshipUrl = Optional.ofNullable(darthVaderInformation)
                .map(i -> i.get("starships"))
                .filter(JsonNode::isArray)
                .filter(array -> array.size() > 0)
                .map(array -> array.get(0))
                .map(JsonNode::asText)
                // TODO: check that url is correct??? idk
                .orElse(null);

        return starshipUrl;

    }

    private Mono<ResponseStarship> getStarShipInformationFromUrl(String urlPath) {

        if (StringUtils.isBlank(urlPath)) {
            return Mono.empty(); // will return empty json body
        }

        System.out.println("Starship URL: "+urlPath);
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
                .bodyToMono(ResponseStarship.class);


        return starshipInformation;
    }

    private Mono<Long> getCrewOnDeathStar() {

        System.out.println("---- getting crew on death star -----");

        Mono<Long> crew = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(swapiPath)
                        .build(starshipsResource, deathStarId))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> Mono.empty())
                .bodyToMono(JsonNode.class)
                .map(jsonNode -> {
                    String crewNumber = Optional.ofNullable(jsonNode)
                            .map(i -> i.get("crew"))
                            .map(JsonNode::asText)
                            .orElse(null);
                    if (StringUtils.isBlank(crewNumber)) {
                        return 0L; // no crew member found in REST API
                    }
                    try {
                        return US_NUMBER_FORMAT.parse(crewNumber).longValue();
                    } catch (ParseException e) {
                        // TODO: throw new business exception to be handled by exception handler
                        throw new RuntimeException(e);
                    }
                });

        return crew;

    }

    private Mono<Boolean> isLeiaOnAlderaan() {

        System.out.println("---- checking if leia on alderaan -----");

        // get alderaan information
        Mono<Boolean> alderaanInformation = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(swapiPath)
                        .build(planetsResource, alderaanId))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> Mono.empty())
                .bodyToMono(JsonNode.class)
                .map( jsonNode -> {
                    ArrayNode residents = (ArrayNode) Optional.ofNullable(jsonNode)
                            .map(i -> i.get("residents"))
                            .filter(JsonNode::isArray)
                            .filter(array -> array.size() > 0)
                            .orElse(null);
                    if (residents == null) {
                        return false; // planet has no residents
                    }
                    boolean residentIds = StreamSupport
                            .stream(residents.spliterator(), false) // FIXME: stream can be parallel?
                            .map(JsonNode::asText)
                            .peek(System.out::println)
                            .map(regexUtils::extractPeopleIdFromUrl)
                            .filter(Objects::nonNull)
                            .anyMatch(personId -> personId.equals(leiaId));
                    return residentIds;

                });


        return alderaanInformation;

    }



}
