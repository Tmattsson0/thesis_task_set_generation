package util;

import java.util.Random;

public class RandomUtil {
    private static final Random random = new Random(1);

    public static Random getRandom(){
        return random;
    }
}
