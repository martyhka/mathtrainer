
package com.example.mathtrainer;

import java.util.Random;

public class Task {
    private int a, b;
    private char op;          // Операции: '+', '-', '*'

    // Поле оставлено для обратной совместимости
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

    /**
     * Генерация случайных задач разных типов:
     *
     * 0 — умножение (1х1, цифры от 2 до 9)
     * 1 — умножение (2х1, двузначные числа на однозначные)
     * 2 — сложение или вычитание двух чисел
     */
    public static Task generate(int type) {
        switch (type) {
            case 0: { // Умножение типа 1х1
                int a = 2 + RANDOM.nextInt(8); // диапазон 2-9
                int b = 2 + RANDOM.nextInt(8); // диапазон 2-9
                return new Task(a, b, '*');
            }
            case 1: { // Умножение типа 2х1
                int twoDigit = 10 + RANDOM.nextInt(90); // диапазон 10-99
                int singleDigit = 2 + RANDOM.nextInt(8); // диапазон 2-9
                return new Task(twoDigit, singleDigit, '*');
            }
            case 2: { // Сложность или вычитание
                int bigNum = 10 + RANDOM.nextInt(90); // диапазон 10-99
                int smallNum = 1 + RANDOM.nextInt(bigNum); // от 1 до меньшего значения
                if (RANDOM.nextBoolean()) {
                    return new Task(bigNum, smallNum, '+'); // сложение
                } else {
                    return new Task(bigNum, smallNum, '-'); // вычитание
                }
            }
            default:
                return generate(RANDOM.nextInt(3)); // Случайный выбор
        }
    }

    public static Task random() {
        return generate(RANDOM.nextInt(3));
    }
}