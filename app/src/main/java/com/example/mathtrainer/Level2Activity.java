package com.example.mathtrainer;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;


/**
 * Второй уровень. ЛОГИКУ MainActivity НЕ ТРОГАЕМ.
 * Здесь — та же механика (таймеры/баллы), но ответы — десятичные (1 знак).
 */
public class Level2Activity extends AppCompatActivity {

    // --- Порог и цели уровня ---
    private static final long   FREE_TIME_LIMIT = 60;   // сек на обдумывание
    private static final double TARGET_SCORE    = 30.0; // целевые баллы

    // --- Шапка ---
    private TextView levelTimerView;
    private TextView tvTotal;
    private TextView penaltyTimerView;
    private TextView penaltyScoreView;
    private Button   btnStop;

    // --- Активная задача ---
    private TextView   tvQuestion;
    private EditText   etAnswer;
    private DecimalTask currentTask;
    private String      lastQuestionText = null;

    // --- Время/штраф ---
    private final Handler handler = new Handler(Looper.getMainLooper());
    private long    levelStartMs     = 0L;
    private long    questionStartMs  = 0L;
    private long    penaltySeconds   = 0L;
    private boolean tickerScheduled  = false;
    private boolean wasTickerRunningBeforePause = false;

    // --- Рекорды уровня ---
    private static final String PREFS        = "leaderboard_level2";
    private static final String KEY_BEST_MS  = "best_ms";
    private static final String KEY_WORST_MS = "worst_ms";

    // --- Счёт ---
    private double totalScore = 0.0;

    private String mode;
    private String nextCls; // имя класса следующего (для марафона)


    // --- Тикер ---
    private final Runnable tick = new Runnable() {
        @Override public void run() {
            tickerScheduled = false;

            long levelSec = (System.currentTimeMillis() - levelStartMs) / 1000L;
            levelTimerView.setText(formatTime(levelSec));

            long sinceStartSec = (System.currentTimeMillis() - questionStartMs) / 1000L;
            penaltySeconds = (sinceStartSec <= FREE_TIME_LIMIT) ? 0L : Math.min(60, sinceStartSec - FREE_TIME_LIMIT);

            penaltyTimerView.setText(formatTime(penaltySeconds));
            updatePenaltyLabel();

            scheduleTick();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Используем тот же layout, что и первый уровень (id должны совпадать)
        setContentView(R.layout.activity_main);

        mode   = getIntent().getStringExtra(Flow.EXTRA_MODE);
        nextCls = getIntent().getStringExtra(Flow.EXTRA_NEXT);
        if (mode == null) mode = Flow.MODE_TRAINING;


        levelTimerView   = findViewById(R.id.levelTimerView);
        tvTotal          = findViewById(R.id.totalScoreView);
        penaltyTimerView = findViewById(R.id.penaltyTimerView);
        penaltyScoreView = findViewById(R.id.penaltyScoreView);
        btnStop          = findViewById(R.id.stopButton);
        tvQuestion       = findViewById(R.id.textQuestion);
        etAnswer         = findViewById(R.id.editAnswer);

        makeBlackBold(levelTimerView);
        makeBlackBold(tvTotal);
        makeBlackBold(penaltyTimerView);
        makeBlackBold(penaltyScoreView);
        makeBlackBold(tvQuestion);

        levelStartMs = System.currentTimeMillis();
        levelTimerView.setText(formatTime(0));
        penaltyTimerView.setText(formatTime(0));
        tvTotal.setText(formatPoints(0.0));
        penaltyScoreView.setText("0,000");

        btnStop.setOnClickListener(v -> pauseTicker());

        // Ответ — десятичные. Разрешаем точку и запятую. IME Done.
        etAnswer.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etAnswer = findViewById(R.id.editAnswer);
        // Разрешаем ввод дробей (точка/запятая)
        etAnswer.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
                        | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
                        | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
        );


        etAnswer.setOnEditorActionListener((v, actionId, event) -> {
            boolean enter = event != null
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_UP;
            if (actionId == EditorInfo.IME_ACTION_DONE || enter) {
                checkAnswer();
                return true;
            }
            return false;
        });

        showNextTask();
        scheduleTick();
    }

