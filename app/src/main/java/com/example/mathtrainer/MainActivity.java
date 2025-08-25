package com.example.mathtrainer;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // --- Порог и цели уровня ---
    private static final long FREE_TIME_LIMIT = 60;     // 60 сек на обдумывание
    private static final double TARGET_SCORE = 30.0;    // целевое количество баллов

    // --- Шапка (2 строки) ---
    private TextView levelTimerView;    // время уровня (MM:SS), верхняя строка
    private TextView tvTotal;           // общий балл (0.000), верхняя строка
    private TextView penaltyTimerView;  // штрафной таймер (MM:SS), нижняя строка
    private TextView penaltyScoreView;  // штрафной балл (0.000), нижняя строка
    private Button   btnStop;

    // --- Активная задача ---
    private TextView tvQuestion;
    private EditText etAnswer;
    private Task currentTask;
    private String lastQuestionText = null;

    // --- Время/штраф ---
    private final Handler handler = new Handler(Looper.getMainLooper());
    private long questionStartMs = 0L;    // старт текущего вопроса
    private long penaltySeconds  = 0L;    // секунд сверх 60 (0..60) для текущего вопроса
    private boolean tickerScheduled = false;
    private boolean wasTickerRunningBeforePause = false;

    // --- Рекорды уровня ---
    private static final String PREFS        = "leaderboard";
    private static final String KEY_BEST_MS  = "best_ms";   // минимальное время (лучший)
    private static final String KEY_WORST_MS = "worst_ms";  // максимальное время (худший)
    private long levelStartMs = 0L; // старт уровня (для общего времени уровня)

    // --- Счёт ---
    private double totalScore = 0.0;

    // Тикер: обновляет таймер уровня + штрафной таймер/балл
    private final Runnable tick = new Runnable() {
        @Override public void run() {
            tickerScheduled = false;

            // таймер уровня
            long levelSec = (System.currentTimeMillis() - levelStartMs) / 1000L;
            levelTimerView.setText(formatTime(levelSec));

            // штрафной таймер (сверх 60 сек на текущий вопрос)
            long sinceStartSec = (System.currentTimeMillis() - questionStartMs) / 1000L;
            if (sinceStartSec <= FREE_TIME_LIMIT) {
                penaltySeconds = 0L;
            } else {
                penaltySeconds = Math.min(60, sinceStartSec - FREE_TIME_LIMIT);
            }
            penaltyTimerView.setText(formatTime(penaltySeconds));
            updatePenaltyLabel();

            scheduleTick();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // findViewById для новой шапки
        levelTimerView   = findViewById(R.id.levelTimerView);
        tvTotal          = findViewById(R.id.totalScoreView);
        penaltyTimerView = findViewById(R.id.penaltyTimerView);
        penaltyScoreView = findViewById(R.id.penaltyScoreView);
        btnStop          = findViewById(R.id.stopButton);
        tvQuestion       = findViewById(R.id.textQuestion);
        etAnswer         = findViewById(R.id.editAnswer);

        // стилизуем числа (чёрные, жирные)
        makeBlackBold(levelTimerView);
        makeBlackBold(tvTotal);
        makeBlackBold(penaltyTimerView);
        makeBlackBold(penaltyScoreView);
        makeBlackBold(tvQuestion);

        // старт уровня и начальные значения
        levelStartMs = System.currentTimeMillis();
        levelTimerView.setText(formatTime(0));
        penaltyTimerView.setText(formatTime(0));
        tvTotal.setText(formatPoints(0.0));
        penaltyScoreView.setText("0,000");

        // Стоп — пауза тикера
        btnStop.setOnClickListener(v -> pauseTicker());

        // Обработка ответа
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

        showNextTask();
        scheduleTick(); // фоновый тикер
    }

    // ---------- Игровая логика ----------

    private void showNextTask() {
        questionStartMs = System.currentTimeMillis();
        penaltySeconds = 0L;
        penaltyTimerView.setText(formatTime(0));
        updatePenaltyLabel();
        etAnswer.setText("");

        // генерируем новый текст, не равный предыдущему
        for (int i = 0; i < 100; i++) {
            Task t = Task.random();
            if (!t.getText().equals(lastQuestionText)) {
                currentTask = t;
                lastQuestionText = t.getText();
                tvQuestion.setText(lastQuestionText);
                return;
            }
        }
        currentTask = Task.random();
        lastQuestionText = currentTask.getText();
        tvQuestion.setText(lastQuestionText);
    }

    private void checkAnswer() {
        String txt = etAnswer.getText().toString().trim();
        if (txt.isEmpty()) { etAnswer.setError("Введите ответ"); return; }

        int user;
        try { user = Integer.parseInt(txt); }
        catch (NumberFormatException e) { etAnswer.setError("Только число"); return; }

        if (currentTask != null && user == currentTask.getAnswer()) {
            long sec = (System.currentTimeMillis() - questionStartMs) / 1000L;

            // штрафной балл убывает от 1.000 до 0.000 в течение штрафной минуты
            double penaltyBallShown = Math.max(0.0, 1.0 - (Math.max(0, sec - FREE_TIME_LIMIT) / 60.0));
            // начисляемый балл:
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

    // ---------- Вспомогательные ----------

    private void updatePenaltyLabel() {
        // убывает: 1 - (штрафные_сек / 60)
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

    /** Сохранить рекорды: минимальное и максимальное время прохождения уровня (в мс) */
    private void updateRecords(long durationMs) {
        android.content.SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        long best  = sp.getLong(KEY_BEST_MS, Long.MAX_VALUE);
        long worst = sp.getLong(KEY_WORST_MS, 0L);

        if (durationMs < best)  best  = durationMs;
        if (durationMs > worst) worst = durationMs;

        sp.edit().putLong(KEY_BEST_MS, best).putLong(KEY_WORST_MS, worst).apply();
    }

    /** Прочитать рекорды (в мс) */
    private long[] getRecords() {
        android.content.SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        long best  = sp.getLong(KEY_BEST_MS, Long.MAX_VALUE);
        long worst = sp.getLong(KEY_WORST_MS, 0L);
        return new long[]{ best, worst };
    }

    /** Форматировать миллисекунды как MM:SS */
    private String formatMsAsMMSS(long ms) {
        long sec = Math.max(0L, ms / 1000L);
        long m = sec / 60;
        long s = sec % 60;
        return String.format(java.util.Locale.getDefault(), "%02d:%02d", m, s);
    }

    // ---------- Победа ----------

    private void showWinScreen() {
        pauseTicker();
        hideKeyboard();

        // время уровня + рекорды
        long levelDurationMs = System.currentTimeMillis() - levelStartMs;
        updateRecords(levelDurationMs);
        long[] rec = getRecords();
        long bestMs  = rec[0];
        long worstMs = rec[1];

        // Вертикальный контейнер: видео сверху (вес 1), инфо + кнопка снизу
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Видео
        VideoView vv = new VideoView(this);
        LinearLayout.LayoutParams videoLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        vv.setLayoutParams(videoLp);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.mal);
        vv.setVideoURI(uri);

        // Инфо-панель снизу
        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setPadding(32, 24, 32, 24);
        info.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView congrats = new TextView(this);
        congrats.setText("Поздравляем! Уровень пройден 🎉");
        congrats.setTextColor(Color.BLACK);
        congrats.setTypeface(Typeface.DEFAULT_BOLD);
        congrats.setTextSize(20f);

        TextView timeTv = new TextView(this);
        timeTv.setText("Время уровня: " + formatMsAsMMSS(levelDurationMs));
        timeTv.setTextColor(Color.BLACK);
        timeTv.setTextSize(16f);

        TextView scoreTv = new TextView(this);
        scoreTv.setText("Баллы: " + String.format(Locale.getDefault(), "%.3f", totalScore));
        scoreTv.setTextColor(Color.BLACK);
        scoreTv.setTextSize(16f);

        String bestTxt  = (bestMs  == Long.MAX_VALUE) ? "—" : formatMsAsMMSS(bestMs);
        String worstTxt = (worstMs == 0L)            ? "—" : formatMsAsMMSS(worstMs);
        TextView lbTv = new TextView(this);
        lbTv.setText("Рекорды: мин " + bestTxt + " / макс " + worstTxt);
        lbTv.setTextColor(Color.BLACK);
        lbTv.setTextSize(16f);

        // Кнопка "Следующий уровень" (перезапуск уровня пока)
        Button nextBtn = new Button(this);
        nextBtn.setText("Следующий уровень");
        nextBtn.setOnClickListener(v -> resetLevel());

        info.addView(congrats);
        info.addView(timeTv);
        info.addView(scoreTv);
        info.addView(lbTv);
        info.addView(nextBtn);

        root.addView(vv);
        root.addView(info);

        setContentView(root);

        vv.setOnPreparedListener(mp -> vv.start());
        vv.setOnCompletionListener(mp -> {
            // остаёмся на экране — можно нажать «Следующий уровень»
        });
    }

    private void resetLevel() {
        // вернуть основной layout и View'хи
        setContentView(R.layout.activity_main);

        // найти новые id
        levelTimerView   = findViewById(R.id.levelTimerView);
        tvTotal          = findViewById(R.id.totalScoreView);
        penaltyTimerView = findViewById(R.id.penaltyTimerView);
        penaltyScoreView = findViewById(R.id.penaltyScoreView);
        btnStop          = findViewById(R.id.stopButton);
        tvQuestion       = findViewById(R.id.textQuestion);
        etAnswer         = findViewById(R.id.editAnswer);

        // стили
        makeBlackBold(levelTimerView);
        makeBlackBold(tvTotal);
        makeBlackBold(penaltyTimerView);
        makeBlackBold(penaltyScoreView);
        makeBlackBold(tvQuestion);

        // листенеры
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

        // Сброс состояния уровня
        totalScore = 0.0;
        tvTotal.setText(formatPoints(totalScore));
        levelStartMs = System.currentTimeMillis();
        levelTimerView.setText(formatTime(0));
        penaltyTimerView.setText(formatTime(0));
        penaltyScoreView.setText("0,000");

        showNextTask();
        scheduleTick();
    }

    // ---------- Жизненный цикл: «заморозка» без Стоп ----------

    @Override
    protected void onPause() {
        super.onPause();
        wasTickerRunningBeforePause = tickerScheduled;
        pauseTicker();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wasTickerRunningBeforePause) {
            scheduleTick();
        }
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
