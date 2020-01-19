package com.mustafadagher.widgets;

import com.mustafadagher.widgets.model.Widget;
import com.mustafadagher.widgets.model.WidgetRequest;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Mocks {

    public static WidgetRequest aValidWidgetRequest() {
        return new WidgetRequest()
                .height(100F).width(100F)
                .x(50L).y(50L).z(-1L);
    }

    public static Widget aValidWidget() {
        return new Widget()
                .id(UUID.randomUUID())
                .lastModificationDate(OffsetDateTime.now())
                .height(100F).width(100F)
                .x(50L).y(50L).z(-1L);
    }
}
