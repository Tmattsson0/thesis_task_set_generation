package util;

import java.util.Random;

public class RandomUtil {
    private static Random random;

    public static Random getRandom(){
        return random;
    }

    public static void setRandom(Random random) {
        RandomUtil.random = random;
    }
}
