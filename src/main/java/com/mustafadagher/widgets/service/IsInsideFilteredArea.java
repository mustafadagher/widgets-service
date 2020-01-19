package com.mustafadagher.widgets.service;

import com.mustafadagher.widgets.model.Widget;
import com.mustafadagher.widgets.model.WidgetAreaFilter;

import java.util.Objects;
import java.util.function.Predicate;

public class IsInsideFilteredArea implements Predicate<Widget> {
    private final WidgetAreaFilter filter;

    private IsInsideFilteredArea(WidgetAreaFilter filter) {
        this.filter = filter;
    }

    @Override
    public boolean test(Widget widget) {
        float deltaX = widget.getWidth() / 2F;
        float deltaY = widget.getHeight() / 2F;

        long widgetLeft = (long) Math.floor(widget.getX() - deltaX);
        long widgetLow = (long) Math.floor(widget.getY() - deltaY);
        long widgetRight = (long) Math.ceil(widget.getX() + deltaX);
        long widgetHigh = (long) Math.ceil(widget.getY() + deltaY);

        return widgetLeft >= filter.getLeftX()
                && widgetLow >= filter.getLowerY()
                && widgetRight <= filter.getRightX()
                && widgetHigh <= filter.getHigherY();
    }

    public static IsInsideFilteredArea withinArea(WidgetAreaFilter filter) {
        return new IsInsideFilteredArea(filter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IsInsideFilteredArea that = (IsInsideFilteredArea) o;
        return filter.equals(that.filter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filter);
    }
}
