package com.example.mathtrainer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnSubmitAnswerListener {

    private static final String PREFS = "math_trainer_prefs";
    private static final String KEY_TOTAL_SCORE = "total_score";

    private TextView timerView, totalScoreView, coeffView;
    private Button stopButton;
    private RecyclerView recyclerView;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private long startTimestamp = 0L;
    private long elapsedMillis = 0L;
    private boolean running = false;
    private boolean started = false;

    private int totalScore;

    private final List<Task> tasks = new ArrayList<>();
    private TaskAdapter adapter;

    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            if (!running) return;
            long now = System.currentTimeMillis();
            elapsedMillis += (now - startTimestamp);
            startTimestamp = now;
            updateTimerUI();
            refreshFooter();
            handler.postDelayed(this, 100L);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerView = findViewById(R.id.timerView);
        totalScoreView = findViewById(R.id.totalScoreView);
        coeffView = findViewById(R.id.coeffView);
        stopButton = findViewById(R.id.stopButton);
        recyclerView = findViewById(R.id.recyclerView);

        totalScore = loadTotalScore(this);
        totalScoreView.setText(String.valueOf(totalScore));
        updateTimerUI();
        updateCoeffUI();

        adapter = new TaskAdapter(tasks, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // первая задача
        tasks.add(Task.random());
        adapter.notifyDataSetChanged();
        scrollToBottom();

        stopButton.setOnClickListener(v -> stopTimer());
        refreshFooter();
    }

    // колбэк проверки ответа
    @Override
    public void onSubmitAnswer(int adapterPosition, @NonNull String input) {
        startTimerIfNeeded();

        // убедимся, что это не футер
        if (adapterPosition >= tasks.size()) return;

        Task t = tasks.get(adapterPosition);
        boolean correct = false;
        if (!TextUtils.isEmpty(input)) {
            try {
                correct = Integer.parseInt(input.trim()) == t.getAnswer();
            } catch (NumberFormatException ignored) {}
        }

        totalScore += correct ? 1 : -2;
        totalScoreView.setText(String.valueOf(totalScore));
        saveTotalScore(this, totalScore);
        updateCoeffUI();

        // новая задача вниз
        tasks.add(Task.random());
        adapter.notifyItemInserted(tasks.size() - 1);
        scrollToBottom();
        refreshFooter();
    }

    // таймер
    private void startTimerIfNeeded() {
        if (!started) {
            started = true;
            startTimer();
        }
    }

    private void startTimer() {
        if (running) return;
        running = true;
        startTimestamp = System.currentTimeMillis();
        handler.post(ticker);
    }

    private void stopTimer() {
        if (!running) return;
        running = false;
        long now = System.currentTimeMillis();
        elapsedMillis += (now - startTimestamp);
        updateTimerUI();
        refreshFooter();
    }

    private void updateTimerUI() {
        long totalSeconds = elapsedMillis / 1000L;
        long m = totalSeconds / 60L;
        long s = totalSeconds % 60L;
        timerView.setText(String.format(Locale.getDefault(), "%02d:%02d", m, s));
        updateCoeffUI();
    }

    // коэффициент
    private void updateCoeffUI() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat("#0.000", symbols);
        coeffView.setText(getString(
                R.string.knowledge_coeff,
                df.format(knowledgeCoefficient(totalScore, elapsedMillis))
        ));
    }

    private double knowledgeCoefficient(int score, long elapsedMs) {
        double minutes = Math.max(1.0, elapsedMs / 60000.0); // минимум 1 минута
        return score / minutes;
    }

    // хранение очков
    private int loadTotalScore(Context ctx) {
        SharedPreferences p = ctx.getSharedPreferences(PREFS, MODE_PRIVATE);
        return p.getInt(KEY_TOTAL_SCORE, 0);
    }

    private void saveTotalScore(Context ctx, int score) {
        SharedPreferences p = ctx.getSharedPreferences(PREFS, MODE_PRIVATE);
        p.edit().putInt(KEY_TOTAL_SCORE, score).apply();
    }

    // обновить футер
    private void refreshFooter() {
        String result = "Баллы: " + totalScore + " — Время: " + formatTimer(elapsedMillis);
        DecimalFormatSymbols s = new DecimalFormatSymbols();
        s.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat("#0.000", s);
        String coeff = "Коэффициент: " + df.format(knowledgeCoefficient(totalScore, elapsedMillis));
        if (adapter != null) adapter.updateFooter(result, coeff);
    }

    private String formatTimer(long ms) {
        long sec = ms / 1000L;
        long m = sec / 60L;
        long s = sec % 60L;
        return String.format(Locale.getDefault(), "%02d:%02d", m, s);
    }

    private void scrollToBottom() {
        recyclerView.post(() -> recyclerView.smoothScrollToPosition(Math.max(0, adapter.getItemCount() - 1)));
    }
}
