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
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;

public class StudyScedule extends AppCompatActivity {

    LinearLayout llScheduleList;
    TextInputEditText etSubject, etDay, etTime;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_scedule);

        db             = new DatabaseHelper(this);
        llScheduleList = findViewById(R.id.llScheduleList);
        etSubject      = findViewById(R.id.etSubject);
        etDay          = findViewById(R.id.etDay);
        etTime         = findViewById(R.id.etTime);

        findViewById(R.id.btnHome).setOnClickListener(v -> finish());

        loadSchedule();

        findViewById(R.id.btnAdd).setOnClickListener(v -> {
            String sub  = etSubject.getText().toString().trim();
            String day  = etDay.getText().toString().trim();
            String time = etTime.getText().toString().trim();

            if (sub.isEmpty()) {
                Toast.makeText(this, "Subject is required",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            db.saveSchedule(sub, day, time);
            etSubject.setText(""); etDay.setText(""); etTime.setText("");
            llScheduleList.removeAllViews();
            loadSchedule();
        });
    }

    void loadSchedule() {
        ArrayList<String[]> list = db.getAllScheduleWithId();
        for (String[] s : list) {
            // s[0]=id, s[1]=subject, s[2]=day, s[3]=time
            addScheduleCard(Integer.parseInt(s[0]), s[1], s[2], s[3]);
        }
    }

    void addScheduleCard(int id, String subject, String day, String time) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cp.setMargins(0, 0, 0, 12);
        card.setLayoutParams(cp);
        card.setRadius(16);
        card.setCardBackgroundColor(Color.parseColor("#CC2A1B5E"));
        card.setCardElevation(0);

        LinearLayout outer = new LinearLayout(this);
        outer.setOrientation(LinearLayout.VERTICAL);
        outer.setPadding(32, 24, 32, 20);

        // ── Top row — subject + edit + delete ──────────
        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView tvSubject = new TextView(this);
        tvSubject.setText(subject);
        tvSubject.setTextColor(Color.parseColor("#C4A8E0"));
        tvSubject.setTextSize(17);
        tvSubject.setTypeface(null, Typeface.BOLD);
        tvSubject.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView btnEdit = new TextView(this);
        btnEdit.setText("Edit");
        btnEdit.setTextColor(Color.parseColor("#8B6BA8"));
        btnEdit.setTextSize(13);
        btnEdit.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams ep = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        ep.setMarginEnd(16);
        btnEdit.setLayoutParams(ep);

        TextView btnDelete = new TextView(this);
        btnDelete.setText("Delete");
        btnDelete.setTextColor(Color.parseColor("#E74C3C"));
        btnDelete.setTextSize(13);
        btnDelete.setTypeface(null, Typeface.BOLD);

        topRow.addView(tvSubject);
        topRow.addView(btnEdit);
        topRow.addView(btnDelete);

        // ── Day ────────────────────────────────────────
        TextView tvDay = new TextView(this);
        tvDay.setText(day);
        tvDay.setTextColor(Color.parseColor("#8B6BA8"));
        tvDay.setTextSize(14);
        LinearLayout.LayoutParams dayP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dayP.setMargins(0, 6, 0, 2);
        tvDay.setLayoutParams(dayP);

        // ── Time ───────────────────────────────────────
        TextView tvTime = new TextView(this);
        tvTime.setText(time);
        tvTime.setTextColor(Color.parseColor("#6B4F8A"));
        tvTime.setTextSize(13);

        // ── Divider ────────────────────────────────────
        View divider = new View(this);
        LinearLayout.LayoutParams divP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        divP.setMargins(0, 12, 0, 0);
        divider.setLayoutParams(divP);
        divider.setBackgroundColor(Color.parseColor("#332A1B5E"));

        outer.addView(topRow);
        outer.addView(tvDay);
        outer.addView(tvTime);
        outer.addView(divider);
        card.addView(outer);
        llScheduleList.addView(card);

        // ── DELETE ─────────────────────────────────────
        btnDelete.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Delete Schedule")
                        .setMessage("\"" + subject + "\" Are you sure to delete?")
                        .setPositiveButton("Delete", (d, w) -> {
                            db.deleteSchedule(id);
                            llScheduleList.removeAllViews();
                            loadSchedule();
                        })
                        .setNegativeButton("Cancel", null)
                        .show());

        // ── EDIT ───────────────────────────────────────
        btnEdit.setOnClickListener(v ->
                showEditDialog(id, subject, day, time));
    }

    void showEditDialog(int id, String oldSub,
                        String oldDay, String oldTime) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 32, 48, 16);
        layout.setBackgroundColor(Color.parseColor("#1A0F2E"));

        EditText etS = makeEditField("Subject", oldSub);
        EditText etD = makeEditField("Day", oldDay);
        EditText etT = makeEditField("Time", oldTime);

        layout.addView(etS);
        layout.addView(etD);
        layout.addView(etT);

        new AlertDialog.Builder(this)
                .setTitle("Edit Schedule")
                .setView(layout)
                .setPositiveButton("Save", (d, w) -> {
                    String ns = etS.getText().toString().trim();
                    String nd = etD.getText().toString().trim();
                    String nt = etT.getText().toString().trim();
                    if (ns.isEmpty()) {
                        Toast.makeText(this, "Subject required",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    db.updateSchedule(id, ns, nd, nt);
                    llScheduleList.removeAllViews();
                    loadSchedule();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    EditText makeEditField(String hint, String value) {
        EditText et = new EditText(this);
        et.setText(value);
        et.setHint(hint);
        et.setTextColor(Color.parseColor("#C4A8E0"));
        et.setHintTextColor(Color.parseColor("#6B4F8A"));
        et.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#4A2D6B")));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, 20);
        et.setLayoutParams(p);
        return et;
    }
}