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

    public CalculationResults recalculateParams(Obstacle obstacle, int distanceFromThreshold, int distanceFromCenterline, Direction direction){

        //determine if calculations need to be redone
        if (distanceFromCenterline > 75 || distanceFromThreshold < -60 || distanceFromThreshold > (originalConfig.getTORA() + 60)){
            return new CalculationResults(originalConfig, "No redeclaration needed.");
        }

        //We need to determine what side of the runway the obstacle is lying before performing any calculations.
        int recalculatedTORA;
        int recalculatedTODA;
        int recalculatedASDA;
        int recalculatedLDA;
        beginCalculation();
        int slopeCalculation = (int) (obstacle.getHeight() * 50);
        if (direction == Direction.TOWARDS){

            addCalcStep ( originalConfig.getRunwayDesignator().toString() + " (" + "Take Off " + direction, true);
            addCalcStep("," + "Landing Towards");

            // In the case of taking off towards from the obstacle / landing over it
            //TORA

            if (slopeCalculation <= RESA){
                recalculatedTORA = distanceFromThreshold + originalConfig.getDisplacementThreshold() - RESA - STRIP_END;
                String spacing = charsBeforeEquals("TORA = Distance from Threshold + Displaced Threshold - RESA - Strip End");
                addCalcStep("TORA = Distance from Threshold + Displaced Threshold - RESA - Strip End");
                addCalcStep(spacing + "= " + distanceFromThreshold + " + " + originalConfig.getDisplacementThreshold() + " - " + RESA + " - " +STRIP_END);
            } else {
                recalculatedTORA = distanceFromThreshold + originalConfig.getDisplacementThreshold() - slopeCalculation - STRIP_END;
                String spacing = charsBeforeEquals("TORA = Distance from Threshold + Displacement Threshold - Slope Calculation - Strip End (Obstacle Height)");
                addCalcStep("TORA = Distance from Threshold + Displacement Threshold - Slope Calculation - Strip End (Obstacle Height)");
                addCalcStep(spacing + "= " + distanceFromThreshold + " + " + originalConfig.getDisplacementThreshold() + " - " + slopeCalculation +" (" + obstacle.getHeight() + " * 50) - " + STRIP_END);
            }
            addCalcStep( "     =" + recalculatedTORA);

            //TODA
            recalculatedTODA = recalculatedTORA;
            String spacing = charsBeforeEquals("TODA = Recalculated TORA");
            addCalcStep("TODA = Recalculated TORA");
            addCalcStep(spacing + "= " + recalculatedTODA);

            //ASDA
            recalculatedASDA = recalculatedTORA;
            spacing = charsBeforeEquals("ASDA = Recalculated TORA");
            addCalcStep("ASDA = Recalculated TORA");
            addCalcStep(spacing + "= " + recalculatedASDA);

            //LDA
            recalculatedLDA = distanceFromThreshold  - STRIP_END - RESA;
            spacing = charsBeforeEquals("LDA = Distance from Threshold - STRIP END - RESA");
            addCalcStep("LDA = Distance from Threshold - STRIP END - RESA");
            addCalcStep(spacing + "= " + distanceFromThreshold + " - " + STRIP_END + " - " + RESA);
            addCalcStep(spacing + "= " + recalculatedLDA);

        } else {

            addCalcStep ( originalConfig.getRunwayDesignator().toString() + " (" + "Take Off " + direction, true);
            addCalcStep("," + "Landing OVER):");

            // In the case of taking off and landing towards the obstacle

            //TORA
            recalculatedTORA = originalConfig.getTORA() - distanceFromThreshold - originalConfig.getDisplacementThreshold();
            String spacing = charsBeforeEquals("TORA = Original TORA - Distance from Threshold - Displacement Threshold");
            addCalcStep("TORA = Original TORA - Distance from Threshold - Displacement Threshold");
            addCalcStep(spacing + "= " + originalConfig.getTORA() + " - " + distanceFromThreshold + " - " + originalConfig.getDisplacementThreshold());

            if (distanceFromThreshold < BLAST_PROTECTION){
                recalculatedTORA -= BLAST_PROTECTION;
                addCalcStep("   Taking blast protection into account");
                addCalcStep(spacing + "= "+ recalculatedTORA + " - " + BLAST_PROTECTION);
            } else {
                recalculatedTORA -= (STRIP_END + RESA);
                addCalcStep(("  Taking RESA and STRIP_END into account"));
                addCalcStep(spacing + "= "+ recalculatedTORA + " - " + STRIP_END + " - " + RESA);
            }
            addCalcStep(spacing + "= " + recalculatedTORA);


            //TODA
            recalculatedTODA = recalculatedTORA + originalConfig.getClearway();
            spacing = charsBeforeEquals("TODA = Recalculated TORA + CLEARWAY");
            addCalcStep("TODA = Recalculated TORA + CLEARWAY");
            addCalcStep(spacing + "= " +  recalculatedTORA + " + " + originalConfig.getClearway());
            addCalcStep(spacing + "= " + recalculatedTODA);

            //ASDA
            recalculatedASDA = recalculatedTORA + originalConfig.getStopway();
            spacing = charsBeforeEquals("ASDA = Recalculated TORA + STOPWAY");
            addCalcStep("ASDA = Recalculated TORA + STOPWAY");
            addCalcStep(spacing + "= " +  recalculatedTORA + " + " + originalConfig.getStopway());
            addCalcStep(spacing + "= " + recalculatedASDA);

            //LDA
            int slopeCalculationLDA = (int) (obstacle.getHeight() * 50);
            slopeCalculationLDA = Math.max(slopeCalculationLDA, RESA);
            spacing = charsBeforeEquals("LDA = ");
            if ((slopeCalculationLDA + STRIP_END) <= BLAST_PROTECTION){
                recalculatedLDA = originalConfig.getLDA() - distanceFromThreshold - BLAST_PROTECTION;
                addCalcStep("LDA = Original LDA - Distance from Threshold - Blast Protection");
                addCalcStep(spacing + "= " + originalConfig.getLDA() + " - " + distanceFromThreshold + " - " + BLAST_PROTECTION);
            } else {
                recalculatedLDA = originalConfig.getLDA() - distanceFromThreshold - STRIP_END - slopeCalculationLDA;
                addCalcStep("LDA = Original LDA - Distance from Threshold - Strip End - Slope Calculation");
                addCalcStep(spacing + "= " + originalConfig.getLDA() + " - " + distanceFromThreshold + " - " + STRIP_END + " - " + slopeCalculation + " (Obstacle Height * 50)");
            }
            addCalcStep(spacing + "= " + recalculatedLDA);
        }

        System.out.println(getCalculationResults());

        return new CalculationResults(new RunwayConfig(originalConfig.getRunwayDesignator(), recalculatedTORA, recalculatedTODA, recalculatedASDA, recalculatedLDA, originalConfig.getDisplacementThreshold()), getCalculationResults());
    }

    private String charsBeforeEquals(String input){
        String maths[] = input.split("=");
        String spacing="";
        for (char ch : maths[0].toCharArray())
        {
            spacing += " ";
        }
        return spacing;
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
