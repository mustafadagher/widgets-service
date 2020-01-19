package com.mustafadagher.widgets.api;

import com.mustafadagher.widgets.model.Widget;
import com.mustafadagher.widgets.model.WidgetRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;


@Validated
public interface WidgetsApi {

    @PostMapping(value = "/widgets",
            produces = {"application/json"},
            consumes = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    Widget addWidget(@Valid @RequestBody WidgetRequest widgetRequest);

    @GetMapping(value = "/widgets",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    List<Widget> getAllWidgets(@RequestParam(defaultValue = "0") int page,
                               @Max(500) @RequestParam(defaultValue = "10") int size,
                               @RequestParam(required = false) Integer leftX,
                               @RequestParam(required = false) Integer rightX,
                               @RequestParam(required = false) Integer lowerY,
                               @RequestParam(required = false) Integer higherY);

    @GetMapping(value = "/widgets/{widgetId}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    Widget getWidgetById(@Valid @NotNull @PathVariable("widgetId") UUID widgetId);

    @PutMapping(value = "/widgets/{widgetId}",
            produces = {"application/json"},
            consumes = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    Widget updateWidgetById(@Valid @NotNull @PathVariable("widgetId") UUID widgetId, @Valid @RequestBody WidgetRequest widgetRequest);

    @DeleteMapping(value = "/widgets/{widgetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteWidgetById(@Valid @NotNull @PathVariable("widgetId") UUID widgetId);
}
