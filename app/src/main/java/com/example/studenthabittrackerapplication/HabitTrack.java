package com.example.studenthabittrackerapplication;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;

public class HabitTrack extends AppCompatActivity {

    LinearLayout llHabitList;
    TextInputEditText etNewHabit;
    TextView tvHabitProgress;
    CircularProgressIndicator progressHabit;
    DatabaseHelper db;
    int total = 0, done = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_track);

        db              = new DatabaseHelper(this);
        llHabitList     = findViewById(R.id.llHabitList);
        etNewHabit      = findViewById(R.id.etNewHabit);
        tvHabitProgress = findViewById(R.id.tvHabitProgress);
        progressHabit   = findViewById(R.id.progressHabit);

        findViewById(R.id.btnHome).setOnClickListener(v -> finish());

        loadHabits();

        findViewById(R.id.btnAddHabit).setOnClickListener(v -> {
            String habit = etNewHabit.getText().toString().trim();
            if (habit.isEmpty()) {
                Toast.makeText(this, "Enter habit name",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            db.saveHabit(habit);
            etNewHabit.setText("");
            llHabitList.removeAllViews();
            total = 0; done = 0;
            loadHabits();
        });
    }

    void loadHabits() {
        ArrayList<String[]> list = db.getAllHabits();
        total = list.size();
        done  = 0;
        for (String[] h : list) {
            int     id     = Integer.parseInt(h[0]);
            String  name   = h[1];
            boolean isDone = h[2].equals("1");
            if (isDone) done++;
            addHabitCard(id, name, isDone);
        }
        updateProgress();
    }

    void addHabitCard(int id, String habit, boolean isDone) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cp.setMargins(0, 0, 0, 12);
        card.setLayoutParams(cp);
        card.setRadius(16);
        card.setCardBackgroundColor(isDone
                ? Color.parseColor("#CC1A0F2E")
                : Color.parseColor("#CC2A1B5E"));
        card.setCardElevation(0);

        LinearLayout outer = new LinearLayout(this);
        outer.setOrientation(LinearLayout.VERTICAL);
        outer.setPadding(32, 20, 32, 16);

        // ── Top row: checkbox + name + edit + delete ───
        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        CheckBox cb = new CheckBox(this);
        cb.setChecked(isDone);
        cb.setButtonTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#C4A8E0")));
        LinearLayout.LayoutParams cbP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cbP.setMarginEnd(16);
        cb.setLayoutParams(cbP);

        TextView tvName = new TextView(this);
        tvName.setText(habit);
        tvName.setTextColor(isDone
                ? Color.parseColor("#8B6BA8")
                : Color.parseColor("#C4A8E0"));
        tvName.setTextSize(17);
        tvName.setTypeface(null, isDone ? Typeface.ITALIC : Typeface.BOLD);
        tvName.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView btnEdit = new TextView(this);
        btnEdit.setText("Edit");
        btnEdit.setTextColor(Color.parseColor("#8B6BA8"));
        btnEdit.setTextSize(13);
        btnEdit.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams ep = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        ep.setMarginEnd(14);
        btnEdit.setLayoutParams(ep);

        TextView btnDelete = new TextView(this);
        btnDelete.setText("Delete");
        btnDelete.setTextColor(Color.parseColor("#E74C3C"));
        btnDelete.setTextSize(13);
        btnDelete.setTypeface(null, Typeface.BOLD);

        topRow.addView(cb);
        topRow.addView(tvName);
        topRow.addView(btnEdit);
        topRow.addView(btnDelete);

        // ── Divider ────────────────────────────────────
        View divider = new View(this);
        LinearLayout.LayoutParams divP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        divP.setMargins(0, 10, 0, 0);
        divider.setLayoutParams(divP);
        divider.setBackgroundColor(Color.parseColor("#332A1B5E"));

        outer.addView(topRow);
        outer.addView(divider);
        card.addView(outer);
        llHabitList.addView(card);

        // ── Checkbox — done toggle ─────────────────────
        cb.setOnCheckedChangeListener((btn, checked) -> {
            db.updateHabit(id, checked);
            if (checked) {
                done++;
                card.setCardBackgroundColor(
                        Color.parseColor("#CC1A0F2E"));
                tvName.setTextColor(Color.parseColor("#8B6BA8"));
                tvName.setTypeface(null, Typeface.ITALIC);
            } else {
                done--;
                card.setCardBackgroundColor(
                        Color.parseColor("#CC2A1B5E"));
                tvName.setTextColor(Color.parseColor("#C4A8E0"));
                tvName.setTypeface(null, Typeface.BOLD);
            }
            updateProgress();
        });

        // ── DELETE ─────────────────────────────────────
        btnDelete.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Delete Habit")
                        .setMessage("\"" + habit + "\" Are you sure to delete?")
                        .setPositiveButton("Delete", (d, w) -> {
                            db.deleteHabit(id);
                            llHabitList.removeAllViews();
                            total = 0; done = 0;
                            loadHabits();
                        })
                        .setNegativeButton("Cancel", null)
                        .show());

        // ── EDIT ───────────────────────────────────────
        btnEdit.setOnClickListener(v -> {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(48, 32, 48, 16);
            layout.setBackgroundColor(Color.parseColor("#1A0F2E"));

            EditText et = new EditText(this);
            et.setText(habit);
            et.setTextColor(Color.parseColor("#C4A8E0"));
            et.setHint("Habit name");
            et.setHintTextColor(Color.parseColor("#6B4F8A"));
            et.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            Color.parseColor("#4A2D6B")));
            layout.addView(et);

            new AlertDialog.Builder(this)
                    .setTitle("Edit Habit")
                    .setView(layout)
                    .setPositiveButton("Save", (d, w) -> {
                        String newName = et.getText().toString().trim();
                        if (newName.isEmpty()) {
                            Toast.makeText(this, "Name required",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        db.updateHabitName(id, newName);
                        llHabitList.removeAllViews();
                        total = 0; done = 0;
                        loadHabits();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    void updateProgress() {
        tvHabitProgress.setText(done + " / " + total + " Done");
        int pct = total == 0 ? 0 : (done * 100) / total;
        progressHabit.setProgress(pct);
    }
}