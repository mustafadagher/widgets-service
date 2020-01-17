package com.mustafadagher.widgets.api;

import com.mustafadagher.widgets.model.Widget;
import com.mustafadagher.widgets.model.WidgetRequest;
import com.mustafadagher.widgets.service.WidgetsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${openapi.widgetsService.base-path:}")
public class WidgetsApiController implements WidgetsApi {

    private final WidgetsService widgetsService;

    public WidgetsApiController(WidgetsService widgetsService) {
        this.widgetsService = widgetsService;
    }

    public Widget addWidget(@Valid WidgetRequest widgetRequest) {
        return widgetsService.addWidget(widgetRequest);
    }

    public Widget getWidgetById(UUID widgetId) {
        return widgetsService.getWidgetById(widgetId);
    }

    public List<Widget> getAllWidgets() {
        return widgetsService.getAllWidgets();
    }

    @Override
    public ResponseEntity<Void> deleteWidget(UUID widgetId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    @Override
    public ResponseEntity<Widget> updateWidget(UUID widgetId, @Valid WidgetRequest widgetRequest) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
