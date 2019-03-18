import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class RunwayRenderer {
    private RunwayPair runwayPair;
    private GraphicsContext graphicsContext;
    // Draw labels above or below the runway. UP implies landing left to right, and down implies landing right to left
    public enum LabelRunwayDirection {UP, DOWN};
    private static final Color RUNWAY_COLOR = Color.web("rgb(60, 67, 79)");
    private RunwayRenderParams runwayRenderParams;

    //used to create a gap in the lines to display a textual label
    private double lableWidth = 40;

    private List<Pair<Line, String>> labelLines;

    public RunwayRenderer(RunwayPair runwayPair, GraphicsContext graphicsContext) {
        this.runwayPair = runwayPair;
        this.graphicsContext = graphicsContext;
        this.runwayRenderParams = new RunwayRenderParams();
        this.initParams();
        this.runwayRenderParams.init();
    }


    public RunwayRenderer(RunwayPair runwayPair, GraphicsContext graphicsContext, Boolean isSideView){

        this.runwayPair = runwayPair;
        this.graphicsContext = graphicsContext;
        this.runwayRenderParams = new RunwayRenderParams();
        this.initParams();
        this.initSideViewParams();
    }

    private void initSideViewParams() {
        //canvas dimensions
        int maxWidth = (int) this.graphicsContext.getCanvas().getWidth();
        double maxHeight = this.graphicsContext.getCanvas().getHeight();

    }

    public void renderSideview(){

        //canvas dimensions
        int maxWidth = (int) this.graphicsContext.getCanvas().getWidth();
        double maxHeight = this.graphicsContext.getCanvas().getHeight();

        graphicsContext.setFill(Color.OLDLACE);
        graphicsContext.fillRect(0, 0, maxWidth, maxHeight);
        Rectangle runwayRect = new Rectangle(60, runwayRenderParams.getCenterLineY(), graphicsContext.getCanvas().getWidth() - 120, 7);
        Rectangle clearAreaLeft = new Rectangle(0, runwayRenderParams.getCenterLineY(), 60, 7);
        Rectangle clearAreaRight = new Rectangle(graphicsContext.getCanvas().getWidth() - 60 , runwayRenderParams.getCenterLineY(), graphicsContext.getCanvas().getWidth() , 7);
        drawRect(this.graphicsContext, runwayRect, RUNWAY_COLOR );
        drawRect(this.graphicsContext, clearAreaLeft, Color.RED );
        drawRect(this.graphicsContext, clearAreaRight, Color.RED );

        this.graphicsContext.setStroke(Color.BLACK);

        //And the labels identifying the runway params
        for (Pair<Line, String> line : labelLines){
            graphicsContext.setFont(new Font(runwayRenderParams.getLabelFontSize()));
            renderParamLine(line);
        }

        this.graphicsContext.fillText(runwayPair.getR1().getRunwayDesignator().toString(),70 , runwayRenderParams.getCenterLineY() + 20);
        this.graphicsContext.fillText(runwayPair.getR2().getRunwayDesignator().toString(), graphicsContext.getCanvas().getWidth() - 90 , runwayRenderParams.getCenterLineY() + 20);
    }

    public void drawObstacle(Integer height, Integer pozx, String tresholdName){
        
        double positionOnRunway = 0;
        String runwayName = runwayPair.getR2().getRunwayDesignator().getDirection();

        System.out.println(runwayName + " "  +tresholdName);
        if(tresholdName.equals(runwayName))
            positionOnRunway = pozx;
        else {
            positionOnRunway = this.graphicsContext.getCanvas().getWidth() - pozx;
        }

        Rectangle runwayRect = new Rectangle(positionOnRunway, runwayRenderParams.getCenterLineY() - 10,10 , height);
        drawRect(this.graphicsContext, runwayRect, Color.RED);
    }

    public void initParams(){
        //canvas dimensions
        int maxWidth = (int) this.graphicsContext.getCanvas().getWidth();
        double maxHeight = this.graphicsContext.getCanvas().getHeight();

        //Layout properties
        this.runwayRenderParams.setMargin((int) (0.01*maxWidth));
        this.runwayRenderParams.setRunwayStartX(runwayRenderParams.getMargin());

        //Runway
        this.runwayRenderParams.setRunwayLength(maxWidth - runwayRenderParams.getMargin());
        this.runwayRenderParams.setRunwayHeight(100);
        this.runwayRenderParams.setCenterLineY((int) (maxHeight/2 - runwayRenderParams.getRunwayHeight()/2));

        //Zebra margin : margin on either side of each zebra crossing
        this.runwayRenderParams.setZebraMarginInner(5);
        this.runwayRenderParams.setZebraMarginOuter(50);

        //Identifier margin : margin after the first zebra and before the second to leave space for the runway identifier
        this.runwayRenderParams.setIdentifierMargin(25);

        //Zebra Crossing
        this.runwayRenderParams.setZebraDashLength(50);
        this.runwayRenderParams.setZebraDashOn(4);
        this.runwayRenderParams.setZebraDashOff(4);

        //Runway dashes
        this.runwayRenderParams.setDashOn(40);
        this.runwayRenderParams.setDashOff(25);
        this.runwayRenderParams.setDashHeight(5);


        //Labels and lines to indicate runway params
        Line toraLine, todaLine, asdaLine, ldaLine;
        //toraLine = new Line(runwayRenderParams.getR);

        //Label params
        this.runwayRenderParams.setLabelFontSize(18);
        this.runwayRenderParams.setLabelTextMargin(10);
        this.runwayRenderParams.setLabelSpacing(20);

        labelLines = runwayPair.getR1().getLabelLines(this.runwayRenderParams, LabelRunwayDirection.UP);
        labelLines.addAll(runwayPair.getR1().getLabelLines(this.runwayRenderParams, LabelRunwayDirection.DOWN));

    }

    public void render(){
        graphicsContext.setFill(Color.GOLD);
        graphicsContext.fillRect(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());
        Rectangle runwayRect = new Rectangle(runwayRenderParams.getRunwayStartX(), runwayRenderParams.getCenterLineY(), runwayRenderParams.getRunwayLength(), runwayRenderParams.getRunwayHeight());

        Rectangle[] zebraDashes = new Rectangle[2*runwayRenderParams.getZebraDashCount()];
        for (int i = 0; i < runwayRenderParams.getZebraDashCount(); i++){
            zebraDashes[i] = new Rectangle(runwayRect.getX() + runwayRenderParams.getZebraMarginOuter(), runwayRenderParams.getZebraDashShift() + runwayRect.getY() + runwayRenderParams.getZebraVertLength() * i, runwayRenderParams.getZebraDashLength(), runwayRenderParams.getZebraDashOn());
        }

        for (int i = runwayRenderParams.getZebraDashCount(); i < 2*runwayRenderParams.getZebraDashCount(); i++){
            zebraDashes[i] = new Rectangle(runwayRect.getX() + runwayRect.getWidth() - runwayRenderParams.getZebraMarginOuter()- runwayRenderParams.getZebraDashLength(), runwayRenderParams.getZebraDashShift() + runwayRect.getY() + runwayRenderParams.getZebraVertLength()*(i - runwayRenderParams.getZebraDashCount()), runwayRenderParams.getZebraDashLength(), runwayRenderParams.getZebraDashOn());
        }


        Rectangle[] dashes = new Rectangle[runwayRenderParams.getDashCount()];
        for (int i = 0; i < runwayRenderParams.getDashCount(); i++){
            dashes[i] = new Rectangle(runwayRenderParams.getDashShift() + runwayRenderParams.getIdentifierMargin() + runwayRenderParams.getZebraMarginInner() + runwayRenderParams.getZebraMarginOuter()+ runwayRenderParams.getZebraDashLength() + runwayRect.getX() + i * runwayRenderParams.getDashLength(), runwayRect.getY() + runwayRect.getHeight()/2, runwayRenderParams.getDashOn(), runwayRenderParams.getDashHeight());
        }

        //Finally we get to the drawing part !
        drawRect(this.graphicsContext, runwayRect, RUNWAY_COLOR);
        for (Rectangle dash : dashes) {
            drawRect(this.graphicsContext, dash, Color.WHITE);
        }
        for (Rectangle zebra : zebraDashes){
            drawRect(this.graphicsContext, zebra, Color.WHITE);
        }

        //And the runway identifiers
        this.graphicsContext.setFill(Color.WHITE);
        this.graphicsContext.setFont(Font.font("Verdana", FontWeight.BOLD, 32));
        this.graphicsContext.save();
        this.graphicsContext.translate(runwayRect.getX() + runwayRenderParams.getZebraMarginOuter() + runwayRenderParams.getZebraMarginInner() + runwayRenderParams.getZebraDashLength(), runwayRect.getY() + runwayRect.getHeight()/2);
        this.graphicsContext.rotate(-90);
        this.graphicsContext.fillText(runwayPair.getR1().getRunwayDesignator().toString(), -20, 30);
        this.graphicsContext.restore();
        this.graphicsContext.save();
        this.graphicsContext.translate(runwayRect.getX() + runwayRect.getWidth() - runwayRenderParams.getZebraMarginOuter() - runwayRenderParams.getZebraMarginInner() - runwayRenderParams.getZebraDashLength() - runwayRenderParams.getIdentifierMargin(), runwayRect.getY());
        this.graphicsContext.rotate(-90);
        this.graphicsContext.fillText(runwayPair.getR2().getRunwayDesignator().toString(), -70, 5);
        this.graphicsContext.restore();
        this.graphicsContext.fillText("SEG BAFFI", 20, 20);

        //And the labels identifying the runway params
        for (Pair<Line, String> line : labelLines){
            graphicsContext.setFont(new Font(runwayRenderParams.getLabelFontSize()));
            renderParamLine(line);
        }
    }

    public void renderParamLine(Pair<Line, String> labelLine){
        Line line = labelLine.getKey();

        this.graphicsContext.moveTo(line.getStartX(), line.getStartY());
        this.graphicsContext.lineTo(line.getEndX(), line.getEndY());
        this.graphicsContext.stroke();
        int midX = (int) (line.getStartX() + line.getEndX())/2;
        int midY = (int) (line.getStartY() + line.getEndY())/2;
        this.graphicsContext.fillText(labelLine.getValue(), midX, midY);
    }
/*
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
*/
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
