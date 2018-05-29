## About 
 EduUrlShortener is an educational project. This is a regular URL shortener 
 written in Java 8 using [Javalin](https://github.com/tipsy/javalin), MongoDB, Maven and Docker.
 It has only REST API and redirect capabilities, no GUI, sorry.
 
##Features
  - REST API - shorten/get
  - redirect with 302/Location
  - Full async - uses Javalin's experimental async feature and MongoDB async client
  - Dockerized
   
## Getting started
 Build dependencies:
  - Java 8
  - Maven 4
  - Docker engine 18 (optional)
  - Docker compose 1.21 (optional)
  
```text
git clone <url>
mvn package
docker-compose up
```
The project uses [Embedded MongoDB](https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo) for the testing proposes,
 and a few minutes is required to download mongoDB binaries for the first time. Be patient.)
 Also TCP ports 7001 and 12345 are used.
## Test
 By default the server listens on 7000, thus you can access it using
 ```text
http://localhost:7000/
```
__Shorten URL__
```text
curl http://localhost:7000/api/v1/url \
-H 'Content-Type: application/json' \
-d '{"longUrl": "https://www.youtube.com/watch?v=YnWhqhNdYyk&t=1920s"}'
```
__Response__
```json
{
  "longUrl":"https://www.youtube.com/watch?v=YnWhqhNdYyk&t=1920s",
  "shortUrl":"http://localhost:7000/7kdeya"
}
```

__Redirect__
```text
curl  -v http://localhost:7000/7kdeya
```
__Response__
```text
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 7000 (#0)
> GET /Kb4ghC HTTP/1.1
> Host: localhost:7000
> User-Agent: curl/7.54.0
> Accept: */*
>
< HTTP/1.1 302 Found
< Date: Tue, 29 May 2018 20:00:48 GMT
< Server: Javalin
< Content-Type: application/json;charset=utf-8
< Location: https://www.youtube.com/watch?v=YnWhqhNdYyk&t=1920s
< Content-Length: 0
<
* Connection #0 to host localhost left intact
```
## Configuration
The configuration file is very simple and self explanatory
```yaml
#mongo db configuration
db:
  connectionString: "mongodb://mongo:27017"
  databaseName: "eduurlshortener"

#server network options
network:
  port: 7000 # port to bind

#shortening service options
shortening:
  baseUrl: "http://localhost:7000" # Constant part of shortened urls
  hashLen: 6 # from 1 to 20, 1 is not recommended!)
```
You can pass URL SHORTENER CONFIG env variable to the container. 
The variable has to contain a full path to the config file.

## TODO
 - Authorization
 - Hash collision handling
 - Find already shortened URLs
 - Statistic 
 - RULs expiration 
 - Logging
 - Repair Docker-Maven integration