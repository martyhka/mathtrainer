package com.example.mathtrainer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * Второй уровень: задачи с десятичными дробями (1 знак после запятой).
 *
 * Типы:
 *  A) a(0.1..9.9) +/- b(0.1..9.9), результат > 0 и ≤ 9.9
 *  B) i(0..9)     +/- d(0.1..9.9), результат > 0 и ≤ 9.9
 *  C) a(0.1..9.9) *  k(1..9),      результат в (0, 9.9], 1 знак после запятой
 *  D) a(0.1..9.9) /  k(1..9),      результат в [0.1, 9.9], 1 знак после запятой
 */
public class DecimalTask {
    private static final Random RND = new Random();

    private final BigDecimal a;
    private final BigDecimal b; // для умножения/деления здесь хранится второй операнд (целое 1..9 как BigDecimal)
    private final char op;      // '+', '-', '×', '÷'

    public DecimalTask(BigDecimal a, BigDecimal b, char op) {
        this.a = scale1(a);
        this.b = scale1(b);
        this.op = op;
    }

    public String getText() {
        // Показываем 1 знак после запятой
        return fmt(a) + " " + op + " " + fmt(b) + " = ?";
    }

    public BigDecimal getAnswer() {
        BigDecimal res;
        switch (op) {
            case '+': res = a.add(b); break;
            case '-': res = a.subtract(b); break;
            case '×': res = a.multiply(toIntIfPossible(b)); break;
            case '÷':
                int k = toIntIfPossible(b).intValueExact();
                res = a.divide(BigDecimal.valueOf(k), 1, RoundingMode.UNNECESSARY);
                break;
            default:  res = BigDecimal.ZERO;
        }
        return scale1(res);
    }

    // ---------- Генерация ----------

    public static DecimalTask random() {
        int t = RND.nextInt(4); // 0..3
        switch (t) {
            case 0: return genAddSubDecDec();
            case 1: return genAddSubIntDec();
            case 2: return genMul();
            default:return genDiv();
        }
    }

    // A) a(0.1..9.9) +/- b(0.1..9.9), res in (0, 9.9]
    private static DecimalTask genAddSubDecDec() {
        for (int i = 0; i < 10_000; i++) {
            BigDecimal a = randTenth(1, 99); // 0.1..9.9
            BigDecimal b = randTenth(1, 99);
            char op = RND.nextBoolean() ? '+' : '-';

            BigDecimal res = (op == '+') ? a.add(b) : a.subtract(b);
            if (res.compareTo(BigDecimal.ZERO) > 0 && res.compareTo(BigDecimal.valueOf(9.9)) <= 0) {
                return new DecimalTask(a, b, op);
            }
        }
        // fallback (теоретически не должен понадобиться)
        return new DecimalTask(BigDecimal.valueOf(0.5), BigDecimal.valueOf(0.4), '+');
    }

    // B) i(0..9) +/- d(0.1..9.9), res in (0, 9.9]
    private static DecimalTask genAddSubIntDec() {
        for (int i = 0; i < 10_000; i++) {
            BigDecimal I = BigDecimal.valueOf(RND.nextInt(10)); // 0..9
            BigDecimal d = randTenth(1, 99);                    // 0.1..9.9
            char op = RND.nextBoolean() ? '+' : '-';

            BigDecimal res = (op == '+') ? I.add(d) : I.subtract(d);
            if (res.compareTo(BigDecimal.ZERO) > 0 && res.compareTo(BigDecimal.valueOf(9.9)) <= 0) {
                return new DecimalTask(I, d, op);
            }
        }
        return new DecimalTask(BigDecimal.valueOf(2), BigDecimal.valueOf(0.5), '-');
    }

    // C) a(0.1..9.9) * k(1..9), result in (0, 9.9], 1 decimal digit
    private static DecimalTask genMul() {
        for (int i = 0; i < 10_000; i++) {
            int k = 1 + RND.nextInt(9); // 1..9
            int maxN = 99 / k;          // n/10 * k <= 9.9  =>  n <= 99/k
            if (maxN < 1) continue;
            int n = 1 + RND.nextInt(maxN);
            BigDecimal a = BigDecimal.valueOf(n).divide(BigDecimal.TEN); // n/10
            BigDecimal res = a.multiply(BigDecimal.valueOf(k));
            if (res.compareTo(BigDecimal.ZERO) > 0 && res.compareTo(BigDecimal.valueOf(9.9)) <= 0) {
                return new DecimalTask(a, BigDecimal.valueOf(k), '×');
            }
        }
        return new DecimalTask(BigDecimal.valueOf(0.5), BigDecimal.valueOf(2), '×'); // 1.0
    }

    // D) a(0.1..9.9) / k(1..9) -> чтобы ответ был с 1 знаком: генерим сначала результат r, затем a=r*k
    private static DecimalTask genDiv() {
        for (int i = 0; i < 10_000; i++) {
            int k = 1 + RND.nextInt(9);             // 1..9
            BigDecimal r = randTenth(1, 99);        // желаемый результат 0.1..9.9
            BigDecimal a = r.multiply(BigDecimal.valueOf(k)); // делимое
            if (a.compareTo(BigDecimal.valueOf(9.9)) <= 0) {
                return new DecimalTask(a, BigDecimal.valueOf(k), '÷');
            }
        }
        return new DecimalTask(BigDecimal.valueOf(3.0), BigDecimal.valueOf(3), '÷'); // 1.0
    }

    // ---------- Утилиты ----------

    private static BigDecimal randTenth(int nMin, int nMaxInclusive) {
        // случайное число вида n/10, где n в [nMin; nMaxInclusive]
        int n = nMin + RND.nextInt(nMaxInclusive - nMin + 1);
        return BigDecimal.valueOf(n).divide(BigDecimal.TEN);
    }

    private static BigDecimal scale1(BigDecimal x) {
        return x.setScale(1, RoundingMode.UNNECESSARY);
    }

    private static BigDecimal toIntIfPossible(BigDecimal x) {
        // для умножения храним k как 1..9, но форматируем с одним знаком, чтобы текст был единообразным
        return BigDecimal.valueOf(x.intValue());
    }

    // было
// private static String fmt(BigDecimal x) {
//     return x.setScale(1, RoundingMode.UNNECESSARY).toPlainString();
// }

    // стало
    private static String fmt(BigDecimal x) {
        // убираем хвост .0, но оставляем 1 знак для неделимых десяток
        BigDecimal z = x.stripTrailingZeros();
        if (z.scale() <= 0) return z.toPlainString();        // 5
        return x.setScale(1, RoundingMode.UNNECESSARY).toPlainString(); // 5.4, 0.6 и т.п.
    }

}
