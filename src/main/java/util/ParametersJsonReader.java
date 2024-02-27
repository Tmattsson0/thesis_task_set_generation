package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.DeadlineType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ParametersJsonReader {
    File parametersFile;
    JsonNode root;

    public ParametersJsonReader(String parametersFilePath) throws FileNotFoundException {
        if (!Files.exists(Paths.get(parametersFilePath))){
            throw new FileNotFoundException();
        }
        parametersFile = new File(parametersFilePath);
        try {
            root =  new ObjectMapper().readTree(parametersFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getNumOfHosts() {
        return root.path("environment").path( "number_of_hosts").asInt();
    }

    public String getScheduleType(){
        return root.path("environment").path( "schedule_type").asText();
    }

    public boolean getIsPreemptible(){
        return root.path("environment").path( "preemptible").asBoolean();
    }

    public int getNumOfCores(){
        return root.path("environment").path( "num_of_cores").asInt();
    }

    public int[] getMicrotickValues(){
        try {
            return new ObjectMapper().readValue(root.path("environment").path("microtick_values").traverse(), int[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public double getTtUtilization(){
        return root.path("system").path( "tt_utilization").asDouble();
    }

    public double getEtUtilization(){
        return root.path("system").path( "et_utilization").asDouble();
    }

    public int getNumOfTtTasks(){
        return root.path("system").path( "num_of_tt_tasks").asInt();

    }

    public int getNumOfEtTasks(){
        return root.path("system").path( "num_of_et_tasks").asInt();
    }

    public double[][] getPeriods() {
        try {

            double[][] periods = new ObjectMapper().readValue(root.path("system").path("periods").traverse(), double[][].class);

            if (!isPeriodParamCorrect(periods)){
                throw new IllegalArgumentException("Period probabilities do not sum to 1");
            }
            return periods;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isPeriodParamCorrect(double[][] periods) {
        double[] periodNumbers = new double[periods.length];
        double[] probabilityNumbers = new double[periods.length];

        for (int i = 0; i < periods.length; i++) {
            periodNumbers[i] = periods[i][0];
            probabilityNumbers[i] = periods[i][1];
        }

        return hasNoDuplicates(periodNumbers) && sumsToOne(probabilityNumbers);
    }

    private boolean sumsToOne(double[] probabilityNumbers) {

        return Math.round(Arrays.stream(probabilityNumbers).sum()) == 1;
    }

    boolean hasNoDuplicates(final double[] list)
    {
        Set<Double> lump = new HashSet<>();
        for (double i : list)
        {
            if (lump.contains(i)) return false;
            lump.add(i);
        }
        return true;
    }

    public double[] getAllowedJitter(){
        try {
            double[] jitter = new ObjectMapper().readValue(root.path("task").path("allowed_jitter").traverse(), double[].class);

            if (sumsToOne(jitter)) {
                return jitter;
            } else {
                throw new IllegalArgumentException("Jitter values do not sum to 1");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public double[] getReleaseTime(){
        try {
            double[] releaseTime = new ObjectMapper().readValue(root.path("task").path("release_time").traverse(), double[].class);

            if (sumsToOne(releaseTime)) {
                return releaseTime;
            } else {
                throw new IllegalArgumentException("Release time does not sum to 1");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public DeadlineType getDeadlineType() throws ParseException {
        String baseText = root.path("task").path("deadline_type").asText();

        String type = baseText.split(";")[0];

        if (type.equalsIgnoreCase("implicit")) {
            return new DeadlineType();
        } else if (type.equalsIgnoreCase("arbitrary")){
            return new DeadlineType(Integer.parseInt(baseText.split(";")[1].split(",")[0]), Integer.parseInt(baseText.split(";")[1].split(",")[1]));
        } else throw new ParseException(baseText, 0);
    }

    public int getNumOfChains(){
        return root.path("chain").path("num_of_chains").asInt();
    }

    public int getNumOfTasksInChains(){
        return root.path("chain").path("num_of_tasks_in_chain").asInt();
    }

    public int getNumOfLow(){
        int numberOfLowToHigh = root.path("chain").path("num_of_low").asInt();

//        if (numberOfLowToHigh > getPeriods().length - 1) {
//            throw new IllegalArgumentException("Number of period transitions exceed number of period values");
//        }

        return numberOfLowToHigh;
    }

    public int getNumOfHigh(){
        int numberOfHighToLow = root.path("chain").path("num_of_high").asInt();

//        if (numberOfHighToLow > getPeriods().length - 1) {
//            throw new IllegalArgumentException("Number of period transitions exceed number of period values");
//        }

        return numberOfHighToLow;
    }

    public int getNumOfHostTransitions(){
        int numOfHostTransitions = root.path("chain").path("num_of_host_transitions").asInt();

        if (getNumOfCores() <= 1 && numOfHostTransitions > 0) {
            throw new IllegalArgumentException("Host transitions cannot happen with only 1 core");
        }

        return numOfHostTransitions;
    }

    public double getLatency(){
        return root.path("chain").path("latency_tightness").asDouble();
    }
}
