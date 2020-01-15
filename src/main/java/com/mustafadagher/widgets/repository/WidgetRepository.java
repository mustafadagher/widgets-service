package com.mustafadagher.widgets.repository;

import com.mustafadagher.widgets.model.Widget;

import java.util.List;

public interface WidgetRepository {
    Widget save(Widget widget);

    List<Widget> findAll();

    List<Widget> findAllByZGreaterThanOrEqual(Long z);
}
