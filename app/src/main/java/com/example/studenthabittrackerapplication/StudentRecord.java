package com.example.studenthabittrackerapplication;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;

public class StudentRecord extends AppCompatActivity {

    LinearLayout llStudentList;
    TextInputEditText etName, etRoll, etDept;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_record);

        db            = new DatabaseHelper(this);
        llStudentList = findViewById(R.id.llStudentList);
        etName        = findViewById(R.id.etName);
        etRoll        = findViewById(R.id.etRoll);
        etDept        = findViewById(R.id.etDept);

        findViewById(R.id.btnHome).setOnClickListener(v -> finish());

        loadNotes();

        findViewById(R.id.btnSave).setOnClickListener(v -> {
            String title   = etName.getText().toString().trim();
            String subject = etRoll.getText().toString().trim();
            String content = etDept.getText().toString().trim();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Title and Content required",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            db.saveStudent(title, subject, content);
            etName.setText(""); etRoll.setText(""); etDept.setText("");
            // Reload
            llStudentList.removeAllViews();
            loadNotes();
        });
    }

    void loadNotes() {
        ArrayList<String[]> list = db.getAllStudentsWithId();
        for (String[] s : list) {
            // s[0]=id, s[1]=title, s[2]=subject, s[3]=content
            addNoteCard(
                    Integer.parseInt(s[0]), s[1], s[2], s[3]);
        }
    }

    void addNoteCard(int id, String title,
                     String subject, String content) {

        CardView card = new CardView(this);
        LinearLayout.LayoutParams cp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        cp.setMargins(0, 0, 0, 12);
        card.setLayoutParams(cp);
        card.setRadius(16);
        card.setCardBackgroundColor(Color.parseColor("#CC1A0F2E"));
        card.setCardElevation(0);

        LinearLayout outer = new LinearLayout(this);
        outer.setOrientation(LinearLayout.VERTICAL);
        outer.setPadding(32, 24, 32, 20);

        // ── Title row ─────────────────────────────────
        LinearLayout titleRow = new LinearLayout(this);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextColor(Color.parseColor("#C4A8E0"));
        tvTitle.setTextSize(16);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        // Edit button
        TextView btnEdit = new TextView(this);
        btnEdit.setText("Edit");
        btnEdit.setTextColor(Color.parseColor("#8B6BA8"));
        btnEdit.setTextSize(13);
        btnEdit.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams editP =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        editP.setMarginEnd(16);
        btnEdit.setLayoutParams(editP);

        // Delete button
        TextView btnDelete = new TextView(this);
        btnDelete.setText("Delete");
        btnDelete.setTextColor(Color.parseColor("#E74C3C"));
        btnDelete.setTextSize(13);
        btnDelete.setTypeface(null, Typeface.BOLD);

        titleRow.addView(tvTitle);
        titleRow.addView(btnEdit);
        titleRow.addView(btnDelete);

        // ── Subject ───────────────────────────────────
        TextView tvSubject = new TextView(this);
        tvSubject.setText(subject);
        tvSubject.setTextColor(Color.parseColor("#6B4F8A"));
        tvSubject.setTextSize(12);
        LinearLayout.LayoutParams subP =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        subP.setMargins(0, 4, 0, 8);
        tvSubject.setLayoutParams(subP);

        // ── Content preview ───────────────────────────
        TextView tvContent = new TextView(this);
        tvContent.setText(content);
        tvContent.setTextColor(Color.parseColor("#9B8EC4"));
        tvContent.setTextSize(13);
        tvContent.setMaxLines(2);
        tvContent.setEllipsize(
                android.text.TextUtils.TruncateAt.END);

        // ── Divider ───────────────────────────────────
        View divider = new View(this);
        LinearLayout.LayoutParams divP =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1);
        divP.setMargins(0, 12, 0, 12);
        divider.setLayoutParams(divP);
        divider.setBackgroundColor(Color.parseColor("#221A0F2E"));

        // ── View Detail button ────────────────────────
        TextView btnDetail = new TextView(this);
        btnDetail.setText("View full note");
        btnDetail.setTextColor(Color.parseColor("#8B6BA8"));
        btnDetail.setTextSize(13);

        outer.addView(titleRow);
        outer.addView(tvSubject);
        outer.addView(tvContent);
        outer.addView(divider);
        outer.addView(btnDetail);
        card.addView(outer);
        llStudentList.addView(card);

        // ── DETAIL — tap to view full ─────────────────
        btnDetail.setOnClickListener(v ->
                showDetailDialog(title, subject, content));

        // ── DELETE ────────────────────────────────────
        btnDelete.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Delete Note")
                        .setMessage("\"" + title + "\" Are you sure to delete?")
                        .setPositiveButton("Delete", (d, w) -> {
                            db.deleteStudent(id);
                            llStudentList.removeAllViews();
                            loadNotes();
                        })
                        .setNegativeButton("Cancel", null)
                        .show());

        // ── EDIT ──────────────────────────────────────
        btnEdit.setOnClickListener(v ->
                showEditDialog(id, title, subject, content));
    }

    // ── Detail Dialog ──────────────────────────────────
    void showDetailDialog(String title,
                          String subject, String content) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 32, 48, 32);
        layout.setBackgroundColor(Color.parseColor("#1A0F2E"));

        TextView tvT = new TextView(this);
        tvT.setText(title);
        tvT.setTextColor(Color.parseColor("#C4A8E0"));
        tvT.setTextSize(18);
        tvT.setTypeface(null, Typeface.BOLD);
        tvT.setPadding(0, 0, 0, 8);

        TextView tvS = new TextView(this);
        tvS.setText(subject);
        tvS.setTextColor(Color.parseColor("#8B6BA8"));
        tvS.setTextSize(13);
        tvS.setPadding(0, 0, 0, 16);

        View div = new View(this);
        div.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        div.setBackgroundColor(Color.parseColor("#4A2D6B"));

        TextView tvC = new TextView(this);
        tvC.setText(content);
        tvC.setTextColor(Color.parseColor("#C4A8E0"));
        tvC.setTextSize(15);
        tvC.setPadding(0, 16, 0, 0);
        tvC.setLineSpacing(6, 1);

        layout.addView(tvT);
        layout.addView(tvS);
        layout.addView(div);
        layout.addView(tvC);

        new AlertDialog.Builder(this)
                .setView(layout)
                .setPositiveButton("Close", null)
                .show();
    }

    // ── Edit Dialog ────────────────────────────────────
    void showEditDialog(int id, String oldTitle,
                        String oldSubject, String oldContent) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 32, 48, 16);
        layout.setBackgroundColor(Color.parseColor("#1A0F2E"));

        EditText etT = new EditText(this);
        etT.setText(oldTitle);
        etT.setTextColor(Color.parseColor("#C4A8E0"));
        etT.setHint("Title");
        etT.setHintTextColor(Color.parseColor("#6B4F8A"));
        etT.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#4A2D6B")));
        LinearLayout.LayoutParams p1 =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        p1.setMargins(0, 0, 0, 16);
        etT.setLayoutParams(p1);

        EditText etS = new EditText(this);
        etS.setText(oldSubject);
        etS.setTextColor(Color.parseColor("#C4A8E0"));
        etS.setHint("Subject");
        etS.setHintTextColor(Color.parseColor("#6B4F8A"));
        etS.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#4A2D6B")));
        LinearLayout.LayoutParams p2 =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        p2.setMargins(0, 0, 0, 16);
        etS.setLayoutParams(p2);

        EditText etC = new EditText(this);
        etC.setText(oldContent);
        etC.setTextColor(Color.parseColor("#C4A8E0"));
        etC.setHint("Content");
        etC.setHintTextColor(Color.parseColor("#6B4F8A"));
        etC.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#4A2D6B")));
        etC.setMinLines(3);
        etC.setGravity(Gravity.TOP);

        layout.addView(etT);
        layout.addView(etS);
        layout.addView(etC);

        new AlertDialog.Builder(this)
                .setTitle("Edit Note")
                .setView(layout)
                .setPositiveButton("Save", (d, w) -> {
                    String newTitle   = etT.getText().toString().trim();
                    String newSubject = etS.getText().toString().trim();
                    String newContent = etC.getText().toString().trim();

                    if (newTitle.isEmpty() || newContent.isEmpty()) {
                        Toast.makeText(this,
                                "Title and Content required",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    db.updateStudent(id, newTitle,
                            newSubject, newContent);
                    llStudentList.removeAllViews();
                    loadNotes();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}