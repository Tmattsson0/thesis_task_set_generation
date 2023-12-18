import data.Singleton;
import model.PlatformModel;
import model.Task;
import taskEngine.WcetGenerator;
import util.ConfigInitializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main {
    public static void main(String[] args) {

        ConfigInitializer.initialize();

        Singleton s = Singleton.getInstance();
        int[] specificP = util.specificPeriodToll.getSpecificPeriods(s.PERIODS, 100);

        WcetGenerator w = new WcetGenerator(specificP, 0.7, specificP.length,0.001, 0.6);

        List<Integer> list = new ArrayList<>();

        list = w.generateRandomWCETValuesHC();

        System.out.println(list);
    }
}