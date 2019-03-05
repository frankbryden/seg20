public class Calculations {
    private RunwayConfig originalConfig;
    private final int RESA = 240;
    private final int STRIP = 60;
    public enum Direction {TOWARDS, AWAY};
    private StringBuilder calcSummary;

    public Calculations(RunwayConfig runwayConfig){
        this.originalConfig = runwayConfig;
    }

    public RunwayConfig recalculateParams(Obstacle obstacle, int distanceFromThreshold, Direction direction){
        //We need to determine what side of the runway the obstacle is lying before performing any calculations.
        int recalculatedTORA;
        int recalculatedTODA;
        int recalculatedASDA;
        int recalculatedLDA;
        beginCalculation();
        if (direction == Direction.TOWARDS){
            // In the case of taking off towards from the obstacle / landing over it
            //TORA
            recalculatedTORA = originalConfig.getTORA() - 300 - distanceFromThreshold;
            addCalcStep("TORA = " + originalConfig.getTORA() + " - " + 300 + " - " + distanceFromThreshold);

            //TODA
            recalculatedTODA = recalculatedTORA + originalConfig.getClearway();
            addCalcStep("TODA = " + recalculatedTORA + " + " + originalConfig.getClearway());

            //ASDA
            recalculatedASDA = recalculatedTORA + originalConfig.getStopway();
            addCalcStep("ASDA = " + recalculatedTORA + " + " + originalConfig.getStopway());

            //LDA
            recalculatedLDA = originalConfig.getLDA() - distanceFromThreshold - (obstacle.getHeight() * 50) - STRIP;
            addCalcStep("LDA = " + originalConfig.getLDA() + " - " + distanceFromThreshold + " - (" + obstacle.getHeight() + " * 50) - " + STRIP);
        } else {
            // In the case of taking off and landing towards the obstacle
            recalculatedTORA = originalConfig.getTORA() - originalConfig.getDisplacementThreshold() - distanceFromThreshold - (obstacle.getHeight() * 50) - STRIP;
            recalculatedTODA = recalculatedTORA;
            recalculatedASDA = recalculatedTORA;
            recalculatedLDA = originalConfig.getLDA() - distanceFromThreshold - RESA - STRIP;
        }

        System.out.println(getCalculationResults());

        return new RunwayConfig(originalConfig.getRunwayDesignator(), recalculatedTORA, recalculatedTODA, recalculatedASDA, recalculatedLDA, originalConfig.getDisplacementThreshold());
    }

    private void beginCalculation(){
        this.calcSummary = new StringBuilder();
    }
    private void addCalcStep(String step){
        this.calcSummary.append(step).append("\n");
    }
    private String getCalculationResults(){
        return this.calcSummary.toString();
    }
}
