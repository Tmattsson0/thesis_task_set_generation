package util;

import model.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import java.util.ArrayList;
import java.util.List;

public class XmlUtil {

    public static PlatformModel readPlatformModelConfig(String platformModelFilePath) throws IOException, FileNotFoundException, JAXBException, SAXException, ParserConfigurationException {
        if (!Files.exists(Paths.get(platformModelFilePath))){
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

                        for (int k = 0; k < cores.getLength(); k++){
                            Node coreNode = cores.item(k);

                            if(cpuNode.getNodeType() == Node.ELEMENT_NODE){
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

                if (switchNode.getNodeType() == Node.ELEMENT_NODE){
                    Element switchElement = (Element) switchNode;

                    Switch sw = new Switch();

                    sw.setId(switchElement.getAttribute("id"));

                    NodeList systems = switchElement.getElementsByTagName("systems");
                    NodeList connections = switchElement.getElementsByTagName("connections");

                    List<String> systemList = new ArrayList<>();
                    List<String> connectionList = new ArrayList<>();

                    Node systemNode = systems.item(0);
                    Node connectionNode = connections.item(0);

                    if (systemNode.getNodeType() == Node.ELEMENT_NODE){
                        Element systemElement = (Element) systemNode;

                        NodeList esIds = systemElement.getElementsByTagName("EndsystemId");

                        for (int j = 0; j < esIds.getLength(); j++) {
                            Node endSystemId = esIds.item(j);

                            if (endSystemId.getNodeType() == Node.ELEMENT_NODE){
                                Element endSystemIdElement = (Element) endSystemId;
                                String endSystemIdString = endSystemIdElement.getTextContent();
                                systemList.add(endSystemIdString);
                            }
                        }
                    }

                    if (connectionNode.getNodeType() == Node.ELEMENT_NODE){
                        Element connectionElement = (Element) connectionNode;
                        NodeList connIds = connectionElement.getElementsByTagName("connection");

                        for (int j = 0; j < connIds.getLength(); j++) {
                            Node connId = connIds.item(j);

                            if (connId.getNodeType() == Node.ELEMENT_NODE){
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

    public static void writePlatformModelConfig(PlatformModel platformModel, String filePath) throws JAXBException {

        JAXBContext context = JAXBContext.newInstance(PlatformModel.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(platformModel, new File(filePath));

    }
}
