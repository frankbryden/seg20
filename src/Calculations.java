
class Calculations {
    private final RunwayConfig originalConfig;
    private final int RESA = 240;
    private final int STRIP_END = 60;
    private final int BLAST_PROTECTION = 300;
    private StringBuilder calcSummary;

    public Calculations(RunwayConfig runwayConfig){
        this.originalConfig = runwayConfig;
    }

    public CalculationResults recalculateParams(Obstacle obstacle, int distanceFromThreshold, int distanceFromCenterline, String direction, int runwayLength){

        // Checking the conditions for which no redeclaration of runway parameters is required
        if (distanceFromCenterline > 75 || distanceFromCenterline < -75 || distanceFromThreshold + originalConfig.getDisplacementThreshold() < -60 || distanceFromThreshold + originalConfig.getDisplacementThreshold() > (runwayLength + 60)){
            return new CalculationResults(originalConfig, "No redeclaration of runway parameters is required for threshold " + originalConfig.getRunwayDesignator() + ".");
        }

        int recalculatedTORA;
        int recalculatedTODA;
        int recalculatedASDA;
        int recalculatedLDA;
        int slopeCalculation = (int) (obstacle.getHeight() * 50);

        beginCalculation();

        if (direction.equals("TOWARDS")){

            addCalcStep ( originalConfig.getRunwayDesignator().toString() + " (" + "Take Off " + direction, true);
            addCalcStep("," + "Landing TOWARDS):");

            // (Take Off Towards, Landing Towards)

            //TORA
            if (slopeCalculation <= RESA){
                recalculatedTORA = distanceFromThreshold + originalConfig.getDisplacementThreshold() - RESA - STRIP_END;
                String spacing = charsBeforeEquals("TORA = Distance From Threshold + Displaced Threshold - RESA - Strip End");
                addCalcStep("TORA = Distance From Threshold + Displaced Threshold - RESA - Strip End");
                addCalcStep(spacing + "= " + distanceFromThreshold + " + " + originalConfig.getDisplacementThreshold() + " - " + RESA + " - " + STRIP_END);
            } else {
                recalculatedTORA = distanceFromThreshold + originalConfig.getDisplacementThreshold() - slopeCalculation - STRIP_END;
                String spacing = charsBeforeEquals("TORA = Distance From Threshold + Displaced Threshold - Slope Calculation - Strip End");
                addCalcStep("TORA = Distance From Threshold + Displaced Threshold - Slope Calculation - Strip End");
                addCalcStep(spacing + "= " + distanceFromThreshold + " + " + originalConfig.getDisplacementThreshold() + " - " + slopeCalculation + " - " + STRIP_END);
            }
            addCalcStep( "     = " + recalculatedTORA);


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
            spacing = charsBeforeEquals("LDA = Distance From Threshold - Strip End - RESA");
            addCalcStep("LDA  = Distance From Threshold - Strip End - RESA");
            addCalcStep(spacing + " = " + distanceFromThreshold + " - " + STRIP_END + " - " + RESA);
            addCalcStep(spacing + " = " + recalculatedLDA);

        } else {

            addCalcStep ( originalConfig.getRunwayDesignator().toString() + " (" + "Take Off " + direction, true);
            addCalcStep("," + "Landing OVER):");

            // (Take Off Away, Landing Over)


            //TORA
            if (STRIP_END + RESA > BLAST_PROTECTION) {
                recalculatedTORA = originalConfig.getTORA() - STRIP_END - RESA - distanceFromThreshold - originalConfig.getDisplacementThreshold();
                String spacing = charsBeforeEquals("TORA = Original TORA - Strip End - RESA - Distance From Threshold - Displaced Threshold");
                addCalcStep("TORA = Original TORA - Strip End - RESA - Distance From Threshold - Displaced Threshold");
                addCalcStep(spacing + "= " + originalConfig.getTORA() + " - " + STRIP_END + " - " + RESA + " - " + distanceFromThreshold + " - " + originalConfig.getDisplacementThreshold());
            } else {
                recalculatedTORA = originalConfig.getTORA() - BLAST_PROTECTION - distanceFromThreshold - originalConfig.getDisplacementThreshold();
                String spacing = charsBeforeEquals("TORA = Original TORA - Blast Protection - Distance From Threshold - Displaced Threshold");
                addCalcStep("TORA = Original TORA - Blast Protection - Distance From Threshold - Displaced Threshold");
                addCalcStep(spacing + "= " + originalConfig.getTORA() + " - " + BLAST_PROTECTION + " - " + distanceFromThreshold + " - " + originalConfig.getDisplacementThreshold());
            }
            addCalcStep( "     = " + recalculatedTORA);


            //TODA
            recalculatedTODA = recalculatedTORA + originalConfig.getClearway();
            String spacing = charsBeforeEquals("TODA = Recalculated TORA + CLEARWAY");
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
            if (STRIP_END + slopeCalculation > BLAST_PROTECTION || STRIP_END + RESA > BLAST_PROTECTION) {
                if (slopeCalculation > RESA) {
                    recalculatedLDA = originalConfig.getLDA() - distanceFromThreshold - slopeCalculation - STRIP_END;
                    String spacingLDA = charsBeforeEquals("LDA = Original LDA - Distance From Threshold - Slope Calculation - Strip End");
                    addCalcStep("LDA  = Original LDA - Distance From Threshold - Slope Calculation - Strip End");
                    addCalcStep(spacingLDA + " = " + originalConfig.getLDA() + " - " + distanceFromThreshold + " - " + slopeCalculation + " - " + STRIP_END);
                } else {
                    recalculatedLDA = originalConfig.getLDA() - distanceFromThreshold - RESA - STRIP_END;
                    String spacingLDA = charsBeforeEquals("LDA = Original LDA - Distance From Threshold - RESA - Strip End");
                    addCalcStep("LDA  = Original LDA - Distance From Threshold - RESA - Strip End");
                    addCalcStep(spacingLDA + " = " + originalConfig.getLDA() + " - " + distanceFromThreshold + " - " + RESA + " - " + STRIP_END);
                }
            } else {
                recalculatedLDA = originalConfig.getLDA() - distanceFromThreshold - BLAST_PROTECTION;
                String spacingLDA = charsBeforeEquals("LDA = Original LDA - Distance From Threshold - Blast Protection");
                addCalcStep("LDA  = Original LDA - Distance From Threshold - Blast Protection");
                addCalcStep(spacing + " = " + originalConfig.getLDA() + " - " + distanceFromThreshold + " - " + BLAST_PROTECTION);
            }
            addCalcStep( "     = " + recalculatedLDA);


        }
        return new CalculationResults(new RunwayConfig(null, originalConfig.getRunwayDesignator(), recalculatedTORA, recalculatedTODA, recalculatedASDA, recalculatedLDA, originalConfig.getDisplacementThreshold()), getCalculationResults());
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
