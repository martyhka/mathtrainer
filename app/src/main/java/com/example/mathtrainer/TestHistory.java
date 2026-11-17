
package com.example.mathtrainer;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TestHistory {

    private static final String STORAGE = "test_history";
    private static final String KEY = "completed_tests";

    public static void save(
            Context context,
            String topic,
            int correct,
            int mistakes,
            long durationMillis,
            int quality,
            int speed,
            int completion,
            int points,
            int grade) {

        SharedPreferences preferences =
                context.getSharedPreferences(
                        STORAGE, Context.MODE_PRIVATE);

        JSONArray history = getAll(context);
        JSONObject test = new JSONObject();

        try {
            test.put("date", System.currentTimeMillis());
            test.put("topic", topic);
            test.put("correct", correct);
            test.put("mistakes", mistakes);
            test.put("duration", durationMillis);
            test.put("quality", quality);
            test.put("speed", speed);
            test.put("completion", completion);
            test.put("points", points);
            test.put("grade", grade);

            JSONArray updated = new JSONArray();
            updated.put(test);

            for (int i = 0; i < history.length(); i++) {
                updated.put(history.get(i));
            }

            preferences.edit()
                    .putString(KEY, updated.toString())
                    .apply();

        } catch (JSONException e) {
            android.util.Log.e(
                    "TestHistory",
                    "Ошибка сохранения теста", e);
        }
    }

    public static JSONArray getAll(Context context) {

        SharedPreferences preferences =
                context.getSharedPreferences(
                        STORAGE, Context.MODE_PRIVATE);

        String data = preferences.getString(KEY, "[]");

        try {
            return new JSONArray(data);
        } catch (JSONException e) {
            android.util.Log.e(
                    "TestHistory",
                    "Ошибка чтения истории", e);
            return new JSONArray();
        }
    }
}

