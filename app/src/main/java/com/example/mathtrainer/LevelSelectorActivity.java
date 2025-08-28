package com.example.mathtrainer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LevelSelectorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_selector);

        Button btnLevel1 = findViewById(R.id.btnLevel1);
        Button btnLevel2 = findViewById(R.id.btnLevel2);

        btnLevel1.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)));

        btnLevel2.setOnClickListener(v ->
                startActivity(new Intent(this, Level2Activity.class)));
    }
}
