public class Calculations {
    private RunwayConfig originalConfig;
    private final int RESA = 240;
    private final int STRIP_END = 60;
    private final int BLAST_PROTECTION = 300;
    public enum Direction {TOWARDS, AWAY};
    private StringBuilder calcSummary;

    public Calculations(RunwayConfig runwayConfig){
        this.originalConfig = runwayConfig;
    }

    public CalculationResults recalculateParams(Obstacle obstacle, int distanceFromThreshold, Direction direction){
        //We need to determine what side of the runway the obstacle is lying before performing any calculations.
        int recalculatedTORA;
        int recalculatedTODA;
        int recalculatedASDA;
        int recalculatedLDA;
        beginCalculation();
        if (direction == Direction.TOWARDS){

            addCalcStep ( originalConfig.getRunwayDesignator().toString() + " (" + "Take Off " + direction, true);
            addCalcStep("," + "Landing Towards");

            // In the case of taking off towards from the obstacle / landing over it
            //TORA
            recalculatedTORA = distanceFromThreshold + originalConfig.getDisplacementThreshold() - (obstacle.getHeight() * 50) - STRIP_END;
            addCalcStep("TORA = " + distanceFromThreshold + " + " + originalConfig.getDisplacementThreshold() + " - (" + obstacle.getHeight() + " * 50) - " + STRIP_END);
            addCalcStep( "           =" + recalculatedTORA);

            //TODA
            recalculatedTODA = recalculatedTORA;
            addCalcStep("TODA = " + recalculatedTORA);
            addCalcStep("           = " + recalculatedTODA);

            //ASDA
            recalculatedASDA = recalculatedTORA;
            addCalcStep("ASDA = " + recalculatedTORA);
            addCalcStep("           = " + recalculatedASDA);

            //LDA
            recalculatedLDA = distanceFromThreshold  - STRIP_END - RESA;
            addCalcStep("LDA = " + distanceFromThreshold + " - " + STRIP_END + " - " + RESA);
            addCalcStep("        = " + recalculatedLDA);

        } else {

            addCalcStep ( originalConfig.getRunwayDesignator().toString() + " (" + "Take Off " + direction, true);
            addCalcStep("," + "Landing OVER):");

            // In the case of taking off and landing towards the obstacle

            //TORA
            recalculatedTORA = originalConfig.getTORA() - distanceFromThreshold - originalConfig.getDisplacementThreshold();
            addCalcStep( "TORA = " + originalConfig.getTORA() + " - " + distanceFromThreshold + " - " + originalConfig.getDisplacementThreshold());

            if (distanceFromThreshold < BLAST_PROTECTION){
                recalculatedTORA -= BLAST_PROTECTION;
                addCalcStep("    Took blast protection into account");
            } else {
                recalculatedTORA -= (STRIP_END + RESA);
                addCalcStep(("    Took RESA and STRIP_END into account"));
            }
            addCalcStep( "           =" + recalculatedTORA);


            //TODA
            recalculatedTODA = recalculatedTORA + originalConfig.getClearway();
            addCalcStep("TODA = " +  recalculatedTORA + " + " + originalConfig.getClearway());
            addCalcStep("           = " + recalculatedTODA);

            //ASDA
            recalculatedASDA = recalculatedTORA + originalConfig.getStopway();
            addCalcStep("ASDA = " +  recalculatedTORA + " + " + originalConfig.getStopway());
            addCalcStep("           = " + recalculatedASDA);

            //LDA
            recalculatedLDA = originalConfig.getLDA() - distanceFromThreshold - STRIP_END - (obstacle.getHeight() * 50);
            addCalcStep("LDA = " + originalConfig.getLDA() + " - " + distanceFromThreshold + " - " + STRIP_END + " - (" + obstacle.getHeight() + " * 50)");
            addCalcStep("        = " + recalculatedLDA);
        }

        System.out.println(getCalculationResults());

        return new CalculationResults(new RunwayConfig(originalConfig.getRunwayDesignator(), recalculatedTORA, recalculatedTODA, recalculatedASDA, recalculatedLDA, originalConfig.getDisplacementThreshold()), getCalculationResults());
    }

    private void beginCalculation(){
        this.calcSummary = new StringBuilder();
    }
    private void addCalcStep(String step) {
        this.calcSummary.append(step).append("\n");
    }

    private void addCalcStep(String step, boolean check) {
        this.calcSummary.append(step);
    }
    private String getCalculationResults(){
        return this.calcSummary.toString();
    }
}
