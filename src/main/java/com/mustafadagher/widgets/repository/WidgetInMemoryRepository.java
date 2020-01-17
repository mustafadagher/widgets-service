package com.mustafadagher.widgets.repository;

import com.mustafadagher.widgets.model.Widget;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class WidgetInMemoryRepository implements WidgetRepository {
    private final Map<UUID, Widget> storage;

    public WidgetInMemoryRepository() {
        this.storage = new ConcurrentHashMap<>();
    }

    public Widget save(Widget widget) {
        return storage.computeIfAbsent(widget.getId(), updateWidgetDescriptionFn(widget));
    }

    public List<Widget> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<Widget> findAllByOrderByZAsc() {
        return findAllByOrderBy(Comparator.comparing(Widget::getZ));
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

    private Function<UUID, Widget> updateWidgetDescriptionFn(Widget widget) {
        return i -> {
            if (storage.containsKey(i)) {
                AtomicReference<Widget> old = new AtomicReference<>(storage.get(i));

                return old.updateAndGet(w -> w.x(widget.getX()).y(widget.getY()).z(widget.getZ()).height(widget.getHeight()).width(widget.getWidth()));
            }
            return widget;
        };
    }
}