    // ---------- Игровая логика ----------

    private void showNextTask() {
        questionStartMs = System.currentTimeMillis();
        penaltySeconds = 0L;
        penaltyTimerView.setText(formatTime(0));
        updatePenaltyLabel();
        etAnswer.setText("");

        for (int i = 0; i < 100; i++) {
            DecimalTask t = DecimalTask.random();
            if (!t.getText().equals(lastQuestionText)) {
                currentTask = t;
                lastQuestionText = t.getText();
                tvQuestion.setText(lastQuestionText);
                return;
            }
        }
        currentTask = DecimalTask.random();
        lastQuestionText = currentTask.getText();
        tvQuestion.setText(lastQuestionText);
    }

    private void checkAnswer() {
        String raw = etAnswer.getText().toString().trim();
        if (raw.isEmpty()) { etAnswer.setError("Введите ответ"); return; }

        // Принимаем и точку, и запятую. Приводим к формату с 1 знаком.
        BigDecimal user;
        try {
            raw = raw.replace(',', '.');
            user = new BigDecimal(raw).setScale(1, RoundingMode.HALF_UP);
            if (user.compareTo(BigDecimal.ZERO) <= 0) {
                etAnswer.setError("Ответ должен быть положительным");
                return;
            }
        } catch (Exception e) {
            etAnswer.setError("Введите число (одна цифра после запятой)");
            return;
        }

        if (currentTask != null) {
            BigDecimal right = currentTask.getAnswer(); // уже scale(1)
            if (user.compareTo(right) == 0) {
                long sec = (System.currentTimeMillis() - questionStartMs) / 1000L;

                double penaltyBallShown = Math.max(0.0, 1.0 - (Math.max(0, sec - FREE_TIME_LIMIT) / 60.0));
                double gained = (sec <= FREE_TIME_LIMIT) ? 1.0 : penaltyBallShown;

                totalScore += Math.max(0.0, gained);
                tvTotal.setText(formatPoints(totalScore));
                updatePenaltyLabel();

                if (totalScore >= TARGET_SCORE) {
                    showWinScreen();
                    return;
                }
                showNextTask();
            } else {
                etAnswer.setError("Неверно");
            }
        }
    }

    // ---------- Вспомогательные ----------

    private void updatePenaltyLabel() {
        double penaltyBall = Math.max(0.0, 1.0 - (penaltySeconds / 60.0));
        penaltyScoreView.setText(String.format(Locale.getDefault(), "%.3f", penaltyBall));
    }

    private void scheduleTick() {
        if (!tickerScheduled) {
            handler.postDelayed(tick, 1000);
            tickerScheduled = true;
        }
    }

    private void pauseTicker() {
        handler.removeCallbacks(tick);
        tickerScheduled = false;
    }

    private static void makeBlackBold(TextView tv) {
        tv.setTextColor(Color.BLACK);
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
    }

    private String formatTime(long seconds) {
        long m = seconds / 60;
        long s = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", m, s);
    }

    private String formatPoints(double p) {
        return String.format(Locale.getDefault(), "%.3f", p);
    }

    private String formatMsAsMMSS(long ms) {
        long sec = Math.max(0L, ms / 1000L);
        long m = sec / 60;
        long s = sec % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", m, s);
    }

    // ---------- Победа ----------

