package taskEngine;

import data.Singleton;
import model.*;
import util.RandomUtil;

import java.util.*;
import java.util.stream.Collectors;

public class ChainGenerator {
    Singleton s;
    int numOfChains;

    //All numbers are treated as "Up to and including"
    int numOfTasksInChain;
    int numOfHostTransitions;
    int numOfLowToHighPeriodTransitions;
    int numOfHighToLowPeriodTransitions;
    double latencyTightness;
    PlatformModel platformModel;
    List<Core> validCoresList;

    public ChainGenerator() {
        s = Singleton.getInstance();
        platformModel = s.PLATFORMMODEL;
        validCoresList = platformModel.getAllCores(ScheduleType.TT).stream().filter(core -> !core.getTasks(ScheduleType.TT).isEmpty()).collect(Collectors.toList());
        latencyTightness = s.LATENCY_TIGHTNESS;
        numOfHighToLowPeriodTransitions = s.NUM_OF_HIGH;
        numOfLowToHighPeriodTransitions = s.NUM_OF_LOW;
        numOfHostTransitions = s.NUM_OF_HOST_TRANSITIONS;
        numOfTasksInChain = s.NUM_OF_TASKS_IN_CHAINS;
        numOfChains = s.NUM_OF_CHAINS;
    }

    public void initializeChains() {
        List<Chain> chains = new ArrayList<>();

        for (int i = 0; i < numOfChains; i++) {
            chains.add(new Chain("Chain" + (i + 1)));
        }

        for (Chain c : chains) {
            int desiredNumOfTasksInChain = randomNumberCustomBounds(1, numOfTasksInChain);
            int desiredNumOfHostTransitions = randomNumberTransitionBound(numOfHostTransitions, desiredNumOfTasksInChain);
            int desiredNumOfLowToHighPeriodTransitions = randomNumberTransitionBound(numOfLowToHighPeriodTransitions, desiredNumOfTasksInChain);
            int desiredNumOfHighToLowPeriodTransitions = randomNumberTransitionBound(numOfHighToLowPeriodTransitions, desiredNumOfTasksInChain);

            c.getDict().put("desiredNumTasksInChain", desiredNumOfTasksInChain);
            c.getDict().put("desiredNumOfHostTransitions", desiredNumOfHostTransitions);
            c.getDict().put("desiredNumOfLowToHighPeriodTransitions", desiredNumOfLowToHighPeriodTransitions);
            c.getDict().put("desiredNumOfHighToLowPeriodTransitions", desiredNumOfHighToLowPeriodTransitions);

        }

        for (Chain c : chains) {
//            assignChainTasksAndValuesSA(c, latencyTightness);
//            assignChainTasksAndValuesHC(c, latencyTightness);
            assignChainTasksAndValuesSteepestAscentHC(c, latencyTightness);
            c.setDelay(util.DelayUtil.calculateDelay(c, s.PLATFORMMODEL));
        }

        s.PLATFORMMODEL.setChains(chains);
    }

    private void assignChainTasksAndValuesSA(Chain c, double latency) {

        Chain currentSolution = new Chain(c);
        setInitialChainValues(currentSolution);
        currentSolution.setFitness(FitnessCalculator.calculateChainFitness(currentSolution));

        // Initial and final temperature
        double T = 1000;

        // Simulated Annealing parameters

        // Temperature at which iteration terminates
        final double Tmin = .001;

        // Decrease in temperature
        final double alpha = 0.99;

        // Number of iterations of annealing
        // before decreasing temperature
        final int numIterations = 100;

        double minFitness = Double.MAX_VALUE;
        Chain bestSolution = null;

        while (T > Tmin) {
            for (int i = 0; i < numIterations; i++) {

                if (FitnessCalculator.calculateChainFitness(currentSolution) == 0) {
                    minFitness = currentSolution.getFitness();
                    bestSolution = new Chain(currentSolution);
                    T = 0;
                    break;
                }

                if (currentSolution.getFitness() < minFitness) {
                    minFitness = currentSolution.getFitness();
                    bestSolution = new Chain(currentSolution);
                }

                Chain newSolution = generateCandidateMove(currentSolution);
                newSolution.setFitness(FitnessCalculator.calculateChainFitness(newSolution));

                double probabilityFunction = (newSolution.getFitness() - currentSolution.getFitness()) / T;
                double ap = Math.pow(Math.E, -probabilityFunction);

                if (newSolution.getFitness() < currentSolution.getFitness()) {
//                    addLineToLog(newSolution, currentSolution);
                    currentSolution = new Chain(newSolution);
                } else if (ap < RandomUtil.getRandom().doubles(1, 0, 1).sum()) {
//                    addLineToLog(newSolution, currentSolution);
                    currentSolution = new Chain(newSolution);
                }
            }
            T *= alpha;
        }

        assert bestSolution != null;

        c.setTasks(bestSolution.getTasks());
        c.setFitness(bestSolution.getFitness());
        c.calculateAndSetLatency(latency);
    }

