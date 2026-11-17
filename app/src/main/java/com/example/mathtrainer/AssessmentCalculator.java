
package com.example.mathtrainer;

public class AssessmentCalculator {

    // Сложность пяти заданий
    private static final int[] WEIGHTS = {1, 1, 2, 3, 4};

    public static class Result {
        public final int total;
        public final int quality;
        public final int speed;
        public final int completion;

        public Result(int total, int quality,
                      int speed, int completion) {
            this.total = total;
            this.quality = quality;
            this.speed = speed;
            this.completion = completion;
        }
    }

    public static Result calculate(
            boolean classroomMode,
            boolean[] solved,
            int[] mistakes,
            long elapsedMillis,
            long targetMillis) {

        if (solved == null || mistakes == null
                || solved.length != WEIGHTS.length
                || mistakes.length != WEIGHTS.length) {
            throw new IllegalArgumentException(
                    "Expected data for five tasks");
        }

        double totalWeight = 0;
        double earnedWeight = 0;
        int solvedCount = 0;

        for (int i = 0; i < WEIGHTS.length; i++) {
            totalWeight += WEIGHTS[i];

            if (solved[i]) {
                solvedCount++;

                // Ошибки уменьшают качество,
                // даже если ответ затем найден.
                int errors = Math.max(0, mistakes[i]);

                earnedWeight +=
                        (double) WEIGHTS[i] / (1 + errors);
            }
        }

        double qualityRatio = earnedWeight / totalWeight;
        double completionRatio =
                (double) solvedCount / WEIGHTS.length;

        int qualityPoints = (int) Math.round(
                qualityRatio * (classroomMode ? 90 : 75));

        int completionPoints = (int) Math.round(
                completionRatio * 10);

        int speedPoints = 0;

        if (!classroomMode && targetMillis > 0) {
            double timeRatio =
                    (double) elapsedMillis / targetMillis;

            // До целевого времени — полный балл.
            // При двойном времени — ноль за скорость.
            double speedRatio = Math.max(
                    0, Math.min(1, 2 - timeRatio));

            speedPoints = (int) Math.round(
                    15 * speedRatio);
        }

        int total = Math.min(100,
                qualityPoints + completionPoints + speedPoints);

        return new Result(
                total,
                qualityPoints,
                speedPoints,
                completionPoints);
    }
}
