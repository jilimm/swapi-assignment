package com.assignment.swapi.models.response;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;


@NoArgsConstructor
@AllArgsConstructor
public class ResponseStarship {

    // TODO: is there better way of making it empty json object?
    // I'm just removing every null field name instead of making entire thing empty :/
    // https://stackoverflow.com/questions/56503042/how-return-a-empty-json-in-spring-mvc

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String name;

    String starshipClass;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String model;

    @JsonGetter("class")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getStarshipClass() {
        return starshipClass;
    }

    @JsonSetter("starship_class")
    public void setStarshipClass(String starshipClass) {
        this.starshipClass = starshipClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
