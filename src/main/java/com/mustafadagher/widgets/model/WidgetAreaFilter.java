package com.mustafadagher.widgets.model;

public class WidgetAreaFilter {
    private final Integer leftX;
    private final Integer rightX;
    private final Integer lowerY;
    private final Integer higherY;

    public WidgetAreaFilter(Integer leftX, Integer rightX, Integer lowerY, Integer higherY) {
        this.leftX = leftX;
        this.rightX = rightX;
        this.lowerY = lowerY;
        this.higherY = higherY;
    }

    public boolean isValid() {
        return leftX != null && rightX != null && lowerY != null && higherY != null;
    }

    public boolean isNotValid() {
        return !isValid();
    }

    public boolean isALineOrADot() {
        return leftX.equals(rightX) || lowerY.equals(higherY);
    }

    public boolean isNotALineNorADot() {
        return !isALineOrADot();
    }

    public Integer getLeftX() {
        return leftX;
    }

    public Integer getRightX() {
        return rightX;
    }

    public Integer getLowerY() {
        return lowerY;
    }

    public Integer getHigherY() {
        return higherY;
    }
}
