
package com.example.mathtrainer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;

public class SchoolHomeActivity extends AppCompatActivity {

    private LinearLayout page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHome();
    }

    private int dp(int value) {
        return (int) (value *
                getResources().getDisplayMetrics().density + 0.5f);
    }

    private void createPage() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundResource(R.drawable.equations_bg);
        scroll.setFillViewport(true);

        page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(24), dp(28), dp(24), dp(28));

        scroll.addView(page);
        setContentView(scroll);
    }

    private void addTitle(String value) {
        TextView title = new TextView(this);
        title.setText(value);
        title.setTextSize(31);
        title.setTextColor(Color.rgb(12, 85, 66));
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, dp(12), 0, dp(25));

        title.setTypeface(ResourcesCompat.getFont(
                this, R.font.ruslandisplay));

        page.addView(title);
    }

    private void addText(String value) {
        TextView message = new TextView(this);
        message.setText(value);
        message.setTextSize(20);
        message.setTextColor(Color.rgb(12, 85, 66));
        message.setGravity(Gravity.CENTER);
        message.setPadding(0, dp(15), 0, dp(25));
        page.addView(message);
    }

    private void addButton(String label, Runnable action) {
        AppCompatButton button = new AppCompatButton(this);

        button.setText(label);
        button.setTextSize(19);
        button.setTextColor(Color.WHITE);
        button.setAllCaps(false);
        button.setBackgroundResource(
                R.drawable.button_green_gradient);
        button.setBackgroundTintList(null);
        button.setElevation(dp(8));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(58));

        params.setMargins(0, 0, 0, dp(14));
        page.addView(button, params);

        button.setOnClickListener(v -> action.run());
    }

    // ГЛАВНАЯ СТРАНИЦА

    private void showHome() {
        createPage();
        addTitle("Матемагия");
        addText("Выбери свой класс");

        for (int grade = 5; grade <= 11; grade++) {
            final int selectedGrade = grade;
            addButton(grade + " класс",
                    () -> showGrade(selectedGrade));
        }

        addButton("Мои результаты",
                () -> startActivity(new Intent(
                        this, ResultsActivity.class)));
    }

    // ВЫБОР ПРЕДМЕТА

    private void showGrade(int grade) {
        createPage();
        addTitle(grade + " класс");
        addText("Выбери предмет");

        String[] subjects;

        if (grade <= 6) {
            subjects = new String[]{
                    "Математика",
                    "Русский язык",
                    "Программирование"
            };
        } else {
            subjects = new String[]{
                    grade >= 10
                            ? "Алгебра и начала анализа"
                            : "Алгебра",
                    "Геометрия",
                    "Вероятность и статистика",
                    "Физика",
                    "Русский язык",
                    "Программирование"
            };
        }

        for (String subject : subjects) {
            addButton(subject,
                    () -> showSection(grade, subject));
        }

        addButton("Все действующие тренажёры",
                () -> startActivity(new Intent(
                        this, LevelSelectorActivity.class)));

        addButton("Назад к классам", this::showHome);
    }

    // ОГЛАВЛЕНИЕ ПРЕДМЕТА

    private void showSection(int grade, String subject) {
        createPage();
        addTitle(subject);
        addText(grade + " класс\nВыбери тему");

        String[] topics = getTopics(grade, subject);

        for (int i = 0; i < topics.length; i++) {
            final String selectedTopic = topics[i];

            addButton((i + 1) + ". " + selectedTopic,
                    () -> showTopic(grade, subject,
                            selectedTopic));
        }

        addButton("Назад к предметам",
                () -> showGrade(grade));
    }

    // СТРАНИЦА ВЫБРАННОЙ ТЕМЫ


    private void showTopic(int grade,
                           String subject, String topic) {
        createPage();

        addTitle(topic);
        addText(subject + " · " + grade + " класс");


        if (grade == 7
                && subject.equals("Физика")

                && topic.toLowerCase().contains("механическое движение")) {


            addButton("Изучить теорию", () ->
                    startActivity(new Intent(
                            this, MotionTheoryActivity.class)));

        } else {

            addButton("Изучить теорию",
                    () -> showTopicMode(
                            grade, subject, topic, "Теория"));
        }


        if (grade == 7
                && subject.equals("Физика")
                && topic.toLowerCase().contains("механическое движение")) {

            addButton("Потренироваться", () ->
                    startActivity(new Intent(
                            this, MotionTrainingActivity.class)));

        } else {

            addButton("Потренироваться",
                    () -> showTopicMode(
                            grade, subject, topic, "Тренировка"));
        }


        if (grade == 7
                && subject.equals("Физика")
                && topic.toLowerCase().contains("механическое движение")) {

            addButton("Пройти тест", () ->
                    startActivity(new Intent(
                            this, MotionTestActivity.class)));

        } else {

            addButton("Пройти тест",
                    () -> showTopicMode(
                            grade, subject, topic, "Тест"));
        }


        if (grade == 7
                && subject.equals("Физика")
                && topic.equals("Масса и плотность")) {

            addButton("Самостоятельная работа", () -> {
                startActivity(new Intent(
                        this, DensityControlActivity.class));
            });
        }

        if (grade == 7
                && subject.equals("Физика")
                && topic.toLowerCase().contains("механическое движение")) {

            addButton("Самостоятельная работа", () -> {
                startActivity(new Intent(
                        this, MotionControlActivity.class));
            });
        }




        addButton("Назад к оглавлению",
                () -> showSection(grade, subject));
    }

    private void showTopicMode(int grade,
                               String subject,
                               String topic,
                               String mode) {
        if (grade == 7
                && subject.equals("Физика")
                && topic.equals("Масса и плотность")
                && mode.equals("Теория")) {
            showDensityTheory(0);
            return;
        }
        if (grade == 7
                && subject.equals("Физика")
                && topic.equals("Масса и плотность")
                && mode.equals("Тренировка")) {

            startActivity(new Intent(
                    this, DensityTrainingActivity.class));
            return;
        }

        createPage();

        addTitle(mode);
        addText(grade + " класс\n" +
                subject + "\n\n" + topic);

        addText("Материалы этого раздела " +
                "скоро появятся.");

        addButton("Назад к теме",
                () -> showTopic(grade, subject, topic));
    }



    private void showDensityTheory(int slide) {
        createPage();

        addTitle("Масса и плотность");
        addText("Физика · 7 класс\n"
                + "Слайд " + (slide + 1) + " из 7");

        switch (slide) {

            case 0:
                addText("ОСНОВНЫЕ ПОНЯТИЯ");

                addText("Масса (m) — физическая величина, "
                        + "характеризующая инертность тела.");

                addText("Объём (V) — величина, показывающая, "
                        + "сколько пространства занимает тело.");

                addText("Плотность (ρ) — отношение "
                        + "массы тела к его объёму.");
                break;

            case 1:
                addText("ФОРМУЛЫ");

                addText("Плотность:\nρ = m / V");

                addText("Масса:\nm = ρ · V");

                addText("Объём:\nV = m / ρ");

                addText("ρ — плотность\n"
                        + "m — масса\n"
                        + "V — объём");
                break;

            case 2:
                addText("ЕДИНИЦЫ ИЗМЕРЕНИЯ");

                addText("Масса (m)\n"
                        + "Основная единица СИ: кг\n"
                        + "Другие: г, мг, т");

                addText("Объём (V)\n"
                        + "Основная единица СИ: м³\n"
                        + "Другие: дм³, см³, л, мл");

                addText("Плотность (ρ)\n"
                        + "Основная единица СИ: кг/м³\n"
                        + "Другие: г/см³, г/мл, кг/л");

                addText("1 кг/м³ означает, что "
                        + "масса 1 м³ вещества равна 1 кг.");
                break;

            case 3:
                addText("ПЕРЕВОД ЕДИНИЦ");

                addText("МАССА\n"
                        + "1 т = 10³ кг\n"
                        + "1 кг = 10³ г\n"
                        + "1 г = 10⁻³ кг\n"
                        + "1 мг = 10⁻⁶ кг");

                addText("ОБЪЁМ\n"
                        + "1 м³ = 10³ дм³\n"
                        + "1 дм³ = 10⁻³ м³\n"
                        + "1 см³ = 10⁻⁶ м³\n"
                        + "1 л = 10⁻³ м³\n"
                        + "1 мл = 10⁻⁶ м³");

                addText("ПЛОТНОСТЬ\n"
                        + "1 г/см³ = 10³ кг/м³\n"
                        + "1 кг/м³ = 10⁻³ г/см³\n"
                        + "1 кг/л = 10³ кг/м³");

                addText("СТЕПЕНИ ЧИСЛА 10\n"
                        + "10³ = 1000\n"
                        + "10⁻³ = 0,001\n"
                        + "10⁻⁶ = 0,000001");

                addText("Важно!\n"
                        + "1 см = 10⁻² м\n"
                        + "1 см³ = (10⁻²)³ м³ = 10⁻⁶ м³");
                break;

            case 4:
                addText("ПЛОТНОСТЬ ЖИДКОСТЕЙ");
                addText("Приблизительно, кг/м³");

                addDensityTable(new String[][]{
                        {"Вода (4 °C)", "1000"},
                        {"Морская вода", "1025"},
                        {"Молоко", "1030"},
                        {"Подсолнечное масло", "920"},
                        {"Этиловый спирт", "789"},
                        {"Глицерин", "1260"},
                        {"Ртуть", "13550"},
                        {"Бензин", "700–780"}
                });

                addText("Плотность жидкостей зависит "
                        + "от температуры и состава.");
                break;

            case 5:
                addText("ПЛОТНОСТЬ ТВЁРДЫХ ТЕЛ");
                addText("Приблизительно, кг/м³");

                addDensityTable(new String[][]{
                        {"Лёд (0 °C)", "917"},
                        {"Алюминий", "2700"},
                        {"Железо", "7870"},
                        {"Сталь", "7850"},
                        {"Медь", "8960"},
                        {"Свинец", "11340"},
                        {"Золото", "19320"},
                        {"Стекло", "2500"},
                        {"Кирпич", "1600–1900"},
                        {"Бетон", "2200–2500"},
                        {"Сосна (сухая)", "400–550"},
                        {"Пробка", "200–250"}
                });

                addText("У разных видов древесины "
                        + "и строительных материалов "
                        + "плотность может различаться.");
                break;

            case 6:
                addText("ПЛОТНОСТЬ ГАЗОВ");
                addText("Приблизительно при 20 °C "
                        + "и давлении 101 кПа, кг/м³");

                addDensityTable(new String[][]{
                        {"Воздух", "1,20"},
                        {"Кислород", "1,33"},
                        {"Азот", "1,17"},
                        {"Углекислый газ", "1,84"},
                        {"Водород", "0,084"},
                        {"Гелий", "0,166"},
                        {"Метан", "0,67"}
                });

                addText("Плотность газов существенно "
                        + "зависит от температуры "
                        + "и давления.");
                break;
        }

        // КНОПКИ ПЕРЕЛИСТЫВАНИЯ

        if (slide > 0) {
            addButton("← Предыдущий слайд",
                    () -> showDensityTheory(slide - 1));
        }

        if (slide < 6) {
            addButton("Следующий слайд →",
                    () -> showDensityTheory(slide + 1));
        }

        // МЕСТО ДЛЯ ССЫЛКИ НА RUTUBE

        if (slide == 6) {
            String rutubeUrl = "";

            if (!rutubeUrl.isEmpty()) {
                addButton("Смотреть видео на RUTUBE",
                        () -> {
                            Intent intent = new Intent(
                                    Intent.ACTION_VIEW,
                                    android.net.Uri.parse(rutubeUrl));
                            startActivity(intent);
                        });
            } else {
                addText("Видеоурок на RUTUBE\n"
                        + "Ссылка будет добавлена позже.");
            }
        }

        addButton("Назад к теме",
                () -> showTopic(
                        7, "Физика", "Масса и плотность"));
    }


