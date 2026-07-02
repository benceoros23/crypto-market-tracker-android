package com.example.crypto;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CryptoAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private TabLayout tabLayout;
    private ApiService apiService;
    private boolean isFavoritesTab = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        searchView = findViewById(R.id.searchView);
        tabLayout = findViewById(R.id.tabLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CryptoAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        fetchData();

        swipeRefreshLayout.setOnRefreshListener(this::fetchData);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyFilters();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilters();
                return false;
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isFavoritesTab = (tab.getPosition() == 1);
                applyFilters();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                isFavoritesTab = (tab.getPosition() == 1);
                applyFilters();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyFilters();
    }

    private void fetchData() {
        swipeRefreshLayout.setRefreshing(true);
        apiService.getCoins().enqueue(new Callback<List<CryptoCoin>>() {
            @Override
            public void onResponse(Call<List<CryptoCoin>> call, Response<List<CryptoCoin>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body());
                    applyFilters();
                } else {
                    Toast.makeText(MainActivity.this, "Hiba az adatok lekérésekor!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<CryptoCoin>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, "Hálózati hiba!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilters() {
        String query = searchView.getQuery().toString();
        adapter.filter(query, isFavoritesTab);
    }
}