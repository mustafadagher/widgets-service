package com.mustafadagher.widgets.model;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Widget {
    private UUID id;
    private Long x;
    private Long y;
    private Long z;
    private Float width;
    private Float height;
    private OffsetDateTime lastModificationDate;

    public Widget id(UUID id) {
        this.id = id;
        return this;
    }

    @Valid
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Widget x(Long x) {
        this.x = x;
        return this;
    }

    @NotNull
    public Long getX() {
        return x;
    }

    public void setX(Long x) {
        this.x = x;
    }

    public Widget y(Long y) {
        this.y = y;
        return this;
    }

    @NotNull
    public Long getY() {
        return y;
    }

    public void setY(Long y) {
        this.y = y;
    }

    public Widget z(Long z) {
        this.z = z;
        return this;
    }

    public Long getZ() {
        return z;
    }

    public void setZ(Long z) {
        this.z = z;
    }

    public Widget width(Float width) {
        this.width = width;
        return this;
    }

    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    public Float getWidth() {
        return width;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public Widget height(Float height) {
        this.height = height;
        return this;
    }

    @NotNull
    @Valid
    @DecimalMin(value = "0", inclusive = false)
    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Widget lastModificationDate(OffsetDateTime lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
        return this;
    }


    @Valid
    public OffsetDateTime getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(OffsetDateTime lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public static Widget mapToWidget(@Valid WidgetRequest widgetRequest) {
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

