
package com.example.mathtrainer;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.util.Locale;

public class ElasticForceTrainingActivity extends AppCompatActivity {

    // Три задания каждого уровня.
    private static final int[] LEVELS = {
            1, 1, 1,
            2, 2, 2,
            3, 3, 3
    };

    // Вес заданий при оценивании.
    private static final int[] WEIGHTS = {
            1, 1, 1,
            2, 2, 2,
            3, 3, 3
    };

    private static final long TARGET_TIME_MS = 15 * 60 * 1000L;

    private final ElasticForceGenerator.Task[] tasks =
            new ElasticForceGenerator.Task[LEVELS.length];


    private final String[] givenAnswers =
            new String[LEVELS.length];


    private final boolean[] correct =
            new boolean[LEVELS.length];

    private int taskNumber = 0;
    private int correctAnswers = 0;
    private int mistakes = 0;

    private long startTime;
    private boolean resultShown = false;
    private boolean submitting = false;

    private ScrollView scrollView;
    private LinearLayout taskArea;
    private TextView progressView;
    private EditText answerInput;

    private final int DARK = Color.rgb(13, 73, 65);

    private int dp(int value) {
        return (int) (
                value * getResources()
                        .getDisplayMetrics().density + 0.5f
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startTime = SystemClock.elapsedRealtime();

        // Создаём девять заданий одного варианта.
        for (int i = 0; i < LEVELS.length; i++) {
            tasks[i] = ElasticForceGenerator.generate(LEVELS[i]);
        }

        createTrainingScreen();
    }

    private TextView makeText(String value, int size) {
        TextView text = new TextView(this);
        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(DARK);
        return text;
    }

    private LinearLayout makeCard(boolean active) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(14), dp(16), dp(14));

        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                active
                        ? new int[]{
                        Color.argb(245, 245, 255, 234),
                        Color.argb(235, 197, 250, 231)
                }
                        : new int[]{
                        Color.argb(235, 251, 255, 251),
                        Color.argb(220, 225, 248, 244)
                }
        );

        bg.setCornerRadius(dp(18));
        bg.setStroke(
                dp(active ? 2 : 1),
                active
                        ? Color.rgb(28, 150, 126)
                        : Color.rgb(130, 192, 180)
        );

        card.setBackground(bg);
        card.setElevation(dp(4));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 0, 0, dp(12));
        card.setLayoutParams(params);

        return card;
    }

    private void addCard(LinearLayout area, LinearLayout card) {

        if (card.getParent() == null) {
            area.addView(card);
        }
    }

    private void createTrainingScreen() {

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundResource(R.drawable.equations_bg);

        scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(18), dp(25), dp(18), dp(30));

        scrollView.addView(page);
        root.addView(scrollView);

        TextView title = makeText("Сила упругости", 27);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(
                ResourcesCompat.getFont(
                        this, R.font.ruslandisplay
                )
        );

        page.addView(title);

        TextView subtitle = makeText("Физика • 7 класс", 17);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, dp(5), 0, dp(12));
        page.addView(subtitle);

        progressView = makeText("", 16);
        progressView.setPadding(0, 0, 0, dp(13));
        page.addView(progressView);

        taskArea = new LinearLayout(this);
        taskArea.setOrientation(LinearLayout.VERTICAL);
        page.addView(taskArea);

        setContentView(root);
        updateTasks();
    }

    private void updateTasks() {

        if (taskNumber >= LEVELS.length) {
            showResult();
            return;
        }

        taskArea.removeAllViews();
        submitting = false;

        int level = LEVELS[taskNumber];

        progressView.setText(
                "Задание " + (taskNumber + 1)
                        + " из " + LEVELS.length
                        + "\nУровень " + level + " из 3"
        );

        // Показываем четыре карточки:
        // выполненные, текущую и следующие.
        int first = Math.max(0, taskNumber - 2);
        int last = Math.min(LEVELS.length, first + 4);
        first = Math.max(0, last - 4);

        LinearLayout activeCard = null;

        for (int i = first; i < last; i++) {

            boolean active = (i == taskNumber);
            LinearLayout card = makeCard(active);

            TextView number = makeText(
                    "Задание " + (i + 1)
                            + " • Уровень " + LEVELS[i],
                    15
            );
            card.addView(number);

            TextView question = makeText(
                    tasks[i].question,
                    active ? 21 : 18
            );

            question.setPadding(0, dp(8), 0, dp(8));
            card.addView(question);

            Button referenceButton = new Button(this);
            referenceButton.setText("📖 Справочные материалы");
            referenceButton.setAllCaps(false);

            referenceButton.setOnClickListener(v ->
                    PhysicsReferenceDialog.show(this)
            );

            card.addView(referenceButton);


            if (i < taskNumber) {

                TextView saved = makeText(
                        "Ваш ответ: " + givenAnswers[i]
                                + " " + tasks[i].unit
                                + "\nОтвет записан",
                        16
                );

                card.addView(saved);

            } else if (active) {

                activeCard = card;

                answerInput = new EditText(this);
                answerInput.setHint(
                        "Введите ответ (" + tasks[i].unit + ")"
                );
                answerInput.setTextSize(20);
                answerInput.setTextColor(DARK);
                answerInput.setSingleLine(true);



                answerInput.setInputType(
                        InputType.TYPE_CLASS_NUMBER
                                | InputType.TYPE_NUMBER_FLAG_DECIMAL
                                | InputType.TYPE_NUMBER_FLAG_SIGNED
                );



                answerInput.setImeOptions(
                        EditorInfo.IME_ACTION_DONE
                );

                answerInput.setOnEditorActionListener(
                        (v, actionId, event) -> {

                            if (actionId == EditorInfo.IME_ACTION_DONE
                                    || (event != null
                                    && event.getKeyCode()
                                    == KeyEvent.KEYCODE_ENTER
                                    && event.getAction()
                                    == KeyEvent.ACTION_DOWN)) {

                                saveAnswer();
                                return true;
                            }

                            return false;
                        }
                );

                card.addView(answerInput);

                Button saveButton = new Button(this);
                saveButton.setText("Сохранить ответ");
                saveButton.setAllCaps(false);

                saveButton.setOnClickListener(
                        v -> saveAnswer()
                );

                card.addView(saveButton);

            } else {

                TextView note = makeText(
                        "Следующее задание", 14
                );

                note.setPadding(0, dp(4), 0, 0);
                card.addView(note);
            }

            addCard(taskArea, card);
        }

        final LinearLayout currentCard = activeCard;

        if (currentCard != null) {
            scrollView.post(() -> {
                int y = taskArea.getTop()
                        + currentCard.getTop() - dp(100);

                scrollView.smoothScrollTo(
                        0, Math.max(0, y)
                );
            });
        }
    }


    private void saveAnswer() {

        if (taskNumber >= LEVELS.length || submitting) {
            return;
        }

        String answer = answerInput.getText()
                .toString().trim();

        if (answer.isEmpty()) {
            answerInput.setError("Введите ответ");
            return;
        }

        try {
            String normalized = answer
                    .replace(',', '.')
                    .replace(" ", "");

            new java.math.BigDecimal(normalized);

        } catch (NumberFormatException e) {
            answerInput.setError("Введите число");
            return;
        }

        submitting = true;

        int index = taskNumber;

        givenAnswers[index] = answer;

        // Единая проверка по физике:
        // округление до десятых.
        correct[index] = PhysicsAnswerUtils.isCorrect(
                answer,
                tasks[index].answer
        );

        if (correct[index]) {
            correctAnswers++;
        } else {
            mistakes++;
        }

        InputMethodManager keyboard =
                (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE
                );

        if (keyboard != null) {
            keyboard.hideSoftInputFromWindow(
                    answerInput.getWindowToken(), 0
            );
        }

        taskNumber++;
        updateTasks();
    }


    private int schoolGrade(int points) {
        if (points >= 90) return 5;
        if (points >= 75) return 4;
        if (points >= 50) return 3;
        return 2;
    }

    private String formatTime(long millis) {
        long seconds = millis / 1000;

        return String.format(
                Locale.getDefault(),
                "%d мин %02d сек",
                seconds / 60,
                seconds % 60
        );
    }

    private void showResult() {

        if (resultShown) return;
        resultShown = true;

        long elapsedMillis =
                SystemClock.elapsedRealtime() - startTime;

        int totalWeight = 0;
        int earnedWeight = 0;

        for (int i = 0; i < LEVELS.length; i++) {
            totalWeight += WEIGHTS[i];

            if (correct[i]) {
                earnedWeight += WEIGHTS[i];
            }
        }

        // Домашняя тренировка:
        // качество 75, скорость 15, выполнение 10.
        int quality = (int) Math.round(
                75.0 * earnedWeight / totalWeight
        );

        double timeRatio =
                (double) elapsedMillis / TARGET_TIME_MS;

        double speedRatio = Math.max(
                0, Math.min(1, 2 - timeRatio)
        );

        int speed = (int) Math.round(
                15 * speedRatio
        );

        int completion = 10;
        int total = Math.min(
                100, quality + speed + completion
        );

        int grade = schoolGrade(total);

        // Сохранение в общей истории тренировок.
        TestHistory.save(
                this,
                "Сила упругости",
                correctAnswers,
                mistakes,
                elapsedMillis,
                quality,
                speed,
                completion,
                total,
                grade
        );

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundResource(R.drawable.equations_bg);

        ScrollView scroll = new ScrollView(this);

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(20), dp(28), dp(20), dp(30));

        scroll.addView(page);
        root.addView(scroll);

        TextView title = makeText(
                "Тренировка завершена!", 25
        );
        title.setGravity(Gravity.CENTER);
        title.setTypeface(
                ResourcesCompat.getFont(
                        this, R.font.ruslandisplay
                )
        );
        page.addView(title);

        LinearLayout summary = makeCard(true);

        TextView info = makeText(
                "Тема: Сила упругости"
                        + "\nПравильных ответов: "
                        + correctAnswers + " из " + LEVELS.length
                        + "\nОшибок: " + mistakes
                        + "\nВремя: " + formatTime(elapsedMillis),
                18
        );
        summary.addView(info);

        TextView result = makeText(
                "\nБаллы: " + total + " / 100"
                        + "\nОценка: " + grade,
                24
        );
        result.setTypeface(Typeface.DEFAULT_BOLD);
        summary.addView(result);

        TextView details = makeText(
                "\nКачество: " + quality + " / 75"
                        + "\nСкорость: " + speed + " / 15"
                        + "\nВыполнение: " + completion + " / 10",
                16
        );
        summary.addView(details);
        page.addView(summary);

        LinearLayout review = makeCard(false);

        TextView reviewTitle = makeText(
                "Проверка ответов", 22
        );
        reviewTitle.setTypeface(Typeface.DEFAULT_BOLD);
        review.addView(reviewTitle);

        for (int i = 0; i < LEVELS.length; i++) {

            TextView item = makeText(
                    "\nЗадание " + (i + 1)
                            + " • Уровень " + LEVELS[i]
                            + "\n" + tasks[i].question
                            + "\nВаш ответ: " + givenAnswers[i]
                            + " " + tasks[i].unit
                            + "\nПравильный ответ: "
                            + tasks[i].answer
                            + " " + tasks[i].unit
                            + "\n" + (correct[i] ? "Верно" : "Ошибка"),
                    16
            );

            review.addView(item);
        }

        page.addView(review);

        Button next = new Button(this);
        next.setText("Следующий вариант");
        next.setOnClickListener(v -> recreate());
        page.addView(next);

        Button back = new Button(this);
        back.setText("В меню");
        back.setOnClickListener(v -> finish());
        page.addView(back);

        setContentView(root);
    }
}