package com.assignment.swapi.service;

import com.assignment.swapi.models.response.InformationResponse;
import com.assignment.swapi.models.response.ResponseStarship;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

@Service
public class InformationService {

    @Value("${swapi.url}")
    private String swapiBaseUrl;
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


    public InformationResponse getInformation() {

        ResponseStarship responseStarship = getStarshipOfDarthVader();
        long crewNumber = getCrewOnDeathStar();
        boolean isLeiaOnAlderaan = isLeiaOnAlderaan();

        return new InformationResponse(responseStarship, crewNumber, isLeiaOnAlderaan);
    }


    private ResponseStarship getStarshipOfDarthVader() {
        // get darth vader information
        // get starship url --> ping the starship???
        System.out.println("---- getting starship of darth vader-----");

        JsonNode darthVaderInformation = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(swapiPath)
                        .build(peopleResource, darthVaderId))
                .retrieve()
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
                .orElse(null);
        // TODO: return empty json if no starship url

        System.out.println("Starship URL: "+starshipUrl);
        String starshipUrlPath = starshipUrl.replaceAll(swapiBaseUrl, StringUtils.EMPTY);

        ResponseStarship starshipInformation = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(starshipUrlPath)
                        .build())
                .retrieve()
                .bodyToMono(ResponseStarship.class)
                .block();


        return starshipInformation;
    }

    private long getCrewOnDeathStar() {

        System.out.println("---- getting crew on death star -----");

        JsonNode deathStarInformation = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(swapiPath)
                        .build(starshipsResource, deathStarId))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (deathStarInformation != null) {
            System.out.println(deathStarInformation.toPrettyString());
        }

        String crewNumber = Optional.ofNullable(deathStarInformation)
                .map(jsonNode -> jsonNode.get("crew"))
                .map(JsonNode::asText)
                .orElse(null);

        if (StringUtils.isBlank(crewNumber)) {
            return 0L; // no crew member found in REST API
        }

        NumberFormat usNumberFormat = NumberFormat.getNumberInstance(Locale.US);
        try {
            return usNumberFormat.parse(crewNumber).longValue();
        } catch (ParseException e) {
            // TODO: throw new business exception to be handled by exception handler
            throw new RuntimeException(e);
        }

    }

    private boolean isLeiaOnAlderaan() {

        System.out.println("---- checking if leia on alderaan -----");

        // get alderaan information
        JsonNode alderaanInformation = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(swapiPath)
                        .build(planetsResource, alderaanId))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (alderaanInformation != null) {
            System.out.println(alderaanInformation.toPrettyString());
        }


        String regexToExtractIdFromUrl = swapiBaseUrl
                .replaceAll(".", "\\.")
                + "/"
                + peopleResource
                + "/(\\d)+/";

        Pattern p = Pattern.compile(regexToExtractIdFromUrl);

        ArrayNode residents = (ArrayNode) Optional.ofNullable(alderaanInformation)
                .map(i -> i.get("residents"))
                .filter(JsonNode::isArray)
                .filter(array -> array.size() > 0)
                .orElse(null);

        if (residents == null) {
            return false; // planet has no residents
        }

        boolean residentIds = StreamSupport
                .stream(residents.spliterator(), false)
                .map(JsonNode::asText)
                .peek(System.out::println)
                .map(s -> {
                    Matcher m = p.matcher(s);
                    if(m.matches()) {
                        String id = m.group(1);
                        System.out.println("The person ID is " + id);
                        return id;
                    }
                    // TODO: error handling?
                    return StringUtils.EMPTY;
                })
                .peek(System.out::println)
                .filter(StringUtils::isNotBlank)
                .map(Integer::valueOf) // regex will ensure only strings with digits reach here
                .anyMatch(personId -> personId.equals(leiaId));

        return residentIds;

    }
}
