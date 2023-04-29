package com.assignment.swapi.models.response;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class InformationResponse {

    private ResponseStarship starship;
    private Number crew;

    private Boolean isLeiaOnPlanet;
}
