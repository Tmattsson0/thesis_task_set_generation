package taskEngine;

import java.math.BigDecimal;
import java.util.*;

public class WcetGenerator {
//    Random random = new Random(123);
//    int[] specificPeriods;
//    double util;
//    int numOfTasks;
//    double individualTaskUtilLowerBound;
//    double individualTaskUtilUpperBound;
//
//    public WcetGenerator(int[] specificPeriods, double util, int numOfTasks, double individualTaskUtilLowerBound, double individualTaskUtilUpperBound) {
//        this.specificPeriods = specificPeriods;
//        this.util = util;
//        this.numOfTasks = numOfTasks;
//        this.individualTaskUtilLowerBound = individualTaskUtilLowerBound;
//        this.individualTaskUtilUpperBound = individualTaskUtilUpperBound;
//    }
//
//    public List<Integer> generateRandomWCETValuesSA(){
//        CandidateSolution currentSolution = generateInitailCandidateSolution();
//        System.out.println("First solution: " + currentSolution.utilPairs);
//
//        // Initial and final temperature
//        double T = 1;
//
//        // Simulated Annealing parameters
//
//        // Temperature at which iteration terminates
//        final double Tmin = .001;
//
//        // Decrease in temperature
//        final double alpha = 0.9;
//
//        // Number of iterations of annealing
//        // before decreasing temperature
//        final int numIterations = 100;
//
//        BigDecimal minCost = BigDecimal.valueOf(Double.MAX_VALUE);
//        CandidateSolution bestSolution = null;
//
//        while(T > Tmin){
//            for (int i = 0; i < numIterations; i++) {
//
//                if (currentSolution.cost.compareTo(BigDecimal.ZERO) == 0){
//                    minCost = currentSolution.cost;
//                    bestSolution = currentSolution;
//                    T = Double.MIN_VALUE;
//                    break;
//                }
//
//                if (currentSolution.cost.compareTo(minCost) < 0) {
//                    minCost = currentSolution.cost;
//                    bestSolution = currentSolution;
//                }
//
//                CandidateSolution newSol = generateSingleMove(currentSolution.utilPairs);
//
//                double ap = Math.pow(Math.E,
//                        (currentSolution.cost.doubleValue() -
//                                newSol.cost.doubleValue()/T));
//
//                if (newSol.cost.compareTo(currentSolution.cost) < 0) {
//                    currentSolution = makeMove(newSol);
//                    System.out.println("betterSolution cost: " + currentSolution.cost);
//                } else if (ap < Math.random()) {
//                    currentSolution = makeMove(newSol);
//                    System.out.println("worseSolution cost: " + currentSolution.cost);
//                }
//            }
//            T *= alpha;
//        }
//
//        assert bestSolution != null;
//        System.out.println("bestSolution" + bestSolution.utilPairs);
//        System.out.println("Cost Function final solution: " + bestSolution.cost);
//
//        return bestSolution.utilPairs.stream().map(UtilPair::getWcet).toList();
//    }
//
//    public List<Integer> generateRandomWCETValuesHC() {
//        CandidateSolution currentSolution = generateInitailCandidateSolution();
//        CandidateSolution newSol;
//        boolean randomRestart = false;
//        List<CandidateSolution> neighbours;
//
//        while (true) {
//            //Generate moves. Random or normal
//            if (randomRestart) {
//                //Random restart
//                System.out.println("Random!");
//                currentSolution = generateInitailCandidateSolution();
//                neighbours = generateMoves(currentSolution.utilPairs);
//                newSol = neighbours.stream().min(Comparator.comparing(solution -> solution.cost)).orElseThrow();
//                randomRestart = false;
//            } else {
//                neighbours = generateMoves(currentSolution.utilPairs);
//                newSol = neighbours.stream().min(Comparator.comparing(solution -> solution.cost)).orElseThrow();
//            }
//
//            System.out.println("New solution cost: " + newSol.cost);
//
//            //Top of hill and good solution: Return
//            if ((currentSolution.cost.compareTo(newSol.cost) < 0) && currentSolution.cost.compareTo(BigDecimal.ZERO) == 0) {
//                System.out.println("Top of hill. BestSolution: " + currentSolution.utilPairs);
//                System.out.println("Cost Function best solution: " + currentSolution.cost);
//                double finalUtil = currentSolution.utilPairs.stream().mapToDouble(UtilPair::getUtil).sum();
//                System.out.println("final util" + finalUtil);
//                return currentSolution.utilPairs.stream().map(UtilPair::getWcet).toList();
//            }
//
////            //Top of hill and "good enough solution": Return
////            else if ((currentSolution.cost.compareTo(newSol.cost) < 0) && currentSolution.cost.doubleValue() <= 0.1) {
////                System.out.println("Top of hill. Good enough solution: " + currentSolution.utilPairs);
////                System.out.println("Cost Function best solution: " + currentSolution.cost);
////                return currentSolution.utilPairs.stream().map(UtilPair::getWcet).toList();
////            }
//
//            //Plateau and good solution: return.
//            else if (currentSolution.cost.compareTo(newSol.cost) == 0 && currentSolution.cost.compareTo(BigDecimal.ZERO) == 0){
////                CandidateSolution chosen = makeMove(currentSolution);
//                System.out.println("Plateau, but good solution. BestSolution: " + currentSolution.utilPairs);
//                System.out.println("Cost Function best solution: " + currentSolution.cost);
//                double finalUtil = currentSolution.utilPairs.stream().mapToDouble(UtilPair::getUtil).sum();
//                System.out.println("final util" + finalUtil);
//                return currentSolution.utilPairs.stream().map(UtilPair::getWcet).toList();
//            }
//
//            //Normal climb
//            else if (currentSolution.cost.compareTo(newSol.cost) > 0){
//                System.out.println("Climb");
//                currentSolution = makeMove(newSol);
//                double finalUtil = currentSolution.utilPairs.stream().mapToDouble(UtilPair::getUtil).sum();
//                System.out.println("final util " + finalUtil);
//            }
//
//            //Top of hill and bad solution: New random start
//            else if ((currentSolution.cost.compareTo(newSol.cost) < 0) && currentSolution.cost.compareTo(BigDecimal.ZERO) > 0) {
//                System.out.println("Top of hill and bad solution");
//                randomRestart = true;
//            }
//        }
//    }
//
//
////    private List<UtilPair> generateRandomUtilPairs() {
////        int targetSum = (int) (Arrays.stream(this.specificPeriods).sum() * this.util);
////        double[] initialRandomNumbers = new double[this.numOfTasks];
////        Arrays.setAll(initialRandomNumbers, i -> random.doubles(1, 0.01, 1).sum());
////        double sumOfinitialRandomNumbers = Arrays.stream(initialRandomNumbers).sum();
////        int[] finalRandomNumbers = new int[this.numOfTasks];
////        List<UtilPair> utilPairs = new ArrayList<>();
////
////        for (int i = 0; i < initialRandomNumbers.length; i++) {
////            finalRandomNumbers[i] = (int) Math.ceil((initialRandomNumbers[i] / sumOfinitialRandomNumbers) * targetSum);
////        }
////
////        for (int i = 0; i < finalRandomNumbers.length; i++) {
////            utilPairs.add(new UtilPair(this.specificPeriods[i], finalRandomNumbers[i]));
////        }
////
////        return utilPairs;
////    }
//
//    private CandidateSolution generateRandomUtilPairs2(){
//        List<UtilPair> utilPairs = new ArrayList<>();
//
//        for (int i = 0; i < this.numOfTasks; i++) {
//            utilPairs.add(new UtilPair(this.specificPeriods[i], random.ints(1, 1, this.specificPeriods[i]).sum()));
//        }
//        return new CandidateSolution(utilPairs, random.ints(1, 0, utilPairs.size()).sum(), 1);
//    }
//
//    private CandidateSolution generateRandomUtilPairs3(){
//        List<UtilPair> utilPairs = new ArrayList<>();
//
//        for (int i = 0; i < this.numOfTasks; i++) {
//            utilPairs.add(new UtilPair(this.specificPeriods[i], random.ints(1, (int) (this.specificPeriods[i] * (util/numOfTasks)) - 1, (int) (this.specificPeriods[i] * (util/numOfTasks))).sum()));
//        }
//        return new CandidateSolution(utilPairs, random.ints(1, 0, utilPairs.size()).sum(), 1);
//    }
//
//    //Todo make better.
//    private CandidateSolution generateInitailCandidateSolution(){
//        List<UtilPair> utilPairs = new ArrayList<>();
//
//        for (int i = 0; i < this.numOfTasks; i++) {
//            utilPairs.add(new UtilPair(this.specificPeriods[i], random.ints(1, 1, (int) (this.specificPeriods[i] * util) / 100).sum()));
////            utilPairs.add(new UtilPair(this.specificPeriods[i], (int) (this.specificPeriods[i] * util)));
//        }
//
//        return new CandidateSolution(utilPairs, random.ints(1, 0, utilPairs.size()).sum(), 1);
//    }
//
//    private List<CandidateSolution> generateMoves(List<UtilPair> utilPairs){
//        int moveSize;
//
//        List<CandidateSolution> candidateSolutions = new ArrayList<>();
//
//        for (UtilPair u: utilPairs) {
//            moveSize = random.ints(1, 1, u.period).sum();
//
//            if (u.isLegalChange(moveSize)) {
//                candidateSolutions.add(new CandidateSolution(utilPairs, utilPairs.indexOf(u), moveSize));
//            }
//
//            if (u.isLegalChange(-moveSize)) {
//                candidateSolutions.add(new CandidateSolution(utilPairs, utilPairs.indexOf(u), -moveSize));
//            }
//        }
//
//        if (candidateSolutions.isEmpty()){
//            System.out.println("I'm empty");
//            return generateMoves(generateRandomUtilPairs2().utilPairs);
//        }
//
//        return candidateSolutions;
//    }
//
//    private CandidateSolution generateSingleMove(List<UtilPair> utilPairs){
//        int moveSize;
//        boolean temp = true;
//        List<CandidateSolution> candidateSolutions = new ArrayList<>();
//
//        while (temp) {
//            Collections.shuffle(utilPairs);
//
//            for (UtilPair u : utilPairs) {
//                moveSize = random.ints(1, 1, u.period).sum();
//                if(!temp){break;}
//
//                if (u.isLegalChange(moveSize)) {
//                    candidateSolutions.add(new CandidateSolution(utilPairs, utilPairs.indexOf(u), moveSize));
//                    temp = false;
//                }
//
//                if (u.isLegalChange(-moveSize)) {
//                    candidateSolutions.add(new CandidateSolution(utilPairs, utilPairs.indexOf(u), -moveSize));
//                    temp = false;
//                }
//            }
//        }
//
//        Collections.shuffle(candidateSolutions);
//
//        return candidateSolutions.get(0);
//    }
//
//    //Todo fix
//    private CandidateSolution makeMove(CandidateSolution solutionToMove){
//
//        solutionToMove.utilPairs.get(solutionToMove.indexOfChange).changeWcet(solutionToMove.change);
//
//        return new CandidateSolution(solutionToMove.utilPairs, 0, 0);
//    }
//
//    private BigDecimal costFunc(CandidateSolution candidateSolution){
//        BigDecimal cost;
//        double sumOfIndividualTaskBoundDeltas;
//        double currentUtil;
//        double utilDelta;
//
//        candidateSolution.utilPairs.get(candidateSolution.indexOfChange).changeWcet(candidateSolution.change);
//
//        //Calculate sum of delta from individual task bounds
//        sumOfIndividualTaskBoundDeltas = calcTaskBoundDelta(candidateSolution.utilPairs);
//
//        currentUtil = candidateSolution.utilPairs.stream().mapToDouble(UtilPair::getUtil).sum();
//
//        utilDelta = Math.abs(currentUtil - util);
//
////        cost = BigDecimal.valueOf(targetWcetSumDelta + sumOfIndividualTaskBoundDeltas);
////        cost = BigDecimal.valueOf(targetWcetSumDelta);
//        cost = BigDecimal.valueOf(utilDelta);
//
////        System.out.println("sumOfIndividualTaskBoundDeltas: " + sumOfIndividualTaskBoundDeltas);
////        System.out.println("WCETDelta: " + wcetDelta);
//
//        candidateSolution.utilPairs.get(candidateSolution.indexOfChange).changeWcet(-candidateSolution.change);
////        System.out.println("Cost: " + cost);
//        return cost;
//    }
//
//    private double calcTaskBoundDelta(List<UtilPair> utilPairs) {
//        double sum = 0.0;
//
//        for (UtilPair u: utilPairs) {
//            sum = sum + u.calculateUtilDelta(individualTaskUtilLowerBound, individualTaskUtilUpperBound);
//        }
//        return sum;
//    }
//
//    private static class UtilPair{
//        int period;
//        int wcet;
//        double util;
//
//        public UtilPair(int period, int wcet) {
//            this.period = period;
//            this.wcet = wcet;
//            this.util = (double) wcet/period;
//        }
//
//        public int getPeriod() {
//            return period;
//        }
//
//        public void setPeriod(int period) {
//            this.period = period;
//            this.util = (double) this.wcet /period;
//        }
//
//        public int getWcet() {
//            return wcet;
//        }
//
//        public void setWcet(int wcet) {
//            this.wcet = wcet;
//            this.util = (double) wcet/this.period;
//        }
//
//        public void changeWcet(int change) {
//            this.wcet = this.wcet + change;
//            setWcet(wcet);
//        }
//
//        public double getUtil() {
//            return util;
//        }
//
//        public double calculateUtilDelta(double lowerBound, double upperBounder) {
//            if (this.util < lowerBound) {
//                return Math.abs(this.util - lowerBound);
//            } else if (this.util > upperBounder) {
//                return Math.abs(this.util - upperBounder);
//            } else {
//                return 0.0;
//            }
//        }
//
//        public boolean isWithinBounds(double lowerBound, double upperBound) {
//            return this.util > lowerBound && this.util < upperBound;
//        }
//
//        public boolean isLegalChange (int wcetChange) {
//            int newWcet = this.wcet + wcetChange;
//            return newWcet > 0 && newWcet <= this.period;
//        }
//
//
//        @Override
//        public String toString() {
//            return "UtilPair{" +
//                    "period=" + period +
//                    ", wcet=" + wcet +
//                    ", util=" + util +
//                    '}';
//        }
//    }
//
//    private class CandidateSolution{
//        List<UtilPair> utilPairs;
//        int indexOfChange;
//        int change;
//        BigDecimal cost;
//
//        public CandidateSolution(List<UtilPair> utilPairs, int indexOfChange, int change) {
//            this.utilPairs = utilPairs;
//            this.indexOfChange = indexOfChange;
//            this.change = change;
//            this.cost = costFunc(this);
//        }
//
//        @Override
//        public String toString() {
//            return "CandidateSolution{" +
//                    "utilPairs=" + utilPairs +
//                    ", indexOfChange=" + indexOfChange +
//                    ", change=" + change +
//                    '}';
//        }
//    }
}
