
package com.example.mathtrainer;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class MotionTheoryActivity extends AppCompatActivity {

    private LinearLayout page;
    private int currentSlide = 0;

    private final String[] titles = {
            "Механическое движение",
            "Траектория и путь",
            "Равномерное и неравномерное движение",
            "Скорость движения",
            "Путь и время",
            "Перевод единиц измерения",
            "Средняя скорость",
            "Главное, что нужно запомнить"
    };

    private final String[] contents = {
            "Механическое движение — изменение положения тела относительно других тел с течением времени.\n\n"
                    + "Примеры:\n"
                    + "• автомобиль движется по дороге;\n"
                    + "• человек идёт по улице;\n"
                    + "• самолёт летит над городом.\n\n"
                    + "Тело отсчёта — тело, относительно которого рассматривается движение.\n\n"
                    + "Одно и то же тело может двигаться относительно одного тела отсчёта и покоиться относительно другого.",

            "Траектория — линия, которую описывает тело при движении.\n\n"
                    + "Путь — длина траектории.\n\n"
                    + "Обозначение: s\n"
                    + "Единица СИ: метр (м).\n\n"
                    + "Виды движения по форме траектории:\n"
                    + "• прямолинейное;\n"
                    + "• криволинейное.\n\n"
                    + "Пример: при движении по извилистой дороге путь равен длине пройденного участка дороги.",

            "Равномерное движение — движение, при котором тело за любые равные промежутки времени проходит одинаковые пути.\n\n"
                    + "Неравномерное движение — движение, при котором тело за равные промежутки времени проходит разные пути.\n\n"
                    + "Пример равномерного движения: автомобиль едет по прямой дороге с постоянной скоростью.\n\n"
                    + "Пример неравномерного движения: автобус разгоняется, тормозит и останавливается.",

            "Скорость показывает, какой путь проходит тело за единицу времени при равномерном движении.\n\n"
                    + "Формула:\n\n"
                    + "v = s / t\n\n"
                    + "v — скорость\n"
                    + "s — путь\n"
                    + "t — время\n\n"
                    + "Единица скорости в СИ — м/с.\n\n"
                    + "Пример:\n"
                    + "Пешеход прошёл 120 м за 60 с.\n"
                    + "v = 120 / 60 = 2 м/с.",

            "Если известны скорость и время:\n\n"
                    + "s = v × t\n\n"
                    + "Если известны путь и скорость:\n\n"
                    + "t = s / v\n\n"
                    + "Пример 1:\n"
                    + "Велосипедист движется со скоростью 5 м/с в течение 20 с.\n"
                    + "s = 5 × 20 = 100 м.\n\n"
                    + "Пример 2:\n"
                    + "Автомобиль проехал 180 км со скоростью 60 км/ч.\n"
                    + "t = 180 / 60 = 3 ч.",

            "Основные единицы:\n\n"
                    + "1 км = 10³ м = 1000 м\n"
                    + "1 см = 10⁻² м\n"
                    + "1 мм = 10⁻³ м\n"
                    + "1 мин = 60 с\n"
                    + "1 ч = 3600 с\n\n"
                    + "Из км/ч в м/с:\n"
                    + "УМНОЖАЕМ НА 10/36.\n\n"
                    + "Пример:\n"
                    + "72 км/ч × 10/36 = 20 м/с.\n\n"
                    + "Из м/с в км/ч:\n"
                    + "УМНОЖАЕМ НА 36/10.\n\n"
                    + "Пример:\n"
                    + "5 м/с × 36/10 = 18 км/ч.\n\n"
                    + "Почему так?\n"
                    + "1 км/ч = 1000 м / 3600 с = 10/36 м/с.",

            "При неравномерном движении используют среднюю скорость.\n\n"
                    + "Формула:\n\n"
                    + "vср = sобщ / tобщ\n\n"
                    + "sобщ — весь пройденный путь\n"
                    + "tобщ — всё затраченное время.\n\n"
                    + "Пример:\n"
                    + "Автобус проехал 60 км за 1 ч, затем 40 км за 1 ч.\n\n"
                    + "Общий путь: 100 км.\n"
                    + "Общее время: 2 ч.\n"
                    + "Средняя скорость: 50 км/ч.\n\n"
                    + "Если учитывается остановка, её время также входит в общее время.",

            "МЕХАНИЧЕСКОЕ ДВИЖЕНИЕ\n"
                    + "Изменение положения тела относительно других тел.\n\n"
                    + "ПУТЬ\n"
                    + "s — измеряется в метрах.\n\n"
                    + "СКОРОСТЬ\n"
                    + "v = s / t\n\n"
                    + "ПУТЬ\n"
                    + "s = v × t\n\n"
                    + "ВРЕМЯ\n"
                    + "t = s / v\n\n"
                    + "СРЕДНЯЯ СКОРОСТЬ\n"
                    + "vср = sобщ / tобщ\n\n"
                    + "КМ/Ч → М/С\n"
                    + "Умножить на 10/36.\n\n"
                    + "М/С → КМ/Ч\n"
                    + "Умножить на 36/10.\n\n"
                    + "Видеоурок на Rutube добавим позднее."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showSlide();
    }

    private int dp(int value) {
        return (int) (value *
                getResources().getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable roundedBackground(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(24));
        return drawable;
    }

    private void showSlide() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundResource(R.drawable.equations_bg);

        page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(20), dp(25), dp(20), dp(30));

        scrollView.addView(page);
        setContentView(scrollView);

        TextView heading = new TextView(this);
        heading.setText("ФИЗИКА · 7 КЛАСС");
        heading.setTextSize(17);
        heading.setTextColor(Color.rgb(18, 100, 77));
        heading.setGravity(Gravity.CENTER);
        page.addView(heading);

        TextView progress = new TextView(this);
        progress.setText("Слайд " + (currentSlide + 1)
                + " из " + titles.length);
        progress.setTextSize(16);
        progress.setTextColor(Color.DKGRAY);
        progress.setGravity(Gravity.CENTER);
        progress.setPadding(0, dp(12), 0, dp(15));
        page.addView(progress);

        LinearLayout contentCard = new LinearLayout(this);
        contentCard.setOrientation(LinearLayout.VERTICAL);
        contentCard.setPadding(
                dp(20), dp(18), dp(20), dp(22));
        contentCard.setBackground(
                roundedBackground(Color.WHITE));

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, dp(20));
        page.addView(contentCard, cardParams);

        TextView title = new TextView(this);
        title.setText(titles[currentSlide]);
        title.setTextSize(24);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.rgb(16, 99, 75));
        title.setPadding(0, 0, 0, dp(20));
        contentCard.addView(title);

        TextView body = new TextView(this);
        body.setText(contents[currentSlide]);
        body.setTextSize(19);
        body.setTextColor(Color.BLACK);
        body.setLineSpacing(dp(4), 1.05f);
        contentCard.addView(body);

        if (currentSlide > 0) {
            addButton("← Назад", () -> {
                currentSlide--;
                showSlide();
            });
        }

        if (currentSlide < titles.length - 1) {
            addButton("Далее →", () -> {
                currentSlide++;
                showSlide();
            });
        } else {
            addButton("Завершить изучение", this::finish);
        }

        addButton("Вернуться к теме", this::finish);
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
                        dp(56));
        params.setMargins(0, 0, 0, dp(12));

        page.addView(button, params);
        button.setOnClickListener(v -> action.run());
    }
}
