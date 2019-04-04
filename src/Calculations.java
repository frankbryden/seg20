import java.util.HashMap;
import java.util.Map;

public class Calculations {
    private RunwayConfig originalConfig;
    private final int RESA = 240;
    private final int STRIP_END = 60;
    private final int BLAST_PROTECTION = 300;
    public enum Direction {TOWARDS, AWAY};
    public static HashMap<Direction,String> directionSpecifier = new HashMap<Direction,String>(){{
        put(Direction.TOWARDS, "Taking Off Towards, Landing Towards");
        put(Direction.AWAY, "Taking Off Away, Landing Over");
    }};
    private StringBuilder calcSummary;

    public static Direction getKey(String runwayDirection){
        System.out.println(runwayDirection);
        for(Direction direction : Direction.values())
        {
            if(directionSpecifier.get(direction).equals(runwayDirection))
                return direction;
        }
        return null;
    }
    public Calculations(RunwayConfig runwayConfig){
        this.originalConfig = runwayConfig;
    }

    public CalculationResults recalculateParams(Obstacle obstacle, int distanceFromThreshold, int distanceFromCenterline, Direction direction){

        // Checking for the conditions for which no redeclaration of runway parameters is required
        if (distanceFromCenterline > 75 || distanceFromCenterline < -75 || distanceFromThreshold < -60 || distanceFromThreshold > (originalConfig.getTORA() + 60)){
            return new CalculationResults(originalConfig, "No redeclaration of runway parameters is required.");
        }

        int recalculatedTORA;
        int recalculatedTODA;
        int recalculatedASDA;
        int recalculatedLDA;
        int slopeCalculation = (int) (obstacle.getHeight() * 50);

        beginCalculation();

        if (direction == Direction.TOWARDS){

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
            addCalcStep("LDA = Distance From Threshold - Strip End - RESA");
            addCalcStep(spacing + "= " + distanceFromThreshold + " - " + STRIP_END + " - " + RESA);
            addCalcStep(spacing + "= " + recalculatedLDA);

        } else {

            addCalcStep ( originalConfig.getRunwayDesignator().toString() + " (" + "Take Off " + direction, true);
            addCalcStep("," + "Landing OVER):");

            // (Take Off Away, Landing Over)

            //TODO - Blast protection and STRIP_END + RESA is always equal, so we never get to see the use of the STRIP_END/RESA formula in the tests
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
            spacing = charsBeforeEquals("LDA  = ");
            if (STRIP_END + slopeCalculation > BLAST_PROTECTION || STRIP_END + RESA > BLAST_PROTECTION) {
                if (slopeCalculation > RESA) {
                    recalculatedLDA = originalConfig.getLDA() - distanceFromThreshold - slopeCalculation - STRIP_END;
                    addCalcStep("LDA = Original LDA - Distance From Threshold - Slope Calculation - Strip End");
                    addCalcStep(spacing + "= " + originalConfig.getLDA() + " - " + distanceFromThreshold + " - " + slopeCalculation + " - " + STRIP_END);
                } else {
                    recalculatedLDA = originalConfig.getLDA() - distanceFromThreshold - RESA - STRIP_END;
                    addCalcStep("LDA = Original LDA - Distance From Threshold - RESA - Strip End");
                    addCalcStep(spacing + "= " + originalConfig.getLDA() + " - " + distanceFromThreshold + " - " + RESA + " - " + STRIP_END);
                }
            } else {
                recalculatedLDA = originalConfig.getLDA() - distanceFromThreshold - BLAST_PROTECTION;
                addCalcStep("LDA = Original LDA - Distance From Threshold - Blast Protection");
                addCalcStep(spacing + "= " + originalConfig.getLDA() + " - " + distanceFromThreshold + " - " + BLAST_PROTECTION);
            }
            addCalcStep(spacing + "= " + recalculatedLDA);


        }
        //TODO now we might migrate this function to take in runway pairs instead of a single runway config - this needs to be discussed
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
