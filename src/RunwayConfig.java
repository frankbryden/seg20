import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.awt.*;

public class RunwayConfig {
    private int TORA;
    private int TODA;
    private int ASDA;
    private int LDA;
    private int CLEARWAY;
    private int STOPWAY;
    private int displacementThreshold;
    private RunwayDesignator runwayDesignator;

    //used to create a gap in the lines to display a textual label
    private double lableWidth = 40;


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

    public void render(GraphicsContext gc){
        // clearway + stopway start after zebra crossing

        int maxLen = this.TORA;

        //canvas dimensions
        double maxWidth = gc.getCanvas().getWidth();
        double maxHeight = gc.getCanvas().getHeight();

        //Layout properties
        double margin = 0.1*maxWidth;
        double halfVert = 0.5*maxHeight;

        //gap between each line (tora/toda/asda/lda)
        double arrowStep = maxHeight * 0.05;


        //end of the runway
        double runwayEnd = maxWidth - 2 * margin;
        double runwayStart = margin;
        double runwayLength = runwayEnd - runwayStart;

        //TODA line
        double todaStartX = margin;
        double todaEndX = getNormalisedTODA(maxLen)*runwayLength;
        double todaY = halfVert - arrowStep;
        double todaLabelStart = (todaEndX - todaStartX + lableWidth)/2 - lableWidth;

        //TORA line
        double toraStartX = margin;
        double toraEndX = getNormalisedTORA(maxLen)*runwayLength;
        double toraY = halfVert - 2*arrowStep;
        double toraLabelStart = (toraEndX - toraStartX + lableWidth)/2 - lableWidth;

        //clear canvas before draw
        gc.clearRect(0, 0, maxWidth, maxHeight);

        //Draw borders
        gc.setLineWidth(1);
        gc.strokeRect(3, 3, maxWidth - 4, maxHeight - 3);

        //Draw runway
        gc.fillRect(runwayStart, halfVert, runwayLength, 20);

        gc.setLineWidth(2);

        //Draw TODA line
        drawParameterLine(gc, todaStartX, todaEndX, todaY, todaLabelStart, "TODA");

        //Draw TORA line
        drawParameterLine(gc, toraStartX, toraEndX, toraY, toraLabelStart, "TORA");

    }

    public void drawParameterLine(GraphicsContext gc, double lineStartX, double lineEndX, double lineY, double lableStart, String label){
        gc.beginPath();
        gc.moveTo(lineStartX, lineY);
        gc.lineTo(lableStart, lineY);
        gc.moveTo(lableStart + lableWidth, lineY);
        gc.lineTo(lineEndX, lineY);
        gc.stroke();
        gc.fillText(label, lableStart + 5, lineY + 5);
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
