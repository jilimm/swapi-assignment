package com.assignment.swapi.models.response;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class InformationResponse {

    ResponseStarship starship;

    Number crew;

    boolean isLeiaOnPlanet;
}
