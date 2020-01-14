package com.mustafadagher.widgets.api;

import com.mustafadagher.widgets.model.Widget;
import com.mustafadagher.widgets.model.WidgetRequest;
import com.mustafadagher.widgets.service.WidgetsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("${openapi.widgetsService.base-path:}")
public class WidgetsApiController implements WidgetsApi {

    private final WidgetsService widgetsService;
    private final NativeWebRequest request;

    public WidgetsApiController(WidgetsService widgetsService, NativeWebRequest request) {
        this.widgetsService = widgetsService;
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    public Widget addWidget(@Valid WidgetRequest widgetRequest) {
        return widgetsService.addWidget(widgetRequest);
    }

}
