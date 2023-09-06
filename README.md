# Apache Pinot™ Movie Rating Demo

An extremely incomplete procedure here...

## Start the system

```
docker-compose up
```

## Create the tables

```
docker exec -it pinot-controller-json bin/pinot-admin.sh AddTable   \
  -tableConfigFile /config/movie-table.json   \
  -schemaFile /config/movie-schema.json \
  -exec
 ```

 ```
docker exec -it pinot-controller-json bin/pinot-admin.sh AddTable   \
  -tableConfigFile /config/rating-table.json   \
  -schemaFile /config/rating-schema.json \
  -exec
 ```

 ## Load the movie data

 This is about a thousand rows of reference data.


 ## Start the ratings script

 A lil' bit of Kotlin that creates random-ish ratings around a few movies and dumps them into Kafka real fast.

 ## Do the Pinot query

 Join the `ratings` table to the `movies` table and average up the ratings.