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
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static com.mustafadagher.widgets.model.Widget.fromWidgetRequest;

@Service
public class WidgetsService {
    private final WidgetRepository widgetRepository;
    private AtomicLong highestZ;

    public WidgetsService(WidgetRepository widgetRepository) {
        this.widgetRepository = widgetRepository;
        highestZ = new AtomicLong(Long.MIN_VALUE);
    }

    public Widget addWidget(WidgetRequest widgetRequest) {
        Widget widget = fromWidgetRequest(widgetRequest);

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
        List<Widget> widgetsToReturn = null;

        if (filter == null || filter.isNotValid()) {
            widgetsToReturn = widgetRepository.findAllByOrderByZAsc(page, size);
        } else if (filter.isNotALineNorADot()) {
            IsInsideFilteredArea filterPredicate = IsInsideFilteredArea.withinArea(filter);
            widgetsToReturn = widgetRepository.findAllByAreaOrderByZAsc(page, size, filterPredicate);
        }

        if (widgetsToReturn == null)
            return Collections.emptyList();

        return widgetsToReturn;
    }

    public void deleteWidgetById(UUID widgetId) {
        Widget widget = widgetRepository
                .findById(widgetId)
                .orElseThrow(WidgetNotFoundException::new);

        widgetRepository.deleteById(widget.getId());
    }

    public Widget updateWidgetById(UUID id, WidgetRequest widgetRequest) {
        Widget current = widgetRepository
                .findById(id)
                .orElseThrow(WidgetNotFoundException::new);

        Widget updated = fromWidgetRequest(widgetRequest)
                .id(current.getId())
                .lastModificationDate(OffsetDateTime.now());

        return saveAndUpdateHighestZ(updated);
    }

    long getHighestZIndex() {
        return highestZ.get();
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
        boolean updateDone;
        do {
            long currentHighestZ = highestZ.get();
            if (currentHighestZ < saved.getZ()) {
                updateDone = highestZ.compareAndSet(currentHighestZ, saved.getZ());
            } else {
                updateDone = true;
            }
        } while (!updateDone);
    }
}
