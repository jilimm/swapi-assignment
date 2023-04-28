package com.assignment.swapi.models.response;

import lombok.*;


@NoArgsConstructor
@Builder
@Getter
@Setter
public class InformationResponse {

    ResponseStarship starship = new ResponseStarship();

    Number crew = 0;

    Boolean isLeiaOnPlanet = false;

    public InformationResponse(ResponseStarship starship, Number crew, Boolean isLeiaOnPlanet) {
        if (starship != null) {
            this.starship = starship;
        }
        if (crew != null) {
            this.crew = crew;
        }
        if (isLeiaOnPlanet != null) {
            this.isLeiaOnPlanet = isLeiaOnPlanet;
        }
    }
}
