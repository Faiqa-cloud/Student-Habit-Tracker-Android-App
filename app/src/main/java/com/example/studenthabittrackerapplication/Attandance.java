package com.example.studenthabittrackerapplication;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import java.text.SimpleDateFormat;
import java.util.*;

public class Attandance extends AppCompatActivity {

    LinearLayout llDaysContainer;
    TextView tvProgressText, tvProgressPercent, tvMonthName;
    CircularProgressIndicator progressAtt;

    int[] dayStatus;
    int totalDays = 30;
    int presentCount = 0;

    CardView[] dayCards;
    TextView[] dayTexts;

    DatabaseHelper db;          // ← ADD 1
    String currentMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attandance);

        db = new DatabaseHelper(this);   // ← ADD 2

        llDaysContainer   = findViewById(R.id.llDaysContainer);
        tvProgressText    = findViewById(R.id.tvProgressText);
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        tvMonthName       = findViewById(R.id.tvMonthName);
        progressAtt       = findViewById(R.id.progressAtt);

        findViewById(R.id.btnHome).setOnClickListener(v -> finish());

        currentMonth = new SimpleDateFormat("MMMM yyyy",
                Locale.getDefault()).format(new Date());
        tvMonthName.setText(currentMonth);

        // ← ADD 3 — DB se load karo, zeros nahi
        dayStatus = db.getAttendance(currentMonth);
        dayCards  = new CardView[totalDays];
        dayTexts  = new TextView[totalDays];

        // Present count calculate karo loaded data se
        for (int s : dayStatus) if (s == 1) presentCount++;

        buildDaysGrid();
        updateProgress();
    }

    void buildDaysGrid() {
        int dayNum = 1;
        for (int row = 0; row < 5; row++) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(0, 0, 0, 10);
            rowLayout.setLayoutParams(rowParams);

            for (int col = 0; col < 6; col++) {
                if (dayNum > totalDays) break;

                final int index = dayNum - 1;

                CardView card = new CardView(this);
                LinearLayout.LayoutParams cardParams =
                        new LinearLayout.LayoutParams(0,
                                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                cardParams.setMargins(4, 0, 4, 0);
                card.setLayoutParams(cardParams);
                card.setRadius(12);
                card.setCardElevation(0);

                LinearLayout inner = new LinearLayout(this);
                inner.setOrientation(LinearLayout.VERTICAL);
                inner.setGravity(Gravity.CENTER);
                inner.setPadding(0, 16, 0, 16);

                TextView tvDay = new TextView(this);
                tvDay.setText(String.valueOf(dayNum));
                tvDay.setTextSize(14);
                tvDay.setTypeface(null, Typeface.BOLD);
                tvDay.setGravity(Gravity.CENTER);

                TextView tvDot = new TextView(this);
                tvDot.setText("●");
                tvDot.setTextSize(8);
                tvDot.setGravity(Gravity.CENTER);

                // Saved color apply karo
                updateDayCard(index, tvDay, tvDot, card);

                inner.addView(tvDay);
                inner.addView(tvDot);
                card.addView(inner);

                dayCards[index] = card;
                dayTexts[index] = tvDay;

                card.setOnClickListener(v -> {
                    dayStatus[index] = (dayStatus[index] + 1) % 3;
                    updateDayCard(index, tvDay, tvDot, card);
                    db.saveAttendance(currentMonth,
                            index + 1, dayStatus[index]); // ← ADD 4
                    recalcPresent();
                    updateProgress();
                });

                rowLayout.addView(card);
                dayNum++;
            }
            llDaysContainer.addView(rowLayout);
        }
    }

    void updateDayCard(int index, TextView tvDay, TextView tvDot, CardView card) {
        switch (dayStatus[index]) {
            case 0:
                card.setCardBackgroundColor(Color.parseColor("#1A1A1A"));
                tvDay.setTextColor(Color.parseColor("#AAAAAA"));
                tvDot.setTextColor(Color.parseColor("#333333"));
                break;
            case 1:
                card.setCardBackgroundColor(Color.parseColor("#0D1F1A"));
                tvDay.setTextColor(Color.parseColor("#1D9E75"));
                tvDot.setTextColor(Color.parseColor("#1D9E75"));
                break;
            case 2:
                card.setCardBackgroundColor(Color.parseColor("#1F0D0D"));
                tvDay.setTextColor(Color.parseColor("#E74C3C"));
                tvDot.setTextColor(Color.parseColor("#E74C3C"));
                break;
        }
    }

    void recalcPresent() {
        presentCount = 0;
        for (int s : dayStatus)
            if (s == 1) presentCount++;
    }

    void updateProgress() {
        tvProgressText.setText(presentCount + " / " + totalDays + " Days");
        int pct = (presentCount * 100) / totalDays;
        tvProgressPercent.setText(pct + "% attendance");
        progressAtt.setProgress(pct);
    }
}