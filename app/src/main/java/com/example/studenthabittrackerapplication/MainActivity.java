package com.example.studenthabittrackerapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
        String name = prefs.getString("username", "Student");

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting = hour < 12 ? "Good Morning," :
                hour < 17 ? "Good Afternoon," : "Good Evening,";

        ((TextView) findViewById(R.id.tvGreeting)).setText(greeting);
        ((TextView) findViewById(R.id.tvName)).setText(name);

        ImageView tvAvatar = findViewById(R.id.tvAvatar);


        // Avatar click → Profile screen
        tvAvatar.setOnClickListener(v ->
                startActivity(new Intent(this, profile.class)));

        findViewById(R.id.cardStudents).setOnClickListener(v ->
                startActivity(new Intent(this, StudentRecord.class)));
        findViewById(R.id.cardAttendance).setOnClickListener(v ->
                startActivity(new Intent(this, Attandance.class)));
        findViewById(R.id.cardSchedule).setOnClickListener(v ->
                startActivity(new Intent(this, StudyScedule.class)));
        findViewById(R.id.cardHabits).setOnClickListener(v ->
                startActivity(new Intent(this, HabitTrack.class)));
    }
}