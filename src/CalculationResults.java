public class CalculationResults {
    private RunwayConfig recalculatedParams;
    private String calculationDetails;

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
