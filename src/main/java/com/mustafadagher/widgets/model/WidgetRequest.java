package com.mustafadagher.widgets.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class WidgetRequest {
    @JsonProperty("x")
    private Long x;

    @JsonProperty("y")
    private Long y;

    @JsonProperty("z")
    private Long z;

    @JsonProperty("width")
    private Float width;

    @JsonProperty("height")
    private Float height;

    public WidgetRequest x(Long x) {
        this.x = x;
        return this;
    }


    @ApiModelProperty(required = true)
    @NotNull
    public Long getX() {
        return x;
    }

    public void setX(Long x) {
        this.x = x;
    }

    public WidgetRequest y(Long y) {
        this.y = y;
        return this;
    }


    @ApiModelProperty(required = true)
    @NotNull
    public Long getY() {
        return y;
    }

    public void setY(Long y) {
        this.y = y;
    }

    public WidgetRequest z(Long z) {
        this.z = z;
        return this;
    }

    @ApiModelProperty
    public Long getZ() {
        return z;
    }

    public void setZ(Long z) {
        this.z = z;
    }

    public WidgetRequest width(Float width) {
        this.width = width;
        return this;
    }


    @ApiModelProperty(required = true)
    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    public Float getWidth() {
        return width;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public WidgetRequest height(Float height) {
        this.height = height;
        return this;
    }

    @ApiModelProperty(required = true)
    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
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

    @Override
    public String toString() {
        return "class WidgetRequest {\n" +
                "    x: " + toIndentedString(x) + "\n" +
                "    y: " + toIndentedString(y) + "\n" +
                "    z: " + toIndentedString(z) + "\n" +
                "    width: " + toIndentedString(width) + "\n" +
                "    height: " + toIndentedString(height) + "\n" +
                "}";
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

