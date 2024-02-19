package util;

import java.util.Random;

public class RandomUtil {
    private static final Random random = new Random(2);

    public static Random getRandom(){
        return random;
    }
}
