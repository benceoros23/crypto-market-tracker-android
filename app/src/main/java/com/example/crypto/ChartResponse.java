package com.example.crypto;

import java.util.List;

public class ChartResponse {
    // A CoinGecko API egy listát ad vissza listákból: [időbélyeg, ár]
    public List<List<Double>> prices;
}