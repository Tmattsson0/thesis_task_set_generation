package taskEngine;

import data.Singleton;
import model.Core;
import model.PlatformModel;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FitnessCalculator {

    static Singleton s = Singleton.getInstance();

    public static double calculateFitness(PlatformModel candidate){
        //Sum deltas of tt and et util.
        //Add large penalty value if core has less than 0.1% util of tt or et.

        double fitness = 0;

        for (Core c : candidate.getAllCores()){
            if (c.getScheduleType().getValue().contains("TT")) {
                fitness += Math.abs(c.calculateTTUtil() - s.TT_UTILIZATION);
            } else if (c.getScheduleType().getValue().contains("ET")) {
                fitness += Math.abs(c.calculateETUtil() - s.ET_UTILIZATION);
            }
        }

        for (Core c : candidate.getAllCores()) {
            if (c.calculateETUtil() <= 0.01 && c.getScheduleType().getValue().contains("ET")) {
                fitness += 5;
            } else if (c.calculateTTUtil() <= 0.01 && c.getScheduleType().getValue().contains("TT")) {
                fitness += 5;
            }
        }
        return new BigDecimal(fitness).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }
}
