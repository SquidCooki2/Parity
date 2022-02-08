package com.squidcookie.parity.util;

public class Math {
    public static int max(int[] arr) {
        int max = arr[0];
        for(int i = 1; i < arr.length; ++i) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }
}
