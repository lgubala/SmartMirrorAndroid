package com.pejko.portal.weather;

public class ModelWeather {

    private double min;
    private double max;
    private String iconText;

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public String getIconText() {
        return iconText;
    }

    public void setIconText(String iconText) {
        this.iconText = iconText;
    }

    @Override
    public String toString() {
        return "ModelWeather{" +
                "min=" + min +
                ", max=" + max +
                ", iconText='" + iconText + '\'' +
                '}';
    }
}
