package com.mustafadagher.widgets.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class Widget {
    @JsonProperty("id")
    private UUID id;

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

    @JsonProperty("lastModificationDate")
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


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Widget widget = (Widget) o;
        return Objects.equals(this.id, widget.id) &&
                Objects.equals(this.x, widget.x) &&
                Objects.equals(this.y, widget.y) &&
                Objects.equals(this.z, widget.z) &&
                Objects.equals(this.width, widget.width) &&
                Objects.equals(this.height, widget.height) &&
                Objects.equals(this.lastModificationDate, widget.lastModificationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, x, y, z, width, height, lastModificationDate);
    }

    @Override
    public String toString() {

        return "class Widget {\n" +
                "    id: " + toIndentedString(id) + "\n" +
                "    x: " + toIndentedString(x) + "\n" +
                "    y: " + toIndentedString(y) + "\n" +
                "    z: " + toIndentedString(z) + "\n" +
                "    width: " + toIndentedString(width) + "\n" +
                "    height: " + toIndentedString(height) + "\n" +
                "    lastModificationDate: " + toIndentedString(lastModificationDate) + "\n" +
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

