
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.util.Locale;

public class EquationTrainingActivity extends AppCompatActivity {

    private final int[] levels = {1, 1, 2, 3, 4};
    private final int[] weights = {1, 1, 2, 3, 4};

    private final LinearEquationGenerator.Equation[] equations =
            new LinearEquationGenerator.Equation[5];

    private final int[] givenAnswers = new int[5];
    private final boolean[] correct = new boolean[5];

    private int taskNumber = 0;
    private int correctAnswers = 0;
    private int mistakes = 0;

    private long startTime;

    private static final long TARGET_TIME_MS = 600000L;

    private final int DARK = Color.rgb(13, 73, 65);

    private ScrollView scrollView;
    private LinearLayout taskArea;
    private TextView progressView;
    private EditText answerInput;

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

        // Создаём пять уравнений одного варианта.
        for (int i = 0; i < levels.length; i++) {
            equations[i] =
                    LinearEquationGenerator.generate(levels[i]);
        }

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundResource(R.drawable.equations_bg);

        scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(20), dp(25), dp(20), dp(35));

        scrollView.addView(page);

        root.addView(
                scrollView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );

        TextView title = createText(
                "Линейные уравнения", 25
        );
        title.setGravity(Gravity.CENTER);

        title.setTypeface(
                ResourcesCompat.getFont(
                        this, R.font.ruslandisplay
                )
        );

        page.addView(title);

        progressView = createText("", 17);
        progressView.setPadding(
                0, dp(15), 0, dp(15)
        );
        page.addView(progressView);

        taskArea = new LinearLayout(this);
        taskArea.setOrientation(LinearLayout.VERTICAL);
        page.addView(taskArea);

        setContentView(root);
        updateTaskList();
    }

    private TextView createText(String value, int size) {
        TextView text = new TextView(this);
        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(DARK);
        return text;
    }

    private LinearLayout createCard(boolean active) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(
                dp(17), dp(14), dp(17), dp(14)
        );

        GradientDrawable background = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                active
                        ? new int[]{
                        Color.argb(240, 245, 255, 233),
                        Color.argb(230, 194, 249, 232)
                }
                        : new int[]{
                        Color.argb(225, 250, 255, 251),
                        Color.argb(210, 223, 249, 240)
                }
        );

        background.setCornerRadius(dp(19));
        background.setStroke(
                dp(active ? 2 : 1),
                active
                        ? Color.rgb(27, 154, 132)
                        : Color.rgb(117, 195, 176)
        );

        card.setBackground(background);
        card.setElevation(dp(4));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 0, 0, dp(13));
        card.setLayoutParams(params);

        return card;
    }

    private void updateTaskList() {

        if (taskNumber >= levels.length) {
            showResult();
            return;
        }

        taskArea.removeAllViews();

        progressView.setText(
                "Задание " + (taskNumber + 1)
                        + " из " + levels.length
                        + "  •  Ответы проверяются в конце"
        );

        // Показываем до четырёх карточек.
        // Сначала сохраняем несколько предыдущих.
        int first = Math.max(0, taskNumber - 2);

        // Если карточек мало, добавляем будущие.
        int last = Math.min(levels.length, first + 4);

        // Держим четыре карточки, пока это возможно.
        first = Math.max(0, last - 4);

        LinearLayout activeCard = null;

        for (int i = first; i < last; i++) {

            boolean active = i == taskNumber;
            LinearLayout card = createCard(active);

            TextView equation = createText(
                    (i + 1) + ". " + equations[i].question,
                    active ? 29 : 23
            );

            if (active) {
                equation.setTypeface(Typeface.DEFAULT_BOLD);
            }

            card.addView(equation);

            if (i < taskNumber) {

                // Никаких подсказок о правильности.
                TextView saved = createText(
                        "Ваш ответ: x = " + givenAnswers[i]
                                + "\nОтвет записан",
                        17
                );

                saved.setPadding(0, dp(7), 0, 0);
                card.addView(saved);

            } else if (active) {

                activeCard = card;

                answerInput = new EditText(this);
                answerInput.setHint("Введите x");
                answerInput.setTextSize(20);
                answerInput.setTextColor(DARK);
                answerInput.setSingleLine(true);

                answerInput.setInputType(
                        InputType.TYPE_CLASS_NUMBER |
                                InputType.TYPE_NUMBER_FLAG_SIGNED
                );

                answerInput.setImeOptions(
                        EditorInfo.IME_ACTION_DONE
                );

                answerInput.setOnEditorActionListener(
                        (v, actionId, event) -> {

                            if (actionId ==
                                    EditorInfo.IME_ACTION_DONE ||
                                    (event != null &&
                                            event.getKeyCode() ==
                                                    KeyEvent.KEYCODE_ENTER &&
                                            event.getAction() ==
                                                    KeyEvent.ACTION_DOWN)) {

                                saveAnswer();
                                return true;
                            }

                            return false;
                        }
                );

                card.addView(answerInput);

                Button saveButton = new Button(this);
                saveButton.setText("Сохранить ответ");
                saveButton.setOnClickListener(
                        v -> saveAnswer()
                );

                card.addView(saveButton);

            } else {

                TextView next = createText(
                        "Следующее задание", 14
                );
                next.setPadding(0, dp(5), 0, 0);
                card.addView(next);
            }

            taskArea.addView(card);
        }

        // Перемещаем экран к активной карточке.
        final LinearLayout currentCard = activeCard;

        if (currentCard != null) {
            scrollView.post(() -> {
                int y = taskArea.getTop()
                        + currentCard.getTop()
                        - dp(100);

                scrollView.smoothScrollTo(
                        0, Math.max(0, y)
                );
            });
        }
    }

    private void saveAnswer() {

        if (taskNumber >= levels.length) {
            return;
        }

        String text = answerInput.getText()
                .toString().trim();

        if (text.isEmpty()) {
            answerInput.setError("Введите ответ");
            return;
        }

        int answer;

        try {
            answer = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            answerInput.setError("Введите целое число");
            return;
        }

        // Фиксируем ответ ученика.
        givenAnswers[taskNumber] = answer;

        // Проверяем скрыто.
        boolean isCorrect =
                LinearEquationGenerator.checkAnswer(
                        equations[taskNumber], answer
                );

        correct[taskNumber] = isCorrect;

        if (isCorrect) {
            correctAnswers++;
        } else {
            mistakes++;
        }

        // Переход независимо от правильности.
        taskNumber++;
        updateTaskList();
    }

    private int getSchoolGrade(int points) {
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

        long elapsedMillis =
                SystemClock.elapsedRealtime() - startTime;

        // Расчёт качества по сложности заданий.
        double earnedWeight = 0;
        double totalWeight = 0;

        for (int i = 0; i < weights.length; i++) {

            totalWeight += weights[i];

            if (correct[i]) {
                earnedWeight += weights[i];
            }
        }

        int quality = (int) Math.round(
                75 * earnedWeight / totalWeight
        );

        // Баллы за скорость.
        double timeRatio =
                (double) elapsedMillis / TARGET_TIME_MS;

        double speedRatio = Math.max(
                0, Math.min(1, 2 - timeRatio)
        );

        int speed = (int) Math.round(
                15 * speedRatio
        );

        // Все пять заданий выполнены.
        int completion = 10;

        int total = Math.min(
                100, quality + speed + completion
        );

        int grade = getSchoolGrade(total);

        // Сохраняем тест в истории с отдельной датой.
        TestHistory.save(
                this,
                "Линейные уравнения",
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
        scroll.setFillViewport(true);

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(
                dp(22), dp(30), dp(22), dp(30)
        );

        scroll.addView(page);

        root.addView(
                scroll,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );

        TextView title = createText(
                "Тренировка завершена!", 27
        );

        title.setTypeface(
                ResourcesCompat.getFont(
                        this, R.font.ruslandisplay
                )
        );

        page.addView(title);

        LinearLayout card = createCard(true);

        TextView info = createText(
                "Ученик: " + StudentData.getName(this)
                        + "\nКласс: " + StudentData.getGrade(this)
                        + "\nТема: Линейные уравнения"
                        + "\n\nПравильных ответов: "
                        + correctAnswers + " из 5"
                        + "\nОшибок: " + mistakes,
                18
        );

        card.addView(info);

        TextView score = createText(
                "\n" + total + " из 100 баллов"
                        + "\nШкольная оценка: " + grade,
                24
        );

        score.setTypeface(Typeface.DEFAULT_BOLD);
        card.addView(score);

        TextView details = createText(
                "\nКачество решений: " + quality + " / 75"
                        + "\nСкорость: " + speed + " / 15"
                        + "\nВыполнение: " + completion + " / 10"
                        + "\nВремя: " + formatTime(elapsedMillis),
                17
        );

        details.setLineSpacing(dp(5), 1f);
        card.addView(details);

        page.addView(card);

        // Теперь можно увидеть подробную проверку.
        LinearLayout answersCard = createCard(false);

        TextView answersTitle = createText(
                "Проверка ответов", 21
        );
        answersTitle.setTypeface(Typeface.DEFAULT_BOLD);
        answersCard.addView(answersTitle);

        for (int i = 0; i < levels.length; i++) {

            String resultText = correct[i]
                    ? "Верно"
                    : "Ошибка";

            TextView line = createText(
                    "\n" + (i + 1) + ". "
                            + equations[i].question
                            + "\nВаш ответ: x = "
                            + givenAnswers[i]
                            + "\nПравильный ответ: x = "
                            + equations[i].answer
                            + "\n" + resultText,
                    16
            );

            answersCard.addView(line);
        }

        page.addView(answersCard);

        Button next = new Button(this);
        next.setText("Следующий вариант");
        next.setOnClickListener(v -> recreate());
        page.addView(next);

        Button back = new Button(this);
        back.setText("В меню уровней");
        back.setOnClickListener(v -> finish());
        page.addView(back);

        setContentView(root);
    }
}
