
package com.example.mathtrainer;

import java.util.Random;

public class LinearEquationGenerator {

    private static final Random random = new Random();

    public static class Equation {
        public final String question;
        public final int answer;
        public final String solution;
        public final int difficulty;

        public Equation(String question, int answer,
                        String solution, int difficulty) {
            this.question = question;
            this.answer = answer;
            this.solution = solution;
            this.difficulty = difficulty;
        }
    }

    public static Equation generate(int difficulty) {

        if (difficulty < 1 || difficulty > 4) {
            throw new IllegalArgumentException(
                    "Сложность должна быть от 1 до 4");
        }

        // Сначала выбираем правильный корень
        int x = random.nextInt(19) - 9;
        int a = random.nextInt(9) + 2;

        int b = random.nextInt(11) + 1;
        int c;
        int d;
        String question;
        String solution;

        switch (difficulty) {

            case 1:
                // ax = c
                c = a * x;
                question = a + "x = " + c;

                solution = question
                        + "\nx = " + c + " / " + a
                        + "\nx = " + x;
                break;

            case 2:
                // ax + b = c
                c = a * x + b;

                question = a + "x + " + b + " = " + c;

                solution = question
                        + "\n" + a + "x = " + (c - b)
                        + "\nx = " + x;
                break;

            case 3:
                // a(x + b) = c
                c = a * (x + b);

                question = a + "(x + " + b + ") = " + c;

                solution = question
                        + "\nx + " + b + " = " + (c / a)
                        + "\nx = " + x;
                break;

            case 4:
                // ax + c = bx + d
                // Используем отдельный коэффициент правой части
                int rightA = random.nextInt(a - 1) + 1;

                c = random.nextInt(11) + 1;
                d = (a - rightA) * x + c;

                question = a + "x + " + c
                        + " = " + rightA + "x + " + d;

                solution = question
                        + "\n" + (a - rightA) + "x = "
                        + (d - c)
                        + "\nx = " + x;
                break;

            default:
                throw new IllegalStateException(
                        "Неизвестная сложность");
        }

        return new Equation(
                question, x, solution, difficulty);
    }

    public static boolean checkAnswer(
            Equation equation, int userAnswer) {

        return equation.answer == userAnswer;
    }
}
