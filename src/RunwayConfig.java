import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;
import javafx.util.Pair;

import java.awt.*;
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
        this.STOPWAY = 0;
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

    public ArrayList<Pair<Line, String>> getLabelLines(RunwayRenderParams runwayRenderParams, RunwayRenderer.LabelRunwayDirection direction){
        //Labels and lines to indicate runway params
        Line toraLine, todaLine, asdaLine, ldaLine;
        int toraY, todaY, asdaY, ldaY;

        toraY = getLabelYShift(runwayRenderParams, direction, 0);
        todaY = getLabelYShift(runwayRenderParams, direction, 1);
        asdaY = getLabelYShift(runwayRenderParams, direction, 2);
        ldaY = getLabelYShift(runwayRenderParams, direction, 3);

        toraLine = new Line(runwayRenderParams.getRunwayStartX(), toraY, getNormalisedTORA(this.TORA) * runwayRenderParams.getRunwayLength(), toraY);
        todaLine = new Line(runwayRenderParams.getRunwayStartX(), todaY, getNormalisedTODA(this.TORA) * runwayRenderParams.getRunwayLength(), todaY);
        asdaLine = new Line(runwayRenderParams.getRunwayStartX(), asdaY, getNormalisedASDA(this.TORA) * runwayRenderParams.getRunwayLength(), asdaY);
        ldaLine = new Line(runwayRenderParams.getRunwayStartX() + getNormalisedDisplacementThreshold(this.TORA), ldaY, getNormalisedLDA(this.TORA) * runwayRenderParams.getRunwayLength(), ldaY);

        ArrayList<Pair<Line, String>> lines = new ArrayList<>();
        lines.add(new Pair<>(toraLine, "TORA"));
        lines.add(new Pair<>(todaLine, "TODA"));
        lines.add(new Pair<>(asdaLine, "ASDA"));
        lines.add(new Pair<>(ldaLine, "LDA"));
        return lines;
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

    public double getNormalisedDisplacementThreshold(int maxVal){
        return this.displacementThreshold / (maxVal * 1.0);
    }

    //Label Y shift
    private int getLabelYShift(RunwayRenderParams runwayRenderParams, RunwayRenderer.LabelRunwayDirection direction, int step){
        int startY = runwayRenderParams.getCenterLineY();
        if (direction == RunwayRenderer.LabelRunwayDirection.UP){
            startY -= runwayRenderParams.getRunwayHeight();
            startY -= step * runwayRenderParams.getLabelSpacing();
        } else {
            startY += runwayRenderParams.getRunwayHeight();
            startY += step * runwayRenderParams.getLabelSpacing();
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