    private void assignChainTasksAndValuesHC(Chain chain, double latency) {
        boolean bool = true;
        Chain currentSolution = new Chain(chain);
        setInitialChainValues(currentSolution);
        currentSolution.setFitness(FitnessCalculator.calculateChainFitness(currentSolution));

        Chain bestSolution = null;

        if (currentSolution.getFitness() == 0) {
            bestSolution = new Chain(currentSolution);
            bool = false;
        }

        while (bool) {
            List<Chain> neighbours = generateCandidateMoves(currentSolution);

            //shuffle
            Collections.shuffle(neighbours, RandomUtil.getRandom());

            //Select first better solution or break if none exist
            Chain firstBetterNeighbour = null;

            for (Chain c : neighbours) {
                double fitness = FitnessCalculator.calculateChainFitness(c);

                if (fitness < currentSolution.getFitness()) {
                    firstBetterNeighbour = new Chain(c);
                    firstBetterNeighbour.setFitness(fitness);
                    break;
                }
            }

            //Top of hill
            if (Objects.isNull(firstBetterNeighbour)) {
                bestSolution = new Chain(currentSolution);
                System.out.println("Top of hill!");
                break;
            }

            //Perfect solution
            else if (firstBetterNeighbour.getFitness() == 0) {
                bestSolution = new Chain(firstBetterNeighbour);
                break;
            }

            //Climb
            else {
                currentSolution = new Chain(firstBetterNeighbour);
            }
        }

        chain.setTasks(bestSolution.getTasks());
        chain.setFitness(bestSolution.getFitness());
        chain.calculateAndSetLatency(latency);
    }

    private void assignChainTasksAndValuesSteepestAscentHC(Chain chain, double latency) {
        boolean bool = true;
        Chain currentSolution = new Chain(chain);
        setInitialChainValues(currentSolution);
        currentSolution.setFitness(FitnessCalculator.calculateChainFitness(currentSolution));

        Chain bestSolution = null;

        if (currentSolution.getFitness() == 0) {
            bestSolution = new Chain(currentSolution);
            bool = false;
        }

        while (bool) {
            List<Chain> neighbours = generateCandidateMoves(currentSolution);

            //Select first better solution or break if none exist
            Chain bestNeighbour;

            bestNeighbour = Collections.min(neighbours, Comparator.comparingDouble(FitnessCalculator::calculateChainFitness));
            bestNeighbour.setFitness(FitnessCalculator.calculateChainFitness(bestNeighbour));

            //Top of hill
            if (currentSolution.getFitness() < bestNeighbour.getFitness()) {
                bestSolution = new Chain(currentSolution);
                System.out.println("Top of hill!");
                break;
            }

            //Perfect solution
            else if (bestNeighbour.getFitness() == 0) {
                bestSolution = new Chain(bestNeighbour);
                break;
            }

            //Climb
            else {
                currentSolution = new Chain(bestNeighbour);
            }
        }

        chain.setTasks(bestSolution.getTasks());
        chain.setFitness(bestSolution.getFitness());
        chain.calculateAndSetLatency(latency);
    }

