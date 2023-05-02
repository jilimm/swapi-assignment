package com.assignment.swapi.service;

import com.assignment.swapi.models.response.InformationResponse;
import com.assignment.swapi.models.response.ResponseStarship;
import com.assignment.swapi.utils.RegexUtils;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
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


@RunWith(MockitoJUnitRunner.class)
class InformationServiceTest {

    private static final String MOCK_DEATH_STAR_RESPONSE = "{\n" +
            "\t\"crew\": \"342,953\",\n" +
            "\t\"url\": \"https://swapi.dev/api/starships/9/\"\n" +
            "}";
    private static final String MOCK_ALDERAAN_RESPONSE = "{\n" +
            "\t\"residents\": [\n" +
            "\t\t\"https://swapi.dev/api/people/5/\",\n" +
            "\t\t\"https://swapi.dev/api/people/68/\",\n" +
            "\t\t\"https://swapi.dev/api/people/81/\"\n" +
            "\t],\n" +
            "\t\"url\": \"https://swapi.dev/api/planets/2/\"\n" +
            "}";
    private static final String MOCK_DART_VADER_RESPONSE = "{\n" +
            "\t\"starships\": [\n" +
            "\t\t\"https://swapi.dev/api/starships/13/\"\n" +
            "\t],\n" +
            "\t\"url\": \"https://swapi.dev/api/people/4/\"\n" +
            "}";
    private static final String MOCK_DARTH_VADER_STARSHIP_RESPONSE = "{\n" +
            "\t\"name\": \"TIE Advanced x1\",\n" +
            "\t\"model\": \"Twin Ion Engine Advanced x1\",\n" +
            "\t\"starship_class\": \"Starfighter\",\n" +
            "\t\"url\": \"https://swapi.dev/api/starships/13/\"\n" +
            "}";

    private static final long EXPECTED_CREW_NUMBER = 342953L;
    private static final Dispatcher dispatcher = new Dispatcher() {

        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {

            switch (request.getPath()) {
                case "/starships/9":
                    return new MockResponse().setResponseCode(200)
                            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .setBody(MOCK_DEATH_STAR_RESPONSE);
                case "/planets/2":
                    return new MockResponse().setResponseCode(200)
                            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .setBody(MOCK_ALDERAAN_RESPONSE);
                case "/people/4":
                    return new MockResponse().setResponseCode(200)
                            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .setBody(MOCK_DART_VADER_RESPONSE);
                case "/starships/13":
                    return new MockResponse().setResponseCode(200)
                            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .setBody(MOCK_DARTH_VADER_STARSHIP_RESPONSE);

            }
            return new MockResponse().setResponseCode(404);
        }
    };
    static MockWebServer mockBackEnd;
    static InformationService informationService;

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
        ReflectionTestUtils.setField(informationService, "leiaId", 5);
        ReflectionTestUtils.setField(informationService, "darthVaderId", 4);
        ReflectionTestUtils.setField(informationService, "alderaanId", 2);
        ReflectionTestUtils.setField(informationService, "deathStarId", 9);
    }

    @Test
    void getCrewOnDeathStar() {
        Mono<Long> crewNumberMono = informationService.getCrewOnDeathStar();

        StepVerifier.create(crewNumberMono)
                .expectNextMatches(l -> l.equals(EXPECTED_CREW_NUMBER))
                .verifyComplete();
    }

    @Test
    void isLeiaOnAlderaan() {
        Mono<Boolean> crewNumberMono = informationService.isLeiaOnAlderaan();

        StepVerifier.create(crewNumberMono)
                .expectNextMatches(b -> b.equals(true))
                .verifyComplete();
    }

    @Test
    void isLeiaOnAlderaan_when_leia_not_in_alderaan_then_false() {
        ReflectionTestUtils.setField(informationService, "leiaId", 20);
        Mono<Boolean> crewNumberMono = informationService.isLeiaOnAlderaan();

        StepVerifier.create(crewNumberMono)
                .expectNextMatches(b -> b.equals(false))
                .verifyComplete();
    }

    @Test
    void getStarShipInformationFromUrl() {
        Mono<ResponseStarship> starshipMono = informationService.getStarShipInformationFromUrl("https://swapi.dev/api/starships/13/");

        StepVerifier.create(starshipMono)
                .expectNextMatches(starship ->
                        starship.getName().equals("TIE Advanced x1") &&
                                starship.getModel().equals("Twin Ion Engine Advanced x1") &&
                                starship.getStarshipClass().equals("Starfighter")
                )
                .verifyComplete();
    }

    @Test
    void getStarshipUrlOfDarthVader() {
        Mono<String> darthVaderStarShipUrl = informationService.getStarshipUrlOfDarthVader();

        StepVerifier.create(darthVaderStarShipUrl)
                .expectNextMatches(url -> url.equals("https://swapi.dev/api/starships/13/"))
                .verifyComplete();
    }

    @Test
    void getInformation() {
        Mono<InformationResponse> informationResponseMono = informationService.getInformation();

        StepVerifier.create(informationResponseMono)
                .expectNextMatches(informationResponse ->
                        informationResponse.getCrew().equals(EXPECTED_CREW_NUMBER) &&
                                informationResponse.getIsLeiaOnPlanet().equals(true) &&
                                informationResponse.getStarship().getName().equals("TIE Advanced x1") &&
                                informationResponse.getStarship().getModel().equals("Twin Ion Engine Advanced x1") &&
                                informationResponse.getStarship().getStarshipClass().equals("Starfighter")
                )
                .verifyComplete();

    }

}