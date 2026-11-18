
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
            selectedLevel = 2;
            generateLevelTwoQuestions();
            showQuestion();
        });



        addButton("3 уровень — Составные задачи", () -> {
            selectedLevel = 3;
            generateLevelThreeQuestions();
            showQuestion();
        });


        addButton("4 уровень — Сложные задачи", () -> {
            selectedLevel = 4;
            generateLevelFourQuestions();
            showQuestion();
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

    private void generateLevelFourQuestions() {

        questions.clear();
        answers.clear();
        current = 0;
        correct = 0;

        int index;
        double density;
        double volume;
        double mass;
        String material;
        String text;

        // ЗАДАЧА 1. ПОЛЫЙ МЕТАЛЛИЧЕСКИЙ БЛОК

        index = random.nextInt(5);
        material = SUBSTANCES[index];
        density = DENSITIES[index];

        int a = 20 + 2 * random.nextInt(6);
        int b = 14 + 2 * random.nextInt(5);
        int c = 10 + 2 * random.nextInt(5);
        int wall = 1 + random.nextInt(2);

        double outer = a * b * c;
        double inner = (a - 2 * wall)
                * (b - 2 * wall)
                * (c - 2 * wall);

        mass = density * (outer - inner) / 1000000.0;

        text = "Из " + material
                + " изготовили полую деталь "
                + "в форме прямоугольного "
                + "параллелепипеда.\n\n"
                + "Внешние размеры: "
                + a + " × " + b + " × " + c
                + " см.\n"
                + "Толщина всех стенок: "
                + wall + " см.\n\n"
                + "Полость полностью закрыта.\n\n"
                + "Определите массу детали "
                + "в килограммах.";

        questions.add(new Question(text, "кг", mass));


        // ЗАДАЧА 2. МАССА МЕТАЛЛИЧЕСКОЙ ТРУБЫ

        index = random.nextInt(5);
        material = SUBSTANCES[index];
        density = DENSITIES[index] / 1000.0;

        int radiusOuter = 4 + random.nextInt(4);
        int radiusInner = 1 + random.nextInt(3);
        int length = 20 + 10 * random.nextInt(5);

        volume = 3.14
                * (radiusOuter * radiusOuter
                - radiusInner * radiusInner)
                * length;

        mass = volume * density;

        text = "Из " + material
                + " изготовили трубу длиной "
                + length + " см.\n\n"
                + "Внешний радиус трубы "
                + radiusOuter + " см, "
                + "внутренний радиус "
                + radiusInner + " см.\n\n"
                + "Определите массу трубы "
                + "в граммах.\n"
                + "Примите π = 3,14.";

        questions.add(new Question(text, "г", mass));


        // ЗАДАЧА 3. СРЕДНЯЯ ПЛОТНОСТЬ ДЕТАЛИ

        int aluminumV =
                100 + 50 * random.nextInt(7);

        int copperV =
                50 + 25 * random.nextInt(9);

        double aluminumM =
                DENSITIES[0] * aluminumV / 1000.0;

        double copperM =
                DENSITIES[2] * copperV / 1000.0;

        double totalV = aluminumV + copperV;
        double totalM = aluminumM + copperM;

        density = totalM / totalV;

        text = "Составная деталь состоит "
                + "из двух сплошных частей.\n\n"
                + "Объём алюминиевой части: "
                + aluminumV + " см³.\n"
                + "Объём медной части: "
                + copperV + " см³.\n\n"
                + "Между частями нет пустот.\n\n"
                + "Определите среднюю плотность "
                + "детали в г/см³.";

        questions.add(new Question(
                text, "г/см³", density));


        // ЗАДАЧА 4. ЗАМЕНА МАТЕРИАЛА

        int bodyVolume =
                200 + 100 * random.nextInt(9);

        double massAl =
                DENSITIES[0] * bodyVolume / 1000000.0;

        double massCu =
                DENSITIES[2] * bodyVolume / 1000000.0;

        double difference = massCu - massAl;

        text = "Сплошную алюминиевую деталь "
                + "объёмом " + bodyVolume
                + " см³ заменили медной деталью "
                + "такой же формы и объёма.\n\n"
                + "На сколько килограммов "
                + "увеличилась масса детали?";

        questions.add(new Question(
                text, "кг", difference));


        // ЗАДАЧА 5. ДВЕ ЖИДКОСТИ В СОСУДЕ

        int waterL = 1 + random.nextInt(5);
        int oilL = 1 + random.nextInt(5);

        double waterM =
                DENSITIES[5] * waterL / 1000.0;

        double oilM =
                DENSITIES[7] * oilL / 1000.0;

        density = (waterM + oilM)
                / (waterL + oilL);

        text = "В сосуд налили "
                + waterL + " л воды и "
                + oilL + " л растительного масла.\n\n"
                + "Жидкости образуют два слоя, "
                + "их объёмы складываются.\n\n"
                + "Определите среднюю плотность "
                + "содержимого сосуда "
                + "в г/см³.\n"
                + "Массой сосуда пренебречь.";

        questions.add(new Question(
                text, "г/см³", density));


        // ЗАДАЧА 6. ТЕЛО В СОСУДЕ С ЖИДКОСТЬЮ

        int capacity = 2 + random.nextInt(5);

        int blockA = 5 + random.nextInt(6);
        int blockB = 4 + random.nextInt(5);
        int blockC = 3 + random.nextInt(5);

        double blockVolume =
                blockA * blockB * blockC;

        double liquidVolume =
                capacity * 1000.0 - blockVolume;

        mass = DENSITIES[5]
                * liquidVolume / 1000000.0;

        text = "В сосуд вместимостью "
                + capacity + " л поместили "
                + "сплошной металлический брусок "
                + "размером "
                + blockA + " × " + blockB
                + " × " + blockC + " см.\n\n"
                + "Брусок полностью погружён "
                + "в воду. После этого сосуд "
                + "заполнили водой до краёв.\n\n"
                + "Определите массу воды "
                + "в сосуде в килограммах.";

        questions.add(new Question(text, "кг", mass));


        // ЗАДАЧА 7. ПЛАСТИНА С ВЫРЕЗОМ

        index = random.nextInt(5);
        material = SUBSTANCES[index];
        density = DENSITIES[index] / 1000.0;

        int plateA = 20 + 5 * random.nextInt(6);
        int plateB = 15 + 5 * random.nextInt(5);
        int thickness = 2 + random.nextInt(5);

        int holeA = 5 + random.nextInt(6);
        int holeB = 4 + random.nextInt(5);

        double fullVolume =
                plateA * plateB * thickness / 10.0;

        double holeVolume =
                holeA * holeB * thickness / 10.0;

        mass = density * (fullVolume - holeVolume);

        text = "Из " + material
                + " изготовили прямоугольную "
                + "пластину размером "
                + plateA + " × " + plateB
                + " см и толщиной "
                + thickness + " мм.\n\n"
                + "В пластине вырезали сквозное "
                + "прямоугольное отверстие "
                + "размером " + holeA + " × "
                + holeB + " см.\n\n"
                + "Определите массу оставшейся "
                + "пластины в граммах.";

        questions.add(new Question(text, "г", mass));


        // ЗАДАЧА 8. СРАВНЕНИЕ ОБЪЁМОВ

        int first = random.nextInt(5);
        int second = random.nextInt(5);

        while (second == first) {
            second = random.nextInt(5);
        }

        int commonMass = 1 + random.nextInt(5);

        double volumeFirst =
                commonMass / DENSITIES[first];

        double volumeSecond =
                commonMass / DENSITIES[second];

        double ratio = volumeFirst / volumeSecond;

        text = "Два сплошных однородных тела "
                + "изготовлены из разных материалов.\n\n"
                + "Первое тело — из "
                + SUBSTANCES[first] + ".\n"
                + "Второе тело — из "
                + SUBSTANCES[second] + ".\n\n"
                + "Масса каждого тела "
                + commonMass + " кг.\n\n"
                + "Во сколько раз объём первого "
                + "тела отличается от объёма "
                + "второго?\n\n"
                + "Ответ запишите как отношение "
                + "объёма первого тела к объёму "
                + "второго.";

        questions.add(new Question(
                text, "раз", ratio));

        Collections.shuffle(questions, random);
    }

    private void generateLevelThreeQuestions() {

        questions.clear();
        answers.clear();
        current = 0;
        correct = 0;

        double volume;
        double mass;
        double density;
        String material;
        String text;

        // ЗАДАЧА 1. ПОЛЫЙ ПРЯМОУГОЛЬНЫЙ БРУСОК

        int index = random.nextInt(5);
        material = SUBSTANCES[index];
        density = DENSITIES[index] / 1000.0;

        int a = 16 + 2 * random.nextInt(8);
        int b = 10 + 2 * random.nextInt(5);
        int c = 8 + 2 * random.nextInt(4);
        int wall = 1 + random.nextInt(2);

        double outerVolume = a * b * c;
        double innerVolume =
                (a - 2 * wall)
                        * (b - 2 * wall)
                        * (c - 2 * wall);

        mass = density * (outerVolume - innerVolume);

        text = "Из " + material
                + " изготовили полый прямоугольный "
                + "брусок с закрытыми стенками.\n\n"
                + "Его внешние размеры: "
                + a + " × " + b + " × " + c
                + " см.\n"
                + "Толщина всех стенок: "
                + wall + " см.\n\n"
                + "Определите массу бруска "
                + "в граммах.";

        questions.add(new Question(text, "г", mass));


        // ЗАДАЧА 2. МЕТАЛЛИЧЕСКАЯ ТРУБА

        index = random.nextInt(5);
        material = SUBSTANCES[index];
        density = DENSITIES[index] / 1000.0;

        int outerRadius = 3 + random.nextInt(4);
        int innerRadius = 1 + random.nextInt(
                outerRadius - 1);
        int pipeLength = 20 + 5 * random.nextInt(9);

        volume = 3.14
                * (outerRadius * outerRadius
                - innerRadius * innerRadius)
                * pipeLength;

        mass = density * volume;

        text = "Однородная труба из "
                + material
                + " имеет длину " + pipeLength
                + " см.\n\n"
                + "Внешний радиус трубы "
                + outerRadius + " см, "
                + "внутренний — "
                + innerRadius + " см.\n\n"
                + "Определите массу трубы "
                + "в граммах.\n"
                + "Примите π = 3,14.";

        questions.add(new Question(text, "г", mass));


        // ЗАДАЧА 3. ДЕТАЛЬ ИЗ ДВУХ МАТЕРИАЛОВ

        int aluminumVolume =
                100 + 50 * random.nextInt(9);

        int copperVolume =
                50 + 25 * random.nextInt(9);

        double aluminumMass =
                DENSITIES[0] * aluminumVolume / 1000.0;

        double copperMass =
                DENSITIES[2] * copperVolume / 1000.0;

        mass = (aluminumMass + copperMass) / 1000.0;

        text = "Деталь состоит из двух "
                + "сплошных частей: алюминиевой "
                + "и медной.\n\n"
                + "Объём алюминиевой части "
                + aluminumVolume + " см³.\n"
                + "Объём медной части "
                + copperVolume + " см³.\n\n"
                + "Определите общую массу "
                + "детали в килограммах.";

        questions.add(new Question(text, "кг", mass));


        // ЗАДАЧА 4. СОСУД С ДВУМЯ ЖИДКОСТЯМИ

        int waterLiters = 1 + random.nextInt(5);
        int oilLiters = 1 + random.nextInt(4);

        double vesselMass =
                (5 + random.nextInt(11)) / 10.0;

        double waterMass =
                DENSITIES[5] * waterLiters / 1000.0;

        double oilMass =
                DENSITIES[7] * oilLiters / 1000.0;

        mass = vesselMass + waterMass + oilMass;

        text = "Масса пустого сосуда "
                + format(vesselMass) + " кг.\n\n"
                + "В него налили "
                + waterLiters + " л воды и "
                + oilLiters
                + " л растительного масла.\n\n"
                + "Определите общую массу "
                + "сосуда с жидкостями "
                + "в килограммах.";

        questions.add(new Question(text, "кг", mass));


        // ЗАДАЧА 5. ВЫТЕСНЕНИЕ ВОДЫ

        index = random.nextInt(5);
        density = DENSITIES[index] / 1000.0;

        int initialWater = 100 + 50
                * random.nextInt(5);

        int displacedVolume = 25 + 25
                * random.nextInt(8);

        int finalWater =
                initialWater + displacedVolume;

        mass = density * displacedVolume;

        text = "В мензурке находилось "
                + initialWater + " мл воды.\n\n"
                + "После полного погружения "
                + "сплошной металлической детали "
                + "показание мензурки стало "
                + finalWater + " мл.\n\n"
                + "Масса детали равна "
                + format(mass) + " г.\n\n"
                + "Определите плотность металла "
                + "в г/см³.";

        questions.add(new Question(
                text, "г/см³", density));


        // ЗАДАЧА 6. ОБЪЁМ ЖИДКОСТИ ПО ВЗВЕШИВАНИЮ

        index = 5 + random.nextInt(3);
        material = SUBSTANCES[index];

        int liquidLiters = 1 + random.nextInt(6);

        double emptyMass =
                (4 + random.nextInt(12)) / 10.0;

        double liquidMass =
                DENSITIES[index] * liquidLiters / 1000.0;

        double fullMass = emptyMass + liquidMass;

        text = "Масса пустого сосуда "
                + format(emptyMass) + " кг.\n\n"
                + "После заполнения сосуда "
                + material
                + " его масса стала "
                + format(fullMass) + " кг.\n\n"
                + "Определите объём налитой "
                + "жидкости в литрах.";

        questions.add(new Question(
                text, "л", liquidLiters));


        // ЗАДАЧА 7. ОБРАБОТКА ЗАГОТОВКИ

        index = random.nextInt(5);
        material = SUBSTANCES[index];
        density = DENSITIES[index] / 1000.0;

        a = 15 + 5 * random.nextInt(5);
        b = 8 + 2 * random.nextInt(5);
        c = 6 + 2 * random.nextInt(4);

        int cutA = 2 + random.nextInt(4);
        int cutB = 2 + random.nextInt(3);
        int cutC = 2 + random.nextInt(3);

        double originalVolume = a * b * c;
        double removedVolume = cutA * cutB * cutC;

        mass = density
                * (originalVolume - removedVolume);

        text = "Из сплошной заготовки "
                + "из " + material
                + " размером "
                + a + " × " + b + " × " + c
                + " см удалили часть материала.\n\n"
                + "Удалённая часть имела форму "
                + "прямоугольного параллелепипеда "
                + "размером "
                + cutA + " × " + cutB + " × "
                + cutC + " см.\n\n"
                + "Определите массу оставшейся "
                + "детали в граммах.";

        questions.add(new Question(text, "г", mass));


        // ЗАДАЧА 8. НЕИЗВЕСТНЫЙ РАЗМЕР ПОЛОСТИ

        index = random.nextInt(5);
        material = SUBSTANCES[index];
        density = DENSITIES[index] / 1000.0;

        a = 20 + 2 * random.nextInt(8);
        b = 12 + 2 * random.nextInt(5);
        c = 10 + 2 * random.nextInt(4);

        int cavityA = a - 4;
        int cavityB = b - 4;
        int cavityHeight = 2 + random.nextInt(c - 4);

        outerVolume = a * b * c;
        innerVolume =
                cavityA * cavityB * cavityHeight;

        mass = density * (outerVolume - innerVolume);

        text = "Внутри прямоугольного "
                + "бруска из " + material
                + " имеется закрытая полость "
                + "в форме прямоугольного "
                + "параллелепипеда.\n\n"
                + "Внешние размеры бруска: "
                + a + " × " + b + " × " + c
                + " см.\n\n"
                + "Длина полости " + cavityA
                + " см, ширина " + cavityB
                + " см.\n"
                + "Масса бруска с полостью "
                + format(mass) + " г.\n\n"
                + "Определите высоту полости "
                + "в сантиметрах.";

        questions.add(new Question(
                text, "см", cavityHeight));

        Collections.shuffle(questions, random);
    }

    private void generateLevelTwoQuestions() {

        questions.clear();
        answers.clear();
        current = 0;
        correct = 0;

        // Материалы и плотности берём из существующего справочника.
        // Индексы 0–4 соответствуют твёрдым материалам.

        int index;
        double density;
        double volume;
        double mass;
        String material;
        String text;

        // Задача 1. Прямоугольный брусок

        index = random.nextInt(5);
        density = DENSITIES[index] / 1000.0;
        material = SUBSTANCES[index];

        int length = 10 + random.nextInt(21);
        int width = 3 + random.nextInt(8);
        int height = 2 + random.nextInt(5);

        volume = length * width * height;
        mass = density * volume / 1000.0;

        text = "Сплошной брусок из " + material
                + " имеет длину " + length
                + " см, ширину " + width
                + " см и высоту " + height
                + " см.\n\n"
                + "Определите его массу в килограммах.";

        questions.add(new Question(text, "кг", mass));


        // Задача 2. Пластина

        index = random.nextInt(5);
        density = DENSITIES[index] / 1000.0;
        material = SUBSTANCES[index];

        length = 20 + 5 * random.nextInt(7);
        width = 10 + 5 * random.nextInt(5);
        int thickness = 2 + random.nextInt(5);

        volume = length * width * thickness / 10.0;
        mass = density * volume;

        text = "Однородная пластина из " + material
                + " имеет длину " + length
                + " см, ширину " + width
                + " см и толщину " + thickness
                + " мм.\n\n"
                + "Определите массу пластины в граммах.";

        questions.add(new Question(text, "г", mass));


        // Задача 3. Медная проволока

        int wireLength = 5 + 5 * random.nextInt(6);
        int area = 1 + random.nextInt(5);

        volume = wireLength * area;
        mass = DENSITIES[2] / 1000.0 * volume;

        text = "Медная проволока имеет длину "
                + wireLength + " м и площадь "
                + "поперечного сечения " + area
                + " мм².\n\n"
                + "Определите её массу в граммах.";

        questions.add(new Question(text, "г", mass));


        // Задача 4. Цилиндр

        index = random.nextInt(5);
        density = DENSITIES[index] / 1000.0;
        material = SUBSTANCES[index];

        int radius = 2 + random.nextInt(4);
        int cylinderHeight = 5 + random.nextInt(11);

        volume = 3.14 * radius * radius * cylinderHeight;
        mass = density * volume;

        text = "Сплошной цилиндр из " + material
                + " имеет радиус основания " + radius
                + " см и высоту " + cylinderHeight
                + " см.\n\n"
                + "Определите его массу в граммах.\n"
                + "Примите π = 3,14.";

        questions.add(new Question(text, "г", mass));


        // Задача 5. Несколько одинаковых брусков

        index = random.nextInt(5);
        density = DENSITIES[index] / 1000.0;
        material = SUBSTANCES[index];

        int quantity = 2 + random.nextInt(5);
        length = 10 + 5 * random.nextInt(5);
        width = 4 + random.nextInt(5);
        height = 2 + random.nextInt(4);

        volume = length * width * height;
        mass = quantity * volume * density;

        text = "Из " + material + " изготовили "
                + quantity + " одинаковых сплошных брусков.\n\n"
                + "Размеры каждого: "
                + length + " × " + width + " × "
                + height + " см.\n\n"
                + "Определите общую массу в граммах.";

        questions.add(new Question(text, "г", mass));


        // Задача 6. Определение плотности

        index = random.nextInt(5);
        density = DENSITIES[index] / 1000.0;
        material = SUBSTANCES[index];

        length = 10 + 5 * random.nextInt(5);
        width = 4 + random.nextInt(5);
        height = 2 + random.nextInt(4);

        volume = length * width * height;
        mass = density * volume;

        text = "Однородный образец из " + material
                + " имеет размеры "
                + length + " × " + width
                + " × " + height + " см.\n\n"
                + "Масса образца равна "
                + format(mass) + " г.\n\n"
                + "Определите его плотность в г/см³.";

        questions.add(new Question(text, "г/см³", density));


        // Задача 7. Жидкость и сосуд

        index = 5 + random.nextInt(3);
        material = SUBSTANCES[index];

        int liters = 1 + random.nextInt(5);
        double vesselMass = (2 + random.nextInt(5)) / 10.0;

        double liquidMass =
                DENSITIES[index] * liters / 1000.0;

        double totalMass = vesselMass + liquidMass;

        text = "В сосуд налили " + liters
                + " л " + material + ".\n\n"
                + "Масса пустого сосуда "
                + format(vesselMass) + " кг, "
                + "а масса сосуда с жидкостью "
                + format(totalMass) + " кг.\n\n"
                + "Определите массу жидкости в килограммах.";

        questions.add(new Question(text, "кг", liquidMass));


        // Задача 8. Неизвестная длина бруска

        index = random.nextInt(5);
        density = DENSITIES[index] / 1000.0;
        material = SUBSTANCES[index];

        length = 10 + 5 * random.nextInt(7);
        width = 3 + random.nextInt(6);
        height = 2 + random.nextInt(5);

        volume = length * width * height;
        mass = density * volume;

        text = "Сплошной брусок из " + material
                + " имеет массу "
                + format(mass) + " г.\n\n"
                + "Его ширина " + width
                + " см, высота " + height
                + " см.\n\n"
                + "Определите длину бруска в сантиметрах.";

        questions.add(new Question(text, "см", length));

        Collections.shuffle(questions, random);
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

        if (selectedLevel == 2) {
            addText(
                    "Округлите только окончательный ответ "
                            + "до десятых.\n"
                            + "Пример: 0,23 → 0,2",
                    16);
        }


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

        if (selectedLevel == 2) {

            // Второй уровень: округляем только конечный ответ
            double expected = Math.round(
                    question.result * 10.0) / 10.0;

            if (Math.abs(value - expected) < 0.000001) {
                correct++;
            }

        } else {

            // Первый уровень: сохраняем прежнюю проверку
            double tolerance = Math.max(
                    0.02, Math.abs(question.result) * 0.005);

            if (Math.abs(value - question.result)
                    <= tolerance) {
                correct++;
            }
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

                    + format(selectedLevel == 2
                            ? Math.round(q.result * 10.0) / 10.0
                            : q.result)

                    + " " + q.unit, 16);
        }


        addButton("Новая тренировка", () -> {
            if (selectedLevel == 4) {
                generateLevelFourQuestions();
            } else if (selectedLevel == 3) {
                generateLevelThreeQuestions();
            } else if (selectedLevel == 2) {
                generateLevelTwoQuestions();
            } else {
                generateQuestions();
            }
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

