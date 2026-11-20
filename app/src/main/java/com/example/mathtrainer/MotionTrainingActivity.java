

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
import java.util.Locale;
import java.util.Random;

public class MotionTrainingActivity extends AppCompatActivity {

    private LinearLayout page;
    private EditText answerInput;

    private final Random random = new Random();
    private final List<Question> questions = new ArrayList<>();
    private final List<Double> studentAnswers = new ArrayList<>();

    private int current = 0;
    private int selectedLevel = 1;

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
        showLevelMenu();
    }

    private int dp(int value) {
        return (int) (value *
                getResources().getDisplayMetrics().density + 0.5f);
    }

    private String fmt(double value) {
        return String.format(
                new Locale("ru", "RU"),
                "%.1f", value);
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

        params.setMargins(0, 0, 0, dp(14));

        page.addView(button, params);
        button.setOnClickListener(v -> action.run());
    }

    private EditText addAnswerField() {
        EditText input = new EditText(this);

        input.setHint("Введите ответ");
        input.setTextSize(21);
        input.setGravity(Gravity.CENTER);
        input.setSingleLine(true);
        input.setTextColor(Color.BLACK);

        input.setInputType(
                InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL);

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
                        dp(65));

        params.setMargins(0, dp(10), 0, dp(20));
        page.addView(input, params);

        return input;
    }

    // МЕНЮ УРОВНЕЙ

    private void showLevelMenu() {
        createPage();

        addText("Физика · 7 класс", 19);
        addText("Механическое движение", 27);
        addText("Тренировка", 24);

        addText("Выберите уровень сложности:", 20);


        addButton("1 уровень — Начальный", () -> {
            selectedLevel = 1;
            generateLevelOneQuestions();
            showQuestion();
        });


        addButton("2 уровень — Базовый", () -> {
            selectedLevel = 2;
            generateLevelTwoQuestions();
            showQuestion();
        });



        addButton("3 уровень — Повышенный", () -> {
            selectedLevel = 3;
            generateLevelThreeQuestions();
            showQuestion();
        });



        addButton("4 уровень — Высокий", () -> {
            selectedLevel = 4;
            generateLevelFourQuestions();
            showQuestion();
        });


        addButton("Вернуться к теме", this::finish);
    }

    private void showComingSoon() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Уровень в разработке")
                .setMessage("Скоро добавим задачи "
                        + "этого уровня сложности.")
                .setPositiveButton("Понятно", null)
                .show();
    }

    // ГЕНЕРАЦИЯ ПЕРВОГО УРОВНЯ

    private void generateLevelOneQuestions() {
        questions.clear();
        studentAnswers.clear();
        current = 0;

        // Тип 1. Перевод из м/с в км/ч
        int speed = 5 * (1 + random.nextInt(6));

        questions.add(new Question(
                "Переведите скорость "
                        + speed + " м/с в км/ч.\n\n"
                        + speed + " м/с в км/ч.",
                "км/ч",
                speed * 36.0 / 10.0));

        // Тип 2. Перевод из км/ч в м/с
        speed = 18 * (1 + random.nextInt(6));

        questions.add(new Question(
                "Переведите скорость "
                        + speed + " км/ч в м/с.\n\n"
                        + speed + " м/с в км/ч.",
                "м/с",
                speed * 10.0 / 36.0));

        // Тип 3. Самостоятельный выбор формулы
        addFormulaQuestion();

        // Тип 4. Путь при равномерном движении
        speed = 3 + random.nextInt(13);
        int time = 10 * (2 + random.nextInt(9));

        questions.add(new Question(
                "Велосипедист движется равномерно "
                        + "со скоростью " + speed + " м/с.\n"
                        + "Какой путь он пройдёт за "
                        + time + " с?",
                "м",
                speed * time));

        // Тип 5. Время движения пешехода
        speed = 1 + random.nextInt(3);
        time = 30 * (2 + random.nextInt(9));

        int path = speed * time;

        questions.add(new Question(
                "Пешеход прошёл " + path
                        + " м, двигаясь равномерно "
                        + "со скоростью " + speed
                        + " м/с.\n\n"
                        + "Сколько секунд он находился в пути?",
                "с",
                time));

        // Тип 6. Два последовательных участка
        speed = 30 + 10 * random.nextInt(7);
        int time1 = 1 + random.nextInt(4);
        int time2 = 1 + random.nextInt(4);

        questions.add(new Question(
                "Автомобиль двигался равномерно "
                        + "со скоростью " + speed + " км/ч.\n"
                        + "Сначала он ехал " + time1
                        + " ч, затем ещё " + time2
                        + " ч с той же скоростью.\n\n"
                        + "Найдите весь пройденный путь.",
                "км",
                speed * (time1 + time2)));

        // Тип 7. Сравнение скоростей двух тел
        int speed1 = 2 + random.nextInt(9);
        int difference = 2 + random.nextInt(5);

        questions.add(new Question(
                "Первый велосипедист движется "
                        + "со скоростью " + speed1
                        + " м/с.\n"
                        + "Скорость второго на "
                        + difference
                        + " м/с больше.\n\n"
                        + "Найдите скорость второго велосипедиста.",
                "м/с",
                speed1 + difference));

        // Восьмое задание — новый вариант одного
        // из уже изученных типов.
        addFormulaQuestion();

        Collections.shuffle(questions, random);
    }

    // СЛУЧАЙНЫЙ ВЫБОР НЕИЗВЕСТНОЙ ВЕЛИЧИНЫ

    private void generateLevelTwoQuestions() {
        questions.clear();
        studentAnswers.clear();
        current = 0;

        // Задача 1. Скорость сближения
        int v1 = 3 + random.nextInt(6);
        int v2 = 2 + random.nextInt(5);

        questions.add(new Question(
                "Два пешехода движутся навстречу "
                        + "друг другу со скоростями "
                        + v1 + " м/с и " + v2
                        + " м/с.\n\n"
                        + "С какой скоростью уменьшается "
                        + "расстояние между ними?",
                "м/с",
                v1 + v2));

        // Задача 2. Движение вдогонку
        int busSpeed = 54 + 18 * random.nextInt(3);
        int walkerSpeed = 1 + random.nextInt(3);
        int distance = 100 + 50 * random.nextInt(7);

        double relativeSpeed =
                busSpeed * 10.0 / 36.0 - walkerSpeed;

        questions.add(new Question(
                "Автобус движется со скоростью "
                        + busSpeed + " км/ч, а пешеход "
                        + "идёт в том же направлении "
                        + "со скоростью " + walkerSpeed
                        + " м/с.\n\n"
                        + "В начальный момент автобус "
                        + "находится позади пешехода "
                        + "на расстоянии " + distance
                        + " м.\n\n"
                        + "Через сколько секунд автобус "
                        + "догонит пешехода?",
                "с",
                distance / relativeSpeed));

        // Задача 3. Поезд проходит тоннель
        int trainLength = 100 + 50 * random.nextInt(5);
        int tunnelLength = 200 + 100 * random.nextInt(6);
        int trainSpeed = 36 + 18 * random.nextInt(4);

        double speedMs = trainSpeed * 10.0 / 36.0;

        questions.add(new Question(
                "Поезд длиной " + trainLength
                        + " м движется со скоростью "
                        + trainSpeed + " км/ч.\n\n"
                        + "Длина тоннеля "
                        + tunnelLength + " м.\n\n"
                        + "За сколько секунд поезд "
                        + "полностью пройдёт тоннель "
                        + "с момента входа его передней части?",
                "с",
                (trainLength + tunnelLength) / speedMs));

        // Задача 4. Прохождение мимо столба
        trainLength = 120 + 30 * random.nextInt(9);
        trainSpeed = 36 + 18 * random.nextInt(4);

        questions.add(new Question(
                "Поезд длиной " + trainLength
                        + " м движется равномерно "
                        + "со скоростью " + trainSpeed
                        + " км/ч.\n\n"
                        + "За сколько секунд поезд "
                        + "полностью пройдёт мимо "
                        + "неподвижного столба?",
                "с",
                trainLength / (trainSpeed * 10.0 / 36.0)));

        // Задача 5. Встречное движение
        v1 = 40 + 10 * random.nextInt(6);
        v2 = 50 + 10 * random.nextInt(5);
        int meetingTime = 2 + random.nextInt(5);

        distance = (v1 + v2) * meetingTime;

        questions.add(new Question(
                "Из двух городов навстречу "
                        + "друг другу одновременно "
                        + "выехали два автомобиля.\n\n"
                        + "Их скорости " + v1
                        + " км/ч и " + v2
                        + " км/ч.\n\n"
                        + "Расстояние между городами "
                        + distance + " км.\n\n"
                        + "Через сколько часов "
                        + "автомобили встретятся?",
                "ч",
                meetingTime));

        // Задача 6. Расстояние между городами
        v1 = 40 + 10 * random.nextInt(6);
        v2 = 50 + 10 * random.nextInt(5);
        meetingTime = 2 + random.nextInt(5);

        questions.add(new Question(
                "Два автомобиля одновременно "
                        + "выехали навстречу друг другу "
                        + "из разных городов.\n\n"
                        + "Первый двигался со скоростью "
                        + v1 + " км/ч, второй — "
                        + v2 + " км/ч.\n\n"
                        + "Они встретились через "
                        + meetingTime + " ч.\n\n"
                        + "Определите первоначальное "
                        + "расстояние между городами.",
                "км",
                (v1 + v2) * meetingTime));

        // Задача 7. Данные равномерного движения
        int speed = 2 + random.nextInt(8);
        int oneMinutePath = speed * 60;

        questions.add(new Question(
                "При наблюдении за равномерно "
                        + "движущимся телом получены данные:\n\n"
                        + "Время, мин:  0 | 1 | 2 | 3\n"
                        + "Путь, м:  0 | "
                        + oneMinutePath + " | "
                        + (oneMinutePath * 2) + " | "
                        + (oneMinutePath * 3)
                        + "\n\nОпределите скорость "
                        + "тела в м/с.",
                "м/с",
                speed));

        // Задача 8. Движение с задержкой старта
        int firstSpeed = 40 + 10 * random.nextInt(5);
        int secondSpeed = 50 + 10 * random.nextInt(5);
        int delay = 1 + random.nextInt(3);
        int travelTime = 2 + random.nextInt(4);

        distance = firstSpeed * delay
                + (firstSpeed + secondSpeed) * travelTime;

        questions.add(new Question(
                "Из города А в город Б выехал "
                        + "автомобиль со скоростью "
                        + firstSpeed + " км/ч.\n\n"
                        + "Через " + delay
                        + " ч навстречу ему из города Б "
                        + "выехал второй автомобиль "
                        + "со скоростью " + secondSpeed
                        + " км/ч.\n\n"
                        + "Расстояние между городами "
                        + distance + " км.\n\n"
                        + "Через сколько часов после "
                        + "выезда второго автомобиля "
                        + "произошла встреча?",
                "ч",
                travelTime));

        Collections.shuffle(questions, random);
    }

    private void generateLevelThreeQuestions() {
        questions.clear();
        studentAnswers.clear();
        current = 0;

        // 1. Половины пути с разными скоростями
        int v1 = 30 + 10 * random.nextInt(5);
        int v2 = 40 + 10 * random.nextInt(5);
        int half = 60 + 20 * random.nextInt(6);

        double totalTime = (double) half / v1
                + (double) half / v2;

        questions.add(new Question(
                "Автомобиль проехал первую половину "
                        + "пути со скоростью " + v1
                        + " км/ч, а вторую половину — "
                        + v2 + " км/ч.\n\n"
                        + "Весь путь составил "
                        + (2 * half) + " км.\n\n"
                        + "Определите среднюю скорость "
                        + "на всём пути.",
                "км/ч",
                2.0 * half / totalTime));

        // 2. Дорога туда и обратно
        int distance = 60 + 20 * random.nextInt(7);
        v1 = 40 + 10 * random.nextInt(5);
        v2 = 50 + 10 * random.nextInt(5);

        questions.add(new Question(
                "Автомобиль проехал из города А "
                        + "в город Б со скоростью "
                        + v1 + " км/ч, а обратно — "
                        + v2 + " км/ч.\n\n"
                        + "Расстояние между городами "
                        + distance + " км.\n\n"
                        + "Определите среднюю скорость "
                        + "за всю поездку.",
                "км/ч",
                2.0 * distance /
                        ((double) distance / v1
                                + (double) distance / v2)));

        // 3. Три четверти пути
        int totalDistance = 80 + 40 * random.nextInt(7);
        v1 = 40 + 10 * random.nextInt(5);
        v2 = 20 + 10 * random.nextInt(5);

        double firstPart = totalDistance * 0.75;
        double secondPart = totalDistance * 0.25;

        questions.add(new Question(
                "Автомобиль проехал три четверти "
                        + "пути со скоростью " + v1
                        + " км/ч, а оставшуюся четверть — "
                        + "со скоростью " + v2
                        + " км/ч.\n\n"
                        + "Общий путь составил "
                        + totalDistance + " км.\n\n"
                        + "Найдите среднюю скорость "
                        + "на всём пути.",
                "км/ч",
                totalDistance /
                        (firstPart / v1
                                + secondPart / v2)));

        // 4. Половины времени движения
        v1 = 30 + 10 * random.nextInt(6);
        v2 = 40 + 10 * random.nextInt(6);

        int hours = 2 + 2 * random.nextInt(4);

        questions.add(new Question(
                "Велосипедист или другой участник "
                        + "движения ехал первую половину "
                        + "времени со скоростью "
                        + v1 + " км/ч, вторую половину "
                        + "времени — со скоростью "
                        + v2 + " км/ч.\n\n"
                        + "Общее время движения "
                        + hours + " ч.\n\n"
                        + "Определите среднюю скорость.",
                "км/ч",
                (v1 + v2) / 2.0));

        // 5. Средняя скорость с остановкой
        int speed = 40 + 10 * random.nextInt(6);
        int movingHours = 2 + random.nextInt(5);
        int stopMinutes = 10 * (1 + random.nextInt(5));

        int path = speed * movingHours;

        questions.add(new Question(
                "Автобус проехал " + path
                        + " км со скоростью "
                        + speed + " км/ч.\n\n"
                        + "Во время поездки автобус "
                        + "останавливался на "
                        + stopMinutes + " мин.\n\n"
                        + "Определите среднюю скорость "
                        + "за всё время поездки, "
                        + "включая остановку.",
                "км/ч",
                path / (movingHours
                        + stopMinutes / 60.0)));



        // 7. Движение вдогонку с задержкой
        int pedestrianSpeed = 4 + random.nextInt(3);
        int cyclistSpeed = 12 + 2 * random.nextInt(7);
        int delayHours = 1 + random.nextInt(3);

        double catchTime =
                (double) pedestrianSpeed * delayHours
                        / (cyclistSpeed - pedestrianSpeed);

        questions.add(new Question(
                "Из посёлка вышел пешеход "
                        + "со скоростью "
                        + pedestrianSpeed + " км/ч.\n\n"
                        + "Через " + delayHours
                        + " ч вслед за ним выехал "
                        + "велосипедист со скоростью "
                        + cyclistSpeed + " км/ч.\n\n"
                        + "Через сколько часов после "
                        + "своего выезда велосипедист "
                        + "догонит пешехода?",
                "ч",
                catchTime));

        // 8. Движение катера по реке
        int boatSpeed = 12 + 2 * random.nextInt(7);
        int currentSpeed = 1 + random.nextInt(4);
        int riverDistance = 24 + 12 * random.nextInt(9);

        double downstreamTime =
                (double) riverDistance /
                        (boatSpeed + currentSpeed);

        double upstreamTime =
                (double) riverDistance /
                        (boatSpeed - currentSpeed);

        questions.add(new Question(
                "Катер прошёл " + riverDistance
                        + " км вниз по течению реки "
                        + "и такое же расстояние "
                        + "против течения.\n\n"
                        + "Собственная скорость катера "
                        + boatSpeed + " км/ч, "
                        + "скорость течения "
                        + currentSpeed + " км/ч.\n\n"
                        + "Определите среднюю скорость "
                        + "катера за весь путь.",
                "км/ч",
                2.0 * riverDistance /
                        (downstreamTime + upstreamTime)));

        // Дополнительная задача. Два участка движения
        int speedA = 30 + 10 * random.nextInt(6);
        int speedB = 40 + 10 * random.nextInt(6);
        int hoursA = 1 + random.nextInt(4);
        int hoursB = 1 + random.nextInt(4);

        double totalPath = speedA * hoursA
                + speedB * hoursB;

        double totalHours = hoursA + hoursB;

        questions.add(new Question(
                "Автомобиль двигался " + hoursA
                        + " ч со скоростью " + speedA
                        + " км/ч, затем ещё " + hoursB
                        + " ч со скоростью " + speedB
                        + " км/ч.\n\n"
                        + "Определите среднюю скорость "
                        + "автомобиля за всё время движения.",
                "км/ч",
                totalPath / totalHours));

        Collections.shuffle(questions, random);
    }

    private void generateLevelFourQuestions() {
        questions.clear();
        studentAnswers.clear();
        current = 0;

        // 1. Встречное движение и движение вдогонку
        int slow = 20 + 10 * random.nextInt(5);
        int fast = 2 * slow;
        int initialDistance = 3 * (fast - slow);

        questions.add(new Question(
                "Автобус и мотоциклист находятся "
                        + "на расстоянии " + initialDistance
                        + " км друг от друга.\n\n"
                        + "При движении в одном направлении "
                        + "мотоциклист догоняет автобус за 3 ч, "
                        + "а при движении навстречу друг другу "
                        + "они встречаются через 1 ч.\n\n"
                        + "Определите скорость мотоциклиста.",
                "км/ч", fast));

        // 2. Средняя скорость катера на двух участках
        int boat = 12 + 2 * random.nextInt(7);
        int flow = 2 + random.nextInt(4);
        int riverPath = 30 + 10 * random.nextInt(7);

        double riverTime = (double) riverPath / (boat + flow)
                + (double) riverPath / (boat - flow);

        questions.add(new Question(
                "Катер прошёл " + riverPath
                        + " км вниз по течению реки и "
                        + "столько же против течения.\n\n"
                        + "Собственная скорость катера "
                        + boat + " км/ч, скорость течения "
                        + flow + " км/ч.\n\n"
                        + "Найдите среднюю скорость катера "
                        + "на всём пути.",
                "км/ч", 2.0 * riverPath / riverTime));

        // 3. Предмет, упавший в реку
        int upstreamHours = 1 + random.nextInt(3);
        int riverFlow = 1 + random.nextInt(4);
        int drift = 2 * upstreamHours * riverFlow;

        questions.add(new Question(
                "Лодка движется вверх по реке "
                        + "с постоянной скоростью "
                        + "относительно воды.\n\n"
                        + "Под мостом из лодки упал предмет. "
                        + "Потерю заметили через "
                        + upstreamHours + " ч.\n\n"
                        + "Лодка сразу развернулась и, "
                        + "двигаясь с прежней скоростью "
                        + "относительно воды, догнала предмет "
                        + "на расстоянии " + drift
                        + " км ниже моста.\n\n"
                        + "Определите скорость течения.",
                "км/ч", riverFlow));

        // 4. Движение по палубе судна
        int shipSpeed = 5 + random.nextInt(4);
        int forwardSpeed = 1 + random.nextInt(3);
        int backwardSpeed = 1 + random.nextInt(3);
        int shipLength = 60 + 20 * random.nextInt(5);

        double forwardTime =
                (double) shipLength / forwardSpeed;
        double backwardTime =
                (double) shipLength / backwardSpeed;

        double shorePath =
                (shipSpeed + forwardSpeed) * forwardTime
                        + (shipSpeed - backwardSpeed) * backwardTime;

        questions.add(new Question(
                "Судно длиной " + shipLength
                        + " м движется по прямой со скоростью "
                        + shipSpeed + " м/с относительно берега.\n\n"
                        + "Матрос проходит по палубе от кормы "
                        + "к носу со скоростью " + forwardSpeed
                        + " м/с относительно судна, затем "
                        + "возвращается со скоростью "
                        + backwardSpeed + " м/с "
                        + "относительно судна.\n\n"
                        + "Какой путь пройдёт матрос "
                        + "относительно берега за весь "
                        + "этот промежуток времени?",
                "м", shorePath));

        // 5. Неизвестная скорость за третий час
        int first = 30 + 30 * random.nextInt(4);
        int second = first + 30;
        int third = second + 30;

        int averageTwo = (first + second) / 2;
        int averageThree = (first + second + third) / 3;

        questions.add(new Question(
                "Автомобиль двигался три часа "
                        + "без остановок.\n\n"
                        + "За первый час его средняя скорость "
                        + "составила " + first + " км/ч.\n"
                        + "Средняя скорость за первые два "
                        + "часа равнялась " + averageTwo
                        + " км/ч, а за все три часа — "
                        + averageThree + " км/ч.\n\n"
                        + "Найдите среднюю скорость "
                        + "за третий час.",
                "км/ч", third));

        // 6. Неизвестная скорость первой половины пути
        int firstSpeed = 30 * (1 + random.nextInt(4));
        int secondSpeed = 2 * firstSpeed;
        int averageSpeed = 4 * firstSpeed / 3;

        questions.add(new Question(
                "Автомобиль проехал первую половину "
                        + "пути с неизвестной скоростью, "
                        + "а вторую половину — со скоростью "
                        + secondSpeed + " км/ч.\n\n"
                        + "Средняя скорость на всём пути "
                        + "равнялась " + averageSpeed
                        + " км/ч.\n\n"
                        + "Определите скорость автомобиля "
                        + "на первой половине пути.",
                "км/ч", firstSpeed));

        // 7. Три равные части пути
        int v1 = 40 + 10 * random.nextInt(5);
        int v2 = 50 + 10 * random.nextInt(5);
        int v3 = 60 + 10 * random.nextInt(5);

        double avg = 3.0 /
                (1.0 / v1 + 1.0 / v2 + 1.0 / v3);

        questions.add(new Question(
                "Самолёт пролетел первую треть "
                        + "пути со скоростью " + v1
                        + " км/ч, вторую треть — "
                        + v2 + " км/ч, а последнюю треть — "
                        + v3 + " км/ч.\n\n"
                        + "Определите среднюю скорость "
                        + "самолёта на всём пути.",
                "км/ч", avg));

        // 8. Часть пути и часть времени
        int runningSpeed = 9 + 3 * random.nextInt(4);
        int walkingSpeed = 3 + random.nextInt(3);
        int finalSpeed = 6 + random.nextInt(5);

        double wholeAverage =
                (double) runningSpeed
                        * (2 * finalSpeed + walkingSpeed)
                        / (finalSpeed + 2 * runningSpeed);

        questions.add(new Question(
                "Путешественник первую треть "
                        + "всего пути двигался со скоростью "
                        + runningSpeed + " км/ч.\n\n"
                        + "Затем треть всего времени "
                        + "путешествия он шёл со скоростью "
                        + walkingSpeed + " км/ч.\n\n"
                        + "Оставшуюся часть пути он "
                        + "двигался со скоростью "
                        + finalSpeed + " км/ч.\n\n"
                        + "Определите среднюю скорость "
                        + "за всё путешествие.",
                "км/ч", wholeAverage));

        Collections.shuffle(questions, random);
    }

    private void addFormulaQuestion() {
        int type = random.nextInt(3);

        int speed = 2 + random.nextInt(14);
        int time = 10 * (2 + random.nextInt(9));
        int path = speed * time;

        if (type == 0) {
            questions.add(new Question(
                    "Тело прошло " + path
                            + " м за " + time
                            + " с при равномерном движении.\n\n"
                            + "Определите скорость тела.",
                    "м/с",
                    speed));

        } else if (type == 1) {
            questions.add(new Question(
                    "Тело движется равномерно "
                            + "со скоростью " + speed
                            + " м/с в течение "
                            + time + " с.\n\n"
                            + "Определите пройденный путь.",
                    "м",
                    path));

        } else {
            questions.add(new Question(
                    "Тело прошло " + path
                            + " м со скоростью "
                            + speed + " м/с.\n\n"
                            + "Определите время движения.",
                    "с",
                    time));
        }
    }

    // ЭКРАН ЗАДАЧИ

    private void showQuestion() {
        createPage();

        Question question = questions.get(current);

        addText("Механическое движение", 24);
        addText(selectedLevel + " уровень", 20);

        addText("Задание " + (current + 1)
                + " из " + questions.size(), 20);

        addText(question.text, 22);

        addButton("📖 Справочные материалы", () -> {
            PhysicsReferenceDialog.show(this);
        });


        addText("Ответ в " + question.unit + ":", 18);

        answerInput = addAnswerField();

        addText("При необходимости округлите "
                        + "окончательный ответ до десятых.",
                16);

        addButton(current == questions.size() - 1
                        ? "Завершить тренировку"
                        : "Следующее задание",
                this::saveAnswer);
    }

    private void saveAnswer() {
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
            answerInput.setError("Введите положительное число");
            return;
        }

        studentAnswers.add(value);
        current++;

        if (current < questions.size()) {
            showQuestion();
        } else {
            showResults();
        }
    }

    // РЕЗУЛЬТАТЫ ТРЕНИРОВКИ

    private void showResults() {
        createPage();

        int correct = 0;

        for (int i = 0; i < questions.size(); i++) {

            boolean answerIsCorrect =
                    PhysicsAnswerUtils.isCorrect(
                            String.valueOf(studentAnswers.get(i)),
                            questions.get(i).answer
                    );

            if (answerIsCorrect) {
                correct++;
            }

        }

        addText("Тренировка завершена!", 27);
        addText("Правильных ответов: "
                + correct + " из " + questions.size(), 23);

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);


            double expected = roundAnswer(question.answer);
            double given = studentAnswers.get(i);

            boolean correctAnswer =
                    PhysicsAnswerUtils.isCorrect(
                            String.valueOf(given),
                            question.answer
                    );


            addText("Задание " + (i + 1) + "\n"
                            + (correctAnswer ? "✓ Верно" : "✗ Ошибка")
                            + "\nВаш ответ: " + fmt(given)
                            + " " + question.unit
                            + "\nПравильный ответ: "
                            + fmt(expected) + " " + question.unit,
                    18);
        }


        addButton("Новая тренировка", () -> {
            if (selectedLevel == 4) {
                generateLevelFourQuestions();
            } else if (selectedLevel == 3) {
                generateLevelThreeQuestions();
            } else if (selectedLevel == 2) {
                generateLevelTwoQuestions();
            } else {
                generateLevelOneQuestions();
            }
            showQuestion();
        });



    }
}
