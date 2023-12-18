package cucumberStepDefs;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import taskEngine.WcetGenerator;
import testData.TestSingleton;
import util.TestInitializer;
import util.specificPeriodToll;

import java.util.ArrayList;
import java.util.List;

public class WcetGenSteps {
    TestSingleton s = TestSingleton.getInstance();
    double util;
    double lowerTaskUtilBound;
    double upperTaskUtilBound;
    int numOfPeriods;
    int[] periods;
    WcetGenerator wcetGenerator;

    @Before
    public void setup(){
        TestInitializer.initializeOnlyParametersFile();
    }

    @Given("a set of {int} periods, a desired utilization of {double} and individual task util bounds of {double} and {double}")
    public void aSetOfPeriodsADesiredUtilizationOfAndIndividualTaskUtilBoundsOfAnd(int numOfPeriods, double util, double lowerTaskUtilBound, double upperTaskUtilBound) {
        this.numOfPeriods = numOfPeriods;
        this.util = util;
        this.lowerTaskUtilBound = lowerTaskUtilBound;
        this.upperTaskUtilBound = upperTaskUtilBound;
    }

    @When("the wcet generator is used")
    public void theWcetGeneratorIsUsed() {
        wcetGenerator = new WcetGenerator(specificPeriodToll.getSpecificPeriods(s.PERIODS, s.NUM_OF_TT_TASKS), this.util, s.NUM_OF_TT_TASKS, lowerTaskUtilBound, upperTaskUtilBound);
    }

    @Then("a set of wcets is produced that is within individual task util bounds of {double} and {double}")
    public void aSetOfWcetsIsProducedThatIsWithinIndividualTaskUtilBoundsOfAnd(double arg0, double arg1) {

        List<Integer> wcets = new ArrayList<>();

        while (wcets.isEmpty()) {
            wcets = wcetGenerator.generateRandomWCETValuesHC();
        }
        System.out.println("wcets: + " + wcets);
    }
}
