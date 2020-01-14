package com.mustafadagher.widgets.service;

import com.mustafadagher.widgets.model.Widget;
import com.mustafadagher.widgets.model.WidgetRequest;
import com.mustafadagher.widgets.repository.WidgetRepository;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class WidgetsService {
    private final WidgetRepository widgetRepository;

    public WidgetsService(WidgetRepository widgetRepository) {
        this.widgetRepository = widgetRepository;
    }

    public Widget addWidget(WidgetRequest widgetRequest) {
        Widget widget = mapToWidget(widgetRequest);
        return widgetRepository.save(widget);
    }

    private Widget mapToWidget(@Valid WidgetRequest widgetRequest) {
        return new Widget()
                .id(UUID.randomUUID())
                .lastModificationDate(OffsetDateTime.now())
                .x(widgetRequest.getX())
                .y(widgetRequest.getY())
                .z(widgetRequest.getZ())
                .height(widgetRequest.getHeight())
                .width(widgetRequest.getWidth());
    }
}
