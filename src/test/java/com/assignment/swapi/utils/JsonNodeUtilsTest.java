package com.assignment.swapi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JsonNodeUtilsTest {

    @InjectMocks
    JsonNodeUtils jsonNodeUtils;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static JsonNode EXAMPLE_JSON_NODE;
    private static JsonNode EMPTY_JSON_NODE;

    private static JsonNode EMPTY_LIST_JSON_NODE;


    @BeforeAll
    static void setUp() throws JsonProcessingException {
        EXAMPLE_JSON_NODE = objectMapper.readTree("{\n" +
                "    \"example\": [\"one\", \"two\", \"three\"]\n" +
                "}");
        EMPTY_JSON_NODE = objectMapper.readTree("{}");
        EMPTY_LIST_JSON_NODE = objectMapper.readTree("{\n" +
                "    \"example\": []\n" +
                "}");
    }

    @Test
    void getOptionalArrayNodeFromJsonNode() {
        Optional<ArrayNode> result = JsonNodeUtils
                .getOptionalArrayNodeFromJsonNode(EXAMPLE_JSON_NODE, "example");
        assertTrue(result.isPresent());
        assertEquals("[\"one\",\"two\",\"three\"]",result.get().toString());
    }

    @Test
    void getOptionalArrayNodeFromJsonNode_when_empty_array_then_empty_optional() {
        Optional<ArrayNode> result = JsonNodeUtils
                .getOptionalArrayNodeFromJsonNode(EMPTY_LIST_JSON_NODE, "example");
        assertTrue(result.isEmpty());
    }
    @Test
    void getOptionalArrayNodeFromJsonNode_when_array_does_not_exist_then_empty_optional() {
        Optional<ArrayNode> result = JsonNodeUtils
                .getOptionalArrayNodeFromJsonNode(EMPTY_JSON_NODE, "example");
        assertTrue(result.isEmpty());
    }

    @Test
    void getOptionalArrayNodeFromJsonNode_when_fieldname_null_then_empty_optional() {
        Optional<ArrayNode> result = JsonNodeUtils
                .getOptionalArrayNodeFromJsonNode(EMPTY_JSON_NODE, null);
        assertTrue(result.isEmpty());
    }
}