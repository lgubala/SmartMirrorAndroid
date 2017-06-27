package com.pejko.portal.entity;

public class ModelBus extends Entity{

    private String city;
    private String station;
    private String time;
    private String symbol;

    private int hour;
    private int minute;

    public ModelBus() {
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    @Override
    public String toString() {
        return "ModelBus{" +
                "city='" + city + '\'' +
                ", station='" + station + '\'' +
                ", time='" + time + '\'' +
                ", symbol='" + symbol + '\'' +
                ", hour=" + hour +
                ", minute=" + minute +
                '}';
    }
}
