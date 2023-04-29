# swapi-assignment
API Assignment with SWAPI

# Requirements
- API: https://swapi.dev/api/
- Starship Darth Vader is using 
- number of crews on board the death star 
- Is Princess Leia (Leia Organa) on Alderaan

## Information
- People
  - Darth Vader (people/4)
  - Leia Organa (people/5)
- Planet
  - Alderaan (planets/2)
- Starships
  - DeathStar (starships/9)


## Expected Output
```
{
 "starship":{
     "name":"String",
     "class":"String",
     "model":"String"
  },
 "crew":"Number",
 "isLeiaOnPlanet":"Boolean"
}
```
- Maximum number of crew? (int may not be big enough?)

## Validation and business logic:
- If no starships were found, set the value for starship as an empty object `{}`.
- If there is no crew on board the death star, set the crew value to `0`.
- If Princess Leia is on the planet, then set `true` else set `false`.

### references
https://www.springcloud.io/post/2022-08/springboot-best-practices/#gsc.tab=0

### Design Decisions made
- Using WebClient (Spring reactive) over blocking
  - one thread is used, but due to non-blocking? requests are sent at once??
  - there is definitely performance benefit


## TODO
[ ] add Unit testing - https://dzone.com/articles/spring-5-web-reactive-flux-and-mono
[ ] test not happy cases?
  [ ] change URL: SWAPI returns 4XX or 5XX
  [ ] Change Darth Vader person ID - to someone that has no starships (mimick no starships found) - shuold be empty json
  [ ] Check planet where there are no crew or JsonNode "crew" does not exist - crew should be `0`
  [ ] Check other planets where princess leia does not exist - should be `false`
    [X] Checked for person `49` on Alderaan, returns `false`
[ ] add actuator? 
