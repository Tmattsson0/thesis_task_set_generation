package util;

import model.PlatformModel;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDate;

public class LogUtil {
    static String testCasesLogFilePath = "/Users/thomasmattsson/Documents/GitHub/thesis_task_set_generation/testCases";
    static String fileName = "test_newFit";

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
                String differences = findDifferences(solution, oldSolutionForLogging);

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
