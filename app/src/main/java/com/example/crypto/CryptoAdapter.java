package com.example.crypto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CryptoAdapter extends RecyclerView.Adapter<CryptoAdapter.CryptoViewHolder> {

    private Context context;
    private List<CryptoCoin> coinList;
    private List<CryptoCoin> coinListFiltered;
    private SharedPreferences prefs;
    private boolean isShowingFavoritesOnly = false;

    public CryptoAdapter(Context context, List<CryptoCoin> coinList) {
        this.context = context;
        this.coinList = coinList;
        this.coinListFiltered = new ArrayList<>(coinList);
        this.prefs = context.getSharedPreferences("crypto_prefs", Context.MODE_PRIVATE);
    }

    public void updateData(List<CryptoCoin> newCoins) {
        this.coinList = newCoins;
    }

    public void filter(String text, boolean showOnlyFavorites) {
        this.isShowingFavoritesOnly = showOnlyFavorites;
        coinListFiltered.clear();
        Set<String> favorites = prefs.getStringSet("favorites", new HashSet<>());

        for (CryptoCoin coin : coinList) {
            boolean matchesSearch = text.isEmpty() ||
                    coin.name.toLowerCase().contains(text.toLowerCase()) ||
                    coin.symbol.toLowerCase().contains(text.toLowerCase());

            boolean matchesTab = !showOnlyFavorites || favorites.contains(coin.id);

            if (matchesSearch && matchesTab) {
                coinListFiltered.add(coin);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CryptoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_crypto, parent, false);
        return new CryptoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CryptoViewHolder holder, int position) {
        CryptoCoin coin = coinListFiltered.get(position);

        holder.tvName.setText(coin.name);
        holder.tvSymbol.setText(coin.symbol);
        holder.tvPrice.setText("$" + coin.currentPrice);

        holder.tvChange.setText(String.format("%.2f%%", coin.priceChange24h));
        if (coin.priceChange24h < 0) {
            holder.tvChange.setTextColor(Color.RED);
        } else {
            holder.tvChange.setTextColor(Color.parseColor("#00AA00"));
        }

        Glide.with(context).load(coin.image).into(holder.imgLogo);

        Set<String> favorites = new HashSet<>(prefs.getStringSet("favorites", new HashSet<>()));
        boolean isFav = favorites.contains(coin.id);

        if (isFav) {
            holder.imgFavorite.setImageResource(android.R.drawable.btn_star_big_on);
            holder.imgFavorite.setColorFilter(Color.parseColor("#FFC107"));
        } else {
            holder.imgFavorite.setImageResource(android.R.drawable.btn_star_big_off);
            holder.imgFavorite.setColorFilter(Color.parseColor("#B0B0B0"));
        }

        holder.imgFavorite.setOnClickListener(v -> {
            boolean currentlyFav = favorites.contains(coin.id);

            v.animate().scaleX(1.3f).scaleY(1.3f).setDuration(150).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
            }).start();

            if (currentlyFav) {
                favorites.remove(coin.id);
                holder.imgFavorite.setImageResource(android.R.drawable.btn_star_big_off);
                holder.imgFavorite.setColorFilter(Color.parseColor("#B0B0B0"));

                if (isShowingFavoritesOnly) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        coinListFiltered.remove(adapterPosition);
                        notifyItemRemoved(adapterPosition);
                    }
                }
            } else {
                favorites.add(coin.id);
                holder.imgFavorite.setImageResource(android.R.drawable.btn_star_big_on);
                holder.imgFavorite.setColorFilter(Color.parseColor("#FFC107"));
            }

            prefs.edit().putStringSet("favorites", favorites).apply();
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("COIN_ID", coin.id);
            intent.putExtra("COIN_NAME", coin.name);
            intent.putExtra("COIN_PRICE", coin.currentPrice);
            intent.putExtra("COIN_HIGH", coin.high24h);
            intent.putExtra("COIN_LOW", coin.low24h);
            intent.putExtra("COIN_MARKET_CAP", coin.marketCap);
            intent.putExtra("COIN_RANK", coin.marketCapRank);
            intent.putExtra("COIN_VOLUME", coin.totalVolume);
            intent.putExtra("COIN_SUPPLY", coin.circulatingSupply);
            intent.putExtra("COIN_ATH", coin.ath);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return coinListFiltered.size();
    }

    public static class CryptoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgLogo, imgFavorite;
        TextView tvName, tvSymbol, tvPrice, tvChange;

        public CryptoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgLogo = itemView.findViewById(R.id.imgLogo);
            imgFavorite = itemView.findViewById(R.id.imgFavorite);
            tvName = itemView.findViewById(R.id.tvName);
            tvSymbol = itemView.findViewById(R.id.tvSymbol);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvChange = itemView.findViewById(R.id.tvChange);
        }
    }
}