package com.assignment.swapi.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class SwapiHttpErrorException extends RuntimeException {

    private final String webClientResponseId;

    private final HttpStatusCode httpStatus;

    public SwapiHttpErrorException(String webClientResponseId, HttpStatusCode httpStatus) {
        super(String.format("SWAPI Http Error for response Id: %s , with status code: %s",
                webClientResponseId, httpStatus));
        this.webClientResponseId = webClientResponseId;
        this.httpStatus = httpStatus;
    }

}
