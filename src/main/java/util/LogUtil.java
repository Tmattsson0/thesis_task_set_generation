package util;

import model.Core;
import model.PlatformModel;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Objects;

public class LogUtil {
    static String testCasesLogFilePath = "/Users/thomasmattsson/Documents/GitHub/thesis_task_set_generation/testCases";
    static String fileName = "test";


    public static void writeToLog(String str) throws IOException {
        File file = new File(testCasesLogFilePath + File.separator + LocalDate.now() + File.separator + fileName + ".log");
        if (!file.isFile()) {
            Files.createDirectories(Paths.get(testCasesLogFilePath + File.separator + LocalDate.now()));
        }

        try (FileWriter fileWriter = new FileWriter(file, true)) {
            PrintWriter writer = new PrintWriter(fileWriter);
            for (String s : str.split("\\r?\\n")){
                writer.write((s));
                writer.println();
            }
            writer.println();
            writer.println();
            writer.println();
        }
    }

    public static void initialAddToLog(PlatformModel initialSolution) {
        try {
            writeToLog(XmlUtil.getTaskListWithUtil(initialSolution));
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addLineToLog(PlatformModel solution, PlatformModel oldSolutionForLogging) {
        if (!(solution == null) && !(oldSolutionForLogging == null)){
            try {
                String oldTotalUtil = String.valueOf(BigDecimal.valueOf(oldSolutionForLogging.getAllCores().stream().mapToDouble(Core::calculateUtil).sum()).setScale(3, RoundingMode.HALF_UP).doubleValue());
                String oldAverageUtil = String.valueOf(BigDecimal.valueOf(oldSolutionForLogging.getAllCores().stream().mapToDouble(Core::calculateUtil).average().stream().sum()).setScale(3, RoundingMode.HALF_UP).doubleValue());
                String newTotalUtil = String.valueOf(BigDecimal.valueOf(solution.getAllCores().stream().mapToDouble(Core::calculateUtil).sum()).setScale(3, RoundingMode.HALF_UP).doubleValue());
                String newAverageUtil = String.valueOf(BigDecimal.valueOf(solution.getAllCores().stream().mapToDouble(Core::calculateUtil).average().stream().sum()).setScale(3, RoundingMode.HALF_UP).doubleValue());

                String differences = findDifferences(solution, oldSolutionForLogging);

//                writeToLog("Old Average Util: " + oldAverageUtil + "\t" + "Old Total Util: " + oldTotalUtil);
//                writeToLog("New Average Util: " + newAverageUtil + "\t" + "New Total Util: " + newTotalUtil);
//
                writeToLog(differences);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String findDifferences(PlatformModel solution, PlatformModel oldSolutionForLogging) {
        try {
            Reader newSolutionString = new StringReader(XmlUtil.getTaskListWithUtil(solution));
            Reader oldSolutionString = new StringReader(XmlUtil.getTaskListWithUtil(oldSolutionForLogging));

            BufferedReader reader1 = new BufferedReader(newSolutionString);
            BufferedReader reader2 = new BufferedReader(oldSolutionString);

            String newLine;
            String oldLine;
            StringBuilder returnString = new StringBuilder();
            StringBuilder oldChange = new StringBuilder();
            StringBuilder newChange = new StringBuilder();

            while ((newLine = reader1.readLine()) != null && (oldLine = reader2.readLine()) != null)
            {
                if (!newLine.equals(oldLine))
                {
                    oldChange.append(oldLine).append("\n");
                    newChange.append(newLine).append("\n");
                }
            }

            returnString
                    .append("Old solution: ")
                    .append(oldChange)
                    .append("\n")
                    .append("New solution: ")
                    .append(newChange);

            return returnString.toString();

        } catch (ParseException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteLogFile(){
        File file = new File(testCasesLogFilePath + File.separator + LocalDate.now() + File.separator + fileName + ".log");
        if (file.isFile()) {
            file.delete();
        }
    }
}
