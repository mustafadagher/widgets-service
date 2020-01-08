/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (4.2.2).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package com.mustafadagher.widgets.api;

import com.mustafadagher.widgets.model.Widget;
import com.mustafadagher.widgets.model.WidgetRequest;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@Validated
@Api(value = "widgets")
public interface WidgetsApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    @ApiOperation(value = "Add a new widget", nickname = "addWidget", response = Widget.class, tags = {"widget",})
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Widget Created", response = Widget.class),
            @ApiResponse(code = 405, message = "Invalid input")})
    @PostMapping(value = "/widgets",
            produces = {"application/json"},
            consumes = {"application/json"})
    default ResponseEntity<Widget> addWidget(@ApiParam(value = "Widget object that needs to be added", required = true) @Valid @RequestBody WidgetRequest widgetRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"lastModificationDate\" : \"2000-01-23T04:56:07.000+00:00\", \"x\" : 0, \"width\" : 0.5962134, \"y\" : 6, \"z\" : 1, \"id\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"height\" : 0.5637376656633328 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    @ApiOperation(value = "Deletes a widget", nickname = "deleteWidget", tags = {"widget",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Widget not found")})
    @DeleteMapping(value = "/widgets/{widgetId}")
    default ResponseEntity<Void> deleteWidget(@ApiParam(value = "Widget id to delete", required = true) @PathVariable("widgetId") Long widgetId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    @ApiOperation(value = "Get all widgets", nickname = "getAllWidgets", notes = "Returns an array of widgets", response = Widget.class, responseContainer = "List", tags = {"widget",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Widget.class, responseContainer = "List")})
    @GetMapping(value = "/widgets",
            produces = {"application/json"})
    default ResponseEntity<List<Widget>> getAllWidgets() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"lastModificationDate\" : \"2000-01-23T04:56:07.000+00:00\", \"x\" : 0, \"width\" : 0.5962134, \"y\" : 6, \"z\" : 1, \"id\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"height\" : 0.5637376656633328 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    @ApiOperation(value = "Find widget by ID", nickname = "getWidgetById", notes = "Returns a single widget", response = Widget.class, tags = {"widget",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Widget.class),
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Widget not found")})
    @GetMapping(value = "/widgets/{widgetId}", produces = {"application/json"})
    default ResponseEntity<Widget> getWidgetById(@ApiParam(value = "ID of widget to return", required = true) @PathVariable("widgetId") Long widgetId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"lastModificationDate\" : \"2000-01-23T04:56:07.000+00:00\", \"x\" : 0, \"width\" : 0.5962134, \"y\" : 6, \"z\" : 1, \"id\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"height\" : 0.5637376656633328 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    @ApiOperation(value = "Updates a widget in the store with form data", nickname = "updateWidgetWithForm", response = Widget.class, tags = {"widget",})
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Widget Updated", response = Widget.class),
            @ApiResponse(code = 405, message = "Invalid input")})
    @PutMapping(value = "/widgets/{widgetId}",
            produces = {"application/json"},
            consumes = {"application/json"})
    default ResponseEntity<Widget> updateWidgetWithForm(@ApiParam(value = "ID of widget that needs to be updated", required = true) @PathVariable("widgetId") Long widgetId, @ApiParam(value = "Widget object that needs to be added", required = true) @Valid @RequestBody WidgetRequest widgetRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"lastModificationDate\" : \"2000-01-23T04:56:07.000+00:00\", \"x\" : 0, \"width\" : 0.5962134, \"y\" : 6, \"z\" : 1, \"id\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"height\" : 0.5637376656633328 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
