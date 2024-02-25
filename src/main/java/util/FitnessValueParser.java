package util;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FitnessValueParser {

    public static void main(String[] args) {
        String fileName = "task_set_gen_test_SAHC_param5_seed_8.log";
        String filePath = "/Users/thomasmattsson/Documents/GitHub/thesis_task_set_generation/testCases/test1/" + fileName;
//        task_set_gen_test_SA_param3_seed_0.log

//        task_set_gen_test_SA_param5_seed_8.log

        // Regex to match lines that contain Fitness value
        // This will match the second line and any line that starts with "New solution" and contains a Fitness value
        String regex = "(Fitness=\"([^\"]+))\"|^(New solution.*Fitness=)(\\d+\\.\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Map<String, Map<String, String>> dataMap = new LinkedHashMap<>();
        Map<String, String> values = new LinkedHashMap<>();
        List<Double> fitness = new ArrayList<>();
        int iteration = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Check if the line is not starting with "Old solution:"
                if (!line.startsWith("Old solution:")) {
                    Matcher matcher = pattern.matcher(line);
                    // Check if the current line matches the pattern
                    if (matcher.find()) {
                        // This will print the Fitness value whether it's from the initial XML-like line or a "New solution" line
                        String fitnessValue = matcher.group(2) != null ? matcher.group(2) : matcher.group(4);
                        System.out.println(fitnessValue);
                        fitness.add(Double.parseDouble(fitnessValue));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeDataToCSV(fitness);
    }

    private static void writeDataToCSV(List<Double> fitnessList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("testCases/test1/output.csv"))) {
            // Write header
            writer.write("Fitness\n");

            // Write data
            for (Double fitness : fitnessList) {
//                String fileNameWithoutPrefix = entry.getKey().replace("/Users/thomasmattsson/Documents/GitHub/thesis_task_set_generation/testCases/test1/testCases/test1/task_set_gen_test", "").replace(".log", "");
//                writer.write(fileNameWithoutPrefix + ";");
                DecimalFormat europeanFormat = new DecimalFormat("#,##0.00");
//                writer.write(values.get("Iteration") + ";");
                writer.write(europeanFormat.format(fitness) + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
