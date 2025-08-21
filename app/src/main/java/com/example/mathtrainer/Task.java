package com.example.mathtrainer;

import java.util.Random;

public class Task {
    private final int a, b;
    private final char op; // '+', '-', '×'

    public Task(int a, int b, char op) { this.a = a; this.b = b; this.op = op; }

    public String getText() { return a + " " + op + " " + b + " = ?"; }

    public int getAnswer() {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '×': return a * b;
            default:  return a + b;
        }
    }

    public static Task random() {
        char[] ops = new char[]{'+', '-', '×'};
        Random r = new Random();
        char op = ops[r.nextInt(ops.length)];
        int a, b;
        if (op == '×') { a = r.nextInt(12); b = r.nextInt(12); }
        else { a = r.nextInt(50); b = r.nextInt(50); }
        return new Task(a, b, op);
    }
}
