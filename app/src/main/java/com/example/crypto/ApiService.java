package com.example.crypto;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // Legnépszerűbb 50 kripto lekérése
    @GET("coins/markets?vs_currency=usd&order=market_cap_desc&per_page=50&page=1&sparkline=false")
    Call<List<CryptoCoin>> getCoins();

    // Historikus adatok lekérése a diagramhoz (pl. elmúlt 7 nap)
    @GET("coins/{id}/market_chart")
    Call<ChartResponse> getHistoricalData(
            @Path("id") String coinId,
            @Query("vs_currency") String currency,
            @Query("days") int days
    );
}