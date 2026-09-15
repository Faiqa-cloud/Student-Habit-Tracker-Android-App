package com.example.studenthabittrackerapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
        String name  = prefs.getString("username", "Student");
        String email = prefs.getString("email", "—");


        // Top name + email
        ((TextView) findViewById(R.id.tvProfileName)).setText(name);
        ((TextView) findViewById(R.id.tvProfileEmail)).setText(email);

        // Cards mein
        ((TextView) findViewById(R.id.tvProfileNameCard)).setText(name);
        ((TextView) findViewById(R.id.tvProfileEmailCard)).setText(email);

        // Back
        findViewById(R.id.btnHome).setOnClickListener(v -> finish());

        // Logout — data clear karo aur login pe jao
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Intent i = new Intent(this, Login.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }
}