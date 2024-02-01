package util;

import java.util.ArrayList;

public class SpecificPeriodToll {

    public static int[] getSpecificPeriods(double[][] periods, int numOfTasks){
        ArrayList<Integer> periodDistributionArray = generatePeriodDistributionArray(periods);
        int[] specificPeriods = new int[numOfTasks];
        for (int i = 0; i < specificPeriods.length; i++) {
            specificPeriods[i] = periodDistributionArray.get((int)(RandomUtil.getRandom().doubles(1, 0, 1).sum() * periodDistributionArray.size()));
        }
        return specificPeriods;
    }

    private static ArrayList<Integer> generatePeriodDistributionArray(double[][] periods){
        int numOfDecimalNumbers = 0;
        for (double[] period : periods) {
            if (Double.toString(period[1]).split("\\.")[1].length() > numOfDecimalNumbers) {
                numOfDecimalNumbers = Double.toString(period[1]).length();
            }
        }

        ArrayList<Integer> periodDistribution = new ArrayList<>();

        for (double[] period : periods) {
            for (int k = 0; k < period[1] * (10 ^ numOfDecimalNumbers); k++) {
                periodDistribution.add((int) period[0]);
            }
        }

        //Create an array of size 10^numOfDecimalNumbers and allocate the periods 0,X * 10^3 times in the array.
        // Pick random value from array.

        return periodDistribution;
    }

    public static int[] getSpecificNumberOfPeriods(double[][] periods, int numOfPeriods){
        ArrayList<Integer> periodDistributionArray = generatePeriodDistributionArray(periods);
        int[] specificPeriods = new int[numOfPeriods];
        for (int i = 0; i < specificPeriods.length; i++) {
            specificPeriods[i] = periodDistributionArray.get((int)(RandomUtil.getRandom().doubles(1, 0, 1).sum()) * periodDistributionArray.size());
        }
        return specificPeriods;
    }
}
