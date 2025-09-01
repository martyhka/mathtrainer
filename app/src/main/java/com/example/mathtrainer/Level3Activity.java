
package com.example.mathtrainer;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.net.Uri;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class Level3Activity extends AppCompatActivity {

    // ---- параметры уровня ----
    private static final long   FREE_TIME_LIMIT = 60;   // сек до штрафа (для пенальти-лейбла)
    private static final int    CENTER_DIAMETER_DP = 88;
    private static final int    OPTION_DIAMETER_DP = 64;

    private static final int MAX1 = 300;   // 1 делитель
    private static final int MAX2 = 450;   // 2 делителя
    private static final int MAX3 = 600;   // 3 делителя

    private static final Random RNG = new Random();

    // ---- шапка ----
    private TextView levelTimerView, tvTotal, penaltyTimerView, penaltyScoreView;
    private Button   btnStop;

    // ---- поле ----
    private ConstraintLayout board;
    private TextView centerCircle, opt1, opt2, opt3, opt4, opt5;

    // ---- время/тикер ----
    private final Handler handler = new Handler(Looper.getMainLooper());
    private long levelStartMs = 0L;
    private long questionStartMs = 0L;
    private long penaltySeconds = 0L;
    private boolean tickerScheduled = false;
    private boolean wasTickerRunningBeforePause = false;

    // ---- счёт/прогресс ----
    private double totalScore = 0.0;
    private int tasksDone = 0; // 0..30 (10+10+10)

    // --- Рекорды уровня 3 ---
    private static final String PREFS        = "leaderboard_level3";
    private static final String KEY_BEST_MS  = "best_ms";
    private static final String KEY_WORST_MS = "worst_ms";

    // текущая задача
    private Task current;

    private static class Task {
        int[] divisors;              // делители (1..3)
        ArrayList<Integer> options;  // 5 чисел
        int oddIndex;                // индекс лишнего
    }

    // тикер времени
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
        setContentView(R.layout.activity_level3);

        // --- findViewById ---
        levelTimerView   = findViewById(R.id.levelTimerView);
        tvTotal          = findViewById(R.id.totalScoreView);
        penaltyTimerView = findViewById(R.id.penaltyTimerView);
        penaltyScoreView = findViewById(R.id.penaltyScoreView);
        btnStop          = findViewById(R.id.stopButton);

        board        = findViewById(R.id.board);
        centerCircle = findViewById(R.id.centerCircle);
        opt1         = findViewById(R.id.opt1);
        opt2         = findViewById(R.id.opt2);
        opt3         = findViewById(R.id.opt3);
        opt4         = findViewById(R.id.opt4);
        opt5         = findViewById(R.id.opt5);

        // стиль шапки
        makeBlackBold(levelTimerView);
        makeBlackBold(tvTotal);
        makeBlackBold(penaltyTimerView);
        makeBlackBold(penaltyScoreView);

        tvTotal.setText(formatPoints(0.0));
        levelTimerView.setText("00:00");
        penaltyTimerView.setText("00:00");
        penaltyScoreView.setText("1,000");

        btnStop.setOnClickListener(v -> pauseTicker());

        // клики по вариантам
        View.OnClickListener click = v -> onOptionClicked((TextView) v);
        opt1.setOnClickListener(click);
        opt2.setOnClickListener(click);
        opt3.setOnClickListener(click);
        opt4.setOnClickListener(click);
        opt5.setOnClickListener(click);

        // 🔔 Подсказка — показать один раз
        android.content.SharedPreferences sp = getSharedPreferences("tips", MODE_PRIVATE);
        if (!sp.getBoolean("level3_hint_shown", false)) {
            android.widget.Toast
                    .makeText(this, "Найди число, которое НЕ делится на все показанные в центре",
                            android.widget.Toast.LENGTH_LONG)
                    .show();
            sp.edit().putBoolean("level3_hint_shown", true).apply();
        }

        // старт
        levelStartMs = System.currentTimeMillis();
        showNextTask();
        scheduleTick();
    }


    // -------- генерация и показ задачи --------
    private void showNextTask() {
        if (tasksDone >= 30) {
            showWinScreen();
            return;
        }

        int k = (tasksDone < 10) ? 1 : (tasksDone < 20 ? 2 : 3); // 10/10/10

        // пробуем сгенерировать задачу с гарантией 5 вариантов
        for (int attempts = 0; attempts < 50; attempts++) {
            current = generateTask(k);
            if (current != null && current.options != null && current.options.size() == 5) {
                break;
            }
            current = null;
        }
        if (current == null) { // на всякий случай –fallback
            // безопасный дефолт: делители 2·3 (или 2·3·5 для k==3)
            current = generateTask(Math.min(k, 2));
        }

        centerCircle.setText(centerLabel(current.divisors));
        centerCircle.setTextSize(k == 1 ? 28 : (k == 2 ? 26 : 22));

        TextView[] opts = { opt1, opt2, opt3, opt4, opt5 };
        for (int i = 0; i < opts.length; i++) {
            opts[i].setText(String.valueOf(current.options.get(i)));
            opts[i].setError(null);
            opts[i].setEnabled(true);
            opts[i].setVisibility(View.VISIBLE);
        }

        placeAroundCircle();

        questionStartMs = System.currentTimeMillis();
        penaltySeconds = 0L;
        penaltyTimerView.setText("00:00");
        updatePenaltyLabel();
    }

    private Task generateTask(int k) {
        final int limit = (k == 1) ? MAX1 : (k == 2 ? MAX2 : MAX3);

        for (int attempt = 0; attempt < 40; attempt++) {
            // 1) выбираем k делителей из [2..9]
            List<Integer> pool = new ArrayList<>();
            for (int d = 2; d <= 9; d++) pool.add(d);
            Collections.shuffle(pool, RNG);

            int[] divisors = new int[k];
            for (int i = 0; i < k; i++) divisors[i] = pool.get(i);

            // 2) считаем НОК и проверяем, что у нас точно будет ≥4 корректных числа
            int base = lcmAll(divisors);
            int maxMul = limit / base;     // сколько целых множителей помещается (1..maxMul)
            if (maxMul < 5) {
                // этого мало: от 2 до maxMul нам нужно ≥4 кандидатов => maxMul >= 5
                continue; // берём другую комбинацию делителей
            }

            // 3) собираем «правильные» числа
            Set<Integer> used = new HashSet<>();
            List<Integer> ok = new ArrayList<>();
            List<Integer> cands = new ArrayList<>();
            for (int m = 2; m <= maxMul; m++) {
                cands.add(base * m);
            }
            Collections.shuffle(cands, RNG);
            for (int v : cands) {
                if (used.add(v)) ok.add(v);
                if (ok.size() == 4) break;
            }
            if (ok.size() < 4) continue; // подстраховка (теоретически уже не должно случиться)

            // 4) «лишнее» — число, которое НЕ делится хотя бы на один делитель
            Integer bad = null;
            for (int tries = 0; tries < 200; tries++) {
                int candidate = 2 + RNG.nextInt(Math.max(2, limit - 1));
                if (!dividesAll(candidate, divisors) && used.add(candidate)) {
                    bad = candidate;
                    break;
                }
            }
            if (bad == null) continue; // попробуем другую попытку

            // 5) формируем задачу
            Task t = new Task();
            t.divisors = divisors;
            t.options = new ArrayList<>(5);
            t.options.addAll(ok);
            t.options.add(bad);
            Collections.shuffle(t.options, RNG);
            t.oddIndex = t.options.indexOf(bad);
            return t;
        }

        // не смогли (очень маловероятно) — вернём null, выше есть fallback
        return null;
    }




    // -------- клики --------
    private void onOptionClicked(TextView tv) {
        if (current == null) return;

        int val;
        try { val = Integer.parseInt(tv.getText().toString()); }
        catch (Exception e) { return; }

        boolean isOdd = (current.options.indexOf(val) == current.oddIndex);
        if (isOdd) {
            totalScore += 1.0;
            tvTotal.setText(formatPoints(totalScore));
            tasksDone++;
            showNextTask();
        } else {
            tv.setError("Неверно");
        }
    }

    // -------- раскладка пузырей по кругу --------
    private void placeAroundCircle() {
        final ConstraintSet cs = new ConstraintSet();
        cs.clone(board);

        cs.connect(R.id.centerCircle, ConstraintSet.TOP,    ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        cs.connect(R.id.centerCircle, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        cs.connect(R.id.centerCircle, ConstraintSet.START,  ConstraintSet.PARENT_ID, ConstraintSet.START);
        cs.connect(R.id.centerCircle, ConstraintSet.END,    ConstraintSet.PARENT_ID, ConstraintSet.END);
        cs.constrainWidth (R.id.centerCircle,  dp(CENTER_DIAMETER_DP));
        cs.constrainHeight(R.id.centerCircle,  dp(CENTER_DIAMETER_DP));

        final int[] ids    = { R.id.opt1, R.id.opt2, R.id.opt3, R.id.opt4, R.id.opt5 };
        final float[] degs = { -90f, -18f, 54f, 126f, 198f };

        board.post(() -> {
            int w = board.getWidth();
            int h = board.getHeight();
            int optionR = dp(OPTION_DIAMETER_DP) / 2;
            int padding = dp(12);

            int rad = Math.min(w, h) / 2 - padding - optionR;
            if (rad < dp(60)) rad = dp(60);

            for (int id : ids) {
                cs.constrainWidth (id, dp(OPTION_DIAMETER_DP));
                cs.constrainHeight(id, dp(OPTION_DIAMETER_DP));
            }
            for (int i = 0; i < ids.length; i++) {
                cs.constrainCircle(ids[i], R.id.centerCircle, rad, degs[i]);
            }
            cs.applyTo(board);
        });
    }

    // -------- утилиты --------
    private static void makeBlackBold(TextView tv) {
        tv.setTextColor(Color.BLACK);
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
    }

    private void updatePenaltyLabel() {
        double penaltyBall = Math.max(0.0, 1.0 - (penaltySeconds / 60.0));
        penaltyScoreView.setText(String.format(Locale.getDefault(), "%.3f", penaltyBall));
    }

    private String formatTime(long seconds) {
        long m = seconds / 60;
        long s = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", m, s);
    }

    private String formatPoints(double p) {
        return String.format(Locale.getDefault(), "%.3f", p);
    }

    private int dp(int v) {
        float d = getResources().getDisplayMetrics().density;
        return Math.round(v * d);
    }

    private static boolean dividesAll(int x, int[] ds) {
        for (int d : ds) if (x % d != 0) return false;
        return true;
    }

    private static int gcd(int a, int b) {
        while (b != 0) { int t = a % b; a = b; b = t; }
        return Math.abs(a);
    }

    private static int lcm(int a, int b) {
        return Math.abs(a / gcd(a,b) * b);
    }

    private static int lcmAll(int[] arr) {
        int r = arr[0];
        for (int i = 1; i < arr.length; i++) r = lcm(r, arr[i]);
        return r;
    }

    // -------- победа --------
    private void showWinScreen() {
        pauseTicker();
        // спрятать клавиатуру (на всякий случай)
        try {
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        } catch (Exception ignored) {}

        long levelDurationMs = System.currentTimeMillis() - levelStartMs;
        updateRecords(levelDurationMs);
        long[] rec = getRecords();
        long bestMs  = rec[0];
        long worstMs = rec[1];

        // Корневой контейнер
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Видео
        VideoView vv = new VideoView(this);
        LinearLayout.LayoutParams videoLp =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        vv.setLayoutParams(videoLp);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.mal);
        vv.setVideoURI(uri);

        // Инфо-блок
        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setPadding(32, 24, 32, 24);
        info.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView congrats = new TextView(this);
        congrats.setText("Поздравляем! Уровень 3 пройден 🎉");
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

        Button nextBtn = new Button(this);
        nextBtn.setText("Ещё раз уровень 3");
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
    }

    private void updateRecords(long durationMs) {
        android.content.SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        long best  = sp.getLong(KEY_BEST_MS, Long.MAX_VALUE);
        long worst = sp.getLong(KEY_WORST_MS, 0L);
        if (durationMs < best)  best  = durationMs;
        if (durationMs > worst) worst = durationMs;
        sp.edit().putLong(KEY_BEST_MS, best).putLong(KEY_WORST_MS, worst).apply();
    }

    private long[] getRecords() {
        android.content.SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        long best  = sp.getLong(KEY_BEST_MS, Long.MAX_VALUE);
        long worst = sp.getLong(KEY_WORST_MS, 0L);
        return new long[]{ best, worst };
    }

    private void resetLevel() {
        recreate(); // простой безопасный сброс Activity
    }

    private String formatMsAsMMSS(long ms) {
        long sec = Math.max(0L, ms / 1000L);
        long m = sec / 60;
        long s = sec % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", m, s);
    }

    // -------- lifecycle --------
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

    private void pauseTicker() {
        handler.removeCallbacks(tick);
        tickerScheduled = false;
    }

    private void scheduleTick() {
        if (!tickerScheduled) {
            handler.postDelayed(tick, 1000);
            tickerScheduled = true;
        }
    }


    private static String centerLabel(int[] ds) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ds.length; i++) {
            if (i > 0) sb.append(':');  // "3:6" читается как "3 И 6"
            sb.append(ds[i]);
        }
        return sb.toString();
    }
}
