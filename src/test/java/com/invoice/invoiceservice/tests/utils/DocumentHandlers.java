package com.invoice.invoiceservice.tests.utils;

import java.util.Random;

public class DocumentHandlers {

    private static final Random RANDOM = new Random();

    private static int calculateCpfDigit(int[] digits, int startWeight, int... extras) {
        int sum = 0;
        for (int i = 0; i < digits.length; i++) {
            sum += digits[i] * (startWeight - i);
        }
        for (int i = 0; i < extras.length; i++) {
            sum += extras[i] * (startWeight - digits.length - i);
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    private static int[] buildCnpjWeights(int startWeight, int length) {
        int[] weights = new int[length];
        int weight = startWeight;
        for (int i = 0; i < length; i++) {
            weights[i] = weight--;
            if (weight < 2) weight = 9;
        }
        return weights;
    }

    private static int calculateCnpjDigit(int[] digits, int startWeight, int... extras) {
        int[] weights = buildCnpjWeights(startWeight, digits.length + extras.length);
        int sum = 0;
        for (int i = 0; i < digits.length; i++) {
            sum += digits[i] * weights[i];
        }
        for (int i = 0; i < extras.length; i++) {
            sum += extras[i] * weights[digits.length + i];
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    public static String generateCpf() {
        int[] digits = new int[9];
        for (int i = 0; i < 9; i++) {
            digits[i] = RANDOM.nextInt(10);
        }
        int first = calculateCpfDigit(digits, 10);
        int second = calculateCpfDigit(digits, 11, first);

        StringBuilder sb = new StringBuilder();
        for (int d : digits) sb.append(d);
        sb.append(first).append(second);
        return sb.toString();
    }

    public static String generateCnpj() {
        int[] digits = new int[12];
        for (int i = 0; i < 8; i++) {
            digits[i] = RANDOM.nextInt(10);
        }
        digits[8] = 0;
        digits[9] = 0;
        digits[10] = 0;
        digits[11] = 1;

        int first = calculateCnpjDigit(digits, 5);
        int second = calculateCnpjDigit(digits, 6, first);

        StringBuilder sb = new StringBuilder();
        for (int d : digits) sb.append(d);
        sb.append(first).append(second);
        return sb.toString();
    }
}
