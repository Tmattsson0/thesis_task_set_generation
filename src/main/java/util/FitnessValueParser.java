package util;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FitnessValueParser {

    public static void main(String[] args) {
        String filePath = "/Users/thomasmattsson/Documents/GitHub/thesis_task_set_generation/testCases/SA_test/task_set_gen_test_SA_param1_seed_0.log";

        // Regex to match lines that contain Fitness value
        // This will match the second line and any line that starts with "New solution" and contains a Fitness value
        String regex = "(Fitness=\"([^\"]+))\"|^(New solution.*Fitness=)(\\d+\\.\\d+)";
        Pattern pattern = Pattern.compile(regex);

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
                        System.out.println("Fitness: " + fitnessValue);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


