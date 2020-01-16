package com.mustafadagher.widgets.exception;

public class WidgetNotFoundException extends RuntimeException {
    public WidgetNotFoundException() {
        super("No Widgets found with the specified id");
    }
}
