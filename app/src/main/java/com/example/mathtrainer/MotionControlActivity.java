

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MotionControlActivity extends AppCompatActivity {

    private LinearLayout page;
    private EditText nameInput;
    private EditText numberInput;
    private EditText classInput;
    private EditText answerInput;

    private final Random random = new Random();
    private final List<Question> questions = new ArrayList<>();
    private final List<Double> studentAnswers = new ArrayList<>();

    private final int[] points = {3, 4, 5, 8};

    private int current = 0;
    private int score = 0;
    private boolean profile = false;

    private String studentName = "";

    private String studentNumber = "";

    private String studentClass = "";

    private static class Question {
        String text;
        String unit;
        double answer;

        Question(String text, String unit, double answer) {
            this.text = text;
            this.unit = unit;
            this.answer = answer;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showStart();
    }

    private int dp(int n) {
        return (int) (n *
                getResources().getDisplayMetrics().density + 0.5f);
    }

    private double roundAnswer(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private void createPage() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundResource(R.drawable.equations_bg);

        page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(22), dp(25), dp(22), dp(30));

        scroll.addView(page);
        setContentView(scroll);
    }

    private void addText(String value, int size) {
        TextView text = new TextView(this);
        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(Color.rgb(15, 90, 68));
        text.setPadding(0, dp(8), 0, dp(14));
        page.addView(text);
    }

    private void addButton(String title, Runnable action) {
        AppCompatButton button = new AppCompatButton(this);
        button.setText(title);
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
        params.setMargins(0, dp(3), 0, dp(13));

        page.addView(button, params);
        button.setOnClickListener(v -> action.run());
    }

    private EditText addInput(String hint, boolean numeric) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setTextSize(20);
        input.setTextColor(Color.BLACK);
        input.setSingleLine(true);
        input.setGravity(Gravity.CENTER);

        if (numeric) {
            input.setInputType(
                    InputType.TYPE_CLASS_NUMBER |
                            InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }

        GradientDrawable background =
                new GradientDrawable();
        background.setColor(Color.WHITE);
        background.setCornerRadius(dp(30));
        background.setStroke(dp(2),
                Color.rgb(25, 115, 85));

        input.setBackground(background);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(64));
        params.setMargins(0, dp(5), 0, dp(15));

        page.addView(input, params);
        return input;
    }

    // ВЫБОР КОНТРОЛЬНОЙ РАБОТЫ

    private void showStart() {
        createPage();

        addText("Физика · 7 класс", 19);
        addText("Механическое движение", 26);
        addText("Самостоятельная работа", 24);


        nameInput = addInput("Имя ученика", false);

        numberInput = addInput("Личный код ученика", false);

        classInput = addInput("Класс, например 7А", false);


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

        studentNumber = numberInput.getText()
                .toString().trim();


        nameInput.setError("Укажите имя ученика");


        if (studentName.isEmpty()) {
            nameInput.setError("Укажите имя ученика");
            return;
        }

        if (studentNumber.isEmpty()
                || !studentNumber.matches("[a-zA-Zа-яА-ЯёЁ0-9-]{3,12}")) {
            numberInput.setError("Введите личный код от 3 до 12 символов");
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

    private void generateControl() {
        questions.clear();
        studentAnswers.clear();
        current = 0;
        score = 0;

        if (profile) {
            generateProfile();
        } else {
            generateStandard();
        }
    }

    // ОБЫЧНЫЙ КЛАСС: 4 ЗАДАНИЯ

    private void generateStandard() {

        // 1. Нахождение неизвестной величины
        int v = 2 + random.nextInt(9);
        int t = 20 + 10 * random.nextInt(9);
        int s = v * t;

        int type = random.nextInt(3);

        if (type == 0) {
            questions.add(new Question(
                    "Тело прошло " + s + " м за "
                            + t + " с.\n\n"
                            + "Определите скорость "
                            + "равномерного движения.",
                    "м/с", v));
        } else if (type == 1) {
            questions.add(new Question(
                    "Тело движется равномерно "
                            + "со скоростью " + v
                            + " м/с в течение " + t
                            + " с.\n\nОпределите путь.",
                    "м", s));
        } else {
            questions.add(new Question(
                    "Тело прошло " + s
                            + " м со скоростью " + v
                            + " м/с.\n\n"
                            + "Определите время движения.",
                    "с", t));
        }

        // 2. Два участка пути
        int v1 = 30 + 10 * random.nextInt(5);
        int v2 = 40 + 10 * random.nextInt(5);
        int t1 = 1 + random.nextInt(4);
        int t2 = 1 + random.nextInt(4);

        questions.add(new Question(
                "Автомобиль ехал " + t1
                        + " ч со скоростью " + v1
                        + " км/ч, затем ещё " + t2
                        + " ч со скоростью " + v2
                        + " км/ч.\n\n"
                        + "Определите общий путь.",
                "км", v1 * t1 + v2 * t2));

        // 3. Средняя скорость на двух участках
        v1 = 30 + 10 * random.nextInt(5);
        v2 = 40 + 10 * random.nextInt(5);
        t1 = 1 + random.nextInt(4);
        t2 = 1 + random.nextInt(4);

        double average =
                (double) (v1 * t1 + v2 * t2) /
                        (t1 + t2);

        questions.add(new Question(
                "Автобус двигался " + t1
                        + " ч со скоростью " + v1
                        + " км/ч и " + t2
                        + " ч со скоростью " + v2
                        + " км/ч.\n\n"
                        + "Найдите среднюю скорость "
                        + "за всё время движения.",
                "км/ч", average));

        // 4. Средняя скорость с остановкой
        v = 40 + 10 * random.nextInt(6);
        t = 2 + random.nextInt(5);
        int stop = 10 * (1 + random.nextInt(5));

        double path = v * t;
        double totalTime = t + stop / 60.0;

        questions.add(new Question(
                "Автомобиль проехал " + path
                        + " км со скоростью " + v
                        + " км/ч.\n\n"
                        + "Во время поездки он сделал "
                        + "остановку продолжительностью "
                        + stop + " мин.\n\n"
                        + "Определите среднюю скорость "
                        + "за всю поездку, включая остановку.",
                "км/ч", path / totalTime));
    }

    // ПРОФИЛЬНЫЙ КЛАСС: 4 ЗАДАНИЯ

    private void generateProfile() {

        // 1. Перевод единиц и вычисление пути
        int vKmh = 36 + 18 * random.nextInt(5);
        int seconds = 20 + 10 * random.nextInt(9);

        double vMs = vKmh * 10.0 / 36.0;

        questions.add(new Question(
                "Автомобиль движется равномерно "
                        + "со скоростью " + vKmh
                        + " км/ч.\n\n"
                        + "Какой путь в метрах он "
                        + "пройдёт за " + seconds + " с?",
                "м", vMs * seconds));

        // 2. Движение вдогонку
        int slow = 4 + random.nextInt(3);
        int fast = 12 + 2 * random.nextInt(6);
        int delay = 1 + random.nextInt(3);

        double catchTime =
                (double) slow * delay / (fast - slow);

        questions.add(new Question(
                "Из посёлка вышел пешеход "
                        + "со скоростью " + slow
                        + " км/ч.\n\n"
                        + "Через " + delay
                        + " ч вслед за ним выехал "
                        + "велосипедист со скоростью "
                        + fast + " км/ч.\n\n"
                        + "Через сколько часов после "
                        + "своего выезда велосипедист "
                        + "догонит пешехода?",
                "ч", catchTime));

        // 3. Средняя скорость с остановкой
        int v1 = 40 + 10 * random.nextInt(5);
        int v2 = 50 + 10 * random.nextInt(5);
        int t1 = 1 + random.nextInt(4);
        int t2 = 1 + random.nextInt(4);
        int stop = 10 * (1 + random.nextInt(5));

        double path = v1 * t1 + v2 * t2;
        double totalTime = t1 + t2 + stop / 60.0;

        questions.add(new Question(
                "Автобус ехал " + t1
                        + " ч со скоростью " + v1
                        + " км/ч, затем остановился на "
                        + stop + " мин.\n\n"
                        + "После остановки он двигался "
                        + t2 + " ч со скоростью "
                        + v2 + " км/ч.\n\n"
                        + "Определите среднюю скорость "
                        + "за всё время поездки.",
                "км/ч", path / totalTime));

        // 4. Сложная задача на среднюю скорость
        int firstSpeed = 30 * (1 + random.nextInt(4));
        int secondSpeed = 2 * firstSpeed;
        int average = 4 * firstSpeed / 3;

        questions.add(new Question(
                "Автомобиль проехал первую половину "
                        + "пути с неизвестной скоростью, "
                        + "а вторую половину — со скоростью "
                        + secondSpeed + " км/ч.\n\n"
                        + "Средняя скорость на всём пути "
                        + "составила " + average
                        + " км/ч.\n\n"
                        + "Определите скорость автомобиля "
                        + "на первой половине пути.",
                "км/ч", firstSpeed));
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
                + " из 4 · "
                + points[current] + " баллов", 19);

        addText(question.text, 21);

        addText("Ответ в " + question.unit + ":", 18);

        answerInput = addInput("Введите ответ", true);


        addText(
                "ПРАВИЛА ВЫПОЛНЕНИЯ:\n"
                        + "Все окончательные ответы округляйте до десятых.\n"
                        + "Промежуточные вычисления не округляйте.\n"
                        + "Можно использовать десятичную запятую или точку.",
                16
        );


        addButton(current == 3
                        ? "Завершить работу"
                        : "Следующее задание",
                this::saveAnswer);
    }

    private void saveAnswer() {
        String input = answerInput.getText()
                .toString().trim().replace(",", ".");

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

        studentAnswers.add(value);

        double expected =
                roundAnswer(questions.get(current).answer);

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

    // ИТОГ РАБОТЫ

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

        addText("Личный код: " + studentNumber, 19);

        addText(studentClass, 19);

        addText(profile
                ? "Профильный класс"
                : "Обычный класс", 20);

        addText("Набрано баллов: "
                + score + " из 20", 25);

        addText("Оценка: " + grade, 29);

        addButton("Вернуться к теме", this::finish);
    }
}
