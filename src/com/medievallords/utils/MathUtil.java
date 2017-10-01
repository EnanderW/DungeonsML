package com.medievallords.utils;

import java.util.Random;

/**
 * Created by WE on 2017-09-27.
 *
 */

public class MathUtil {

    public static Random random = new Random();

    public static int getRandom(int min, int max) {
        return random.nextInt(max - min) + min;
    }
}
