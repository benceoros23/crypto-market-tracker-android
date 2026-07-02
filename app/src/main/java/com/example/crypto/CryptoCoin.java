package com.example.crypto;

import com.google.gson.annotations.SerializedName;

public class CryptoCoin {
    public String id;
    public String symbol;
    public String name;
    public String image;

    @SerializedName("current_price")
    public double currentPrice;

    @SerializedName("price_change_percentage_24h")
    public double priceChange24h;

    @SerializedName("high_24h")
    public double high24h;

    @SerializedName("low_24h")
    public double low24h;

    @SerializedName("market_cap")
    public double marketCap;

    @SerializedName("market_cap_rank")
    public int marketCapRank;

    @SerializedName("total_volume")
    public double totalVolume;

    @SerializedName("circulating_supply")
    public double circulatingSupply;

    @SerializedName("ath")
    public double ath;
}