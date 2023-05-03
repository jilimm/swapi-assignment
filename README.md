# swapi-assignment
API Assignment with SWAPI

# Requirements
Using third party API: https://swapi.dev/api/, retrieve the following information
1. Starship Darth Vader is using 
2. number of crews on board the death star 
3. Is Princess Leia (Leia Organa) on Alderaan

## SWAPI API Information
- People
  - Darth Vader (people/4)
  - Leia Organa (people/5)
- Planet
  - Alderaan (planets/2)
- Starships
  - DeathStar (starships/9)

# API
> GET `/information`

Retrieves the following information from SWAPI API 
1. Starship Darth Vader is using
2. number of crews on board the death star
3. Is Princess Leia (Leia Organa) on Alderaan

## Response 

- Response Status: 200
  - Response
    - Sample Response: 
    ```
    {
      "starship":{
      "name": "TIE Advanced x1",
      "model": "Twin Ion Engine Advanced x1",
      "class": "Starfighter"
      },
      "crew": 342953,
      "isLeiaOnPlanet": true
    }
    ```
    - Response Schema:
    ```
    {
    "starship":{
       "name": "string",
       "class": "string",
       "model": "string"
    },
    "crew": "number",
    "isLeiaOnPlanet": "boolean"
    }
    ```
      - `starship`
        - if no starships found, value shuold be an empty object `{}`.
      - `crew`
        - if there is no crew on board, value should be `0`.
      - `isLeiaOnPlanet`
        - if Princess Leia is on the planet, then set `true` else set `false`.


# Project Description

## Modules
- configuration
  - contains Webclient to connect to SWAPI API.
- controller
  - contains controller classes for web application.
- exceptions
  - custom exceptions thrown when obtaining & processing SWAPI results
- models
  - POJOs to represent response objects
- service
  - service classes for web application
- utils
  - utility classes for web application

## Endpoints

> GET /information

3 asynchronous HTTP REST calls to SWAPI are made.
1. Get information about Planet Alderaan
2. Get information about Darth Vader
3. Get information about Death Star. 

Responses from calls 1-3 are processed and used to populate response body like so:
1. Check if `residents` list for url contains url corresponding to Princess Leia
   1. result is mapped to `isLeiaOnPlanet` in response body.
2. Obtain first url in `starships` list.
   1. HTTP call to starship url obtained is made.
   2. Response from HTTP call to starship url is used to populate response body: 
      1. Field `name` is mapped to `starship.name` in response body
      2. Field `starship_class` is mapped to `starship.class` in response body
      3. Field `model` is mapped to `starship.model` in response body
3. Obtain value from `crew` and parse it as a Number.
    1. result is mapped to `crew` in response body.


