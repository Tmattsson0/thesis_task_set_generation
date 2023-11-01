package cucumberStepDefs;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import model.Task;
import model.TaskType;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import static util.CsvWriter.writeDataAtOnce;

public class CsvSteps {
    public String filepath = "/Users/thomasmattsson/Documents/GitHub/thesis_task_set_generation/testCasesTest";
    public List<Task> testCase =  new ArrayList<>();
    public String parameterName = "testParameterName";
    public String fileName = "testFileName";

    @Before(value="@saveCsv")
    public void beforeScenario() throws IOException {
        FileUtils.cleanDirectory(Paths.get(filepath).toFile());
    }

    @Given("an empty testCasesTest directory")
    public void theCsvMethodIsCalled() throws IOException {
        Assertions.assertTrue(isEmpty(Paths.get(filepath)));
    }

    @When("the csv method is called")
    public void aCsvFileAndASubfolderDoesNotExist() {
        testCase.add(new Task("1", "TTt01", 360, 80000, 80000, -1, 0, 0, 0, TaskType.TT, 1));
        testCase.add(new Task("2", "TTt02", 360, 80000, 80000, -1, 0, 0, 0, TaskType.TT, 1));
        testCase.add(new Task("3", "TTt03", 360, 80000, 80000, -1, 0, 0, 0, TaskType.TT, 1));
        testCase.add(new Task("4", "TTt04", 360, 80000, 80000, -1, 0, 0, 0, TaskType.TT, 1));
        testCase.add(new Task("5", "TTt05", 360, 80000, 80000, -1, 0, 0, 0, TaskType.TT, 1));
        writeDataAtOnce(filepath, testCase, parameterName, fileName);
    }

    @Then("a file with the correct filename is created in a subfolder matching parameter name")
    public void aFileWithTheCorrectFilenameIsCreatedInASubfolderMatchingParameterName() {
        System.out.println(java.time.LocalDate.now());
        File f = new File(filepath
                + File.separator
                + java.time.LocalDate.now() + "_"
                + parameterName);

        File file = new File(filepath
                + File.separator
                + java.time.LocalDate.now() + "_"
                + parameterName
                + File.separator
                + fileName
                + ".csv");

        Assertions.assertTrue(f.isDirectory());
        Assertions.assertTrue(file.exists());
    }

    public boolean isEmpty(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> directory = Files.newDirectoryStream(path)) {
                return !directory.iterator().hasNext();
            }
        }
        return false;
    }
}
