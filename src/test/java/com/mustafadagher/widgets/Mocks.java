package com.mustafadagher.widgets;

import com.mustafadagher.widgets.model.WidgetRequest;

public class Mocks {

    public static WidgetRequest aValidWidgetRequest() {
        return new WidgetRequest()
                .height(1.5F).width(1.5F)
                .x(0L).y(0L).z(-1L);
    }
}
