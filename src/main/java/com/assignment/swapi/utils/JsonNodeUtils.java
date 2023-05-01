package com.assignment.swapi.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.Optional;

/**
 * Utility class for processing JsonNode
 */
public class JsonNodeUtils {

    /**
     * Extract Optional array node with field name in jsonNode
     * ArrayNode must:
     * 1. be an array
     * 2. NOT be empty
     *
     * @param jsonNode jsonNode containing array Node
     * @param field    field name of array node
     * @return Optional of array node
     */
    public static Optional<ArrayNode> getOptionalArrayNodeFromJsonNode(JsonNode jsonNode, String field) {
        return Optional.ofNullable(jsonNode.get(field))
                .filter(JsonNode::isArray)
                .filter(array -> array.size() > 0)
                .map(json -> (ArrayNode) json);
    }
}
