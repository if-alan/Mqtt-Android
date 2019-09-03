package com.monitoring.kendali.listrik.data;

import java.text.NumberFormat;
import java.util.Locale;

public class Report {
    private String date;
    private int energy;
    private String price;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        double energyKwh = energy / 1000.0;

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        setPrice(formatRupiah.format(energyKwh * 1467));

        this.energy = energy;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
