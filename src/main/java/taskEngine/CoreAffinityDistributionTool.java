package taskEngine;

import model.Core;
import util.RandomUtil;

import java.util.*;

public class CoreAffinityDistributionTool {

    public static String[][] specificCoreAffinity(int numOfTasks, double[] coreAffinityDist, List<Core> coreList){

        int[] specificAmounts = calculateAmountsOfDifferentTypes(numOfTasks, coreAffinityDist);

        String[][] arr = new String[numOfTasks][];

        //jokers
        for (int i = 0; i < specificAmounts[0]; i++) {
            arr[i] = new String[]{};
        }

        //single
        for (int i = specificAmounts[0]; i < specificAmounts[0] + specificAmounts[1]; i++) {
            arr[i] = new String[]{coreList.get(RandomUtil.getRandom().ints(1, 0, coreList.size() - 1).sum()).getId()};
        }

        //multi
        for (int i = specificAmounts[0] + specificAmounts[1]; i < numOfTasks; i++) {
            arr[i] = pickRandomCores(coreList);
        }
        return arr;
    }

    private static String[] pickRandomCores(List<Core> coreList) {
        int amountOfCoresToReturn;
        if (coreList.size() == 1) {
            amountOfCoresToReturn = 1;
        } else if (coreList.size() <= 3) {
            amountOfCoresToReturn = RandomUtil.getRandom().ints(1, 1, coreList.size()).sum();
        } else {
            amountOfCoresToReturn = RandomUtil.getRandom().ints(1, 2, coreList.size() - 1).sum();
        }

        String[] cores = new String[amountOfCoresToReturn];

        Stack<Core> coreStack = new Stack<>();
        coreStack.addAll(coreList);
        Collections.shuffle(coreStack, RandomUtil.getRandom());

        for (int i = 0; i < amountOfCoresToReturn; i++) {
            cores[i] = coreStack.pop().getId();
        }
        return cores;
    }

    public static int[] calculateAmountsOfDifferentTypes(int numOfTasks, double[] coreAffinityDist) {
        int shareZero = (int) Math.round(numOfTasks * coreAffinityDist[0]);
        int shareOne = (int) Math.round(numOfTasks * coreAffinityDist[1]);
        int shareTwo = (int) Math.round(numOfTasks * coreAffinityDist[2]);

        if ((shareZero + shareOne + shareTwo) != numOfTasks) {
            int total = numOfTasks;

            if (shareZero >= shareOne && shareZero >= shareTwo){
                total -= shareZero;
                total -= shareOne;
                total -= shareTwo;
                shareZero += total;
            } else if (shareOne >= shareZero && shareOne >= shareTwo){
                total -= shareZero;
                total -= shareOne;
                total -= shareTwo;
                shareOne += total;
            } else {
                total -= shareZero;
                total -= shareOne;
                total -= shareTwo;
                shareTwo += total;
            }
        }
        return new int[]{shareZero, shareOne, shareTwo};
    }
}
