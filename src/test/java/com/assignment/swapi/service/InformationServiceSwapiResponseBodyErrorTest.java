package com.assignment.swapi.service;

import com.assignment.swapi.exceptions.ParseSwapiResponseExcpetion;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit Test for Information Service
 * SWAPI API returns 2XX HTTP status code
 * but response body is not valid
 *  - json node contains invalid information
 *  - json node does not exist in json response body
 */
@RunWith(MockitoJUnitRunner.class)
class InformationServiceSwapiResponseBodyErrorTest {

    static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String DEATH_STAR_RESPONSE_WITH_INVALID_CREW_NUMBER = "{\n" +
            "\t\"crew\": \"invalidNumber\",\n" +
            "\t\"url\": \"https://swapi.dev/api/starships/9/\"\n" +
            "}";
    private static final String DEFAULT_ERROR_RESPONSE = "{\"starship\":{},\"crew\":0,\"isLeiaOnPlanet\":false}";
    private static final String MOCK_ALDERAAN_RESPONSE_INVALID_RESIDENTS_LIST = "{\n" +
            "\t\"residents\": \"invalidList\",\n" +
            "\t\"url\": \"https://swapi.dev/api/planets/2/\"\n" +
            "}";
    private static final String MOCK_DART_VADER_RESPONSE_EMPTY_STARSHIPS_LIST = "{\n" +
            "\t\"starships\": [\n" +
            "\t],\n" +
            "\t\"url\": \"https://swapi.dev/api/people/4/\"\n" +
            "}";
    private static final String MOCK_DARTH_VADER_STARSHIP_RESPONSE = "{\n" +
            "\t\"name\": \"TIE Advanced x1\",\n" +
            "\t\"model\": \"Twin Ion Engine Advanced x1\",\n" +
            "\t\"starship_class\": \"Starfighter\",\n" +
            "\t\"url\": \"https://swapi.dev/api/starships/13/\"\n" +
            "}";
    private static final Dispatcher dispatcher = new Dispatcher() {

        @Override
        public MockResponse dispatch(RecordedRequest request)  {

            switch (request.getPath()) {
                case "/starships/9":
                    return new MockResponse().setResponseCode(200)
                            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .setBody(DEATH_STAR_RESPONSE_WITH_INVALID_CREW_NUMBER);
                case "/planets/2":
                    return new MockResponse().setResponseCode(200)
                            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .setBody(MOCK_ALDERAAN_RESPONSE_INVALID_RESIDENTS_LIST);
                case "/people/4":
                    return new MockResponse().setResponseCode(200)
                            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .setBody(MOCK_DART_VADER_RESPONSE_EMPTY_STARSHIPS_LIST);
                case "/starships/13":
                    return new MockResponse().setResponseCode(200)
                            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .setBody(MOCK_DARTH_VADER_STARSHIP_RESPONSE);

            }
            return new MockResponse().setResponseCode(404);
        }
    };

    private static final Dispatcher empty_json_dispatcher = new Dispatcher() {
        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            return new MockResponse().setResponseCode(200)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody("{}");
        }
    };
    static MockWebServer mockBackEnd;
    private static InformationService informationService;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();

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

    @BeforeEach
    public void setup() throws IOException {
        mockBackEnd.setDispatcher(dispatcher);
    }

    @Test
    void getCrewOnDeathStar() {
        Mono<Long> crewNumberMono = informationService.getCrewOnDeathStar();
        StepVerifier.create(crewNumberMono)
                .expectErrorMatches(throwable -> throwable instanceof ParseSwapiResponseExcpetion &&
                        throwable.getMessage().contains("No valid crew number found in SWAPI response"))
                .verify();
    }

    @Test
    void getCrewOnDeathStar_when_empty_response_then_exception_thrown() {
        mockBackEnd.setDispatcher(empty_json_dispatcher);
        Mono<Long> crewNumberMono = informationService.getCrewOnDeathStar();
        StepVerifier.create(crewNumberMono)
                .expectErrorMatches(throwable -> throwable instanceof ParseSwapiResponseExcpetion &&
                        throwable.getMessage().contains("No valid crew number found in SWAPI response"))
                .verify();
    }

    @Test
    void isLeiaOnAlderaan() {
        Mono<Boolean> leiaOnAlderaan = informationService.isLeiaOnAlderaan();
        StepVerifier.create(leiaOnAlderaan)
                .expectErrorMatches(throwable -> throwable instanceof ParseSwapiResponseExcpetion &&
                        throwable.getMessage().contains("No residents list found in SWAPI response"))
                .verify();
    }

    @Test
    void isLeiaOnAlderaan_when_empty_response_then_exception_thrown() {
        mockBackEnd.setDispatcher(empty_json_dispatcher);

        Mono<Boolean> leiaOnAlderaan = informationService.isLeiaOnAlderaan();
        StepVerifier.create(leiaOnAlderaan)
                .expectErrorMatches(throwable -> throwable instanceof ParseSwapiResponseExcpetion &&
                        throwable.getMessage().contains("No residents list found in SWAPI response"))
                .verify();
    }

    @Test
    void getStarShipInformationFromUrl() {
        Mono<ResponseStarship> starshipMono = informationService.getStarShipInformationFromUrl("invalidUrl");

        StepVerifier.create(starshipMono)
                .expectErrorMatches(throwable -> throwable instanceof ParseSwapiResponseExcpetion &&
                        throwable.getMessage().contains("Invalid Starship URL"))
                .verify();
    }

    @Test
    void getStarShipInformationFromUrl_when_url_null_exception_thrown() {
        Mono<ResponseStarship> starshipMono = informationService.getStarShipInformationFromUrl(null);

        StepVerifier.create(starshipMono)
                .expectErrorMatches(throwable -> throwable instanceof ParseSwapiResponseExcpetion &&
                        throwable.getMessage().contains("Invalid Starship URL"))
                .verify();
    }

    @Test
    void getStarshipUrlOfDarthVader() {
        Mono<String> starShipUrlOfDarthVader = informationService.getStarshipUrlOfDarthVader();
        StepVerifier.create(starShipUrlOfDarthVader)
                .expectErrorMatches(throwable -> throwable instanceof ParseSwapiResponseExcpetion &&
                        throwable.getMessage().contains("No valid starship URL found"))
                .verify();
    }

    @Test
    void getStarshipUrlOfDarthVader_when_empty_response_then_exception_thrown() {
        mockBackEnd.setDispatcher(empty_json_dispatcher);

        Mono<String> starShipUrlOfDarthVader = informationService.getStarshipUrlOfDarthVader();
        StepVerifier.create(starShipUrlOfDarthVader)
                .expectErrorMatches(throwable -> throwable instanceof ParseSwapiResponseExcpetion &&
                        throwable.getMessage().contains("No valid starship URL found"))
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

    @Test
    void getInformation_when_empty_response_then_default_response_returned() {
        mockBackEnd.setDispatcher(empty_json_dispatcher);

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