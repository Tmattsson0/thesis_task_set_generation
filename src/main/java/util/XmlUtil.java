package util;

import model.PlatformModel;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class XmlUtil {

    public static PlatformModel readPlatformModelConfig(String platformModelFilePath) throws IOException, FileNotFoundException, JAXBException {
        if (!Files.exists(Paths.get(platformModelFilePath))){
            throw new FileNotFoundException();
        }
        File file = new File(platformModelFilePath);
        JAXBContext context = JAXBContext.newInstance(PlatformModel.class);
        return (PlatformModel) context.createUnmarshaller()
                .unmarshal(new FileReader(platformModelFilePath));
    }

    public static void writePlatformModelConfig(PlatformModel platformModel, String filePath) throws JAXBException {

        JAXBContext context = JAXBContext.newInstance(PlatformModel.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(platformModel, new File(filePath));

    }
}
