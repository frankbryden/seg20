public class Calculations {
    private RunwayConfig originalConfig;
    private final int RESA = 240;
    private final int STRIP = 60;
    private final int BLAST_PROTECTION = 300;
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
            recalculatedTORA = distanceFromThreshold + originalConfig.getDisplacementThreshold() - (obstacle.getHeight() * 50) - STRIP;
            addCalcStep("TORA = " + distanceFromThreshold + " + " + originalConfig.getDisplacementThreshold() + " - (" + obstacle.getHeight() + " * 50) - " + STRIP);

            //TODA
            recalculatedTODA = recalculatedTORA + originalConfig.getClearway();
            addCalcStep("TODA = " + recalculatedTORA + " + " + originalConfig.getClearway());

            //ASDA
            recalculatedASDA = recalculatedTORA + originalConfig.getStopway();
            addCalcStep("ASDA = " + recalculatedTORA + " + " + originalConfig.getStopway());

            //LDA
            recalculatedLDA = distanceFromThreshold  - STRIP - RESA;
            addCalcStep("LDA = " + distanceFromThreshold + " - " + STRIP + " - " + RESA);
        } else {
            // In the case of taking off and landing towards the obstacle

            //TORA
            recalculatedTORA = originalConfig.getTORA() - BLAST_PROTECTION - distanceFromThreshold - originalConfig.getDisplacementThreshold();
            addCalcStep(originalConfig.getTORA() + " - " + BLAST_PROTECTION + " - " + distanceFromThreshold + " - " + originalConfig.getDisplacementThreshold());

            //TODA
            recalculatedTODA = recalculatedTORA + originalConfig.getStopway();
            addCalcStep(recalculatedTORA + " + " + originalConfig.getStopway());

            //ASDA
            recalculatedASDA = recalculatedTORA + originalConfig.getClearway();
            addCalcStep(recalculatedTORA + " + " + originalConfig.getClearway());

            //LDA
            recalculatedLDA = originalConfig.getLDA() - distanceFromThreshold - STRIP - (obstacle.getHeight() * 50);
            addCalcStep(originalConfig.getLDA() + " - " + distanceFromThreshold + " - " + STRIP + " - (" + obstacle.getHeight() + " * 50)");
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
