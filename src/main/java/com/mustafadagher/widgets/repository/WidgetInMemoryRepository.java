package com.mustafadagher.widgets.repository;

import com.mustafadagher.widgets.model.Widget;
import org.springframework.stereotype.Repository;

@Repository
public class WidgetInMemoryRepository implements WidgetRepository {
    public Widget save(Widget widget) {
        return widget;
    }
}
