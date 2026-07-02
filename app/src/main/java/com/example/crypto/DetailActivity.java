package com.example.crypto;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private LineChart lineChart;
    private TextView tvDetailName, tvDetailPrice, tvHigh, tvLow;
    private TextView tvMarketCap, tvRank, tvVolume, tvSupply, tvAth;
    private ImageView btnBack;
    private ApiService apiService;
    private String coinId;

    private List<Long> timestamps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        lineChart = findViewById(R.id.lineChart);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvHigh = findViewById(R.id.tvHigh);
        tvLow = findViewById(R.id.tvLow);
        btnBack = findViewById(R.id.btnBack);

        tvMarketCap = findViewById(R.id.tvMarketCap);
        tvRank = findViewById(R.id.tvRank);
        tvVolume = findViewById(R.id.tvVolume);
        tvSupply = findViewById(R.id.tvSupply);
        tvAth = findViewById(R.id.tvAth);

        btnBack.setOnClickListener(v -> finish());

        coinId = getIntent().getStringExtra("COIN_ID");
        String name = getIntent().getStringExtra("COIN_NAME");
        double price = getIntent().getDoubleExtra("COIN_PRICE", 0);
        double high24h = getIntent().getDoubleExtra("COIN_HIGH", 0);
        double low24h = getIntent().getDoubleExtra("COIN_LOW", 0);

        double marketCap = getIntent().getDoubleExtra("COIN_MARKET_CAP", 0);
        int rank = getIntent().getIntExtra("COIN_RANK", 0);
        double volume = getIntent().getDoubleExtra("COIN_VOLUME", 0);
        double supply = getIntent().getDoubleExtra("COIN_SUPPLY", 0);
        double ath = getIntent().getDoubleExtra("COIN_ATH", 0);

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

        tvDetailName.setText(name);
        tvDetailPrice.setText("Jelenlegi ár: $" + numberFormat.format(price));
        tvHigh.setText("Napi Max: $" + numberFormat.format(high24h));
        tvLow.setText("Napi Min: $" + numberFormat.format(low24h));

        tvMarketCap.setText("$" + numberFormat.format(marketCap));
        tvRank.setText("#" + rank);
        tvVolume.setText("$" + numberFormat.format(volume));
        tvSupply.setText(numberFormat.format(supply));
        tvAth.setText("$" + numberFormat.format(ath));

        apiService = ApiClient.getClient().create(ApiService.class);

        setupChart();
        fetchChartData();
    }

    private void setupChart() {
        lineChart.setDrawGridBackground(false);
        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);
        xAxis.setLabelCount(5, false);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.setDoubleTapToZoomEnabled(false);
    }

    private void fetchChartData() {
        apiService.getHistoricalData(coinId, "usd", 7).enqueue(new Callback<ChartResponse>() {
            @Override
            public void onResponse(Call<ChartResponse> call, Response<ChartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<List<Double>> prices = response.body().prices;
                    ArrayList<Entry> entries = new ArrayList<>();
                    timestamps.clear();

                    for (int i = 0; i < prices.size(); i++) {
                        long time = prices.get(i).get(0).longValue();
                        timestamps.add(time);
                        float price = prices.get(i).get(1).floatValue();
                        entries.add(new Entry(i, price));
                    }

                    LineDataSet dataSet = new LineDataSet(entries, "Árfolyam (USD)");
                    dataSet.setColor(Color.BLUE);
                    dataSet.setDrawCircles(false);
                    dataSet.setLineWidth(2f);
                    dataSet.setDrawFilled(true);
                    dataSet.setFillColor(Color.BLUE);
                    dataSet.setFillAlpha(30);

                    lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
                        private SimpleDateFormat mFormat = new SimpleDateFormat("MM.dd", Locale.getDefault());
                        @Override
                        public String getAxisLabel(float value, AxisBase axis) {
                            int index = (int) value;
                            if (index >= 0 && index < timestamps.size()) {
                                return mFormat.format(new Date(timestamps.get(index)));
                            }
                            return "";
                        }
                    });

                    CustomMarkerView mv = new CustomMarkerView(DetailActivity.this, R.layout.custom_marker_view, timestamps);
                    mv.setChartView(lineChart);
                    lineChart.setMarker(mv);

                    LineData lineData = new LineData(dataSet);
                    lineChart.setData(lineData);
                    lineChart.invalidate();
                } else {
                    Toast.makeText(DetailActivity.this, "Nem sikerült betölteni a diagramot.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ChartResponse> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Hálózati hiba a diagramnál.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}