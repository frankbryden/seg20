import javafx.scene.paint.Stop;
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
    private RunwayPair parent;
    private RunwayPair.Side side;

    public RunwayConfig(RunwayPair parent, RunwayDesignator runwayDesignator, int TORA, int TODA, int ASDA, int LDA, int displacementThreshold){
        this.parent = parent;
        if (this.parent != null) {
            this.side = (this.parent.getR1() == this) ? RunwayPair.Side.R1 : RunwayPair.Side.R2;
            System.out.println("I am a runway config ! I am on side " + this.side + " with designator " + runwayDesignator);
        } else {
            this.side = RunwayPair.Side.Unknown;
        }

        this.runwayDesignator = runwayDesignator;
        this.TORA = TORA;
        this.TODA = TODA;
        this.ASDA = ASDA;
        this.LDA  = LDA;
        this.displacementThreshold = TORA - LDA;
        this.STOPWAY = ASDA - TORA;
        this.CLEARWAY = TODA - TORA;
    }

    public RunwayConfig(RunwayDesignator runwayDesignator, int TORA, int TODA, int ASDA, int LDA, int displacementThreshold){
        this(null, runwayDesignator, TORA, TODA, ASDA, LDA, displacementThreshold);
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

    public ArrayList<Pair<Line, String>> getLabelLines(RunwayRenderParams runwayRenderParams, RunwayRenderer.LabelRunwayDirection direction, RunwayRenderer.RunwayParams highlightedLine, int lineStartX){
        //Labels and lines to indicate runway params
        Line toraLine, todaLine, asdaLine, ldaLine;
        int toraY, todaY, asdaY, ldaY;

        //max len
        int maxLen = (this.side == RunwayPair.Side.R1) ? runwayRenderParams.getRealLifeMaxLenR1() : runwayRenderParams.getRealLifeMaxLenR2();

        toraY = getLabelYShift(runwayRenderParams, direction, 0);
        todaY = getLabelYShift(runwayRenderParams, direction, 1);
        asdaY = getLabelYShift(runwayRenderParams, direction, 2);
        ldaY = getLabelYShift(runwayRenderParams, direction, 3);

        int lineEndBelowRunway = lineStartX + runwayRenderParams.getRunwayLength();
        double p2Tora = getNormalisedTORA(maxLen)*runwayRenderParams.getRunwayLength();
        double p2Toda = getNormalisedTODA(maxLen)*runwayRenderParams.getRunwayLength();
        double p2Asda = getNormalisedASDA(maxLen)*runwayRenderParams.getRunwayLength();
        double p2Lda = getNormalisedLDA(maxLen)*runwayRenderParams.getRunwayLength();
        System.out.println("ref : " + System.identityHashCode(this));
        System.out.println("all obj : " + this.toString());
        System.out.println("TODA : " + this.TODA);
        System.out.println(getNormalisedTODA(this.TORA) + " * " + runwayRenderParams.getRunwayLength());
        System.out.println("TODA LENGTH : " + p2Toda);

        // In both cases, we need to add the start point to the end point, as we have calculated the line LENGTH and not END X value
        if (direction == RunwayRenderer.LabelRunwayDirection.UP){
            toraLine = new Line(lineStartX, toraY, lineStartX + p2Tora, toraY);
            todaLine = new Line(lineStartX, todaY, lineStartX + p2Toda, todaY);
            asdaLine = new Line(lineStartX, asdaY, lineStartX + p2Asda, asdaY);
            ldaLine = new Line(lineStartX + getNormalisedDisplacementThreshold(maxLen), ldaY, lineStartX + p2Lda, ldaY);
        } else {
            /*toraLine = new Line(lineEndBelowRunway, toraY, lineEndBelowRunway + p2Tora, toraY);
            todaLine = new Line(lineEndBelowRunway, todaY, lineEndBelowRunway + p2Toda, todaY);
            asdaLine = new Line(lineEndBelowRunway, asdaY, lineEndBelowRunway + p2Asda, asdaY);
            ldaLine = new Line(lineEndBelowRunway, ldaY, lineEndBelowRunway + p2Lda, ldaY);*/

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

    public void setClearway(int Clearway) {
        this.CLEARWAY = Clearway;
    }

    public int getStopway() {
        return STOPWAY;
    }

    public void setStopway(int Stopway) {
        this.STOPWAY = Stopway;
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

    public RunwayPair.Side getSide() {
        return side;
    }

    public void setSide(RunwayPair.Side side) {
        this.side = side;
    }

    public RunwayPair getParent() {
        return parent;
    }

    public void setParent(RunwayPair parent) {
        this.parent = parent;
    }
}
