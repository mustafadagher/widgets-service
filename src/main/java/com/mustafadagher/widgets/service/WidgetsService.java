package com.mustafadagher.widgets.service;

import com.mustafadagher.widgets.exception.WidgetNotFoundException;
import com.mustafadagher.widgets.model.Widget;
import com.mustafadagher.widgets.model.WidgetAreaFilter;
import com.mustafadagher.widgets.model.WidgetRequest;
import com.mustafadagher.widgets.repository.WidgetRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static com.mustafadagher.widgets.model.Widget.mapToWidget;

@Service
public class WidgetsService {
    private final WidgetRepository widgetRepository;

    private AtomicLong highestZ = new AtomicLong(Long.MIN_VALUE);

    public WidgetsService(WidgetRepository widgetRepository) {
        this.widgetRepository = widgetRepository;
    }

    public Widget addWidget(WidgetRequest widgetRequest) {
        Widget widget = mapToWidget(widgetRequest);

        if (widget.getZ() == null) {
            moveWidgetToForegroundIfZIndexNotSpecified(widget);
        } else {
            shiftAllWidgetsWithSameAndGreaterZIndexUpwards(widget);
        }

        return saveAndUpdateHighestZ(widget);
    }

    public Widget getWidgetById(UUID widgetId) {
        return widgetRepository.findById(widgetId).orElseThrow(WidgetNotFoundException::new);
    }

    public List<Widget> getAllWidgets(int page, int size, WidgetAreaFilter filter) {
        List<Widget> allByOrderByZAsc;

        if (filter == null || !filter.isValid()) {
            allByOrderByZAsc = widgetRepository.findAllByOrderByZAsc(page, size);
        } else if (filter.isALineOrADot()) {
            return Collections.emptyList();
        } else {
            allByOrderByZAsc = widgetRepository.findAllByAreaOrderByZAsc(page, size, filter);
        }

        if (allByOrderByZAsc == null)
            return Collections.emptyList();

        return allByOrderByZAsc;
    }

    public void deleteWidget(UUID widgetId) {
        Optional<Widget> widget = widgetRepository.findById(widgetId);

        if (widget.isPresent())
            widgetRepository.deleteById(widgetId);
        else
            throw new WidgetNotFoundException();
    }

    public Widget updateWidgetById(UUID id, WidgetRequest widgetRequest) {
        Widget current = widgetRepository.findById(id).orElseThrow(WidgetNotFoundException::new);

        Widget updated = mapToWidget(widgetRequest);
        updated.id(current.getId()).lastModificationDate(OffsetDateTime.now());

        return saveAndUpdateHighestZ(updated);
    }

    AtomicLong getHighestZIndex() {
        return highestZ;
    }

    private void shiftAllWidgetsWithSameAndGreaterZIndexUpwards(Widget widgetToBeInserted) {
        Long insertedZIndex = widgetToBeInserted.getZ();
        List<Widget> allByZGreaterThanOrEqual = widgetRepository.findAllByZGreaterThanOrEqual(insertedZIndex);
        if (hasAWidgetWithZIndexEqualTo(allByZGreaterThanOrEqual, insertedZIndex)) {
            allByZGreaterThanOrEqual.forEach(this::incrementZIndexAndSave);
        }
    }

    private boolean hasAWidgetWithZIndexEqualTo(List<Widget> allByZGreaterThanOrEqual, Long zIndex) {
        return allByZGreaterThanOrEqual != null
                && !allByZGreaterThanOrEqual.isEmpty()
                && allByZGreaterThanOrEqual.get(0).getZ().equals(zIndex);
    }

    private void incrementZIndexAndSave(Widget w) {
        AtomicReference<Widget> current = new AtomicReference<>(w);

        Widget updated = current
                .updateAndGet(wi -> wi
                        .z(w.getZ() + 1)
                        .lastModificationDate(OffsetDateTime.now()));

        saveAndUpdateHighestZ(updated);
    }

    private Widget saveAndUpdateHighestZ(Widget widget) {
        Widget saved = widgetRepository.save(widget);
        updateHighestZ(saved);
        return saved;
    }

    private void moveWidgetToForegroundIfZIndexNotSpecified(Widget widget) {
        widget.z(highestZ.incrementAndGet());
    }

    private void updateHighestZ(Widget saved) {
        long currentHighestZ = highestZ.get();
        if (currentHighestZ < saved.getZ()) {
            highestZ.compareAndSet(currentHighestZ, saved.getZ());
        }
    }
}
