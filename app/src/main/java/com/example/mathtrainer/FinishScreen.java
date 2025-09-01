
package com.example.mathtrainer;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public final class FinishScreen {
    private FinishScreen() {}

    public static void show(@NonNull AppCompatActivity a,
                            @NonNull String title,
                            @NonNull String timeText,
                            @NonNull String scoreText,
                            @NonNull String recordsText,
                            @NonNull String buttonText,
                            @NonNull Runnable onNext) {

        LinearLayout root = new LinearLayout(a);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        VideoView vv = new VideoView(a);
        LinearLayout.LayoutParams videoLp =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        vv.setLayoutParams(videoLp);
        Uri uri = Uri.parse("android.resource://" + a.getPackageName() + "/" + R.raw.mal);
        vv.setVideoURI(uri);

        LinearLayout info = new LinearLayout(a);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setPadding(32, 24, 32, 24);
        info.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView congrats = new TextView(a);
        congrats.setText(title);
        congrats.setTextColor(Color.BLACK);
        congrats.setTypeface(Typeface.DEFAULT_BOLD);
        congrats.setTextSize(20f);

        TextView timeTv = new TextView(a);
        timeTv.setText(timeText);
        timeTv.setTextColor(Color.BLACK);
        timeTv.setTextSize(16f);

        TextView scoreTv = new TextView(a);
        scoreTv.setText(scoreText);
        scoreTv.setTextColor(Color.BLACK);
        scoreTv.setTextSize(16f);

        TextView lbTv = new TextView(a);
        lbTv.setText(recordsText);
        lbTv.setTextColor(Color.BLACK);
        lbTv.setTextSize(16f);

        Button nextBtn = new Button(a);
        nextBtn.setText(buttonText);
        nextBtn.setOnClickListener(v -> onNext.run());

        info.addView(congrats);
        info.addView(timeTv);
        info.addView(scoreTv);
        info.addView(lbTv);
        info.addView(nextBtn);

        root.addView(vv);
        root.addView(info);

        a.setContentView(root);
        vv.setOnPreparedListener(mp -> vv.start());
    }
}
