package com.mustafadagher.widgets.repository;

import com.mustafadagher.widgets.model.Widget;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WidgetRepository {
    Widget save(Widget widget);

    Optional<Widget> findById(UUID id);

    List<Widget> findAll();

    List<Widget> findAllByZGreaterThanOrEqual(Long z);

    List<Widget> findAllByOrderByZAsc(int page, int size);

    void deleteById(UUID widgetId);
}
