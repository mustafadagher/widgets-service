package com.mustafadagher.widgets.service;

import com.mustafadagher.widgets.model.Widget;
import com.mustafadagher.widgets.model.WidgetRequest;
import com.mustafadagher.widgets.repository.WidgetRepository;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class WidgetsService {
    private final WidgetRepository widgetRepository;

    private AtomicLong highestZ = new AtomicLong(Long.MIN_VALUE);

    public WidgetsService(WidgetRepository widgetRepository) {
        this.widgetRepository = widgetRepository;
    }

    public Widget addWidget(WidgetRequest widgetRequest) {
        Widget widget = mapToWidget(widgetRequest);
        moveWidgetToForegroundIfZIndexNotSpecified(widget);
        Widget saved = widgetRepository.save(widget);
        updateHighestZ(saved);
        return saved;
    }

    AtomicLong getHighestZIndex() {
        return highestZ;
    }

    private void moveWidgetToForegroundIfZIndexNotSpecified(Widget widget) {
        if(widget.getZ() == null) {
            widget.z(highestZ.incrementAndGet());
        }
    }

    private void updateHighestZ(Widget saved) {
        long currentHighestZ = highestZ.get();
        if (currentHighestZ < saved.getZ()) {
            highestZ.compareAndSet(currentHighestZ, saved.getZ());
        }
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
