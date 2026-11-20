
package com.example.mathtrainer;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PhysicsAnswerUtils {

    // Округление окончательного результата
    // до десятых по обычным школьным правилам.
    public static BigDecimal roundToTenths(
            BigDecimal value) {

        return value.setScale(
                1, RoundingMode.HALF_UP
        );
    }

    // Проверка числового ответа ученика.
    // Запятая и точка равнозначны.
    public static boolean isCorrect(
            String studentAnswer,
            double correctAnswer) {

        if (studentAnswer == null
                || studentAnswer.trim().isEmpty()) {
            return false;
        }

        try {
            String normalized = studentAnswer
                    .trim()
                    .replace(',', '.')
                    .replace(" ", "");

            BigDecimal student =
                    new BigDecimal(normalized);

            BigDecimal correct =
                    BigDecimal.valueOf(correctAnswer);

            return roundToTenths(student)
                    .compareTo(roundToTenths(correct)) == 0;

        } catch (NumberFormatException e) {
            return false;
        }
    }
}
