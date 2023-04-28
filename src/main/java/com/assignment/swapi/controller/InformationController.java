package com.assignment.swapi.controller;

import com.assignment.swapi.models.response.InformationResponse;
import com.assignment.swapi.service.InformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class InformationController {

    @Autowired
    InformationService informationService;

    @GetMapping("/information")
    public Mono<InformationResponse> getInformation() {
        // TODO: to implement
        return informationService.getInformation();
    }
}
