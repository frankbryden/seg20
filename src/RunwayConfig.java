import javafx.scene.shape.Line;
import javafx.util.Pair;

import java.util.ArrayList;

public class RunwayConfig {
    private int TORA;
    private int TODA;
    private int ASDA;
    private int LDA;
    private int CLEARWAY;
    private int STOPWAY;
    private int displacementThreshold;
    private RunwayDesignator runwayDesignator;

    public RunwayConfig(RunwayDesignator runwayDesignator, int TORA, int TODA, int ASDA, int LDA, int displacementThreshold){
        this.runwayDesignator = runwayDesignator;
        this.TORA = TORA;
        this.TODA = TODA;
        this.ASDA = ASDA;
        this.LDA  = LDA;
        this.displacementThreshold = displacementThreshold;
        this.STOPWAY = ASDA - TORA;
        this.CLEARWAY = TODA - TORA;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.runwayDesignator);
        sb.append(" -> TORA : ");
        sb.append(this.TORA);
        sb.append(", TODA : ");
        sb.append(this.TODA);
        sb.append(", ASDA : ");
        sb.append(this.ASDA);
        sb.append(", LDA : ");
        sb.append(this.LDA);
        sb.append(", Displacement Threshold :");
        sb.append(this.displacementThreshold);
        return sb.toString();
    }

    public ArrayList<Pair<Line, String>> getLabelLines(RunwayRenderParams runwayRenderParams, RunwayRenderer.LabelRunwayDirection direction, RunwayRenderer.RunwayParams highlightedLine){
        //Labels and lines to indicate runway params
        Line toraLine, todaLine, asdaLine, ldaLine;
        int toraY, todaY, asdaY, ldaY;

        toraY = getLabelYShift(runwayRenderParams, direction, 0);
        todaY = getLabelYShift(runwayRenderParams, direction, 1);
        asdaY = getLabelYShift(runwayRenderParams, direction, 2);
        ldaY = getLabelYShift(runwayRenderParams, direction, 3);

        int p1 = runwayRenderParams.getRunwayStartX();
        int lineEndBelowRunway = p1 + runwayRenderParams.getRunwayLength();
        double p2Tora = getNormalisedTORA(this.TORA)*runwayRenderParams.getRunwayLength();
        double p2Toda = getNormalisedTODA(this.TORA)*runwayRenderParams.getRunwayLength();
        double p2Asda = getNormalisedASDA(this.TORA)*runwayRenderParams.getRunwayLength();
        double p2Lda = getNormalisedLDA(this.TORA)*runwayRenderParams.getRunwayLength();
        System.out.println("ref : " + System.identityHashCode(this));
        System.out.println("all obj : " + this.toString());
        System.out.println("TODA : " + this.TODA);
        System.out.println(getNormalisedTODA(this.TORA) + " * " + runwayRenderParams.getRunwayLength());
        System.out.println("TODA LENGTH : " + p2Toda);

        // In both cases, we need to add the start point to the end point, as we have calculated the line LENGTH and not END X value
        if (direction == RunwayRenderer.LabelRunwayDirection.UP){
            toraLine = new Line(p1, toraY, p1 + p2Tora, toraY);
            todaLine = new Line(p1, todaY, p1 + p2Toda, todaY);
            asdaLine = new Line(p1, asdaY, p1 + p2Asda, asdaY);
            ldaLine = new Line(p1 + getNormalisedDisplacementThreshold(this.TORA), ldaY,p1 + p2Lda, ldaY);
        } else {
            toraLine = new Line(lineEndBelowRunway - p2Tora, toraY, lineEndBelowRunway, toraY);
            todaLine = new Line(lineEndBelowRunway - p2Toda, todaY, lineEndBelowRunway, todaY);
            asdaLine = new Line(lineEndBelowRunway - p2Asda, asdaY, lineEndBelowRunway, asdaY);
            ldaLine = new Line(lineEndBelowRunway - p2Lda, ldaY, lineEndBelowRunway, ldaY);
        }

        switch (highlightedLine){
            case LDA:
                ldaLine.setUserData("highlighted");
                break;
            case ASDA:
                asdaLine.setUserData("highlighted");
                break;
            case TODA:
                todaLine.setUserData("highlighted");
                break;
            case TORA:
                toraLine.setUserData("highlighted");
                break;
            case NONE:
                break;
        }

        ArrayList<Pair<Line, String>> lines = new ArrayList<>();
        lines.add(new Pair<>(toraLine, "TORA"));
        lines.add(new Pair<>(todaLine, "TODA"));
        lines.add(new Pair<>(asdaLine, "ASDA"));
        lines.add(new Pair<>(ldaLine, "LDA"));
        return lines;
    }

    public double getStopwayLength(){
        return this.getNormalisedStopway(this.TORA);
    }

    public double getClearwayLength(){
        System.out.println("Clearway is : " + this.CLEARWAY);
        System.out.println("Normalised clearway is : " + this.getNormalisedClearway(this.TORA));
        return this.getNormalisedClearway(this.TORA);
    }



    //Normalised values - used for graphics
    public double getNormalisedTORA(int maxVal){
        return this.TORA / (maxVal*1.0);
    }

    public double getNormalisedTODA(int maxVal){
        return this.TODA / (maxVal*1.0);
    }

    public double getNormalisedASDA(int maxVal){
        return this.ASDA / (maxVal*1.0);
    }

    public double getNormalisedLDA(int maxVal){
        return this.LDA / (maxVal*1.0);
    }

    public double getNormalisedClearway(int maxVal){
        return this.CLEARWAY / (maxVal * 1.0);
    }

    public double getNormalisedStopway(int maxVal){
        return this.STOPWAY / (maxVal * 1.0);
    }

    public double getNormalisedDisplacementThreshold(int maxVal){
        return this.displacementThreshold / (maxVal * 1.0);
    }

    //Label Y shift
    private int getLabelYShift(RunwayRenderParams runwayRenderParams, RunwayRenderer.LabelRunwayDirection direction, int step){
        int startY = runwayRenderParams.getRunwayStartY();
        if (direction == RunwayRenderer.LabelRunwayDirection.UP){
            //startY -= runwayRenderParams.getRunwayHeight();
            startY -= (step + 1) * runwayRenderParams.getLabelSpacing();
        } else {
            startY += runwayRenderParams.getRunwayHeight();
            startY += (step + 1) * runwayRenderParams.getLabelSpacing();
        }

        return startY;
    }

    //Getters and setters

    public int getTORA() {
        return TORA;
    }

    public void setTORA(int TORA) {
        this.TORA = TORA;
    }

    public int getTODA() {
        return TODA;
    }

    public void setTODA(int TODA) {
        this.TODA = TODA;
    }

    public int getASDA() {
        return ASDA;
    }

    public void setASDA(int ASDA) {
        this.ASDA = ASDA;
    }

    public int getLDA() {
        return LDA;
    }

    public void setLDA(int LDA) {
        this.LDA = LDA;
    }

    public int getClearway() {
        return CLEARWAY;
    }

    public int getStopway() {
        return STOPWAY;
    }

    public int getDisplacementThreshold() {
        return displacementThreshold;
    }

    public RunwayDesignator getRunwayDesignator() {
        return runwayDesignator;
    }

    public void setRunwayDesignator(RunwayDesignator runwayDesignator) {
        this.runwayDesignator = runwayDesignator;
    }
}
