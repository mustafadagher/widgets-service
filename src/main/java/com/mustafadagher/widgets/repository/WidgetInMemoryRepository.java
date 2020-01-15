package com.mustafadagher.widgets.repository;

import com.mustafadagher.widgets.model.Widget;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
        return storage.values().stream()
                .sorted(Comparator.comparing(Widget::getZ))
                .collect(Collectors.toList());
    }

    public List<Widget> findAllByZGreaterThanOrEqual(Long z) {
        return storage.values().stream()
                .filter(w -> w.getZ() >= z)
                .sorted(Comparator.comparing(Widget::getZ))
                .collect(Collectors.toList());
    }

    public Widget findById(UUID id) {
        return storage.get(id);
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
