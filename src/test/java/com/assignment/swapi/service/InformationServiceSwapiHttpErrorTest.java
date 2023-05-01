package com.assignment.swapi.service;

import com.assignment.swapi.exceptions.SwapiHttpErrorException;
import com.assignment.swapi.models.response.InformationResponse;
import com.assignment.swapi.models.response.ResponseStarship;
import com.assignment.swapi.utils.RegexUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit Test for Information Service
 * if SWAPI API returns 404 HTTP status code
 */
@RunWith(MockitoJUnitRunner.class)
class InformationServiceSwapiHttpErrorTest {

    static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String DEFAULT_ERROR_RESPONSE = "{\"starship\":{},\"crew\":0,\"isLeiaOnPlanet\":false}";
    private static final Dispatcher dispatcher = new Dispatcher() {
        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            return new MockResponse().setResponseCode(HttpStatus.NOT_FOUND.value());
        }
    };
    static MockWebServer mockBackEnd;
    private static InformationService informationService;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();

        mockBackEnd.setDispatcher(dispatcher);
        mockBackEnd.start();

        informationService = new InformationService();
        ReflectionTestUtils.setField(informationService, "swapiPath", "/{resource}/{id}");
        ReflectionTestUtils.setField(informationService, "peopleResource", "people");
        ReflectionTestUtils.setField(informationService, "starshipsResource", "starships");
        ReflectionTestUtils.setField(informationService, "planetsResource", "planets");
        ReflectionTestUtils.setField(informationService, "leiaId", 5);
        ReflectionTestUtils.setField(informationService, "darthVaderId", 4);
        ReflectionTestUtils.setField(informationService, "alderaanId", 2);
        ReflectionTestUtils.setField(informationService, "deathStarId", 9);


        String baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
        ReflectionTestUtils.setField(informationService, "webClient", WebClient.create(baseUrl));
        ReflectionTestUtils.setField(informationService, "regexUtils", new RegexUtils(
                "https://swapi.dev/api", "starships", "people"
        ));
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    void getCrewOnDeathStar() {
        Mono<Long> crewNumberMono = informationService.getCrewOnDeathStar();
        StepVerifier.create(crewNumberMono)
                .expectErrorMatches(throwable -> throwable instanceof SwapiHttpErrorException &&
                        throwable.getMessage().contains(String.valueOf(HttpStatus.NOT_FOUND.value())))
                .verify();
    }

    @Test
    void isLeiaOnAlderaan() {
        Mono<Boolean> leiaOnAlderaan = informationService.isLeiaOnAlderaan();
        StepVerifier.create(leiaOnAlderaan)
                .expectErrorMatches(throwable -> throwable instanceof SwapiHttpErrorException &&
                        throwable.getMessage().contains(String.valueOf(HttpStatus.NOT_FOUND.value())))
                .verify();
    }

    @Test
    void getStarShipInformationFromUrl() {
        Mono<ResponseStarship> starshipMono = informationService.getStarShipInformationFromUrl("https://swapi.dev/api/starships/13/");

        StepVerifier.create(starshipMono)
                .expectErrorMatches(throwable -> throwable instanceof SwapiHttpErrorException &&
                        throwable.getMessage().contains(String.valueOf(HttpStatus.NOT_FOUND.value())))
                .verify();
    }

    @Test
    void getStarshipUrlOfDarthVader() {
        Mono<String> starShipUrlOfDarthVader = informationService.getStarshipUrlOfDarthVader();
        StepVerifier.create(starShipUrlOfDarthVader)
                .expectErrorMatches(throwable -> throwable instanceof SwapiHttpErrorException &&
                        throwable.getMessage().contains(String.valueOf(HttpStatus.NOT_FOUND.value())))
                .verify();
    }

    @Test
    void getInformation() {
        Mono<InformationResponse> informationResponseMono = informationService.getInformation();
        StepVerifier.create(informationResponseMono)
                .expectNextMatches(informationResponse ->
                        Objects.nonNull(informationResponse.getStarship()) &&
                                informationResponse.getStarship().getName() == null &&
                                informationResponse.getStarship().getModel() == null &&
                                informationResponse.getStarship().getStarshipClass() == null &&
                                informationResponse.getCrew().equals(0L) &&
                                informationResponse.getIsLeiaOnPlanet().equals(false)
                )
                .verifyComplete();
        String informationResponseBody = informationResponseMono
                .map(value -> {
                    try {
                        return objectMapper.writeValueAsString(value);
                    } catch (JsonProcessingException e) {
                        return StringUtils.EMPTY;
                    }
                })
                .block();
        assertEquals(DEFAULT_ERROR_RESPONSE, informationResponseBody);
    }
}