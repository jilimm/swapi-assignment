package com.assignment.swapi.exceptions;

import org.springframework.http.HttpStatusCode;

public class SwapiHttpErrorException extends  RuntimeException {

    private String webClientResponseId;

    private HttpStatusCode httpStatus;

    public SwapiHttpErrorException(String webClientResponseId, HttpStatusCode httpStatus) {
        super(String.format("SWAPI Http Error for response Id: %s , with status code: %s",
    webClientResponseId, httpStatus));
        this.webClientResponseId = webClientResponseId;
        this.httpStatus = httpStatus;
    }

}
