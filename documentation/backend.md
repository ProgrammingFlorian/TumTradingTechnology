# Backend

### Technologies used

- Java 17
- Gradle
- Lombok
- Spring Boot
- Spring Security
- MySQL

### Overall structure

TODO: Add UML Diagrams

Backend Controller (using data transfer objects)  \
        |           \
Backend Service     (using models) \
        |           \
Database

### Spring structure

source folder         \
|- controller (the rest controllers, handling a category of endpoint) \
|- dto (data transfer objects) \
|- model (the data models) \
|- repository (the database repositories)

### Security

Endpoints are secured using JsonWebTokens

[Private](../backend/src/main/resources/private.key) and [Public](../backend/src/main/resources/public.pub) Keys are 
stored in git for ease of development, but should be stored in a secure place during production!

### API Documentation

API Documentation can be found at [/api-docs](http://localhost:8080/api-docs)
and visualized with Swagger at [/swagger-ui.html](http://localhost:8080/swagger-ui.html)