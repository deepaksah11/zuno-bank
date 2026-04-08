package com.zunoBank.Authentication.utils;

import java.security.SecureRandom;

public class TempPasswordGenerator {

    private static final String UPPER   = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER   = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS  = "0123456789";
    private static final String SPECIAL = "@#$!&";
    private static final String ALL     = UPPER + LOWER + DIGITS + SPECIAL;

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate() {
        char[] password = new char[8];

        // guarantee one of each required type
        password[0] = UPPER.charAt(RANDOM.nextInt(UPPER.length()));
        password[1] = LOWER.charAt(RANDOM.nextInt(LOWER.length()));
        password[2] = DIGITS.charAt(RANDOM.nextInt(DIGITS.length()));
        password[3] = SPECIAL.charAt(RANDOM.nextInt(SPECIAL.length()));

        // fill remaining positions randomly
        for (int i = 4; i < 8; i++) {
            password[i] = ALL.charAt(RANDOM.nextInt(ALL.length()));
        }

        // shuffle so pattern is not predictable
        for (int i = 7; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char temp = password[i];
            password[i] = password[j];
            password[j] = temp;
        }

        return new String(password);
        // e.g. "Nx7B#k2P"
    }
}