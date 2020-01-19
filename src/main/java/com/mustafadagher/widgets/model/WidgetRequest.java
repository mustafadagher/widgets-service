package com.mustafadagher.widgets.model;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class WidgetRequest {
    @NotNull
    private Long x;
    @NotNull
    private Long y;
    private Long z;
    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    private Float width;
    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    private Float height;

    public WidgetRequest x(Long x) {
        this.x = x;
        return this;
    }

    public Long getX() {
        return x;
    }

    public WidgetRequest y(Long y) {
        this.y = y;
        return this;
    }

    public Long getY() {
        return y;
    }

    public WidgetRequest z(Long z) {
        this.z = z;
        return this;
    }

    public Long getZ() {
        return z;
    }

    public WidgetRequest width(Float width) {
        this.width = width;
        return this;
    }

    public Float getWidth() {
        return width;
    }

    public WidgetRequest height(Float height) {
        this.height = height;
        return this;
    }

    public Float getHeight() {
        return height;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WidgetRequest widgetRequest = (WidgetRequest) o;
        return Objects.equals(this.x, widgetRequest.x) &&
                Objects.equals(this.y, widgetRequest.y) &&
                Objects.equals(this.z, widgetRequest.z) &&
                Objects.equals(this.width, widgetRequest.width) &&
                Objects.equals(this.height, widgetRequest.height);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, width, height);
    }
}

