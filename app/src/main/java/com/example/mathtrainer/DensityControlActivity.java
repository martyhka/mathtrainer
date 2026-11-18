
package com.example.mathtrainer;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DensityControlActivity
        extends AppCompatActivity {

    private LinearLayout page;
    private EditText answerInput;
    private EditText nameInput;
    private EditText classInput;

    private final Random random = new Random();
    private final List<Question> questions =
            new ArrayList<>();

    private final int[] points = {3, 4, 5, 8};

    private int current = 0;
    private int score = 0;
    private boolean profile = false;

    private String studentName = "";
    private String studentClass = "";

    // Значения согласованы со справочником тренажёра.
    // Плотности в кг/м³.
    private static final String[] MATERIALS = {
            "алюминия", "железа", "меди",
            "стали", "стекла"
    };

    private static final double[] DENSITIES = {
            2700, 7870, 8960, 7850, 2500,
            1000, 1030, 920
    };

    private static final String[] LIQUIDS = {
            "воды", "молока", "растительного масла"
    };

    private static class Question {
        String text;
        String unit;
        double answer;

        Question(String text, String unit,
                 double answer) {
            this.text = text;
            this.unit = unit;
            this.answer = answer;
        }
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        showStart();
    }

    private int dp(int value) {
        return (int) (value *
                getResources().getDisplayMetrics()
                        .density + 0.5f);
    }

    private String fmt(double value) {
        return String.format(
                        new Locale("ru", "RU"),
                        "%.2f", value)
                .replaceAll("0+$", "")
                .replaceAll(",$", "");
    }

    private double roundAnswer(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private void createPage() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundResource(
                R.drawable.equations_bg);

        page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(
                dp(24), dp(25), dp(24), dp(30));

        scroll.addView(page);
        setContentView(scroll);
    }

    private void addText(String value, int size) {
        TextView text = new TextView(this);
        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(Color.rgb(12, 85, 66));
        text.setPadding(0, dp(10), 0, dp(12));
        page.addView(text);
    }

    private EditText addInput(String hint,
                              boolean numeric) {

        EditText field = new EditText(this);
        field.setHint(hint);
        field.setTextSize(20);
        field.setSingleLine(true);
        field.setGravity(Gravity.CENTER);
        field.setTextColor(Color.BLACK);
        field.setHintTextColor(Color.GRAY);

        if (numeric) {
            field.setInputType(
                    InputType.TYPE_CLASS_NUMBER
                            | InputType.TYPE_NUMBER_FLAG_DECIMAL
                            | InputType.TYPE_NUMBER_FLAG_SIGNED);
        }

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(dp(35));
        bg.setStroke(dp(2),
                Color.rgb(24, 112, 86));

        field.setBackground(bg);
        field.setPadding(dp(15), 0, dp(15), 0);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(62));

        params.setMargins(0, dp(5), 0, dp(16));
        page.addView(field, params);

        return field;
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

        params.setMargins(0, 0, 0, dp(14));
        page.addView(button, params);
        button.setOnClickListener(v -> action.run());
    }

    // ВЫБОР ВИДА РАБОТЫ

    private void showStart() {
        createPage();

        addText("Физика · 7 класс", 20);
        addText("Самостоятельная работа", 28);
        addText("Масса и плотность", 23);

        nameInput = addInput(
                "Фамилия и имя", false);
        classInput = addInput(
                "Класс, например 7А", false);

        addText("Выберите уровень обучения:", 20);

        addButton("Обычный класс", () ->
                startControl(false));

        addButton("Профильный класс", () ->
                startControl(true));

        addButton("Вернуться к теме", this::finish);
    }

    private void startControl(boolean isProfile) {

        studentName = nameInput.getText()
                .toString().trim();

        studentClass = classInput.getText()
                .toString().trim();

        if (studentName.isEmpty()) {
            nameInput.setError("Укажите имя");
            return;
        }

        if (studentClass.isEmpty()) {
            classInput.setError("Укажите класс");
            return;
        }

        profile = isProfile;
        generateControl();
        showQuestion();
    }

    // СОЗДАНИЕ ЧЕТЫРЁХ ЗАДАНИЙ

    private void generateControl() {

        questions.clear();
        current = 0;
        score = 0;

        if (profile) {
            generateProfile();
        } else {
            generateStandard();
        }
    }

    // ОБЫЧНЫЙ КЛАСС

    private void generateStandard() {

        int i = random.nextInt(5);
        int v = 100 + 50 * random.nextInt(7);

        double rho = DENSITIES[i] / 1000.0;
        double m = rho * v;

        questions.add(new Question(
                "Образец из " + MATERIALS[i]
                        + " имеет массу " + fmt(m)
                        + " г и объём " + v
                        + " см³.\n\nОпределите его плотность.",
                "г/см³", rho));

        i = 5 + random.nextInt(3);
        v = 100 + 50 * random.nextInt(9);

        rho = DENSITIES[i] / 1000.0;
        m = rho * v;

        questions.add(new Question(
                "В мерный сосуд налили "
                        + v + " см³ "
                        + LIQUIDS[i - 5]
                        + ".\n\nОпределите массу жидкости.",
                "г", m));

        i = random.nextInt(5);
        int a = 10 + 5 * random.nextInt(5);
        int b = 4 + random.nextInt(6);
        int c = 2 + random.nextInt(5);

        double volume = a * b * c;
        m = volume * DENSITIES[i] / 1000000.0;

        questions.add(new Question(
                "Сплошной брусок из "
                        + MATERIALS[i]
                        + " имеет размеры "
                        + a + " × " + b + " × " + c
                        + " см.\n\nОпределите массу "
                        + "бруска в килограммах.",
                "кг", m));

        i = random.nextInt(5);
        int count = 2 + random.nextInt(5);
        v = 100 + 50 * random.nextInt(7);

        m = count * v * DENSITIES[i] / 1000.0;

        questions.add(new Question(
                "Из " + MATERIALS[i]
                        + " изготовили " + count
                        + " одинаковых сплошных деталей."
                        + "\n\nОбъём каждой детали "
                        + v + " см³.\n\nОпределите "
                        + "общую массу деталей в граммах.",
                "г", m));
    }

    // ПРОФИЛЬНЫЙ КЛАСС

    private void generateProfile() {

        int length = 5 + 5 * random.nextInt(6);
        int area = 1 + random.nextInt(5);

        // 1 м · 1 мм² = 1 см³
        double volume = length * area;
        double mass =
                volume * DENSITIES[2] / 1000.0;

        questions.add(new Question(
                "Медная проволока имеет длину "
                        + length + " м и площадь "
                        + "поперечного сечения "
                        + area + " мм².\n\nОпределите "
                        + "её массу в граммах.",
                "г", mass));

        int i = random.nextInt(5);
        int a = 20 + 5 * random.nextInt(6);
        int b = 10 + 5 * random.nextInt(5);
        int thickness = 2 + random.nextInt(5);

        volume = a * b * thickness / 10.0;
        mass = volume * DENSITIES[i] / 1000.0;

        questions.add(new Question(
                "Однородная пластина из "
                        + MATERIALS[i]
                        + " имеет длину " + a
                        + " см, ширину " + b
                        + " см и толщину " + thickness
                        + " мм.\n\nОпределите её "
                        + "массу в граммах.",
                "г", mass));

        i = random.nextInt(5);
        a = 18 + 2 * random.nextInt(6);
        b = 12 + 2 * random.nextInt(5);
        int c = 10 + 2 * random.nextInt(5);
        int wall = 1 + random.nextInt(2);

        double outer = a * b * c;
        double inner =
                (a - 2 * wall)
                        * (b - 2 * wall)
                        * (c - 2 * wall);

        mass = (outer - inner)
                * DENSITIES[i] / 1000.0;

        questions.add(new Question(
                "Полая закрытая деталь из "
                        + MATERIALS[i]
                        + " имеет внешние размеры "
                        + a + " × " + b + " × " + c
                        + " см.\n\nТолщина всех стенок "
                        + wall + " см. Внутренняя полость "
                        + "имеет форму прямоугольного "
                        + "параллелепипеда.\n\n"
                        + "Определите массу детали "
                        + "в граммах.",
                "г", mass));

        int aluminumV =
                100 + 50 * random.nextInt(7);
        int copperV =
                50 + 25 * random.nextInt(9);

        double totalMass =
                (DENSITIES[0] * aluminumV
                        + DENSITIES[2] * copperV)
                        / 1000.0;

        double totalVolume =
                aluminumV + copperV;

        double averageDensity =
                totalMass / totalVolume;

        questions.add(new Question(
                "Деталь состоит из сплошных "
                        + "алюминиевой и медной частей "
                        + "без пустот.\n\nОбъём "
                        + "алюминиевой части "
                        + aluminumV + " см³, "
                        + "медной — " + copperV
                        + " см³.\n\nОпределите среднюю "
                        + "плотность детали в г/см³.",
                "г/см³", averageDensity));
    }

    // ЭКРАН ЗАДАНИЯ

    private void showQuestion() {

        createPage();

        Question question = questions.get(current);

        addText("Самостоятельная работа", 25);
        addText(profile
                ? "Профильный класс"
                : "Обычный класс", 20);

        addText("Задание " + (current + 1)
                + " из 4  ·  "
                + points[current] + " баллов", 19);

        addText(question.text, 21);

        addText("Округлите только окончательный "
                + "ответ до десятых.\n"
                + "Пример: 0,23 → 0,2", 16);

        addText("Ответ в " + question.unit + ":", 18);

        answerInput = addInput("Ваш ответ", true);

        addButton(current == 3
                        ? "Завершить работу"
                        : "Следующее задание",
                this::submitAnswer);
    }

    private void submitAnswer() {

        String input = answerInput.getText()
                .toString()
                .trim()
                .replace(",", ".");

        double value;

        try {
            value = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            answerInput.setError("Введите число");
            return;
        }

        if (!Double.isFinite(value) || value <= 0) {
            answerInput.setError(
                    "Введите положительное число");
            return;
        }

        double expected = roundAnswer(
                questions.get(current).answer);

        if (Math.abs(value - expected) < 0.000001) {
            score += points[current];
        }

        current++;

        if (current < questions.size()) {
            showQuestion();
        } else {
            showResult();
        }
    }

    // РЕЗУЛЬТАТ

    private void showResult() {

        createPage();

        int grade;

        if (score >= 18) {
            grade = 5;
        } else if (score >= 14) {
            grade = 4;
        } else if (score >= 9) {
            grade = 3;
        } else {
            grade = 2;
        }

        addText("Работа завершена!", 28);
        addText(studentName, 21);
        addText(studentClass, 19);

        addText(profile
                ? "Профильный класс"
                : "Обычный класс", 20);

        addText("Набрано баллов: "
                + score + " из 20", 25);

        addText("Оценка: " + grade, 29);

        addText("Правильность ответов "
                        + "во время работы не показывалась.",
                17);

        addButton("Вернуться к теме", this::finish);
    }
}

