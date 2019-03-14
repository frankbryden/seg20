import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class RunwayRenderer {
    private RunwayPair runwayPair;
    private GraphicsContext graphicsContext;
    // Draw labels above or below the runway. UP implies landing left to right, and down implies landing right to left
    public enum LabelRunwayDirection {UP, DOWN};
    private static final Color RUNWAY_COLOR = Color.web("rgb(60, 67, 79)");

    //used to create a gap in the lines to display a textual label
    private double lableWidth = 40;

    public RunwayRenderer(RunwayPair runwayPair, GraphicsContext graphicsContext) {
        this.runwayPair = runwayPair;
        this.graphicsContext = graphicsContext;
    }

    public void render(GraphicsContext gc){
        //canvas dimensions
        double maxWidth = gc.getCanvas().getWidth();
        double maxHeight = gc.getCanvas().getHeight();

        //Layout properties
        double margin = 0.01*maxWidth;
        double halfVert = 0.5*maxHeight;

        //Runway
        int runwayLength = (int) (maxWidth - margin);
        int runwayHeight = 100;
        Rectangle runwayRect = new Rectangle(margin, maxHeight/2 - runwayHeight/2, runwayLength, runwayHeight);

        //Zebra margin : margin on either side of each zebra crossing
        int zebraMarginInner = 5;
        int zebraMarginOuter = 50;

        //Identifier margin : margin after the first zebra and before the second to leave space for the runway identifier
        int identifierMargin = 25;

        //Zebra Crossing
        int zebraDashLength = 50;
        int zebraDashOn = 4;
        int zebraDashOff = 4;
        int zebraVertLength = zebraDashOn + zebraDashOff;
        int zebraDashCount = runwayHeight/zebraVertLength;
        int zebraDashShift = runwayHeight % zebraVertLength;
        Rectangle[] zebraDashes = new Rectangle[2*zebraDashCount];
        for (int i = 0; i < zebraDashCount; i++){
            zebraDashes[i] = new Rectangle(runwayRect.getX() + zebraMarginOuter, zebraDashShift + runwayRect.getY() + zebraVertLength * i, zebraDashLength, zebraDashOn);
        }

        for (int i = zebraDashCount; i < 2*zebraDashCount; i++){
            zebraDashes[i] = new Rectangle(runwayRect.getX() + runwayRect.getWidth() - zebraMarginOuter - zebraDashLength, zebraDashShift + runwayRect.getY() + zebraVertLength * (i - zebraDashCount), zebraDashLength, zebraDashOn);
        }

        //Runway dashes
        int dashOn = 40;
        int dashOff = 25;
        int dashLength = dashOn + dashOff;
        int dashHeight = 5;
        int remainingRunwayLength = runwayLength - 2*(zebraMarginInner + zebraMarginOuter + zebraDashLength) - 2*identifierMargin;
        int dashCount = (int) (remainingRunwayLength/dashLength);
        int dashShift = (int) (remainingRunwayLength % dashLength);
        if (dashShift > dashLength/2){
            dashShift /= 2;
        }
        System.out.println("Shift is " + dashShift);
        Rectangle[] dashes = new Rectangle[dashCount];
        for (int i = 0; i < dashCount; i++){
            dashes[i] = new Rectangle(dashShift + identifierMargin + zebraMarginInner + zebraMarginOuter + zebraDashLength + runwayRect.getX() + i * dashLength, runwayRect.getY() + runwayRect.getHeight()/2, dashOn, dashHeight);
        }

        //Finally we get to the drawing part !
        drawRect(gc, runwayRect, RUNWAY_COLOR);
        for (Rectangle dash : dashes) {
            drawRect(gc, dash, Color.WHITE);
        }
        for (Rectangle zebra : zebraDashes){
            drawRect(gc, zebra, Color.WHITE);
        }

        //And the runway identifiers
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 32));
        gc.save();
        gc.translate(runwayRect.getX() + zebraMarginOuter + zebraMarginInner + zebraDashLength, runwayRect.getY() + runwayRect.getHeight()/2);
        gc.rotate(-90);
        gc.fillText(r1.getRunwayDesignator().toString(), -20, 30);
        gc.restore();
        gc.save();
        gc.translate(runwayRect.getX() + runwayRect.getWidth() - zebraMarginOuter - zebraMarginInner - zebraDashLength - identifierMargin, runwayRect.getY());
        gc.rotate(-90);
        gc.fillText(r2.getRunwayDesignator().toString(), -70, 5);
        gc.restore();
        gc.fillText("hey", 20, 20);
    }

    public void renderLogicalRunway(RunwayConfig runwayConfig, int baseY, int runwayLength, LabelRunwayDirection direction){
        // clearway + stopway start after zebra crossing
        int maxLen = runwayConfig.getTORA();

        //canvas dimensions
        double maxWidth = gc.getCanvas().getWidth();
        double maxHeight = gc.getCanvas().getHeight();

        //gap between each line (tora/toda/asda/lda)
        double arrowStep = maxHeight * 0.05;

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

    public void drawRect(GraphicsContext gc, Rectangle rect, Color color){
        gc.setFill(color);
        System.out.println(rect.getX());
        System.out.println(rect.getY());
        System.out.println(rect.getWidth());
        System.out.println(rect.getHeight());
        gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }

}
