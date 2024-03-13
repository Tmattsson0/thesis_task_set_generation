package util;

public class CheekyLetterToNumberConverter {

    public static int coreIdLetterToNumberConverter(String coreId) {

        String str = coreId.substring(1);

        return Integer.parseInt(str) - 1;
    }

    public static int cpuIdLetterToNumberConverter(String cpuId) {

        char c = cpuId.charAt(0);

        // Check if the letter is within the A-Z range
        if (c < 'A' || c > 'Z') {
            throw new IllegalArgumentException("Input must be a letter from A to Z.");
        }

        // 'A' corresponds to 0, so we subtract the ASCII value of 'A' from the letter
        return c - 'A';
    }
}
