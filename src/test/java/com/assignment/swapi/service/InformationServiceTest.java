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

    static MockWebServer mockBackEnd;

    private static final String MOCK_DEATH_STAR_RESPONSE = "{\n" +
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

    private static final String MOCK_ALDERAAN_RESPONSE = "{\n" +
            "\t\"name\": \"Alderaan\",\n" +
            "\t\"rotation_period\": \"24\",\n" +
            "\t\"orbital_period\": \"364\",\n" +
            "\t\"diameter\": \"12500\",\n" +
            "\t\"climate\": \"temperate\",\n" +
            "\t\"gravity\": \"1 standard\",\n" +
            "\t\"terrain\": \"grasslands, mountains\",\n" +
            "\t\"surface_water\": \"40\",\n" +
            "\t\"population\": \"2000000000\",\n" +
            "\t\"residents\": [\n" +
            "\t\t\"https://swapi.dev/api/people/5/\",\n" +
            "\t\t\"https://swapi.dev/api/people/68/\",\n" +
            "\t\t\"https://swapi.dev/api/people/81/\"\n" +
            "\t],\n" +
            "\t\"films\": [\n" +
            "\t\t\"https://swapi.dev/api/films/1/\",\n" +
            "\t\t\"https://swapi.dev/api/films/6/\"\n" +
            "\t],\n" +
            "\t\"created\": \"2014-12-10T11:35:48.479000Z\",\n" +
            "\t\"edited\": \"2014-12-20T20:58:18.420000Z\",\n" +
            "\t\"url\": \"https://swapi.dev/api/planets/2/\"\n" +
            "}";

    private static final String MOCK_DART_VADER_RESPONSE = "{\n" +
            "\t\"name\": \"Darth Vader\",\n" +
            "\t\"height\": \"202\",\n" +
            "\t\"mass\": \"136\",\n" +
            "\t\"hair_color\": \"none\",\n" +
            "\t\"skin_color\": \"white\",\n" +
            "\t\"eye_color\": \"yellow\",\n" +
            "\t\"birth_year\": \"41.9BBY\",\n" +
            "\t\"gender\": \"male\",\n" +
            "\t\"homeworld\": \"https://swapi.dev/api/planets/1/\",\n" +
            "\t\"films\": [\n" +
            "\t\t\"https://swapi.dev/api/films/1/\",\n" +
            "\t\t\"https://swapi.dev/api/films/2/\",\n" +
            "\t\t\"https://swapi.dev/api/films/3/\",\n" +
            "\t\t\"https://swapi.dev/api/films/6/\"\n" +
            "\t],\n" +
            "\t\"species\": [],\n" +
            "\t\"vehicles\": [],\n" +
            "\t\"starships\": [\n" +
            "\t\t\"https://swapi.dev/api/starships/13/\"\n" +
            "\t],\n" +
            "\t\"created\": \"2014-12-10T15:18:20.704000Z\",\n" +
            "\t\"edited\": \"2014-12-20T21:17:50.313000Z\",\n" +
            "\t\"url\": \"https://swapi.dev/api/people/4/\"\n" +
            "}";

    private static final String MOCK_DARTH_VADER_STARSHIP_RESPONSE = "{\n" +
            "\t\"name\": \"TIE Advanced x1\",\n" +
            "\t\"model\": \"Twin Ion Engine Advanced x1\",\n" +
            "\t\"manufacturer\": \"Sienar Fleet Systems\",\n" +
            "\t\"cost_in_credits\": \"unknown\",\n" +
            "\t\"length\": \"9.2\",\n" +
            "\t\"max_atmosphering_speed\": \"1200\",\n" +
            "\t\"crew\": \"1\",\n" +
            "\t\"passengers\": \"0\",\n" +
            "\t\"cargo_capacity\": \"150\",\n" +
            "\t\"consumables\": \"5 days\",\n" +
            "\t\"hyperdrive_rating\": \"1.0\",\n" +
            "\t\"MGLT\": \"105\",\n" +
            "\t\"starship_class\": \"Starfighter\",\n" +
            "\t\"pilots\": [\n" +
            "\t\t\"https://swapi.dev/api/people/4/\"\n" +
            "\t],\n" +
            "\t\"films\": [\n" +
            "\t\t\"https://swapi.dev/api/films/1/\"\n" +
            "\t],\n" +
            "\t\"created\": \"2014-12-12T11:21:32.991000Z\",\n" +
            "\t\"edited\": \"2014-12-20T21:23:49.889000Z\",\n" +
            "\t\"url\": \"https://swapi.dev/api/starships/13/\"\n" +
            "}";
    private InformationService informationService;

    private static final Dispatcher dispatcher = new Dispatcher() {

        @Override
        public MockResponse dispatch (RecordedRequest request) throws InterruptedException {

            switch (request.getPath()) {
                case "/starships/9":
                    return  new MockResponse().setResponseCode(200)
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

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();

        mockBackEnd.setDispatcher(dispatcher);
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    public void setup() throws IOException {
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

    @Test
    void getCrewOnDeathStar() {
        Mono<Long> crewNumberMono = informationService.getCrewOnDeathStar();

        StepVerifier.create(crewNumberMono)
                .expectNextMatches(l -> l.equals(342953L))
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
                    informationResponse.getCrew().equals(342953L) &&
                            informationResponse.getIsLeiaOnPlanet().equals(true) &&
                            informationResponse.getStarship().getName().equals("TIE Advanced x1") &&
                            informationResponse.getStarship().getModel().equals("Twin Ion Engine Advanced x1") &&
                            informationResponse.getStarship().getStarshipClass().equals("Starfighter")
                )
                .verifyComplete();

    }

    @Test
    void handleSwapiErrorResponse() {
        Dispatcher dispatcher404 = new Dispatcher() {
            @Override
            public MockResponse dispatch (RecordedRequest request) throws InterruptedException {
                return new MockResponse().setResponseCode(404);
            }
        };
        mockBackEnd.setDispatcher(dispatcher404);

        Mono<Long> crewNumberMono = informationService.getCrewOnDeathStar();
        StepVerifier.create(crewNumberMono).expectNextCount(0).verifyComplete();

        Mono<String> starShipUrlOfDarthVader = informationService.getStarshipUrlOfDarthVader();
        StepVerifier.create(starShipUrlOfDarthVader).expectNextCount(0).verifyComplete();

        Mono<Boolean> leiaOnAlderaan = informationService.isLeiaOnAlderaan();
        StepVerifier.create(leiaOnAlderaan).expectNextCount(0).verifyComplete();



       mockBackEnd.setDispatcher(dispatcher);
    }


    // TODO:
    // not happy cases
    // 404: // get mockserver to only return 404, all methods shuold return empty??
    // Crew -> empty body/ invalid integer body
    //      -> no "crew" jsonNode
    // Leia -> empty resident array
    //      -> resident array does not contain leia
    // DarthVader -> no starships/empty object



}