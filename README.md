# Apache Pinot™ Movie Rating Demo

An extremely incomplete procedure here...

## Start the system

```
docker-compose up
```

## Create the tables

```
docker exec -it pinot-controller bin/pinot-admin.sh AddTable   \
  -tableConfigFile /config/movie-table.json   \
  -schemaFile /config/movie-schema.json \
  -exec
 ```

 ```
docker exec -it pinot-controller bin/pinot-admin.sh AddTable   \
  -tableConfigFile /config/rating-table.json   \
  -schemaFile /config/rating-schema.json \
  -exec
 ```

 ## Load the movie data

```
docker exec -it pinot-controller bin/pinot-admin.sh LaunchDataIngestionJob \
  -jobSpecFile /config/movie-ingest-spec.yaml
```

 ## Start the ratings script

 A lil' bit of Kotlin that creates random-ish ratings around a few movies and dumps them into Kafka real fast.

First, install Kotlin and https://github.com/kscripting/kscript[`kscript`^] if you don't already have that installed:

```
curl -s "https://get.sdkman.io" | bash     # install sdkman
source "$HOME/.sdkman/bin/sdkman-init.sh"  # add sdkman to PATH

sdk install kotlin                         # install Kotlin
sdk install kscript                        # install kscript
```

And then run the script:

```
kscript code/ratings.kts
```

 ## Do the Pinot query

 Join the `ratings` table to the `movies` table and average up the ratings.

```sql
SELECT title, releaseYear, ceil(avg(ratings.rating)*10)/10 as avgRating
  FROM ratings INNER JOIN movies 
    ON ratings.movieId = movies.movieId
  GROUP BY title, releaseYear
  ORDER BY avgRating DESC;
```