    private void showWinScreen() {
        pauseTicker();
        hideKeyboard();

        long levelDurationMs = System.currentTimeMillis() - levelStartMs;
        updateRecords(levelDurationMs);
        long[] rec = getRecords();
        long bestMs  = rec[0];
        long worstMs = rec[1];

        String title = "Поздравляем! Уровень 2 пройден 🎉";
        String timeText  = "Время уровня: " + formatMsAsMMSS(levelDurationMs);
        String scoreText = "Баллы: " + String.format(java.util.Locale.getDefault(), "%.3f", totalScore);
        String bestTxt   = (bestMs  == Long.MAX_VALUE) ? "—" : formatMsAsMMSS(bestMs);
        String worstTxt  = (worstMs == 0L)            ? "—" : formatMsAsMMSS(worstMs);
        String recText   = "Рекорды: мин " + bestTxt + " / макс " + worstTxt;

        Runnable onNext;
        String btnText;
        if (Flow.MODE_MARATHON.equals(mode)) {
            // в марафоне отсюда идём на уровень 3
            btnText = "Следующий уровень";
            onNext = () -> {
                android.content.Intent i = new android.content.Intent(this, Level3Activity.class);
                i.putExtra(Flow.EXTRA_MODE, Flow.MODE_MARATHON);
                i.putExtra(Flow.EXTRA_NEXT, (String) null); // у 3-го нет следующего
                startActivity(i);
                finish();
            };
        } else {
            btnText = "В меню уровней";
            onNext = () -> startActivity(new android.content.Intent(this, LevelSelectorActivity.class));
        }

        FinishScreen.show(this, title, timeText, scoreText, recText, btnText, onNext);
    }

    private void resetLevel() {
        setContentView(R.layout.activity_main);

        levelTimerView   = findViewById(R.id.levelTimerView);
        tvTotal          = findViewById(R.id.totalScoreView);
        penaltyTimerView = findViewById(R.id.penaltyTimerView);
        penaltyScoreView = findViewById(R.id.penaltyScoreView);
        btnStop          = findViewById(R.id.stopButton);
        tvQuestion       = findViewById(R.id.textQuestion);
        etAnswer         = findViewById(R.id.editAnswer);

        makeBlackBold(levelTimerView);
        makeBlackBold(tvTotal);
        makeBlackBold(penaltyTimerView);
        makeBlackBold(penaltyScoreView);
        makeBlackBold(tvQuestion);

        btnStop.setOnClickListener(v -> pauseTicker());
        etAnswer.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etAnswer.setOnEditorActionListener((v, actionId, event) -> {
            boolean enter = event != null
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_UP;
            if (actionId == EditorInfo.IME_ACTION_DONE || enter) {
                checkAnswer();
                return true;
            }
            return false;
        });

        totalScore = 0.0;
        tvTotal.setText(formatPoints(totalScore));
        levelStartMs = System.currentTimeMillis();
        levelTimerView.setText(formatTime(0));
        penaltyTimerView.setText(formatTime(0));
        penaltyScoreView.setText("0,000");

        showNextTask();
        scheduleTick();
    }

    // ---------- Рекорды ----------

    private void updateRecords(long durationMs) {
        android.content.SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        long best  = sp.getLong(KEY_BEST_MS, Long.MAX_VALUE);
        long worst = sp.getLong(KEY_WORST_MS, 0L);

        if (durationMs < best)  best  = durationMs;
        if (durationMs > worst) worst = durationMs;

        sp.edit().putLong(KEY_BEST_MS, best)
                .putLong(KEY_WORST_MS, worst)
                .apply();
    }

    private long[] getRecords() {
        android.content.SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        long best  = sp.getLong(KEY_BEST_MS, Long.MAX_VALUE);
        long worst = sp.getLong(KEY_WORST_MS, 0L);
        return new long[]{ best, worst };
    }

    // ---------- Жизненный цикл ----------

    @Override
    protected void onPause() {
        super.onPause();
        wasTickerRunningBeforePause = tickerScheduled;
        pauseTicker();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wasTickerRunningBeforePause) scheduleTick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pauseTicker();
    }

    private void hideKeyboard() {
        try {
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null && etAnswer != null) {
                imm.hideSoftInputFromWindow(etAnswer.getWindowToken(), 0);
            }
        } catch (Exception ignored) {}
    }
}
