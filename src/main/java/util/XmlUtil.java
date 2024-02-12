package util;

import model.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;


public class XmlUtil {

    public static PlatformModel readPlatformModelConfig(String platformModelFilePath) throws IOException, SAXException, ParserConfigurationException {
        if (!Files.exists(Paths.get(platformModelFilePath))) {
            throw new FileNotFoundException();
        }

        PlatformModel platformModel = new PlatformModel();

        List<EndSystem> endSystemList = new ArrayList<>();

        File fXmlFile = new File(platformModelFilePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();

        NodeList endSystems = doc.getElementsByTagName("EndSystem");

        for (int i = 0; i < endSystems.getLength(); i++) {
            Node endSystemNode = endSystems.item(i);

            if (endSystemNode.getNodeType() == Node.ELEMENT_NODE) {
                Element endSystemElement = (Element) endSystemNode;

                EndSystem endSystem = new EndSystem();
                endSystem.setId(endSystemElement.getAttribute("id"));

                NodeList cpus = endSystemElement.getElementsByTagName("Cpu");

                List<CPU> cpuList = new ArrayList<>();

                for (int j = 0; j < cpus.getLength(); j++) {
                    Node cpuNode = cpus.item(j);

                    if (cpuNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element cpuElement = (Element) cpuNode;

                        CPU cpu = new CPU(cpuElement.getAttribute("Name"), cpuElement.getAttribute("id"));

                        List<Core> coreList = new ArrayList<>();

                        NodeList cores = cpuElement.getElementsByTagName("Core");

                        for (int k = 0; k < cores.getLength(); k++) {
                            Node coreNode = cores.item(k);

                            if (cpuNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element coreElement = (Element) coreNode;

                                Core core = new Core(coreElement.getAttribute("Name"),
                                        coreElement.getAttribute("id"),
                                        Integer.parseInt(coreElement.getAttribute("MicroTick")),
                                        convertScheduleType(coreElement.getAttribute("ScheduleType")));
                                coreList.add(core);
                            }
                        }
                        cpu.setCoreList(coreList);
                        cpuList.add(cpu);
                    }
                }
                endSystem.setCpuList(cpuList);
                endSystemList.add(endSystem);
            }
        }

        NodeList topology = doc.getElementsByTagName("Topology");

        Node topologyNode = topology.item(0);

        Topology top = new Topology();

        if (topologyNode.getNodeType() == Node.ELEMENT_NODE) {
            Element topologyElement = (Element) topologyNode;

            NodeList switches = topologyElement.getElementsByTagName("Switch");

            List<Switch> switchList = new ArrayList<>();

            for (int i = 0; i < switches.getLength(); i++) {
                Node switchNode = switches.item(i);

                if (switchNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element switchElement = (Element) switchNode;

                    Switch sw = new Switch();

                    sw.setId(switchElement.getAttribute("id"));

                    NodeList systems = switchElement.getElementsByTagName("systems");
                    NodeList connections = switchElement.getElementsByTagName("connections");

                    List<String> systemList = new ArrayList<>();
                    List<String> connectionList = new ArrayList<>();

                    Node systemNode = systems.item(0);
                    Node connectionNode = connections.item(0);

                    if (systemNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element systemElement = (Element) systemNode;

                        NodeList esIds = systemElement.getElementsByTagName("EndsystemId");

                        for (int j = 0; j < esIds.getLength(); j++) {
                            Node endSystemId = esIds.item(j);

                            if (endSystemId.getNodeType() == Node.ELEMENT_NODE) {
                                Element endSystemIdElement = (Element) endSystemId;
                                String endSystemIdString = endSystemIdElement.getTextContent();
                                systemList.add(endSystemIdString);
                            }
                        }
                    }

                    if (connectionNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element connectionElement = (Element) connectionNode;
                        NodeList connIds = connectionElement.getElementsByTagName("connection");

                        for (int j = 0; j < connIds.getLength(); j++) {
                            Node connId = connIds.item(j);

                            if (connId.getNodeType() == Node.ELEMENT_NODE) {
                                Element connIdElement = (Element) connId;
                                String connIsString = connIdElement.getTextContent();
                                systemList.add(connIsString);
                            }
                        }
                    }
                    sw.setConnectedSystemIds(systemList);
                    sw.setConnections(connectionList);
                    switchList.add(sw);
                }
            }
            top.setSwitches(switchList);
        }

        platformModel.setEndSystems(endSystemList);
        platformModel.setTopology(top);

        return platformModel;
    }

    private static ScheduleType convertScheduleType(String scheduleType) {
        return switch (scheduleType) {
            case "TT + ET" -> ScheduleType.TTET;
            case "TT" -> ScheduleType.TT;
            case "ET" -> ScheduleType.ET;
            default -> ScheduleType.NONE;
        };
    }

    public static void writeTaskList(PlatformModel platformModel, String fileName) {
        String testCasesFilePath = "./testCases";
        Document dom;
        Element element;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            //File stuff
            File file = new File(testCasesFilePath + File.separator + LocalDate.now() + File.separator + fileName + ".xml");
            Files.createDirectories(Paths.get(testCasesFilePath + File.separator + LocalDate.now()));

            //XML stuff
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.newDocument();

            Element rootEle = dom.createElement("Taskset");

            List<? extends Task> tasks = platformModel.getAllTasks();

            tasks.sort(Comparator.comparingInt((Task o) -> Integer.parseInt(o.getId())));

            for (Task t : tasks) {
                element = dom.createElement("Task");
                element.setAttribute("A__Id", t.getId());
                element.setAttribute("B__Name", t.getName());
                if (t instanceof TTtask) {element.setAttribute("C__Type", String.valueOf(((TTtask) t).getTaskType()));}
                if (t instanceof ETtask) {element.setAttribute("C__Type", String.valueOf(((ETtask) t).getTaskType()));}
                element.setAttribute("D__WCET", String.valueOf(t.getWcet()));
                if (t instanceof TTtask) {element.setAttribute("E__Period", String.valueOf(t.getPeriod()));}
                if (t instanceof ETtask) {element.setAttribute("E__MIT", String.valueOf(t.getPeriod()));}
                element.setAttribute("F__Deadline", String.valueOf(t.getDeadline()));
                element.setAttribute("G__MaxJitter", String.valueOf(t.getMaxJitter()));
                if (t instanceof TTtask) {element.setAttribute("H__Offset", String.valueOf(((TTtask) t).getOffset()));}
                if (t instanceof TTtask){element.setAttribute("I__Priority", String.valueOf(((TTtask) t).getPriority()));}
                if (t instanceof ETtask){element.setAttribute("I__Priority", String.valueOf(((ETtask) t).getPriority()));}
                element.setAttribute("J__CpuId", t.getCpuId());
                element.setAttribute("K__CoreId", t.getCoreId());
                element.setAttribute("L__CoreAffinity", Arrays.toString(t.getCoreAffinity()));

                rootEle.appendChild(element);

            }

            dom.appendChild(rootEle);

            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                StringWriter writer = new StringWriter();

                tr.transform(new DOMSource(dom),
                        new StreamResult(writer));

                //Custom manipulation of string (sorting and stuff):_
                String output = writer.getBuffer().toString();

                output = output.replaceAll("[A-Z]__", "");

                FileOutputStream outputStream = new FileOutputStream(file);

                byte[] strToBytes = output.getBytes();
                outputStream.write(strToBytes);

                outputStream.close();

            } catch (TransformerException te) {
                System.out.println(te.getMessage());
            }
        } catch (IOException e) {
            System.out.println("I failed");
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeTaskListWithUtil(PlatformModel platformModel, String fileName) {
        String testCasesFilePath = "./testCases";
        Document dom;
        Element element;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            //File stuff
            File file = new File(testCasesFilePath + File.separator + LocalDate.now() + File.separator + fileName + ".xml");
            Files.createDirectories(Paths.get(testCasesFilePath + File.separator + LocalDate.now()));

            //XML stuff
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.newDocument();

            Element rootEle = dom.createElement("Taskset");
            rootEle.setAttribute("A__TotalTTUtil", String.valueOf(BigDecimal.valueOf(platformModel.getAllCores().stream().mapToDouble(Core::calculateTTUtil).sum()).setScale(3, RoundingMode.HALF_UP).doubleValue()));

            rootEle.setAttribute("B__TotalETUtil", String.valueOf(BigDecimal.valueOf(platformModel.getAllCores().stream().mapToDouble(Core::calculateETUtil).sum()).setScale(3, RoundingMode.HALF_UP).doubleValue()));

            rootEle.setAttribute("C__Fitness", String.valueOf(BigDecimal.valueOf(platformModel.getFitness()).setScale(4, RoundingMode.HALF_UP)));



            List<? extends Task> tasks = platformModel.getAllTasks();

            tasks.sort(Comparator.comparingInt((Task o) -> Integer.parseInt(o.getId())));

            for (Task t : tasks) {
                element = dom.createElement("Task");
                element.setAttribute("A__Id", t.getId());
                element.setAttribute("B__Name", t.getName());
                if (t instanceof TTtask) {element.setAttribute("C__Type", String.valueOf(((TTtask) t).getTaskType()));}
                if (t instanceof ETtask) {element.setAttribute("C__Type", String.valueOf(((ETtask) t).getTaskType()));}
                element.setAttribute("D__WCET", String.valueOf(t.getWcet()));
                if (t instanceof TTtask) {element.setAttribute("E__Period", String.valueOf(t.getPeriod()));}
                if (t instanceof ETtask) {element.setAttribute("E__MIT", String.valueOf(t.getPeriod()));}
                element.setAttribute("F__Deadline", String.valueOf(t.getDeadline()));
                element.setAttribute("G__MaxJitter", String.valueOf(t.getMaxJitter()));
                if (t instanceof TTtask) {element.setAttribute("H__Offset", String.valueOf(((TTtask) t).getOffset()));}
                if (t instanceof TTtask){element.setAttribute("I__Priority", String.valueOf(((TTtask) t).getPriority()));}
                if (t instanceof ETtask){element.setAttribute("I__Priority", String.valueOf(((ETtask) t).getPriority()));}
                element.setAttribute("J__CpuId", t.getCpuId());
                element.setAttribute("K__CoreId", t.getCoreId());
                element.setAttribute("L__CoreAffinity", Arrays.toString(t.getCoreAffinity()));

                rootEle.appendChild(element);
            }

            for (Core c : platformModel.getAllCores()){
                element = dom.createElement("Core");
                element.setAttribute("A__Id", c.getId());
                element.setAttribute("B__Name", c.getName());
                element.setAttribute("C__TTUtil", String.valueOf(c.calculateTTUtil()));
                element.setAttribute("D__ETUtil", String.valueOf(c.calculateETUtil()));
                rootEle.appendChild(element);

            }
            dom.appendChild(rootEle);

            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                StringWriter writer = new StringWriter();

                tr.transform(new DOMSource(dom),
                        new StreamResult(writer));

                //Custom manipulation of string (sorting and stuff):
                String output = writer.getBuffer().toString();

                output = output.replaceAll("[A-Z]__", "");

                FileOutputStream outputStream = new FileOutputStream(file);

                byte[] strToBytes = output.getBytes();
                outputStream.write(strToBytes);

                outputStream.close();

            } catch (TransformerException te) {
                System.out.println(te.getMessage());
            }
        } catch (IOException e) {
            System.out.println("I failed");
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeTaskListWithUtilAndChains(PlatformModel platformModel, String fileName) {
        String testCasesFilePath = "./testCases";
        Document dom;
        Element element;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            //File stuff
            File file = new File(testCasesFilePath + File.separator + LocalDate.now() + File.separator + fileName + ".xml");
            Files.createDirectories(Paths.get(testCasesFilePath + File.separator + LocalDate.now()));

            //XML stuff
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.newDocument();

            Element rootEle = dom.createElement("Taskset");
            rootEle.setAttribute("A__TotalTTUtil", String.valueOf(BigDecimal.valueOf(platformModel.getAllCores().stream().mapToDouble(Core::calculateTTUtil).sum()).setScale(3, RoundingMode.HALF_UP).doubleValue()));

            rootEle.setAttribute("B__TotalETUtil", String.valueOf(BigDecimal.valueOf(platformModel.getAllCores().stream().mapToDouble(Core::calculateETUtil).sum()).setScale(3, RoundingMode.HALF_UP).doubleValue()));

            rootEle.setAttribute("C__Fitness", String.valueOf(BigDecimal.valueOf(platformModel.getFitness()).setScale(4, RoundingMode.HALF_UP)));



            List<? extends Task> tasks = platformModel.getAllTasks();

            tasks.sort(Comparator.comparingInt((Task o) -> Integer.parseInt(o.getId())));

            for (Task t : tasks) {
                element = dom.createElement("Task");
                element.setAttribute("A__Id", t.getId());
                element.setAttribute("B__Name", t.getName());
                if (t instanceof TTtask) {element.setAttribute("C__Type", String.valueOf(((TTtask) t).getTaskType()));}
                if (t instanceof ETtask) {element.setAttribute("C__Type", String.valueOf(((ETtask) t).getTaskType()));}
                element.setAttribute("D__WCET", String.valueOf(t.getWcet()));
                if (t instanceof TTtask) {element.setAttribute("E__Period", String.valueOf(t.getPeriod()));}
                if (t instanceof ETtask) {element.setAttribute("E__MIT", String.valueOf(t.getPeriod()));}
                element.setAttribute("F__Deadline", String.valueOf(t.getDeadline()));
                element.setAttribute("G__MaxJitter", String.valueOf(t.getMaxJitter()));
                if (t instanceof TTtask) {element.setAttribute("H__Offset", String.valueOf(((TTtask) t).getOffset()));}
                if (t instanceof TTtask){element.setAttribute("I__Priority", String.valueOf(((TTtask) t).getPriority()));}
                if (t instanceof ETtask){element.setAttribute("I__Priority", String.valueOf(((ETtask) t).getPriority()));}
                element.setAttribute("J__CpuId", t.getCpuId());
                element.setAttribute("K__CoreId", t.getCoreId());
                element.setAttribute("L__CoreAffinity", Arrays.toString(t.getCoreAffinity()));

                rootEle.appendChild(element);
            }

            for (Core c : platformModel.getAllCores()){
                element = dom.createElement("Core");
                element.setAttribute("A__Id", c.getId());
                element.setAttribute("B__Name", c.getName());
                element.setAttribute("C__TTUtil", String.valueOf(c.calculateTTUtil()));
                element.setAttribute("D__ETUtil", String.valueOf(c.calculateETUtil()));
                rootEle.appendChild(element);

            }

            for (Chain c : platformModel.getChains()) {
                element = dom.createElement("Chain");
                element.setAttribute("A__Latency", String.valueOf(c.getLatency()));
                element.setAttribute("B__Priority", String.valueOf(c.getPriority()));
                element.setAttribute("C__Name", c.getName());
                element.setAttribute("D__Fitness", String.valueOf(c.getFitness()));
                for (Task t : c.getTasks()) {
                    Element taskElement = dom.createElement("Runnable");
                    taskElement.setAttribute("A__Name", t.getName());
                    element.appendChild(taskElement);
                }
                rootEle.appendChild(element);
            }

            dom.appendChild(rootEle);

            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                StringWriter writer = new StringWriter();

                tr.transform(new DOMSource(dom),
                        new StreamResult(writer));

                //Custom manipulation of string (sorting and stuff):
                String output = writer.getBuffer().toString();

                output = output.replaceAll("[A-Z]__", "");

                FileOutputStream outputStream = new FileOutputStream(file);

                byte[] strToBytes = output.getBytes();
                outputStream.write(strToBytes);

                outputStream.close();

            } catch (TransformerException te) {
                System.out.println(te.getMessage());
            }
        } catch (IOException e) {
            System.out.println("I failed");
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getTaskListWithUtil(PlatformModel platformModel) throws ParseException {
        Document dom;
        Element element;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            //XML stuff
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.newDocument();

            Element rootEle = dom.createElement("Taskset");
            rootEle.setAttribute("A__TotalTTUtil", String.valueOf(BigDecimal.valueOf(platformModel.getAllCores().stream().mapToDouble(Core::calculateTTUtil).sum()).setScale(3, RoundingMode.HALF_UP).doubleValue()));

            rootEle.setAttribute("B__TotalETUtil", String.valueOf(BigDecimal.valueOf(platformModel.getAllCores().stream().mapToDouble(Core::calculateETUtil).sum()).setScale(3, RoundingMode.HALF_UP).doubleValue()));

            rootEle.setAttribute("C__Fitness", String.valueOf(BigDecimal.valueOf(platformModel.getFitness()).setScale(4, RoundingMode.HALF_UP)));

            List<? extends Task> tasks = platformModel.getAllTasks();

            tasks.sort(Comparator.comparingInt((Task o) -> Integer.parseInt(o.getId())));

            for (Task t : tasks) {
                element = dom.createElement("Task");
                element.setAttribute("A__Id", t.getId());
                element.setAttribute("B__Name", t.getName());
                if (t instanceof TTtask) {element.setAttribute("C__Type", String.valueOf(((TTtask) t).getTaskType()));}
                if (t instanceof ETtask) {element.setAttribute("C__Type", String.valueOf(((ETtask) t).getTaskType()));}
                element.setAttribute("D__WCET", String.valueOf(t.getWcet()));
                if (t instanceof TTtask) {element.setAttribute("E__Period", String.valueOf(t.getPeriod()));}
                if (t instanceof ETtask) {element.setAttribute("E__MIT", String.valueOf(t.getPeriod()));}
                element.setAttribute("F__Deadline", String.valueOf(t.getDeadline()));
                element.setAttribute("G__MaxJitter", String.valueOf(t.getMaxJitter()));
                if (t instanceof TTtask) {element.setAttribute("H__Offset", String.valueOf(((TTtask) t).getOffset()));}
                if (t instanceof TTtask){element.setAttribute("I__Priority", String.valueOf(((TTtask) t).getPriority()));}
                if (t instanceof ETtask){element.setAttribute("I__Priority", String.valueOf(((ETtask) t).getPriority()));}
                element.setAttribute("J__CpuId", t.getCpuId());
                element.setAttribute("K__CoreId", t.getCoreId());
                element.setAttribute("L__CoreAffinity", Arrays.toString(t.getCoreAffinity()));

                rootEle.appendChild(element);
            }

            for (Core c : platformModel.getAllCores()){
                element = dom.createElement("Core");
                element.setAttribute("A__Id", c.getId());
                element.setAttribute("B__Name", c.getName());
                element.setAttribute("C__TTUtil", String.valueOf(c.calculateTTUtil()));
                element.setAttribute("D__ETUtil", String.valueOf(c.calculateETUtil()));
                rootEle.appendChild(element);

            }
            dom.appendChild(rootEle);


            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            StringWriter writer = new StringWriter();

            tr.transform(new DOMSource(dom),
                    new StreamResult(writer));

            //Custom manipulation of string (sorting and stuff):
            String output = writer.getBuffer().toString();

            return output.replaceAll("[A-Z]__", "");

        } catch (TransformerException | ParserConfigurationException te) {
            System.out.println(te.getMessage());
            throw new ParseException("Error", 0);
        }
    }
}
