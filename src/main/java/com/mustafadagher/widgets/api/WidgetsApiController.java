package com.mustafadagher.widgets.api;

import com.mustafadagher.widgets.model.Widget;
import com.mustafadagher.widgets.model.WidgetAreaFilter;
import com.mustafadagher.widgets.model.WidgetRequest;
import com.mustafadagher.widgets.service.WidgetsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${openapi.widgetsService.base-path:}")
public class WidgetsApiController implements WidgetsApi {

    private final WidgetsService widgetsService;

    public WidgetsApiController(WidgetsService widgetsService) {
        this.widgetsService = widgetsService;
    }

    public Widget addWidget(WidgetRequest widgetRequest) {
        return widgetsService.addWidget(widgetRequest);
    }

    public Widget getWidgetById(UUID widgetId) {
        return widgetsService.getWidgetById(widgetId);
    }

    public List<Widget> getAllWidgets(int page, int size, Integer leftX, Integer rightX, Integer lowerY, Integer higherY) {
        WidgetAreaFilter filter = new WidgetAreaFilter(leftX, rightX, lowerY, higherY);
        return widgetsService.getAllWidgets(page, size, filter);
    }

    public Widget updateWidgetById(UUID widgetId, WidgetRequest widgetRequest) {
        return widgetsService.updateWidgetById(widgetId, widgetRequest);
    }

    public void deleteWidgetById(UUID widgetId) {
        widgetsService.deleteWidgetById(widgetId);
    }

}
