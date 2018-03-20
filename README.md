


To run the app:  `sbt run`

Example commands (GET, POST, PUT, DELETE):


curl -i http://localhost:8080/huts/123


curl -v -H "Content-Type: application/json" -X POST http://localhost:8080/huts -d '{"name":"River Hut"}'


curl -v -H "Content-Type: application/json" -X PUT http://localhost:8080/huts/123 -d '{"id":"123","name":"Mountain Hut"}'


curl -v -X DELETE http://localhost:8080/huts/123