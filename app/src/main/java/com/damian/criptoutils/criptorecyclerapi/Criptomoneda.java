package com.damian.criptoutils.criptorecyclerapi;

import org.json.JSONArray;

public class Criptomoneda {

    private String id;
    private String symbol;
    private String name;
    private String image;
    private double current_price;
    private long market_cap;
    private int market_cap_rank;
    private long fully_diluted_valuation;
    private long total_volume;
    private double high_24h;
    private double low_24h;
    private double price_change_24h;
    private double price_change_percentage_24h;
    private double market_cap_change_24h;
    private double market_cap_change_percentage_24h;
    private double circulating_supply;
    private double total_supply;
    private double max_supply;
    private double ath;
    private double ath_change_percentage;
    private String ath_date;
    private double atl;
    private double atl_change_percentage;
    private String atl_date;
    private JSONArray roi;
    private String last_updated;

    public String getNombre() {
        return name;
    }

    public double getPrecio() {
        return current_price;
    }

    public double getVariacionPrecio() {
        return price_change_percentage_24h;
    }

    public String getVariacionPrecio2Decis() {
        return String.format("%.2f", price_change_percentage_24h);
    }

    public String getIcono() {
        return image;
    }

    public String getId() {
        return id;
    }

    public long getMarketCap() {
        return market_cap;
    }
}
