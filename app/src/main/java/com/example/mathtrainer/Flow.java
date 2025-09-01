package com.example.mathtrainer;

public final class Flow {
    private Flow() {}

    // режимы
    public static final String EXTRA_MODE = "mode";
    public static final String MODE_TRAINING = "training";
    public static final String MODE_MARATHON = "marathon";

    // класс следующего экрана для марафона (передаём как имя класса)
    public static final String EXTRA_NEXT = "next";
}
