
package com.example.mathtrainer;

import java.util.Random;

public class Task {
    private int a, b;
    private char op; // '+', '-', '*'

    // Для обратной совместимости со старым форматом
    private String text;
    private int answer;

    public Task(String text, int answer) {
        this.text = text;
        this.answer = answer;
    }

    public Task(int a, int b, char op) {
        this.a = a;
        this.b = b;
        this.op = op;
    }

    public String getText() {
        if (text != null) return text;
        return a + " " + op + " " + b + " = ?";
    }

    public int getAnswer() {
        if (text != null) return answer;
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            default:  return 0;
        }
    }

    private static final Random RANDOM = new Random();

    // 0 — умножение 1×1 (2..9); 1 — умножение 2×1; 2 — сложение/вычитание
    public static Task generate(int type) {
        switch (type) {
            case 0: {
                int a = 2 + RANDOM.nextInt(8); // 2..9
                int b = 2 + RANDOM.nextInt(8); // 2..9
                return new Task(a, b, '*');
            }
            case 1: {
                int twoDigit = 10 + RANDOM.nextInt(90); // 10..99
                int single   = 2 + RANDOM.nextInt(8);   // 2..9
                return new Task(twoDigit, single, '*');
            }
            case 2: {
                int big   = 10 + RANDOM.nextInt(90);    // 10..99
                int small = 1 + RANDOM.nextInt(big);    // 1..big
                return new Task(big, small, RANDOM.nextBoolean() ? '+' : '-');
            }
            default:
                return generate(RANDOM.nextInt(3));
        }
    }

    public static Task random() { return generate(RANDOM.nextInt(3)); }
}
