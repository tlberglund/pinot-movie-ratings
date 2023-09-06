



```
docker exec -it pinot-controller-json bin/pinot-admin.sh AddTable   \
  -tableConfigFile /config/movie-table.json   \
  -schemaFile /config/movie-schema.json \
  -exec
 ```