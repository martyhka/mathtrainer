
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
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MotionTestActivity extends AppCompatActivity {

    private LinearLayout page;
    private EditText answerInput;

    private final Random random = new Random();
    private final List<Question> questions = new ArrayList<>();
    private final List<Double> answers = new ArrayList<>();

    private int current = 0;
    private boolean profile = false;

    private static class Question {
        String text;
        String unit;
        double result;

        Question(String text, String unit, double result) {
            this.text = text;
            this.unit = unit;
            this.result = result;
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
        text.setTextColor(Color.rgb(12, 85, 66));
        text.setPadding(0, dp(8), 0, dp(14));
        page.addView(text);
    }

    private void addButton(String label, Runnable action) {
        AppCompatButton button = new AppCompatButton(this);
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
        params.setMargins(0, dp(4), 0, dp(14));

        page.addView(button, params);
        button.setOnClickListener(v -> action.run());
    }

    private void showStart() {
        createPage();

        addText("Физика · 7 класс", 20);
        addText("Механическое движение", 27);
        addText("Проверочный тест", 24);

        addText("12 заданий разной сложности.\n\n"
                + "Ответы вводите числом в указанных "
                + "единицах измерения.\n\n"
                + "Если нужно, округлите окончательный "
                + "ответ до десятых.\n\n"
                + "Результат появится только "
                + "после завершения теста.", 19);


        addButton("Обычный класс", () -> {
            profile = false;
            generateQuestions();
            showQuestion();
        });

        addButton("Профильный класс", () -> {
            profile = true;
            generateQuestions();
            showQuestion();
        });


        addButton("Вернуться к теме", this::finish);
    }

    private void generateQuestions() {
        if (!profile) {
            generateStandardQuestions();
            return;
        }

        questions.clear();
        answers.clear();
        current = 0;

        // 1. Перевод скорости
        int v = 18 * (2 + random.nextInt(5));
        questions.add(new Question(
                "Переведите скорость " + v
                        + " км/ч в м/с.",
                "м/с", v * 10.0 / 36.0));

        // 2. Нахождение пути
        v = 3 + random.nextInt(12);
        int t = 20 + 10 * random.nextInt(9);
        questions.add(new Question(
                "Тело равномерно движется "
                        + "со скоростью " + v
                        + " м/с в течение " + t
                        + " с. Какой путь оно пройдёт?",
                "м", v * t));

        // 3. Нахождение времени
        v = 2 + random.nextInt(8);
        t = 30 + 10 * random.nextInt(9);
        questions.add(new Question(
                "Пешеход прошёл " + (v * t)
                        + " м со скоростью " + v
                        + " м/с. Определите время движения.",
                "с", t));

        // 4. Два участка движения
        int v1 = 30 + 10 * random.nextInt(5);
        int v2 = 40 + 10 * random.nextInt(5);
        int t1 = 1 + random.nextInt(4);
        int t2 = 1 + random.nextInt(4);
        questions.add(new Question(
                "Автомобиль двигался " + t1
                        + " ч со скоростью " + v1
                        + " км/ч, затем " + t2
                        + " ч со скоростью " + v2
                        + " км/ч. Найдите общий путь.",
                "км", v1 * t1 + v2 * t2));

        // 5. Встречное движение
        v1 = 40 + 10 * random.nextInt(5);
        v2 = 50 + 10 * random.nextInt(5);
        t = 2 + random.nextInt(5);
        questions.add(new Question(
                "Два автомобиля выехали одновременно "
                        + "навстречу друг другу со скоростями "
                        + v1 + " км/ч и " + v2
                        + " км/ч. Они встретились через "
                        + t + " ч. Каково первоначальное "
                        + "расстояние между ними?",
                "км", (v1 + v2) * t));

        // 6. Поезд проходит мост
        int train = 100 + 50 * random.nextInt(5);
        int bridge = 200 + 100 * random.nextInt(5);
        v = 36 + 18 * random.nextInt(4);
        questions.add(new Question(
                "Поезд длиной " + train
                        + " м движется со скоростью "
                        + v + " км/ч. Длина моста "
                        + bridge + " м. За сколько секунд "
                        + "поезд полностью пройдёт мост?",
                "с", (train + bridge) /
                (v * 10.0 / 36.0)));

        // 7. Средняя скорость с остановкой
        v = 40 + 10 * random.nextInt(5);
        t = 2 + random.nextInt(4);
        int stop = 10 * (1 + random.nextInt(5));
        questions.add(new Question(
                "Автобус двигался " + t
                        + " ч со скоростью " + v
                        + " км/ч, затем остановился на "
                        + stop + " мин. Определите среднюю "
                        + "скорость за всё время.",
                "км/ч", v * t /
                (t + stop / 60.0)));

        // 8. Две половины пути
        v1 = 30 + 10 * random.nextInt(5);
        v2 = 40 + 10 * random.nextInt(5);
        questions.add(new Question(
                "Первую половину пути автомобиль "
                        + "проехал со скоростью " + v1
                        + " км/ч, вторую — " + v2
                        + " км/ч. Найдите среднюю скорость "
                        + "на всём пути.",
                "км/ч", 2.0 * v1 * v2 / (v1 + v2)));

        // 9. Движение вдогонку
        int slow = 4 + random.nextInt(3);
        int fast = 12 + 2 * random.nextInt(6);
        int delay = 1 + random.nextInt(3);
        questions.add(new Question(
                "Пешеход вышел со скоростью "
                        + slow + " км/ч. Через " + delay
                        + " ч вслед за ним выехал велосипедист "
                        + "со скоростью " + fast
                        + " км/ч. Через сколько часов после "
                        + "выезда велосипедист догонит пешехода?",
                "ч", (double) slow * delay / (fast - slow)));

        // 10. Катер по течению и против течения
        int boat = 12 + 2 * random.nextInt(6);
        int flow = 1 + random.nextInt(4);
        questions.add(new Question(
                "Собственная скорость катера "
                        + boat + " км/ч, скорость течения "
                        + flow + " км/ч. Катер прошёл "
                        + "одинаковые расстояния по течению "
                        + "и против течения. Найдите "
                        + "среднюю скорость за весь путь.",
                "км/ч",
                (boat * boat - flow * flow) /
                        (double) boat));

        // 11. Средняя скорость за три часа
        int first = 30 + 30 * random.nextInt(4);
        int second = first + 30;
        int third = second + 30;
        int avg2 = (first + second) / 2;
        questions.add(new Question(
                "За первый час автомобиль прошёл "
                        + first + " км. Средняя скорость "
                        + "за первые два часа составила "
                        + avg2 + " км/ч, а за три часа — "
                        + (first + second + third) / 3
                        + " км/ч. Найдите путь за третий час.",
                "км", third));

        // 12. Скорость первой половины пути
        int firstSpeed = 30 * (1 + random.nextInt(4));
        int secondSpeed = 2 * firstSpeed;
        int average = 4 * firstSpeed / 3;
        questions.add(new Question(
                "Вторую половину пути автомобиль "
                        + "проехал со скоростью "
                        + secondSpeed + " км/ч. Средняя "
                        + "скорость за весь путь равнялась "
                        + average + " км/ч. Найдите скорость "
                        + "на первой половине пути.",
                "км/ч", firstSpeed));

        Collections.shuffle(questions, random);
    }



    private void generateStandardQuestions() {
        questions.clear();
        answers.clear();
        current = 0;

        // 1. Скорость
        int v = 2 + random.nextInt(8);
        int t = 20 + 10 * random.nextInt(8);

        questions.add(new Question(
                "Тело прошло " + (v * t) + " м за "
                        + t + " с. Определите скорость.",
                "м/с", v));

        // 2. Путь
        v = 3 + random.nextInt(10);
        t = 30 + 10 * random.nextInt(8);

        questions.add(new Question(
                "Велосипедист двигался со скоростью "
                        + v + " м/с в течение " + t
                        + " с. Найдите пройденный путь.",
                "м", v * t));

        // 3. Время
        v = 2 + random.nextInt(8);
        t = 40 + 20 * random.nextInt(6);

        questions.add(new Question(
                "Пешеход прошёл " + (v * t)
                        + " м со скоростью " + v
                        + " м/с. Найдите время движения.",
                "с", t));

        // 4. Перевод км/ч в м/с
        v = 18 * (2 + random.nextInt(6));

        questions.add(new Question(
                "Переведите " + v + " км/ч в м/с.",
                "м/с", v * 10.0 / 36.0));

        // 5. Перевод м/с в км/ч
        v = 5 * (1 + random.nextInt(7));

        questions.add(new Question(
                "Переведите " + v + " м/с в км/ч.",
                "км/ч", v * 36.0 / 10.0));

        // 6. Скорость автомобиля
        v = 40 + 10 * random.nextInt(6);
        t = 2 + random.nextInt(5);

        questions.add(new Question(
                "Автомобиль проехал " + (v * t)
                        + " км за " + t
                        + " ч. Определите его скорость.",
                "км/ч", v));

        // 7. Два участка с одинаковой скоростью
        v = 30 + 10 * random.nextInt(6);
        int t1 = 1 + random.nextInt(4);
        int t2 = 1 + random.nextInt(4);

        questions.add(new Question(
                "Автомобиль ехал со скоростью "
                        + v + " км/ч сначала " + t1
                        + " ч, затем ещё " + t2
                        + " ч. Найдите общий путь.",
                "км", v * (t1 + t2)));

        // 8. Два участка с разными скоростями
        int v1 = 30 + 10 * random.nextInt(5);
        int v2 = 40 + 10 * random.nextInt(5);
        t1 = 1 + random.nextInt(4);
        t2 = 1 + random.nextInt(4);

        questions.add(new Question(
                "Автобус ехал " + t1
                        + " ч со скоростью " + v1
                        + " км/ч, затем " + t2
                        + " ч со скоростью " + v2
                        + " км/ч. Найдите общий путь.",
                "км", v1 * t1 + v2 * t2));

        // 9. Средняя скорость
        v1 = 30 + 10 * random.nextInt(5);
        v2 = 40 + 10 * random.nextInt(5);
        t1 = 1 + random.nextInt(4);
        t2 = 1 + random.nextInt(4);

        questions.add(new Question(
                "Автомобиль ехал " + t1
                        + " ч со скоростью " + v1
                        + " км/ч и " + t2
                        + " ч со скоростью " + v2
                        + " км/ч. Определите среднюю скорость.",
                "км/ч",
                (double) (v1 * t1 + v2 * t2)
                        / (t1 + t2)));

        // 10. Средняя скорость с остановкой
        v = 40 + 10 * random.nextInt(5);
        t = 2 + random.nextInt(4);
        int stop = 10 * (1 + random.nextInt(5));

        questions.add(new Question(
                "Автобус проехал " + (v * t)
                        + " км за " + t
                        + " ч движения и остановился на "
                        + stop + " мин. Найдите среднюю "
                        + "скорость за всю поездку.",
                "км/ч",
                (double) (v * t) / (t + stop / 60.0)));

        // 11. Встречное движение
        v1 = 30 + 10 * random.nextInt(5);
        v2 = 40 + 10 * random.nextInt(5);
        t = 2 + random.nextInt(4);

        questions.add(new Question(
                "Два автомобиля одновременно "
                        + "выехали навстречу друг другу "
                        + "со скоростями " + v1
                        + " км/ч и " + v2
                        + " км/ч. Они встретились через "
                        + t + " ч. Найдите первоначальное "
                        + "расстояние между ними.",
                "км", (v1 + v2) * t));

        // 12. Поезд проходит мимо столба
        int length = 100 + 50 * random.nextInt(6);
        v = 36 + 18 * random.nextInt(5);

        questions.add(new Question(
                "Поезд длиной " + length
                        + " м движется со скоростью "
                        + v + " км/ч. За сколько секунд "
                        + "он полностью пройдёт "
                        + "мимо неподвижного столба?",
                "с", length / (v * 10.0 / 36.0)));

        Collections.shuffle(questions, random);
    }



    private void showQuestion() {
        // Экран задания
    
        createPage();

        Question q = questions.get(current);

        addText("Проверочный тест", 26);
        addText("Задание " + (current + 1)
                + " из " + questions.size(), 19);
        addText(q.text, 22);

        addButton("📖 Справочные материалы", () -> {
            PhysicsReferenceDialog.show(this);
        });

        addText("Ответ в " + q.unit + ":", 18);

        answerInput = new EditText(this);
        answerInput.setHint("Введите ответ");
        answerInput.setTextSize(20);
        answerInput.setTextColor(Color.BLACK);
        answerInput.setSingleLine(true);
        answerInput.setGravity(Gravity.CENTER);
        answerInput.setInputType(
                InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(dp(32));
        bg.setStroke(dp(2), Color.rgb(24, 112, 86));
        answerInput.setBackground(bg);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(64));
        params.setMargins(0, dp(8), 0, dp(18));
        page.addView(answerInput, params);

        addButton(current == questions.size() - 1
                        ? "Завершить тест"
                        : "Следующее задание",
                this::saveAnswer);
    }

    private void saveAnswer() {
        String value = answerInput.getText()
                .toString().trim().replace(",", ".");

        try {
            double answer = Double.parseDouble(value);

            if (!Double.isFinite(answer) || answer <= 0) {
                answerInput.setError("Введите положительное число");
                return;
            }

            answers.add(answer);
            current++;

            if (current < questions.size()) {
                showQuestion();
            } else {
                showResults();
            }

        } catch (NumberFormatException e) {
            answerInput.setError("Введите число");
        }
    }

    private void showResults() {
        createPage();

        int correct = 0;
        for (int i = 0; i < questions.size(); i++) {
            double expected = Math.round(
                    questions.get(i).result * 10.0) / 10.0;

            if (Math.abs(answers.get(i) - expected) < 0.000001) {
                correct++;
            }
        }

        addText("Тест завершён!", 28);
        addText("Правильных ответов: "
                + correct + " из " + questions.size(), 24);

        addText("Результат: "
                + Math.round(correct * 100.0 / questions.size())
                + "%", 23);

        addButton("Пройти тест заново", () -> {
            generateQuestions();
            showQuestion();
        });

        addButton("Вернуться к теме", this::finish);
    }
}
