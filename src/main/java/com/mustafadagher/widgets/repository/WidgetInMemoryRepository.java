package com.mustafadagher.widgets.repository;

import com.mustafadagher.widgets.model.Widget;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Repository
public class WidgetInMemoryRepository implements WidgetRepository {
    private final Map<UUID, Widget> storage;

    public WidgetInMemoryRepository() {
        this.storage = new ConcurrentHashMap<>();
    }

    public Widget save(Widget widget) {
        return storage.computeIfAbsent(widget.getId(), updateWidgetDescriptionFn(widget));
    }

    public Widget findById(UUID id) {
        return storage.get(id);
    }

    private Function<UUID, Widget> updateWidgetDescriptionFn(Widget widget) {
        return i -> {
            if (storage.containsKey(i)) {
                Widget old = storage.get(i);
                return old.x(widget.getX()).y(widget.getY()).z(widget.getZ()).height(widget.getHeight()).width(widget.getWidth());
            }
            return widget;
        };
    }
}
