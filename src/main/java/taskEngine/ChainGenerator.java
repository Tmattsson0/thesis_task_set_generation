package taskEngine;

import data.Singleton;
import model.Chain;
import util.RandomUtil;

import java.util.ArrayList;
import java.util.List;

public class ChainGenerator {
    Singleton s = Singleton.getInstance();
    int numOfChains = s.NUM_OF_CHAINS;
    //All numbers are treated as "Up to and including"
    int numOfTaskInChain = s.NUM_OF_TASKS_IN_CHAINS;
    int numOfHostTransitions = s.NUM_OF_HOST_TRANSITIONS;
    int numOfLowToHighPeriodTransitions = s.NUM_OF_LOW;
    int numOfHighToLowPeriodTransitions = s.NUM_OF_HIGH;
    double latency = s.LATENCY;

    public List<Chain> initializeChains() {
        List<Chain> chains = new ArrayList<>();

        for (int i = 0; i < numOfChains; i++) {
            chains.add(new Chain("Chain" + i));
        }

        for (Chain c : chains) {
            int specificNumOfTaskInChain = randomNumberCustomBounds(1, numOfTaskInChain);
            int specificNumOfHostTransitions = randomNumberCustomBounds(0, numOfHostTransitions);
            int specificNumOfLowToHighPeriodTransitions = randomNumberCustomBounds(0, numOfLowToHighPeriodTransitions);
            int specificNumOfHighToLowPeriodTransitions = randomNumberCustomBounds(0, numOfHighToLowPeriodTransitions);



        }


        return chains;
    }


    private int randomNumberCustomBounds(int origin, int bound){
        if (origin == bound) {
            return origin;
        } else {
            return RandomUtil.getRandom().ints(1, origin, bound).sum();
        }
    }

    private int randomNumberCustomBounds(int origin, int bound, int logicalBound){
        if (bound <= logicalBound) {
            if (origin == bound) {
                return origin;
            } else {
                return RandomUtil.getRandom().ints(1, origin, bound).sum();
            }
        } else {
            if (origin == logicalBound) {
                return origin;
            } else {
                return RandomUtil.getRandom().ints(1, origin, logicalBound).sum();
            }
        }
    }
}
