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
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.mustafadagher.widgets.model.Widget.fromWidgetRequest;

@Service
public class WidgetsService {
    private final WidgetRepository widgetRepository;
    private final AtomicLong highestZ;
    private final ReentrantReadWriteLock lock;

    public WidgetsService(WidgetRepository widgetRepository) {
        this.widgetRepository = widgetRepository;
        lock = new ReentrantReadWriteLock();
        highestZ = new AtomicLong(Long.MIN_VALUE);
    }

    public Widget addWidget(WidgetRequest widgetRequest) {
        Widget widget = fromWidgetRequest(widgetRequest);
        lock.writeLock().lock();
        try {
            if (widget.getZ() == null) {
                moveWidgetToForegroundIfZIndexNotSpecified(widget);
            } else {
                shiftAllWidgetsWithSameAndGreaterZIndexUpwards(widget);
            }
            return saveAndUpdateHighestZ(widget);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Widget getWidgetById(UUID widgetId) {
        lock.readLock().lock();
        try {
            return widgetRepository.findById(widgetId).orElseThrow(WidgetNotFoundException::new);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Widget> getAllWidgets(int page, int size, WidgetAreaFilter filter) {
        lock.readLock().lock();
        try {
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
        } finally {
            lock.readLock().unlock();
        }
    }

    public void deleteWidgetById(UUID widgetId) {
        lock.writeLock().lock();
        try {
            Widget widget = widgetRepository
                    .findById(widgetId)
                    .orElseThrow(WidgetNotFoundException::new);

            widgetRepository.deleteById(widget.getId());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Widget updateWidgetById(UUID id, WidgetRequest widgetRequest) {
        lock.writeLock().lock();
        try {
            Widget current = widgetRepository
                    .findById(id)
                    .orElseThrow(WidgetNotFoundException::new);

            Widget updated = fromWidgetRequest(widgetRequest)
                    .id(current.getId())
                    .lastModificationDate(OffsetDateTime.now());

            return saveAndUpdateHighestZ(updated);
        } finally {
            lock.writeLock().unlock();
        }
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
                .updateAndGet(wi -> w
                        .clone()
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
        // The loop can result in an infinite iterations here if there are too many threads affecting the current value
        // A max number of trials is a work around to overcome an infinite loop
        int numberOfUpdateTrials = 0;
        int maxNumberOfUpdateTrials = 3;

        boolean updateDone;
        do {
            long currentHighestZ = highestZ.get();
            if (currentHighestZ < saved.getZ()) {
                updateDone = highestZ.compareAndSet(currentHighestZ, saved.getZ());
                numberOfUpdateTrials++;
            } else {
                updateDone = true;
            }
        } while (!updateDone && numberOfUpdateTrials < maxNumberOfUpdateTrials);
    }
}
