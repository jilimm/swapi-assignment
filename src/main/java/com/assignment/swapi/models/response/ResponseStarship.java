package com.assignment.swapi.models.response;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
public class ResponseStarship {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;

    private String starshipClass;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String model;

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
