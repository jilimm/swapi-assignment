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