
package com.example.mathtrainer;

import java.util.Random;

public class ElasticForceGenerator {

    private static final Random random = new Random();

    public static class Task {
        public final String question;
        public final int answer;
        public final String unit;
        public final int level;

        public Task(String question, int answer,
                    String unit, int level) {
            this.question = question;
            this.answer = answer;
            this.unit = unit;
            this.level = level;
        }
    }

    private static int number(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    public static Task generate(int level) {

        // ПРОСТОЙ УРОВЕНЬ
        if (level == 1) {

            int type = random.nextInt(3);
            int k = number(1, 8) * 100;
            int dx = number(2, 10);
            int force = k * dx / 100;

            if (type == 0) {
                return new Task(
                        "Жёсткость пружины " + k
                                + " Н/м. Она удлинилась на "
                                + dx + " см. "
                                + "Найдите силу упругости.",
                        force, "Н", 1
                );
            }

            if (type == 1) {
                return new Task(
                        "При растяжении пружины на "
                                + dx + " см возникла сила "
                                + force + " Н. "
                                + "Найдите жёсткость пружины.",
                        k, "Н/м", 1
                );
            }

            return new Task(
                    "Жёсткость пружины "
                            + k + " Н/м. "
                            + "На пружину действует сила "
                            + force + " Н. "
                            + "Найдите удлинение в сантиметрах.",
                    dx, "см", 1
            );
        }

        // СЛОЖНЫЙ УРОВЕНЬ
        if (level == 2) {

            int type = random.nextInt(3);

            int k = number(1, 6) * 100;
            int dx1 = number(2, 7);
            int dx2 = number(8, 15);

            int force1 = k * dx1 / 100;
            int force2 = k * dx2 / 100;

            if (type == 0) {

                // Сначала найти жёсткость,
                // затем новую силу.

                return new Task(
                        "Под действием силы "
                                + force1 + " Н пружина "
                                + "удлинилась на " + dx1
                                + " см. Какую силу нужно "
                                + "приложить к этой же пружине, "
                                + "чтобы она удлинилась на "
                                + dx2 + " см?",
                        force2, "Н", 2
                );
            }

            if (type == 1) {

                // Определить жёсткость,
                // затем найти новое удлинение.

                return new Task(
                        "При действии силы "
                                + force1 + " Н пружина "
                                + "удлинилась на " + dx1
                                + " см. На сколько сантиметров "
                                + "удлинится эта же пружина "
                                + "под действием силы "
                                + force2 + " Н?",
                        dx2, "см", 2
                );
            }

            // Масса груза, сила тяжести,
            // жёсткость и новое удлинение.
            // g = 10 Н/кг.

            int mass1 = number(1, 4);
            int mass2 = mass1 + number(1, 3);

            int extension1 = mass1 * 10;
            int extension2 = mass2 * 10;

            // Берём жёсткость 100 Н/м:
            // один килограмм даёт 10 см удлинения.

            return new Task(
                    "К вертикальной пружине "
                            + "подвесили груз массой "
                            + mass1 + " кг. "
                            + "Пружина удлинилась на "
                            + extension1 + " см. "
                            + "На сколько сантиметров "
                            + "удлинится эта же пружина, "
                            + "если подвесить вместо него "
                            + "груз массой " + mass2
                            + " кг? Примите g = 10 Н/кг.",
                    extension2, "см", 2
            );
        }

        throw new IllegalArgumentException(
                "Неизвестный уровень: " + level
        );
    }

    public static boolean checkAnswer(
            Task task, int answer) {
        return task.answer == answer;
    }
}
