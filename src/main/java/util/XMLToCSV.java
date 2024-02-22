package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

    public class XMLToCSV {

        public static void main(String[] args) {
            Map<String, Map<String, Double>> dataMap = new LinkedHashMap<>();

            for (int j = 1; j <= 5; j++) {
                for (int i = 0; i < 10; i++) {
                    String fileName = "testCases/SA_test/task_set_gen_SA_param" + j + "_seed_" + i + ".xml";
                    try {
                        File file = new File(fileName);
                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                        Document doc = dBuilder.parse(file);
                        doc.getDocumentElement().normalize();

                        Element taskSetElement = (Element) doc.getElementsByTagName("Taskset").item(0);
                        double fitness = Double.parseDouble(taskSetElement.getAttribute("Fitness"));
                        double timeForTasks = Double.parseDouble(taskSetElement.getAttribute("time_for_tasks"));
                        double timeForChains = Double.parseDouble(taskSetElement.getAttribute("time_for_chains"));

                        Map<String, Double> values = new LinkedHashMap<>();
                        values.put("Fitness", fitness);
                        values.put("time_for_tasks", timeForTasks);
                        values.put("time_for_chains", timeForChains);

                        dataMap.put(fileName, values);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            writeDataToCSV(dataMap);
        }

        private static void writeDataToCSV(Map<String, Map<String, Double>> dataMap) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("testCases/SA_test/output.csv"))) {
                // Write header
                writer.write("File Name,Fitness,time_for_tasks,time_for_chains\n");

                // Write data
                for (Map.Entry<String, Map<String, Double>> entry : dataMap.entrySet()) {
                    String fileNameWithoutPrefix = entry.getKey().replace("testCases/SA_test/", "").replace(".xml", "");
                    writer.write(fileNameWithoutPrefix + ";");
                    Map<String, Double> values = entry.getValue();
                    DecimalFormat europeanFormat = new DecimalFormat("#,##0.00");
                    writer.write(europeanFormat.format(values.get("Fitness")) + ";");
                    writer.write(europeanFormat.format(values.get("time_for_tasks")) + ";");
                    writer.write(europeanFormat.format(values.get("time_for_chains")) + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
