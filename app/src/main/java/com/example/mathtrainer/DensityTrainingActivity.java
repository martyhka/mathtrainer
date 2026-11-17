
package com.example.mathtrainer;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DensityTrainingActivity
        extends AppCompatActivity {

    private LinearLayout page;
    private EditText answerInput;

    private final Random random = new Random();

    private final List<Question> questions =
            new ArrayList<>();

    private final List<String> answers =
            new ArrayList<>();

    private int current = 0;
    private int correct = 0;

    private int selectedLevel = 1;


    // Плотность в кг/м³.
    private static final String[] SUBSTANCES = {
            "алюминия", "железа", "меди",
            "стали", "стекла", "воды",
            "молока", "растительного масла"
    };

    private static final double[] DENSITIES = {
            2700, 7870, 8960, 7850,
            2500, 1000, 1030, 920
    };

    private static class Question {
        String text;
        String unit;
        double result;

        Question(String text, String unit,
                 double result) {
            this.text = text;
            this.unit = unit;
            this.result = result;
        }
    }


    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        showLevelMenu();
    }


    private int dp(int value) {
        return (int) (value *
                getResources().getDisplayMetrics()
                        .density + 0.5f);
    }

    private String format(double number) {
        DecimalFormatSymbols symbols =
                DecimalFormatSymbols.getInstance(
                        new Locale("ru", "RU"));

        DecimalFormat df = new DecimalFormat(
                "0.##", symbols);

        return df.format(number);
    }

    private void createPage() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundResource(
                R.drawable.equations_bg);

        page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(
                dp(24), dp(24), dp(24), dp(30));

        scroll.addView(page);
        setContentView(scroll);
    }

    private void addText(String value, int size) {
        TextView text = new TextView(this);
        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(Color.rgb(12, 85, 66));
        text.setPadding(0, dp(10), 0, dp(14));
        page.addView(text);
    }

    private void addButton(String label,
                           Runnable action) {

        AppCompatButton button =
                new AppCompatButton(this);

        button.setText(label);
        button.setAllCaps(false);
        button.setTextSize(18);
        button.setTextColor(Color.WHITE);
        button.setBackgroundResource(
                R.drawable.button_green_gradient);
        button.setBackgroundTintList(null);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(58));

        params.setMargins(0, 0, 0, dp(12));
        page.addView(button, params);

        button.setOnClickListener(
                v -> action.run());
    }

    // СОЗДАНИЕ НОВЫХ ВАРИАНТОВ


    private void showLevelMenu() {

        createPage();

        addText("Физика · 7 класс", 20);
        addText("Масса и плотность", 28);
        addText("Выбери уровень сложности", 22);


        addButton("1 уровень — Основные формулы", () -> {
            selectedLevel = 1;
            generateQuestions();
            showQuestion();
        });



        addButton("2 уровень — Расчётные задачи", () -> {
            showComingSoon(2);
        });

        addButton("3 уровень — Составные задачи", () -> {
            showComingSoon(3);
        });

        addButton("4 уровень — Сложные задачи", () -> {
            showComingSoon(4);
        });

        addButton("Назад к теме", this::finish);
    }

    private void showComingSoon(int level) {

        new AlertDialog.Builder(this)
                .setTitle(level + " уровень")
                .setMessage(
                        "Задания этого уровня пока " +
                                "готовятся. Скоро они появятся!")
                .setPositiveButton("Понятно", null)
                .show();
    }


    private void generateQuestions() {

        questions.clear();
        answers.clear();
        current = 0;
        correct = 0;

        // 0 — плотность, 1 — масса, 2 — объём
        int[] types = {0, 1, 0, 2, 2, 1, 1, 2};

        // 0 — твёрдое тело
        // 1 — жидкость
        // 2 — газ
        int[] states = {0, 1, 2, 0, 1, 2, 0, 2};

        int[] volumes = {50, 100, 200, 250, 400, 500};
        int[] gasVolumes = {10, 20, 30, 40};

        String[] gasNames = {
                "воздух", "кислород", "азот", "гелий"
        };

        String[] gasGenitive = {
                "воздуха", "кислорода", "азота", "гелия"
        };

        double[] gasDensities = {
                1.20, 1.33, 1.17, 0.166
        };

        for (int i = 0; i < types.length; i++) {

            int type = types[i];
            int state = states[i];

            String text;
            String unit;
            double result;

            if (state == 2) {

                // ГАЗООБРАЗНЫЕ ВЕЩЕСТВА

                int index = random.nextInt(gasNames.length);
                int volume = gasVolumes[
                        random.nextInt(gasVolumes.length)];

                String gas = gasNames[index];
                String gasOf = gasGenitive[index];

                double density = gasDensities[index];
                double mass = density * volume;

                String conditions =
                        "Температура газа 20 °C, "
                                + "давление 101 кПа.\n\n";

                if (type == 0) {

                    text = "В герметичном сосуде "
                            + "объёмом " + volume
                            + " м³ находится " + gas + ".\n\n"
                            + "Масса газа равна "
                            + format(mass) + " кг.\n\n"
                            + conditions
                            + "Определите плотность "
                            + gasOf + ".";

                    unit = "кг/м³";
                    result = density;

                } else if (type == 1) {

                    text = "Герметичный сосуд "
                            + "объёмом " + volume
                            + " м³ заполнен газом.\n\n"
                            + "В сосуде находится "
                            + gas + ".\n\n"
                            + conditions
                            + "Определите массу "
                            + gasOf + ".";

                    unit = "кг";
                    result = mass;

                } else {

                    text = "В герметичном сосуде "
                            + "находится " + gas + ".\n\n"
                            + "Масса газа составляет "
                            + format(mass) + " кг.\n\n"
                            + conditions
                            + "Определите объём "
                            + "сосуда, полностью "
                            + "заполненного газом.";

                    unit = "м³";
                    result = volume;
                }

            } else {

                // ТВЁРДЫЕ ТЕЛА И ЖИДКОСТИ

                int index;

                if (state == 0) {
                    index = random.nextInt(5);
                } else {
                    index = 5 + random.nextInt(3);
                }

                int volume = volumes[
                        random.nextInt(volumes.length)];

                double density = DENSITIES[index] / 1000.0;
                double mass = density * volume;

                String substance = SUBSTANCES[index];

                if (state == 0) {

                    String object =
                            "Однородный сплошной образец "
                                    + "из " + substance;

                    if (type == 0) {

                        text = object
                                + " имеет массу "
                                + format(mass)
                                + " г и объём "
                                + volume + " см³.\n\n"
                                + "Определите плотность "
                                + "материала.";

                    } else if (type == 1) {

                        text = object
                                + " имеет объём "
                                + volume + " см³.\n\n"
                                + "Определите массу "
                                + "образца.";

                    } else {

                        text = object
                                + " имеет массу "
                                + format(mass) + " г.\n\n"
                                + "Определите объём "
                                + "образца.";
                    }

                } else {

                    // ЖИДКОСТИ

                    if (type == 0) {

                        text = "В мерный сосуд налили "
                                + volume + " см³ "
                                + substance + ".\n\n"
                                + "Масса этой жидкости "
                                + "равна "
                                + format(mass) + " г.\n\n"
                                + "Определите плотность "
                                + "жидкости.";

                    } else if (type == 1) {

                        text = "В сосуд налили "
                                + volume + " см³ "
                                + substance + ".\n\n"
                                + "Определите массу "
                                + "жидкости.";

                    } else {

                        text = "В сосуд налили "
                                + substance + ".\n\n"
                                + "Масса налитой "
                                + "жидкости равна "
                                + format(mass) + " г.\n\n"
                                + "Определите её объём.";
                    }
                }

                if (type == 0) {
                    unit = "г/см³";
                    result = density;
                } else if (type == 1) {
                    unit = "г";
                    result = mass;
                } else {
                    unit = "см³";
                    result = volume;
                }
            }

            questions.add(new Question(text, unit, result));
        }

        Collections.shuffle(questions, random);
    }

    // ЭКРАН ЗАДАЧИ

    private void showQuestion() {

        createPage();

        Question question = questions.get(current);

        addText("Физика · 7 класс", 19);
        addText("Масса и плотность", 27);

        addText("Первый уровень\n"
                + "Задача " + (current + 1)
                + " из " + questions.size(), 20);

        addText(question.text, 21);

        addText("Введите числовой ответ "
                + "в " + question.unit + ":", 18);

        answerInput = new EditText(this);

        answerInput.setTextColor(Color.BLACK);
        answerInput.setHintTextColor(Color.GRAY);

        answerInput.setGravity(android.view.Gravity.CENTER);

        answerInput.setTextAlignment(
                android.view.View.TEXT_ALIGNMENT_CENTER);

// Овальное поле с зелёной рамкой
        android.graphics.drawable.GradientDrawable inputBg =
                new android.graphics.drawable.GradientDrawable();

        inputBg.setColor(Color.WHITE);
        inputBg.setCornerRadius(dp(32));
        inputBg.setStroke(dp(2), Color.rgb(24, 112, 86));

        answerInput.setBackground(inputBg);
        answerInput.setPadding(dp(20), 0, dp(20), 0);


        answerInput.setSingleLine(true);
        answerInput.setTextSize(21);

        answerInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
                        | InputType.TYPE_NUMBER_FLAG_SIGNED);

        answerInput.setHint("Ваш ответ");

        LinearLayout.LayoutParams inputParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(65));

        inputParams.setMargins(
                0, 0, 0, dp(18));

        page.addView(answerInput, inputParams);

        addButton("Справочник плотностей",
                this::showReference);

        addButton("Следующая задача",
                this::saveAnswer);

        addButton("Выйти из тренировки",
                this::confirmExit);
    }

    // СОХРАНЕНИЕ И ПРОВЕРКА ОТВЕТА

    private void saveAnswer() {

        String entered = answerInput.getText()
                .toString()
                .trim()
                .replace(",", ".");

        if (entered.isEmpty()) {
            answerInput.setError(
                    "Введите ответ");
            return;
        }

        double value;

        try {
            value = Double.parseDouble(entered);
        } catch (NumberFormatException e) {
            answerInput.setError(
                    "Введите число");
            return;
        }

        if (!Double.isFinite(value)
                || value <= 0) {

            answerInput.setError(
                    "Введите положительное число");
            return;
        }

        Question question = questions.get(current);

        answers.add(entered);

        // Допуск на округление ответа.
        double tolerance = Math.max(
                0.02, Math.abs(question.result) * 0.005);

        if (Math.abs(value - question.result)
                <= tolerance) {
            correct++;
        }

        current++;

        if (current < questions.size()) {
            showQuestion();
        } else {
            showResults();
        }
    }

    // СПРАВОЧНАЯ ТАБЛИЦА

    private void showReference() {

        StringBuilder table = new StringBuilder();

        for (int i = 0; i < SUBSTANCES.length; i++) {

            table.append(SUBSTANCES[i])
                    .append(" — ")
                    .append(format(DENSITIES[i]))
                    .append(" кг/м³")
                    .append("\n");
        }

        table.append("\n")
                .append("1 г/см³ = 1000 кг/м³\n")
                .append("1 см³ = 10⁻⁶ м³\n")
                .append("1 г = 10⁻³ кг");

        new AlertDialog.Builder(this)
                .setTitle("Справочник плотностей")
                .setMessage(table.toString())
                .setPositiveButton(
                        "Закрыть", null)
                .show();
    }

    // ИТОГОВЫЙ РЕЗУЛЬТАТ

    private void showResults() {

        createPage();

        int total = questions.size();
        int percent = correct * 100 / total;

        addText("Тренировка завершена!", 27);

        addText("Правильных ответов: "
                        + correct + " из " + total
                        + "\nРезультат: " + percent + "%",
                23);

        addText("Ваши ответы и правильные значения:",
                19);

        for (int i = 0; i < total; i++) {

            Question q = questions.get(i);

            addText("Задача " + (i + 1)
                    + "\nВаш ответ: "
                    + answers.get(i) + " " + q.unit
                    + "\nПравильный ответ: "
                    + format(q.result)
                    + " " + q.unit, 16);
        }

        addButton("Новая тренировка", () -> {
            generateQuestions();
            showQuestion();
        });

        addButton("Выбрать другой уровень",
                this::showLevelMenu);
        addButton("Вернуться к теме",
                this::finish);
    }

    // ВЫХОД

    private void confirmExit() {

        new AlertDialog.Builder(this)
                .setTitle("Завершить тренировку?")
                .setMessage(
                        "Текущие ответы не сохранятся.")
                .setNegativeButton("Остаться", null)
                .setPositiveButton(
                        "Выйти",
                        (dialog, which) -> finish())
                .show();
    }
}

