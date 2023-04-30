package com.assignment.swapi.service;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
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

    static MockWebServer mockBackEnd;
    private final String MOCK_DEATH_STAR_RESPONSE = "{\n" +
            "\t\"name\": \"Death Star\",\n" +
            "\t\"model\": \"DS-1 Orbital Battle Station\",\n" +
            "\t\"manufacturer\": \"Imperial Department of Military Research, Sienar Fleet Systems\",\n" +
            "\t\"cost_in_credits\": \"1000000000000\",\n" +
            "\t\"length\": \"120000\",\n" +
            "\t\"max_atmosphering_speed\": \"n/a\",\n" +
            "\t\"crew\": \"342,953\",\n" +
            "\t\"passengers\": \"843,342\",\n" +
            "\t\"cargo_capacity\": \"1000000000000\",\n" +
            "\t\"consumables\": \"3 years\",\n" +
            "\t\"hyperdrive_rating\": \"4.0\",\n" +
            "\t\"MGLT\": \"10\",\n" +
            "\t\"starship_class\": \"Deep Space Mobile Battlestation\",\n" +
            "\t\"pilots\": [],\n" +
            "\t\"films\": [\n" +
            "\t\t\"https://swapi.dev/api/films/1/\"\n" +
            "\t],\n" +
            "\t\"created\": \"2014-12-10T16:36:50.509000Z\",\n" +
            "\t\"edited\": \"2014-12-20T21:26:24.783000Z\",\n" +
            "\t\"url\": \"https://swapi.dev/api/starships/9/\"\n" +
            "}";
    private InformationService informationService;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    // references
    // https://www.baeldung.com/spring-mocking-webclient
    // https://www.arhohuttunen.com/spring-boot-webclient-mockwebserver/
    // https://rieckpil.de/test-spring-webclient-with-mockwebserver-from-okhttp/

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    public void setup() throws IOException {
        System.out.println("--- Before called ----");
        informationService = new InformationService();
        ReflectionTestUtils.setField(informationService, "swapiPath", "/{resource}/{id}");
        ReflectionTestUtils.setField(informationService, "peopleResource", "people");
        ReflectionTestUtils.setField(informationService, "starshipsResource", "starships");
        ReflectionTestUtils.setField(informationService, "planetsResource", "planets");
        ReflectionTestUtils.setField(informationService, "deathStarId", 9);
        String baseUrl = String.format("http://localhost:%s",
                mockBackEnd.getPort());
        ReflectionTestUtils.setField(informationService, "webClient", WebClient.create(baseUrl));
    }

    @Test
    void getCrewOnDeathStar() {
        ReflectionTestUtils.setField(informationService, "webClient", WebClient.create("https://swapi.dev/api"));

        mockBackEnd.url("/starships/9");

        mockBackEnd.enqueue(
                new MockResponse().setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(MOCK_DEATH_STAR_RESPONSE)
        );

        Mono<Long> crewNumberMono = informationService.getCrewOnDeathStar();

        StepVerifier.create(crewNumberMono)
                .expectNextMatches(l -> l.equals(342953L))
                .verifyComplete();


    }

    @Test
    void getInformation() {
    }
}