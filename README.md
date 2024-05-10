#Pre-reqs 

-Docker Compose
-Observability Cloud environment and Ingest token

#How to set up the project

-Build the docker compose Stack 

```docker compose up``` 

-Generate request on the stack 

Test that the api is reachable:

- ```localhost:8080/echo```

Send a payload to the recursive task function :

- ```localhost:8080/recursive```

Example request the function will take every number from the Array of values for the key ar and add them to a sum recursively. (It is a recursive process)

``` 
curl --location 'localhost:8080/recursive' \
--header 'Content-Type: application/json' \
--data '{"ar": ["213131321","231321", "1321"] }' 
```

