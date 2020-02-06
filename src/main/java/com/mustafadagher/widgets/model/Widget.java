package com.mustafadagher.widgets.model;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Widget {
    @Valid
    private UUID id;
    @NotNull
    private Long x;
    @NotNull
    private Long y;
    private Long z;
    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    private Float width;
    @NotNull
    @Valid
    @DecimalMin(value = "0", inclusive = false)
    private Float height;
    @Valid
    private OffsetDateTime lastModificationDate;

    public Widget id(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getId() {
        return id;
    }

    public Widget x(Long x) {
        this.x = x;
        return this;
    }

    public Long getX() {
        return x;
    }

    public Widget y(Long y) {
        this.y = y;
        return this;
    }

    public Long getY() {
        return y;
    }

    public Widget z(Long z) {
        this.z = z;
        return this;
    }

    public Long getZ() {
        return z;
    }

    public Widget width(Float width) {
        this.width = width;
        return this;
    }

    public Float getWidth() {
        return width;
    }

    public Widget height(Float height) {
        this.height = height;
        return this;
    }


    public Float getHeight() {
        return height;
    }

    public Widget lastModificationDate(OffsetDateTime lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
        return this;
    }

    public OffsetDateTime getLastModificationDate() {
        return lastModificationDate;
    }

    public static Widget fromWidgetRequest(WidgetRequest widgetRequest) {
        return new Widget()
                .id(UUID.randomUUID())
                .lastModificationDate(OffsetDateTime.now())
                .x(widgetRequest.getX())
                .y(widgetRequest.getY())
                .z(widgetRequest.getZ())
                .height(widgetRequest.getHeight())
                .width(widgetRequest.getWidth());
    }
}

