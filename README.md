


To run the app:  `sbt run`

Example commands (GET, POST, PUT, DELETE):


curl -i http://localhost:8080/listings/123


curl -v -H "Content-Type: application/json" -X POST http://localhost:8080/listings -d '{"contact":{"phone":"0123456789","formattedPhone":"01234 567 89"},"address":{"address":"10 Downing Street","postalCode":"SW1A 2AA","countryCode":"UK","city":"London","state":"London","country":"United Kingdom"},"location":{"lat":"40.4255485534668","lng":"-3.7075681686401367"}}'


curl -v -H "Content-Type: application/json" -X PUT http://localhost:8080/listings/123 -d '{"id":"123","contact":{"phone":"0123456789","formattedPhone":"01234 567 89"},"address":{"address":"10 Downing Street","postalCode":"SW1A 2AA","countryCode":"UK","city":"London","state":"London","country":"United States"},"location":{"lat":"40.4255485534668","lng":"-3.7075681686401367"}}'


curl -v -X DELETE http://localhost:8080/listings/123