    private List<Chain> generateCandidateMoves(Chain currentSolution) {
        List<Chain> neighbours = new ArrayList<>();

        int chainSize = currentSolution.getTasks().size();

        //Replace tasks
        for (int i = 0; i < chainSize; i++) {
            Chain candidateChain = new Chain(currentSolution);
            Task newRandomTask = validCoresList.get(RandomUtil.getRandom().ints(1, 0, validCoresList.size()).sum()).getRandomTask(ScheduleType.TT);
            candidateChain.getTasks().set(i, newRandomTask);
            neighbours.add(new Chain(candidateChain));
        }

        //Swap every task in chain with another random one
        for (int i = 0; i < chainSize; i++) {
            Chain candidateChain = new Chain(currentSolution);
            int b = RandomUtil.getRandom().ints(1, 0, chainSize).sum();
            Collections.swap(candidateChain.getTasks(), i, b);
            neighbours.add(new Chain(candidateChain));
        }

        //Swap every task with its immediate neighbour + 1
        for (int i = 0; i < chainSize; i++) {
            Chain candidateChain = new Chain(currentSolution);
            int b = i + 1;
            Collections.swap(candidateChain.getTasks(), i, Math.min(b, chainSize - 1));
            neighbours.add(new Chain(candidateChain));
        }

        //Swap every task with its immediate neighbour - 1
        for (int i = 0; i < chainSize; i++) {
            Chain candidateChain = new Chain(currentSolution);
            int b = i - 1;
            Collections.swap(candidateChain.getTasks(), i, Math.abs(b));
            neighbours.add(new Chain(candidateChain));
        }
        return neighbours;
    }


    private Chain generateCandidateMove(Chain c) {
        Chain chain = new Chain(c);
        double diceMove = RandomUtil.getRandom().doubles(1, 0, 1).sum();

        //Replace task in list with new task
        if (diceMove <= 0.5) {
            Task newRandomTask = validCoresList.get(RandomUtil.getRandom().ints(1, 0, validCoresList.size()).sum()).getRandomTask(ScheduleType.TT);
            chain.getTasks().set(RandomUtil.getRandom().ints(1, 0, chain.getTasks().size()).sum(), newRandomTask);
        } else {
            //Move task order in list
            int listSize = chain.getTasks().size();
            int a = RandomUtil.getRandom().ints(1, 0, listSize).sum();
            int b = RandomUtil.getRandom().ints(1, 0, listSize).sum();
            Collections.swap(chain.getTasks(), a, b);
        }
        return chain;
    }

    private void setInitialChainValues(Chain c) {
        List<Task> initialTasks = new ArrayList<>();
        List<Core> initialCores = validCoresList;

        //Old pick from x cores method
//        List<Core> initialCores = new ArrayList<>();

//        for (int i = 0; i < c.getDict().get("desiredNumOfHostTransitions") + 1; i++) {
//            Collections.shuffle(validCoresList, RandomUtil.getRandom());
//            initialCores.add(validCoresList.get(0));
//        }

        for (int i = 0; i < c.getDict().get("desiredNumTasksInChain"); i++) {
            Collections.shuffle(initialCores, RandomUtil.getRandom());
            initialTasks.add(initialCores.get(0).getTasks(ScheduleType.TT).get(RandomUtil.getRandom().ints(1, 0, initialCores.get(0).getTasks(ScheduleType.TT).size()).sum()));
        }
        c.setTasks(initialTasks);
    }

    private int randomNumberCustomBounds(int origin, int bound){
        if (origin == bound) {
            return origin;
        } else {
            return RandomUtil.getRandom().ints(1, origin, bound).sum();
        }
    }

    private int randomNumberTransitionBound(int numOfTransitions, int specificNumOfTasksInChain) {
        if (numOfTransitions >= specificNumOfTasksInChain - 1) {
            return randomNumberCustomBounds(0, specificNumOfTasksInChain - 1);
        } else {
            return randomNumberCustomBounds(0, numOfTransitions);
        }
    }
}
