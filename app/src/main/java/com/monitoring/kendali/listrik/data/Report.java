package com.monitoring.kendali.listrik.data;

public class Report {
    private String time;
    private String voltage;
    private String current;
    private String power;
    private String watt_h;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getWatt_h() {
        return watt_h;
    }

    public void setWatt_h(String watt_h) {
        this.watt_h = watt_h;
    }
}
