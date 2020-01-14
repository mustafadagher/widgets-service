package com.mustafadagher.widgets;

import com.mustafadagher.widgets.model.Widget;
import com.mustafadagher.widgets.model.WidgetRequest;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Mocks {

    public static WidgetRequest aValidWidgetRequest() {
        return new WidgetRequest()
                .height(1.5F).width(1.5F)
                .x(0L).y(0L).z(-1L);
    }

    public static Widget aValidWidget() {
        return new Widget()
                .id(UUID.randomUUID())
                .lastModificationDate(OffsetDateTime.now())
                .height(1.5F).width(1.5F)
                .x(0L).y(0L).z(-1L);
    }
}
