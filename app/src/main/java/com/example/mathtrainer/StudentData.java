


package com.example.mathtrainer;

import android.content.Context;
import android.content.SharedPreferences;

public class StudentData {

    private static final String PREFS = "student_profile";

    public static void register(
            Context context, String name, String grade) {

        SharedPreferences prefs =
                context.getSharedPreferences(
                        PREFS, Context.MODE_PRIVATE);

        prefs.edit()
                .putString("name", name.trim())
                .putString("grade", grade.trim())
                .apply();
    }

    public static String getName(Context context) {
        return context.getSharedPreferences(
                        PREFS, Context.MODE_PRIVATE)
                .getString("name", "");
    }

    public static String getGrade(Context context) {
        return context.getSharedPreferences(
                        PREFS, Context.MODE_PRIVATE)
                .getString("grade", "");
    }

    public static boolean isRegistered(Context context) {
        return !getName(context).isEmpty();
    }

    public static int getCorrect(Context context) {
        return context.getSharedPreferences(
                        PREFS, Context.MODE_PRIVATE)
                .getInt("correct", 0);
    }

    public static int getWrong(Context context) {
        return context.getSharedPreferences(
                        PREFS, Context.MODE_PRIVATE)
                .getInt("wrong", 0);
    }

    public static int getCompleted(Context context) {
        return context.getSharedPreferences(
                        PREFS, Context.MODE_PRIVATE)
                .getInt("completed", 0);
    }

    public static void addCorrect(Context context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putInt("correct", getCorrect(context) + 1)
                .apply();
    }

    public static void addWrong(Context context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putInt("wrong", getWrong(context) + 1)
                .apply();
    }

    public static void addCompleted(Context context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putInt("completed", getCompleted(context) + 1)
                .apply();
    }

    public static int getAccuracy(Context context) {
        int correct = getCorrect(context);
        int total = correct + getWrong(context);

        if (total == 0) return 0;

        return Math.round(correct * 100f / total);
    }

}



