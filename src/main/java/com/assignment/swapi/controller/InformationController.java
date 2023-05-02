package com.assignment.swapi.controller;

import com.assignment.swapi.models.response.InformationResponse;
import com.assignment.swapi.service.InformationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class InformationController {

    @Autowired
    InformationService informationService;

    @GetMapping("/information")
    public InformationResponse getInformation() {
        long timenow = System.currentTimeMillis();
        InformationResponse responseMono = informationService.getInformation().block();
        long timeend = System.currentTimeMillis();
        log.info("time elapsed: " + (timeend - timenow));
        return responseMono;
    }
}
