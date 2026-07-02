package com.example.crypto;

import android.content.Context;
import android.widget.TextView;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomMarkerView extends MarkerView {

    private TextView tvContent;
    private List<Long> timestamps;
    private SimpleDateFormat sdf;

    public CustomMarkerView(Context context, int layoutResource, List<Long> timestamps) {
        super(context, layoutResource);
        tvContent = findViewById(R.id.tvContent);
        this.timestamps = timestamps;
        // Dátum formátum: Pl. "Márc 28, 14:30"
        this.sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
    }

    // Ez a metódus frissíti a szöveget, amikor mozgatod az ujjad
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        int index = (int) e.getX();
        if (index >= 0 && index < timestamps.size()) {
            long time = timestamps.get(index);
            String dateStr = sdf.format(new Date(time));
            tvContent.setText("Ár: $" + e.getY() + "\nIdő: " + dateStr);
        } else {
            tvContent.setText("Ár: $" + e.getY());
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2f), -getHeight() - 15f);
    }
}