
package com.example.mathtrainer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LevelSelectorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!StudentData.isRegistered(this)) {
            startActivity(new Intent(this, ResultsActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_level_selector);

        Button btnLevel1 = findViewById(R.id.btnLevel1);
        Button btnLevel2 = findViewById(R.id.btnLevel2);
        Button btnLevel3 = findViewById(R.id.btnLevel3); // ← внутри onCreate
        Button btnMarathon = findViewById(R.id.btnMarathon);

        Button btnResults = findViewById(R.id.btnResults);

        btnResults.setOnClickListener(v ->
                startActivity(new Intent(
                        this, ResultsActivity.class)));

        Button btnEquations = findViewById(R.id.btnEquations);

        btnEquations.setOnClickListener(v ->
                startActivity(new Intent(
                        this, EquationTrainingActivity.class)));



        btnLevel1.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)));

        btnLevel2.setOnClickListener(v ->
                startActivity(new Intent(this, Level2Activity.class)));

        btnLevel3.setOnClickListener(v ->
                startActivity(new Intent(this, Level3Activity.class)));

        btnMarathon.setOnClickListener(v -> {
                    // стартуем уровень 1 в режиме марафона и передаём, кто следующий
                    Intent i = new Intent(this, MainActivity.class);
            i.putExtra(Flow.EXTRA_MODE, Flow.MODE_MARATHON);
            i.putExtra(Flow.EXTRA_NEXT, Level2Activity.class.getName());
            startActivity(i);
        });

    }


}
