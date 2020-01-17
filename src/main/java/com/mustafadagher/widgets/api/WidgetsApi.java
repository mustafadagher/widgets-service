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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;


@Validated
@Api(value = "widgets")
public interface WidgetsApi {

    @ApiOperation(value = "Add a new widget", nickname = "addWidget", response = Widget.class, tags = {"widget",})
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Widget Created", response = Widget.class),
            @ApiResponse(code = 405, message = "Invalid input")})
    @PostMapping(value = "/widgets",
            produces = {"application/json"},
            consumes = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    Widget addWidget(@ApiParam(value = "Widget object that needs to be added", required = true) @Valid @RequestBody WidgetRequest widgetRequest);


    @ApiOperation(value = "Deletes a widget", nickname = "deleteWidget", tags = {"widget",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Widget not found")})
    @DeleteMapping(value = "/widgets/{widgetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteWidget(@ApiParam(value = "Widget id to delete", required = true) @Valid @NotNull @PathVariable("widgetId") UUID widgetId);

    @ApiOperation(value = "Get all widgets", nickname = "getAllWidgets", notes = "Returns an array of widgets", response = Widget.class, responseContainer = "List", tags = {"widget",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Widget.class, responseContainer = "List")})
    @GetMapping(value = "/widgets",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    List<Widget> getAllWidgets();


    @ApiOperation(value = "Find widget by ID", nickname = "getWidgetById", notes = "Returns a single widget", response = Widget.class, tags = {"widget",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Widget.class),
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Widget not found")})
    @GetMapping(value = "/widgets/{widgetId}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    Widget getWidgetById(@ApiParam(value = "ID of widget to return", required = true) @Valid @NotNull @PathVariable("widgetId") UUID widgetId);

    @ApiOperation(value = "Updates a widget in the store", nickname = "updateWidget", response = Widget.class, tags = {"widget",})
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Widget Updated", response = Widget.class),
            @ApiResponse(code = 405, message = "Invalid input")})
    @PutMapping(value = "/widgets/{widgetId}",
            produces = {"application/json"},
            consumes = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    Widget updateWidget(@ApiParam(value = "ID of widget that needs to be updated", required = true) @Valid @NotNull @PathVariable("widgetId") UUID widgetId, @ApiParam(value = "Widget object that needs to be added", required = true) @Valid @RequestBody WidgetRequest widgetRequest);

}
