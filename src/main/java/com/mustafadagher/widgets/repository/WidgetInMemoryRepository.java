package com.mustafadagher.widgets.repository;

import com.mustafadagher.widgets.model.Widget;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Repository
public class WidgetInMemoryRepository implements WidgetRepository {
    private final Map<UUID, Widget> storage;

    public WidgetInMemoryRepository() {
        this.storage = new ConcurrentHashMap<>();
    }

    public Widget save(Widget widget) {
        return storage.merge(widget.getId(), widget, updateWidgetDescriptionFn());
    }

    public List<Widget> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<Widget> findAllByOrderByZAsc() {
        return findAllByOrderBy(Comparator.comparing(Widget::getZ));
    }

    public void deleteById(UUID widgetId) {
        storage.remove(widgetId);
    }

    private List<Widget> findAllByOrderBy(Comparator<Widget> sortedBy) {
        return storage.values().stream()
                .sorted(sortedBy)
                .collect(Collectors.toList());
    }

    public List<Widget> findAllByZGreaterThanOrEqual(Long z) {
        return storage.values().stream()
                .filter(w -> w.getZ() >= z)
                .sorted(Comparator.comparing(Widget::getZ))
                .collect(Collectors.toList());
    }

    public Optional<Widget> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    private BiFunction<Widget, Widget, Widget> updateWidgetDescriptionFn() {
        return (current, updated) -> {
            AtomicReference<Widget> currentReference = new AtomicReference<>(current);

            return currentReference.updateAndGet(w -> w.x(updated.getX())
                    .y(updated.getY())
                    .z(updated.getZ())
                    .height(updated.getHeight())
                    .width(updated.getWidth()))
                    .lastModificationDate(OffsetDateTime.now());
        };
    }
}
