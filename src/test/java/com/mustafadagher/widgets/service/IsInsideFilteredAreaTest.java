package com.mustafadagher.widgets.service;

import com.mustafadagher.widgets.model.Widget;
import com.mustafadagher.widgets.model.WidgetAreaFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class IsInsideFilteredAreaTest {

    @ParameterizedTest
    @CsvSource({"50,50", "50,100"})
    void testReturnsTrueIfInside(long x, long y) {
        // Given
        Widget w = new Widget().width(100F).height(100F).x(x).y(y);
        WidgetAreaFilter filter = new WidgetAreaFilter(0, 100, 0, 150);

        // When
        IsInsideFilteredArea isInsideFilteredArea = IsInsideFilteredArea.withinArea(filter);
        boolean test = isInsideFilteredArea.test(w);

        // Then
        assertThat(test).isTrue();
    }

    @Test
    void testReturnsFalseIfOutside() {
        // Given
        Widget w = new Widget().width(100F).height(100F).x(100L).y(100L);
        WidgetAreaFilter filter = new WidgetAreaFilter(0, 100, 0, 150);

        // When
        IsInsideFilteredArea isInsideFilteredArea = IsInsideFilteredArea.withinArea(filter);
        boolean test = isInsideFilteredArea.test(w);

        // Then
        assertThat(test).isFalse();
    }
}