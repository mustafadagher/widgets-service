# Widgets Service

A web service (HTTP REST API) to work with widgets.

## Overview  

Operations to be provided by the web service:

* Creating a widget. Having set coordinates, z-index, width, and height, we get a complete widget description in the response.

    * The server generates the identifier.
    * If a z-index is not specified, the widget moves to the foreground. If the existing z-index is specified, then the new widget shifts all widgets with the same and greater index upwards.

* Getting a widget by its identifier. In the response we get a complete description of the
widget.

* Change widget data by its identifier. In the response we get an updated full description of the widget.
    
    * You cannot change widget id.
    * You cannot delete widget attributes.
    * All changes to widgets must occur atomically. That is, if we change the ​XY coordinates of the widget, then we should not get an intermediate state during concurrent reading.

* Deleting a widget. We can delete the widget by its identifier.

* Getting a list of widgets. In the response we get a list of all widgets sorted by z-index,
from smallest to largest.

## Getting it to work
### Requirements
- JDK 8
- Maven 3+
### Build the app
    mvn clean package
### Run the spring app 
    
    mvn spring-boot:run
The app runs on port 8080 by default. You can then hit the service on `http://localhost:8080/widgets`
### Run Tests and generate test-coverage report
    mvn clean test
Report should exist in the `target/site` folder 
## Swagger UI
You can access the swagger ui generated docs on `http://localhost:8080/swagger-ui`

## Complications Implemented
* Pagination
* Rate limiting
    * In a real-life scenario to implement `rate limiting` with the required configurable features, I'd use a `proxy/gateway/load-balancer` tool in front of my services that has such features and configure my rate limiting requirements there. By this, I'd take it outside my application's business domain and make it easily configurable away from my application's life cycle. 
    
    * I could've used something like `guava`'s `RateLimitter` or `bucket4j` to implement a basic rate limiter on the controller level, but I thould delivering it as configurable as desired would be an over kill.
