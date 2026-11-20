
package com.example.mathtrainer;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class PhysicsReferenceDialog {

    private static int dp(Context context, int value) {
        return (int) (
                value * context.getResources()
                        .getDisplayMetrics().density + 0.5f
        );
    }

    private static void addText(
            Context context,
            LinearLayout layout,
            String value,
            int size,
            boolean bold) {

        TextView text = new TextView(context);

        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(Color.rgb(18, 69, 61));

        if (bold) {
            text.setTypeface(null, Typeface.BOLD);
        }

        text.setPadding(
                0, dp(context, 7),
                0, dp(context, 7)
        );

        layout.addView(text);
    }

    public static void show(Context context) {

        ScrollView scroll = new ScrollView(context);
        scroll.setFillViewport(false);

        scroll.setBackgroundColor(
                Color.rgb(245, 255, 250)
        );


        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);

        content.setPadding(
                dp(context, 20),
                dp(context, 12),
                dp(context, 20),
                dp(context, 20)
        );

        scroll.addView(content);

        addText(
                context, content,
                "СПРАВОЧНЫЕ МАТЕРИАЛЫ",
                22, true
        );

        addText(
                context, content,
                "Физика • 7 класс\n"
                        + "Значения используйте в соответствии "
                        + "с условием задачи.",
                15, false
        );

        addText(
                context, content,
                "Ускорение свободного падения",
                19, true
        );

        addText(
                context, content,
                "Земля — 10 Н/кг\n"
                        + "Луна — примерно 1,6 Н/кг\n"
                        + "Марс — примерно 3,7 Н/кг",
                17, false
        );

        addText(
                context, content,
                "Плотность веществ при обычных условиях",
                19, true
        );

        addText(
                context, content,
                "Значения приблизительные, кг/м³.\n\n"
                        + "Вода — 1000\n"
                        + "Морская вода — 1030\n"
                        + "Лёд — 900\n"
                        + "Алюминий — 2700\n"
                        + "Железо — 7800\n"
                        + "Сталь — 7800\n"
                        + "Медь — 8900\n"
                        + "Свинец — 11300\n"
                        + "Золото — 19300\n"
                        + "Стекло — 2500\n"
                        + "Гранит — 2600\n"
                        + "Кирпич — 1800\n"
                        + "Сосна сухая — 400–600\n"
                        + "Воздух — примерно 1,29",
                17, false
        );

        addText(
                context, content,
                "Перевод единиц",
                19, true
        );

        addText(
                context, content,
                "1 км = 1000 м\n"
                        + "1 м = 100 см\n"
                        + "1 см = 0,01 м\n"
                        + "1 ч = 3600 с\n"
                        + "1 мин = 60 с\n"
                        + "1 кг = 1000 г\n"
                        + "1 г = 0,001 кг\n"
                        + "1 г/см³ = 1000 кг/м³",
                17, false
        );

        new AlertDialog.Builder(context)
                .setView(scroll)
                .setPositiveButton(
                        "Закрыть",
                        (dialog, which) -> dialog.dismiss()
                )
                .show()
                .getWindow()
                .setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
    }
}
