package util;

import com.opencsv.CSVWriter;
import model.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/*
Should get naming scheme from what parameters are used anc create the subfolder for it.
-testCases
--->Date_subfolder
------>file
 */

public class CsvWriter {

    public static void writeDataAtOnce(String testCasesFilePath, List<Task> data, String parameterSubFolderName, String fileName)
    {
        // first create file object for file placed at location
        // specified by filepath

        try {

            File file = new File(testCasesFilePath + File.separator + java.time.LocalDate.now() + "_" + parameterSubFolderName + File.separator + fileName + ".csv");

            Files.createDirectories(Paths.get(testCasesFilePath + File.separator + java.time.LocalDate.now() + "_" + parameterSubFolderName));

            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);

            // create a List which contains String array
            List<String[]> textData = csvTaskToStringConverter(data);

            textData.add(0, new String[] { "tasks", "id", "name", "WCET", "period", "deadline", "maxJitter", "offset", "CpuID", "CoreID", "type", "priority"});
            writer.writeAll(textData);

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("I failed");
            e.printStackTrace();
        }
    }

    private static List<String[]> csvTaskToStringConverter(List<Task> taskSet){
        List<String[]> stringTaskSet = new ArrayList<>();

        for (Task task : taskSet){
            stringTaskSet.add(new String[] {
                    "",
                    task.getId(),
                    task.getName(),
                    String.valueOf(task.getWcet()),
                    String.valueOf(task.getPeriod()),
                    String.valueOf(task.getDeadline()),
                    String.valueOf(task.getMaxJitter()),
                    String.valueOf(task.getOffset()),
                    String.valueOf(task.getCpuId()),
                    String.valueOf(task.getCoreId()),
                    task.getTaskType().toString(),
                    String.valueOf(task.getPriority())
            });
        }
            return stringTaskSet;
    }
}
