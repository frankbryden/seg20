public class CalculationResults {
    private final RunwayConfig recalculatedParams;
    private final String calculationDetails;

    public CalculationResults(RunwayConfig recalculatedParams, String calculationDetails) {
        this.recalculatedParams = recalculatedParams;
        this.calculationDetails = calculationDetails;
    }

    public RunwayConfig getRecalculatedParams() {
        return recalculatedParams;
    }

    public String getCalculationDetails() {
        return calculationDetails;
    }
}
