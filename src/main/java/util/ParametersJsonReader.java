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
            e.printStackTrace();
//            throw new RuntimeException(e);
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
//        return root.path("environment").path( "microtick_values").a;
        try {
            return new ObjectMapper().readValue(root.path("environment").path("microtick_values").traverse(), int[].class);
        } catch (IOException e) {
            e.printStackTrace();
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
            return new ObjectMapper().readValue(root.path("system").path("periods").traverse(), double[][].class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public double getAllowedJitter(){
        return root.path("task").path("allowed_jitter").asDouble();
    }

    public double getReleaseTime(){
        return root.path("task").path("release_time").asDouble();
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
        return root.path("chain").path("num_of_low").asInt();
    }

    public int getNumOfHigh(){
        return root.path("chain").path("num_of_high").asInt();
    }

    public int getNumOfHostTransitions(){
        return root.path("chain").path("num_of_host_transitions").asInt();
    }

    public double getLatency(){
        return root.path("chain").path("latency").asDouble();
    }
}
