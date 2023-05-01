package com.assignment.swapi.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RegexUtilsTest {

    private static final String BASE_URL = "https://swapi.dev/api";
    private static final String STARSHIPS_RESOURCE = "starships";
    private static final String PEOPLE_RESOURCE = "people";

    private static final RegexUtils regexUtils =
            new RegexUtils(BASE_URL, STARSHIPS_RESOURCE, PEOPLE_RESOURCE);

    @Test
    void extractStarShipIdFromUrl() {
        int expectedId = 145;
        String url = String.format("%s/%s/%s/", BASE_URL, STARSHIPS_RESOURCE, expectedId);

        assertEquals(expectedId, regexUtils.extractStarShipIdFromUrl(url));
    }

    @Test
    void extractStarShipIdFromUrl_when_invalid_id_then_null() {
        String url = String.format("%s/%s/%s/",
                BASE_URL, STARSHIPS_RESOURCE, "93ie");
        assertNull(regexUtils.extractStarShipIdFromUrl(url));
    }

    @Test
    void extractStarShipIdFromUrl_when_invalid_urlPattern_then_null() {
        String url = "lorem ipsum";
        assertNull(regexUtils.extractStarShipIdFromUrl(url));
    }

    @Test
    void extractPeopleIdFromUrl() {
        int expectedId = 68;
        String url = String.format("%s/%s/%s/", BASE_URL, PEOPLE_RESOURCE, expectedId);

        assertEquals(expectedId, regexUtils.extractPeopleIdFromUrl(url));
    }

    @Test
    void extractPeopleIdFromUrl_when_invalid_id_then_null() {
        String url = String.format("%s/%s/%s/",
                BASE_URL, PEOPLE_RESOURCE, "8e");
        assertNull(regexUtils.extractPeopleIdFromUrl(url));
    }

    @Test
    void extractPeopleIdFromUrl_when_invalid_urlPattern_then_null() {
        String url = "testing.com";
        assertNull(regexUtils.extractPeopleIdFromUrl(url));
    }
}