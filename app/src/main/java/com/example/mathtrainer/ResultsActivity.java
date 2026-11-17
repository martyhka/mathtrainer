

package com.example.mathtrainer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ResultsActivity extends AppCompatActivity {

    private static final int WHITE = Color.WHITE;
    private static final int SOFT_WHITE = Color.rgb(231, 250, 245);
    private static final int LEMON = Color.rgb(232, 255, 143);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (StudentData.isRegistered(this)) {
            showResults();
        } else {
            showRegistration();
        }
    }

    private int dp(int value) {
        return (int) (value *
                getResources().getDisplayMetrics().density + 0.5f);
    }

    // Наш сказочный шрифт для заголовков.
    private void applyMagicFont(TextView text) {
        Typeface font = ResourcesCompat.getFont(
                this, R.font.ruslandisplay
        );
        if (font != null) {
            text.setTypeface(font);
        }
    }

    private TextView makeText(String content, int size, int color) {
        TextView text = new TextView(this);
        text.setText(content);
        text.setTextSize(size);
        text.setTextColor(color);
        return text;
    }

    private GradientDrawable cardBackground() {
        GradientDrawable background = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        Color.argb(230, 9, 55, 52),
                        Color.argb(215, 18, 91, 76)
                }
        );

        background.setCornerRadius(dp(20));
        background.setStroke(
                dp(1),
                Color.argb(200, 143, 238, 211)
        );

        return background;
    }

    private LinearLayout makeCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(17), dp(18), dp(17));
        card.setBackground(cardBackground());
        card.setElevation(dp(6));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 0, 0, dp(17));
        card.setLayoutParams(params);
        return card;
    }

    private void showRegistration() {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(24), dp(35), dp(24), dp(24));

        layout.setBackgroundResource(R.drawable.results_clean_bg);

        TextView title = makeText(
                "Добро пожаловать в Матемагию!",
                27,
                WHITE
        );
        applyMagicFont(title);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        EditText name = new EditText(this);
        name.setHint("Введите имя");
        name.setTextColor(WHITE);
        name.setHintTextColor(SOFT_WHITE);
        layout.addView(name);

        EditText grade = new EditText(this);
        grade.setHint("Введите класс, например 7");
        grade.setTextColor(WHITE);
        grade.setHintTextColor(SOFT_WHITE);
        layout.addView(grade);

        Button button = new Button(this);
        button.setText("Начать обучение");
        layout.addView(button);

        button.setOnClickListener(v -> {
            String studentName =
                    name.getText().toString().trim();

            String studentGrade =
                    grade.getText().toString().trim();

            if (studentName.isEmpty()) {
                name.setError("Введите имя");
                return;
            }

            if (studentGrade.isEmpty()) {
                grade.setError("Введите класс");
                return;
            }

            StudentData.register(
                    this, studentName, studentGrade
            );

            startActivity(new Intent(
                    this, LevelSelectorActivity.class
            ));
            finish();
        });

        setContentView(layout);
    }

    private void showResults() {

        // Фон занимает весь экран.
        FrameLayout root = new FrameLayout(this);

        root.setBackgroundResource(R.drawable.results_clean_bg);


        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(
                dp(18), dp(32), dp(18), dp(36)
        );

        scroll.addView(layout);

        root.addView(scroll, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        // Заголовок страницы.
        TextView title = makeText(
                "Мои результаты", 32, WHITE
        );
        applyMagicFont(title);
        title.setGravity(Gravity.CENTER);
        title.setShadowLayer(
                dp(4), 0, dp(3), Color.rgb(4, 56, 51)
        );
        title.setPadding(0, 0, 0, dp(20));
        layout.addView(title);

        // Информация об ученике.
        LinearLayout studentCard = makeCard();

        TextView student = makeText(
                "Ученик: " + StudentData.getName(this)
                        + "\nКласс: " + StudentData.getGrade(this),
                19,
                WHITE
        );
        studentCard.addView(student);
        layout.addView(studentCard);

        // Прежняя статистика обучения.
        LinearLayout statisticsCard = makeCard();

        TextView statisticsTitle = makeText(
                "Статистика обучения", 23, LEMON
        );
        applyMagicFont(statisticsTitle);
        statisticsTitle.setPadding(
                0, 0, 0, dp(12)
        );
        statisticsCard.addView(statisticsTitle);

        TextView statistics = makeText(
                "Правильных ответов: "
                        + StudentData.getCorrect(this)
                        + "\nОшибок: "
                        + StudentData.getWrong(this)
                        + "\nПройдено уровней: "
                        + StudentData.getCompleted(this)
                        + "\nТочность ответов: "
                        + StudentData.getAccuracy(this) + "%",
                17,
                SOFT_WHITE
        );
        statistics.setLineSpacing(dp(6), 1f);
        statisticsCard.addView(statistics);
        layout.addView(statisticsCard);

        // История отдельных тренировок.
        TextView historyTitle = makeText(
                "История тренировок", 26, WHITE
        );
        applyMagicFont(historyTitle);
        historyTitle.setPadding(
                dp(4), dp(7), 0, dp(17)
        );
        layout.addView(historyTitle);

        JSONArray history = TestHistory.getAll(this);

        if (history.length() == 0) {

            LinearLayout emptyCard = makeCard();
            TextView empty = makeText(
                    "Пока нет сохранённых тестов.\n"
                            + "Пройди тренировку, и здесь "
                            + "появится первая оценка!",
                    17,
                    WHITE
            );
            emptyCard.addView(empty);
            layout.addView(emptyCard);

        } else {

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat(
                            "dd.MM.yyyy  HH:mm",
                            new Locale("ru", "RU")
                    );

            for (int i = 0; i < history.length(); i++) {

                JSONObject test = history.optJSONObject(i);
                if (test == null) {
                    continue;
                }

                long timestamp = test.optLong("date", 0);

                String date = timestamp > 0
                        ? dateFormat.format(new Date(timestamp))
                        : "Дата не указана";

                String topic = test.optString(
                        "topic", "Тренировка"
                );

                int points = test.optInt("points", 0);
                int grade = test.optInt("grade", 0);
                int correct = test.optInt("correct", 0);
                int mistakes = test.optInt("mistakes", 0);

                long duration = test.optLong("duration", 0);
                long totalSeconds = duration / 1000;
                long minutes = totalSeconds / 60;
                long seconds = totalSeconds % 60;

                LinearLayout testCard = makeCard();

                TextView topicView = makeText(
                        topic, 22, LEMON
                );
                applyMagicFont(topicView);
                testCard.addView(topicView);

                TextView dateView = makeText(
                        date, 15, SOFT_WHITE
                );
                dateView.setPadding(
                        0, dp(5), 0, dp(12)
                );
                testCard.addView(dateView);

                TextView scoreView = makeText(
                        points + " / 100 баллов"
                                + "     •     Оценка: " + grade,
                        20,
                        WHITE
                );
                scoreView.setTypeface(Typeface.DEFAULT_BOLD);
                testCard.addView(scoreView);

                TextView detailsView = makeText(
                        "Решено: " + correct
                                + "    Ошибок: " + mistakes
                                + "\nВремя: " + minutes
                                + " мин " + seconds + " сек"
                                + "\nКачество: "
                                + test.optInt("quality", 0)
                                + " баллов"
                                + "\nСкорость: "
                                + test.optInt("speed", 0)
                                + " баллов"
                                + "\nВыполнение: "
                                + test.optInt("completion", 0)
                                + " баллов",
                        16,
                        SOFT_WHITE
                );

                detailsView.setPadding(
                        0, dp(12), 0, 0
                );
                detailsView.setLineSpacing(dp(4), 1f);
                testCard.addView(detailsView);

                layout.addView(testCard);
            }
        }

        Button back = new Button(this);
        back.setText("В меню уровней");
        back.setOnClickListener(v -> {
            startActivity(new Intent(
                    this, LevelSelectorActivity.class
            ));
            finish();
        });

        layout.addView(back);
        setContentView(root);
    }
}

