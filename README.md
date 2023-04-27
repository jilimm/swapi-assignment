# swapi-assignment
API Assignment with SWAPI

# Requirements
- API: https://swapi.dev/api/
- Starship Darth Vader is using 
  - information about Darth Vader = people/4
- number of crews on board the death star 
  - information about death Star = starships/9/
- Is Princess Leia (Leia Organa) on Alderaan
  - information about Aldernaan = planets/2
  - information about Leia = people/5/

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

## Validation and business logic:
- If no starships were found, set the value for starship as an empty object `{}`.
- If there is no crew on board the death star, set the crew value to `0`.
- If Princess Leia is on the planet, then set `true` else set `false`