// ТАБЛИЦА ПЛОТНОСТЕЙ

    private void addDensityTable(String[][] data) {

        addDensityRow("Вещество", "кг/м³", true);

        for (String[] item : data) {
            addDensityRow(item[0], item[1], false);
        }
    }

    private void addDensityRow(String name,
                               String density,
                               boolean header) {

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dp(10), dp(12),
                dp(10), dp(12));

        if (header) {
            row.setBackgroundColor(
                    Color.rgb(24, 112, 86));
        } else {
            row.setBackgroundColor(
                    Color.argb(225, 245, 250, 247));
        }

        TextView nameView = new TextView(this);
        nameView.setText(name);
        nameView.setTextSize(16);
        nameView.setTextColor(
                header ? Color.WHITE : Color.BLACK);

        if (header) {
            nameView.setTypeface(
                    null, android.graphics.Typeface.BOLD);
        }

        TextView densityView = new TextView(this);
        densityView.setText(density);
        densityView.setTextSize(16);
        densityView.setGravity(Gravity.END);
        densityView.setTextColor(
                header ? Color.WHITE : Color.BLACK);

        if (header) {
            densityView.setTypeface(
                    null, android.graphics.Typeface.BOLD);
        }

        row.addView(nameView,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        2f));

        row.addView(densityView,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 0, 0, dp(2));
        page.addView(row, params);
    }

    // СПИСКИ ТЕМ ПО КЛАССАМ И ПРЕДМЕТАМ

    private String[] getTopics(int grade, String subject) {

        switch (subject) {

            case "Математика":
                if (grade == 5) return new String[]{
                        "Натуральные числа",
                        "Действия с натуральными числами",
                        "Числовые выражения",
                        "Буквенные выражения и уравнения",
                        "Делимость чисел",
                        "Обыкновенные дроби",
                        "Десятичные дроби",
                        "Действия с десятичными дробями",
                        "Проценты",
                        "Геометрические фигуры",
                        "Периметр, площадь и объём",
                        "Текстовые задачи"
                };

                return new String[]{
                        "Делимость натуральных чисел",
                        "Обыкновенные дроби",
                        "Отношения и пропорции",
                        "Проценты и задачи на проценты",
                        "Положительные и отрицательные числа",
                        "Модуль числа",
                        "Действия с рациональными числами",
                        "Выражения и уравнения",
                        "Координатная плоскость",
                        "Геометрические фигуры",
                        "Окружность и круг",
                        "Текстовые задачи"
                };

            case "Алгебра":
                switch (grade) {
                    case 7:
                        return new String[]{
                                "Числовые и буквенные выражения",
                                "Уравнения с одной переменной",
                                "Функции и графики",
                                "Линейная функция",
                                "Степень с натуральным показателем",
                                "Одночлены и многочлены",
                                "Формулы сокращённого умножения",
                                "Разложение на множители",
                                "Системы линейных уравнений",
                                "Текстовые задачи"
                        };
                    case 8:
                        return new String[]{
                                "Рациональные выражения",
                                "Алгебраические дроби",
                                "Квадратные корни",
                                "Действительные числа",
                                "Квадратные уравнения",
                                "Теорема Виета",
                                "Дробно-рациональные уравнения",
                                "Неравенства",
                                "Степень с целым показателем",
                                "Функции и графики"
                        };
                    default:
                        return new String[]{
                                "Квадратичная функция",
                                "Степенная функция",
                                "Уравнения и системы уравнений",
                                "Неравенства и их системы",
                                "Рациональные неравенства",
                                "Метод интервалов",
                                "Арифметическая прогрессия",
                                "Геометрическая прогрессия",
                                "Текстовые задачи",
                                "Повторение курса алгебры"
                        };
                }

            case "Алгебра и начала анализа":
                if (grade == 10) return new String[]{
                        "Действительные числа",
                        "Степени и корни",
                        "Степенная функция",
                        "Показательная функция",
                        "Показательные уравнения и неравенства",
                        "Логарифмы",
                        "Логарифмическая функция",
                        "Логарифмические уравнения",
                        "Тригонометрические функции",
                        "Тригонометрические формулы",
                        "Тригонометрические уравнения",
                        "Производная и её смысл",
                        "Правила дифференцирования"
                };

                return new String[]{
                        "Повторение функций",
                        "Производная функции",
                        "Исследование функций",
                        "Наибольшее и наименьшее значения",
                        "Первообразная",
                        "Определённый интеграл",
                        "Площади с помощью интеграла",
                        "Уравнения и неравенства",
                        "Системы уравнений",
                        "Задачи с параметрами",
                        "Повторение курса алгебры",
                        "Подготовка к ЕГЭ"
                };

            case "Геометрия":
                switch (grade) {
                    case 7:
                        return new String[]{
                                "Точки, прямые и отрезки",
                                "Углы",
                                "Треугольники",
                                "Признаки равенства треугольников",
                                "Равнобедренный треугольник",
                                "Параллельные прямые",
                                "Сумма углов треугольника",
                                "Прямоугольные треугольники",
                                "Геометрические построения"
                        };
                    case 8:
                        return new String[]{
                                "Многоугольники",
                                "Параллелограмм",
                                "Прямоугольник, ромб и квадрат",
                                "Трапеция",
                                "Площади фигур",
                                "Теорема Пифагора",
                                "Подобие треугольников",
                                "Признаки подобия",
                                "Окружность",
                                "Касательная и секущая"
                        };
                    case 9:
                        return new String[]{
                                "Векторы",
                                "Координаты на плоскости",
                                "Метод координат",
                                "Синус, косинус и тангенс",
                                "Теорема синусов",
                                "Теорема косинусов",
                                "Решение треугольников",
                                "Правильные многоугольники",
                                "Длина окружности",
                                "Площадь круга",
                                "Геометрические преобразования"
                        };
                    case 10:
                        return new String[]{
                                "Аксиомы стереометрии",
                                "Прямые в пространстве",
                                "Параллельность прямых и плоскостей",
                                "Сечения многогранников",
                                "Перпендикулярность прямых",
                                "Перпендикулярность прямой и плоскости",
                                "Угол между прямой и плоскостью",
                                "Двугранный угол",
                                "Многогранники",
                                "Призма",
                                "Пирамида",
                                "Площади поверхностей"
                        };
                    default:
                        return new String[]{
                                "Векторы в пространстве",
                                "Координаты в пространстве",
                                "Скалярное произведение",
                                "Движения в пространстве",
                                "Цилиндр",
                                "Конус",
                                "Сфера и шар",
                                "Объёмы многогранников",
                                "Объёмы тел вращения",
                                "Площади поверхностей",
                                "Комбинации геометрических тел",
                                "Подготовка к ЕГЭ"
                        };
                }

            case "Вероятность и статистика":
                switch (grade) {
                    case 7:
                        return new String[]{
                                "Сбор и представление данных",
                                "Таблицы и диаграммы",
                                "Числовые и категориальные данные",
                                "Среднее арифметическое",
                                "Медиана, мода и размах",
                                "Случайные события",
                                "Частота и вероятность",
                                "Диаграммы Эйлера — Венна"
                        };
                    case 8:
                        return new String[]{
                                "Описательная статистика",
                                "Диаграммы и графики",
                                "Рассеивание данных",
                                "Случайные опыты",
                                "Элементарные исходы",
                                "Вероятность события",
                                "Противоположные события",
                                "Объединение и пересечение событий",
                                "Дерево случайного опыта"
                        };
                    case 9:
                        return new String[]{
                                "Комбинаторика",
                                "Правило умножения",
                                "Перестановки и сочетания",
                                "Вероятность случайного события",
                                "Операции над событиями",
                                "Условная вероятность",
                                "Независимые события",
                                "Случайные величины",
                                "Математическое ожидание"
                        };
                    case 10:
                        return new String[]{
                                "Совокупность и выборка",
                                "Числовые характеристики данных",
                                "Дисперсия и стандартное отклонение",
                                "Комбинаторные правила",
                                "Перестановки, размещения, сочетания",
                                "Условная вероятность",
                                "Формула полной вероятности",
                                "Независимые события",
                                "Случайные величины"
                        };
                    default:
                        return new String[]{
                                "Повторение теории вероятностей",
                                "Дискретные случайные величины",
                                "Математическое ожидание",
                                "Дисперсия",
                                "Биномиальное распределение",
                                "Закон больших чисел",
                                "Непрерывные случайные величины",
                                "Нормальное распределение",
                                "Статистические исследования",
                                "Практические задачи"
                        };
                }

            case "Физика":
                switch (grade) {
                    case 7:
                        return new String[]{
                                "Что изучает физика",
                                "Физические величины и измерения",
                                "Строение вещества",
                                "Механическое движение",
                                "Масса и плотность",
                                "Силы",
                                "Давление",
                                "Работа и мощность",
                                "Кинетическая и потенциальная энергия"
                        };
                    case 8:
                        return new String[]{
                                "Тепловое движение",
                                "Внутренняя энергия",
                                "Способы теплопередачи",
                                "Количество теплоты",
                                "Плавление и кристаллизация",
                                "Испарение и конденсация",
                                "Электризация тел",
                                "Электрический ток",
                                "Сила тока и напряжение",
                                "Сопротивление. Закон Ома",
                                "Работа и мощность тока",
                                "Магнитные явления",
                                "Световые явления"
                        };
                    case 9:
                        return new String[]{
                                "Механическое движение",
                                "Равномерное и равноускоренное движение",
                                "Законы Ньютона",
                                "Закон всемирного тяготения",
                                "Импульс и его сохранение",
                                "Механическая энергия",
                                "Механические колебания",
                                "Механические волны",
                                "Звук",
                                "Электромагнитное поле",
                                "Электромагнитная индукция",
                                "Строение атома и ядра"
                        };
                    case 10:
                        return new String[]{
                                "Физика и методы исследования",
                                "Кинематика",
                                "Законы Ньютона",
                                "Силы в механике",
                                "Законы сохранения в механике",
                                "Молекулярно-кинетическая теория",
                                "Идеальный газ",
                                "Газовые законы",
                                "Термодинамика",
                                "Электрическое поле",
                                "Постоянный электрический ток",
                                "Электрический ток в различных средах"
                        };
                    default:
                        return new String[]{
                                "Магнитное поле",
                                "Электромагнитная индукция",
                                "Самоиндукция",
                                "Механические колебания",
                                "Электромагнитные колебания",
                                "Электромагнитные волны",
                                "Геометрическая оптика",
                                "Волновая оптика",
                                "Специальная теория относительности",
                                "Квантовая физика",
                                "Атомная физика",
                                "Ядерная физика",
                                "Повторение и подготовка к ЕГЭ"
                        };
                }

            case "Русский язык":
                switch (grade) {
                    case 5:
                        return new String[]{
                                "Фонетика и графика",
                                "Орфография",
                                "Лексика",
                                "Морфемика",
                                "Имя существительное",
                                "Имя прилагательное",
                                "Глагол",
                                "Словосочетание и предложение",
                                "Главные и второстепенные члены",
                                "Однородные члены предложения",
                                "Прямая речь",
                                "Развитие речи"
                        };
                    case 6:
                        return new String[]{
                                "Лексика и фразеология",
                                "Словообразование",
                                "Орфография",
                                "Имя существительное",
                                "Имя прилагательное",
                                "Имя числительное",
                                "Местоимение",
                                "Глагол",
                                "Морфологический разбор",
                                "Текст и стили речи"
                        };
                    case 7:
                        return new String[]{
                                "Повторение орфографии",
                                "Причастие",
                                "Причастный оборот",
                                "Деепричастие",
                                "Деепричастный оборот",
                                "Наречие",
                                "Категория состояния",
                                "Предлог",
                                "Союз",
                                "Частица",
                                "Междометие",
                                "Развитие речи"
                        };
                    case 8:
                        return new String[]{
                                "Словосочетание",
                                "Простое предложение",
                                "Главные члены предложения",
                                "Второстепенные члены",
                                "Односоставные предложения",
                                "Однородные члены",
                                "Обособленные определения",
                                "Обособленные обстоятельства",
                                "Вводные слова",
                                "Обращения",
                                "Чужая речь"
                        };
                    case 9:
                        return new String[]{
                                "Сложное предложение",
                                "Сложносочинённое предложение",
                                "Сложноподчинённое предложение",
                                "Виды придаточных",
                                "Бессоюзное сложное предложение",
                                "Сложные предложения разных видов",
                                "Пунктуация",
                                "Текст и средства связи",
                                "Изложение",
                                "Сочинение-рассуждение",
                                "Подготовка к ОГЭ"
                        };
                    case 10:
                        return new String[]{
                                "Язык и речь",
                                "Лексика и фразеология",
                                "Фонетика и орфоэпия",
                                "Морфемика и словообразование",
                                "Морфология",
                                "Орфография",
                                "Правописание корней",
                                "Правописание приставок",
                                "Правописание суффиксов",
                                "Правописание окончаний",
                                "Стили речи",
                                "Анализ текста"
                        };
                    default:
                        return new String[]{
                                "Повторение орфографии",
                                "Синтаксис простого предложения",
                                "Однородные члены",
                                "Обособленные члены",
                                "Вводные слова и обращения",
                                "Сложные предложения",
                                "Пунктуация",
                                "Смысловой анализ текста",
                                "Средства выразительности",
                                "Лексические нормы",
                                "Грамматические нормы",
                                "Сочинение ЕГЭ",
                                "Подготовка к ЕГЭ"
                        };
                }

            case "Программирование":
                switch (grade) {
                    case 5:
                        return new String[]{
                                "Что такое алгоритм",
                                "Исполнитель и команды",
                                "Линейные алгоритмы",
                                "Блок-схемы",
                                "Циклы",
                                "Условия",
                                "Основы Scratch",
                                "Создание первой программы"
                        };
                    case 6:
                        return new String[]{
                                "Алгоритмы и исполнители",
                                "Переменные",
                                "Условия и ветвления",
                                "Циклы",
                                "События",
                                "Координаты объектов",
                                "Scratch: игры и анимации",
                                "Создание собственного проекта"
                        };
                    case 7:
                        return new String[]{
                                "Введение в программирование",
                                "Знакомство с Python",
                                "Ввод и вывод данных",
                                "Переменные и типы данных",
                                "Арифметические операции",
                                "Условный оператор",
                                "Циклы",
                                "Простые программы"
                        };
                    case 8:
                        return new String[]{
                                "Повторение Python",
                                "Логические выражения",
                                "Вложенные условия",
                                "Циклы for и while",
                                "Строки",
                                "Списки",
                                "Функции",
                                "Алгоритмы обработки данных",
                                "Практические задачи"
                        };
                    case 9:
                        return new String[]{
                                "Алгоритмы и их сложность",
                                "Ветвления и циклы",
                                "Строки и списки",
                                "Массивы",
                                "Поиск и сортировка",
                                "Функции",
                                "Обработка файлов",
                                "Решение задач ОГЭ",
                                "Программный проект"
                        };
                    case 10:
                        return new String[]{
                                "Основы алгоритмизации",
                                "Типы данных",
                                "Условия и циклы",
                                "Функции",
                                "Рекурсия",
                                "Списки и двумерные массивы",
                                "Строки",
                                "Алгоритмы поиска",
                                "Алгоритмы сортировки",
                                "Работа с файлами",
                                "Практические проекты"
                        };
                    default:
                        return new String[]{
                                "Повторение Python",
                                "Алгоритмическая сложность",
                                "Рекурсивные алгоритмы",
                                "Обработка массивов",
                                "Строковые алгоритмы",
                                "Словари и множества",
                                "Динамическое программирование",
                                "Алгоритмы на графах",
                                "Обработка файлов",
                                "Решение задач ЕГЭ",
                                "Итоговый проект"
                        };
                }
        }

        return new String[]{"Темы будут добавлены"};
    }
}
