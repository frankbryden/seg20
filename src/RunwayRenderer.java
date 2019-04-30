import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.*;
import javafx.stage.Window;
import javafx.util.Pair;

import java.awt.*;
import java.util.List;

public class RunwayRenderer {
    private final RunwayPair runwayPair;
    private final GraphicsContext graphicsContext;
    // Draw labels above or below the runway. UP implies landing left to right, and down implies landing right to left
    public enum LabelRunwayDirection {UP, DOWN}
    // Each param line needs an arrow cap on each end. Used to tell which end (therefore which way to draw the cap)
    private enum ArrowDirection {LEFT, RIGHT}
    public enum RunwayParams {TORA, TODA, ASDA, LDA, NONE}
    private static final Color RUNWAY_COLOR = Color.web("rgb(60, 67, 79)");
    private final RunwayRenderParams runwayRenderParams;

    //Tranform properties
    private int rotation;
    private int zoomDelta;
    private double currentZoom;
    public static final double MIN_ZOOM = 0.5;
    public static final double MAX_ZOOM = 2.4;
    public static final double ZOOM_STEP = (MAX_ZOOM - MIN_ZOOM)/30;

    //Tranforms
    private Affine scaleAffine;
    private Affine translateAffine;
    private int translateX;
    private int translateY;
    private Point mouseLoc;

    //temp
    private int red = 50;

    //Wind
    private double windAngle;

    //used to create a gap in the lines to display a textual label
    private final double lableWidth = 25;

    //Dynamic rendering properties
    private boolean renderLabelLines = true;
    private boolean renderRunwayRotated = false;
    private boolean renderWindCompass = true;
    private Color topDownBackgroundColor = Color.GOLD;
    private Color sideOnBackgroundColor = Color.SKYBLUE;

    private List<Pair<Line, String>> labelLines;
    private RunwayParams currentlyHighlightedParam = RunwayParams.NONE;

    public RunwayRenderer(RunwayPair runwayPair, GraphicsContext graphicsContext) {
        this.runwayPair = runwayPair;
        this.graphicsContext = graphicsContext;
        this.runwayRenderParams = new RunwayRenderParams();
        this.scaleAffine = new Affine(new Scale(1, 1, getCenterX(), getCenterY()));
        this.translateAffine = new Affine(new Translate(0, 0));
        this.mouseLoc = new Point(0, 0);
        this.zoomDelta = 1;
        this.currentZoom = 0;
        this.translateX = 0;
        this.translateY = 0;
        this.windAngle = -1;
        this.red = 50;
        this.initParams();
        this.runwayRenderParams.init();
        this.setZoom(MIN_ZOOM + (MAX_ZOOM-MIN_ZOOM)/3);
    }


    public RunwayRenderer(RunwayPair runwayPair, GraphicsContext graphicsContext, Boolean isSideView){

        this.runwayPair = runwayPair;
        this.graphicsContext = graphicsContext;
        this.runwayRenderParams = new RunwayRenderParams();
        this.initParams();
        this.initSideViewParams();
    }

    private void initSideViewParams() {
        this.runwayRenderParams.setSideOnRunwayHeight(7);
        this.runwayRenderParams.setSideOnRunwayStartY((int) (graphicsContext.getCanvas().getHeight()/2));
    }



    public void drawObstacle(Obstacle obstacle, int distanceFromThreshold, int distanceFromCenterline, String selectedThresholdName, String unselectedThresholdName){

        Image planeImage;
        int selected = Integer.parseInt( selectedThresholdName.substring(0,2) );
        int unSelected = Integer.parseInt( unselectedThresholdName.substring(0,2) );
        System.out.println("Draw obstacle, runways are : " + selected + unSelected);
        int objectStartX;
        if (selectedThresholdName.equals(runwayPair.getR2().getRunwayDesignator().toString())){
            System.out.println("using runway " + runwayPair.getR2().getRunwayDesignator().toString());
            objectStartX = runwayRenderParams.getRunwayStartX() + runwayRenderParams.getRunwayLength();
            int obstacleShift = (int) (distanceFromThreshold*1.0/runwayRenderParams.getRealLifeMaxLenR2()*1.0 * runwayRenderParams.getRunwayLength());
            objectStartX -= obstacleShift;
            System.out.print("Percentage : ");
            System.out.println(distanceFromThreshold*1.0/runwayRenderParams.getRealLifeMaxLenR2()*1.0);
            System.out.println("Shifting the obstacle by " + obstacleShift + "m");

            planeImage = new Image("/rec/object-airplaneLeft.png");

        } else {
            planeImage = new Image("/rec/object-airplaneRight.png");
            System.out.println("using runway instead " + runwayPair.getR1().getRunwayDesignator().toString());
            objectStartX = runwayRenderParams.getRunwayStartX() + (int)(distanceFromThreshold*1.0/runwayRenderParams.getRealLifeMaxLenR1()*1.0 * runwayRenderParams.getRunwayLength());
        }
        // Not too happy with this following line ._.
        //Rectangle obstacle = new Rectangle(objectStartPosition,this.graphicsContext.getCanvas().getHeight()/2 - 25,this.graphicsContext.getCanvas().getWidth() / 30 ,height * this.graphicsContext.getCanvas().getHeight() / 200);

        int maxWidth = (int) this.graphicsContext.getCanvas().getWidth();
        double maxHeight = this.graphicsContext.getCanvas().getHeight();
        // 100 height scaled to the length of the runway
        int obstacleHeight = (int) (obstacle.getHeight() * maxHeight) / 100;
        // 80 m length of airplane
        int obstacleWidth = (int) ( maxWidth) * 80 / 1000;
        runwayRenderParams.setObstacleHeight(obstacleHeight);
        runwayRenderParams.setObastacleWidth(obstacleWidth);
/*
        if(obstacle.getName().contains("Airbus") || obstacle.getName().contains("Boeing")){

            this.graphicsContext.drawImage(planeImage,objectStartX,runwayRenderParams.getSideOnRunwayStartY()-obstacleHeight, obstacleWidth, obstacleHeight);
        }else{
            Rectangle obstacleRect = new Rectangle(objectStartX, runwayRenderParams.getSideOnRunwayStartY()-obstacleHeight, obstacleWidth/4, obstacleHeight);
            drawRect(obstacleRect, Color.RED);
        }*/

        Rectangle obstacleRect = new Rectangle(objectStartX, runwayRenderParams.getSideOnRunwayStartY()-obstacleHeight, obstacleWidth/4, obstacleHeight);
        drawRect(obstacleRect, Color.RED);

        //obstacle will not cover the take off message
        renderTakeOfMessages(maxWidth, (int) maxHeight);


    }

    private void initParams(){
        //canvas dimensions
        int maxWidth = (int) this.graphicsContext.getCanvas().getWidth();
        double maxHeight = this.graphicsContext.getCanvas().getHeight();

        //Layout properties
        this.runwayRenderParams.setMargin((int) (0.15*maxWidth));
        this.runwayRenderParams.setRunwayStartX(runwayRenderParams.getMargin());

        //Runway
        this.runwayRenderParams.setRunwayLength(maxWidth - 2*runwayRenderParams.getRunwayStartX());
        this.runwayRenderParams.setRunwayHeight(100);
        this.runwayRenderParams.setRunwayStartY((int) (maxHeight/2 - runwayRenderParams.getRunwayHeight()/2));
        this.runwayRenderParams.setRealLifeMaxLenR1(runwayPair.getR1().getTORA());
        this.runwayRenderParams.setRealLifeMaxLenR2(runwayPair.getR2().getTORA());

        //Clearway
        this.runwayRenderParams.setClearwayHeight(10);

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
        this.runwayRenderParams.setLabelFontSize(18);
        this.runwayRenderParams.setLabelTextMargin(10);
        this.runwayRenderParams.setLabelSpacing(30);

        //Wind direction indicator
        this.runwayRenderParams.setWindArrowLength(50);
        this.runwayRenderParams.setWindArrowX(50);
        this.runwayRenderParams.setWindArrowY(50);

        refreshLines();
    }

    public void refreshLines(){
        labelLines = runwayPair.getR1().getLabelLines(this.runwayRenderParams, LabelRunwayDirection.UP, currentlyHighlightedParam, runwayRenderParams.getRunwayStartX());
        labelLines.addAll(runwayPair.getR2().getLabelLines(this.runwayRenderParams, LabelRunwayDirection.DOWN, currentlyHighlightedParam, runwayRenderParams.getRunwayStartX()));
    }

    public void render(){
        double rotationAngle;
        if (runwayPair.getR1().getRunwayDesignator().angle <= 9){
            rotationAngle = runwayPair.getR1().getRunwayDesignator().angle;
        } else {
            rotationAngle = runwayPair.getR2().getRunwayDesignator().angle;
        }
        rotationAngle -= 9;
        Affine rotate = new Affine(new Rotate(rotationAngle*10, getCenterX(), getCenterY()));
//        Scale myScale = new Scale(1.04, 1.04, getCenterX(), getCenterY());
        Scale myScale = new Scale(currentZoom, currentZoom, getCenterX(), getCenterY());
        scaleAffine.setToTransform(new Scale(currentZoom, currentZoom, getCenterX(), getCenterY()));
        //we can zoomDelta, just decide whether user wants to zoomDelta in or zoomDelta out
        if (zoomDelta > 0){
            scaleAffine.append(myScale);
        } else if (zoomDelta < 0){
            try {
                scaleAffine.append(myScale.createInverse());
            } catch (NonInvertibleTransformException e) {
                e.printStackTrace();
            }
        }

        //Clamp zoomDelta to boundary levels, as Jasmine wanted
        if (scaleAffine.getMxx() > MAX_ZOOM){
            scaleAffine.setMxx(MAX_ZOOM);
            scaleAffine.setMyy(MAX_ZOOM);
            scaleAffine.setToTransform(new Scale(MAX_ZOOM, MAX_ZOOM, getCenterX(), getCenterY()));
        } else if (scaleAffine.getMxx() < MIN_ZOOM) {
            scaleAffine.setMxx(MIN_ZOOM);
            scaleAffine.setMyy(MIN_ZOOM);
            scaleAffine.setToTransform(new Scale(MIN_ZOOM, MIN_ZOOM, getCenterX(), getCenterY()));
        }

        //System.out.println(scaleAffine.toString());


        //Did a zoomDelta happen? if one did happen, we need to notify user along with resetting the delta
        if (zoomDelta != 0){
            int zoomLevel = (int) (scaleAffine.getMxx()/(MAX_ZOOM-MIN_ZOOM) * 100);
            Window window = graphicsContext.getCanvas().getScene().getWindow();
            zoomDelta = 0;
        }



        Translate myTranslate = new Translate(translateX, translateY);
        translateX = 0;
        translateY = 0;
        translateAffine.append(myTranslate);

        //Draw background
        graphicsContext.setFill(topDownBackgroundColor);
        //graphicsContext.setFill(Color.rgb(red, 0, 0));
        red += 5;
        if (red > 255){
            red = 255;
        }
        graphicsContext.fillRect(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());

        //Rotate runway as per orientation of it
        graphicsContext.save();
        graphicsContext.transform(translateAffine);
        if (renderRunwayRotated){
            graphicsContext.transform(rotate);
        }
        graphicsContext.transform(scaleAffine);


        Rectangle runwayRect = new Rectangle(runwayRenderParams.getRunwayStartX(), runwayRenderParams.getRunwayStartY(), runwayRenderParams.getRunwayLength(), runwayRenderParams.getRunwayHeight());

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

        //Stopway creation
        Rectangle[] stopways = getStopways(runwayRenderParams.getRunwayStartY(), runwayRenderParams.getRunwayHeight());

        //Clearway creation
        Rectangle[] clearways = getClearways(runwayRenderParams.getRunwayStartY() - runwayRenderParams.getClearwayHeight(), runwayRenderParams.getRunwayHeight() + 2*runwayRenderParams.getClearwayHeight());

        //Finally we get to the drawing part !

        //Draw runway
        drawRect(runwayRect, RUNWAY_COLOR);

        //Draw dashes
        for (Rectangle dash : dashes) {
            drawRect(dash, Color.WHITE);
        }

        //Draw dashes
        for (Rectangle zebra : zebraDashes){
            drawRect(zebra, Color.WHITE);
        }

        //Draw clearway
        for (Rectangle clearway : clearways){
            drawRect(clearway, Color.GREEN);
        }

        //Draw stopway
        for (Rectangle stopway : stopways){
            drawRect(stopway, Color.PINK);
        }

        //And the runway identifiers
        this.graphicsContext.setFill(Color.WHITE);
        this.graphicsContext.setFont(Font.font("Verdana", FontWeight.BOLD, 32));
        this.graphicsContext.save();
        this.graphicsContext.translate(runwayRect.getX() + runwayRenderParams.getZebraMarginOuter() + runwayRenderParams.getZebraMarginInner() + runwayRenderParams.getZebraDashLength(), runwayRect.getY() + runwayRect.getHeight()/2);
        this.graphicsContext.rotate(-90);
        this.graphicsContext.fillText(runwayPair.getR1().getRunwayDesignator().toString(), -25, 30);
        this.graphicsContext.restore();
        this.graphicsContext.save();
        this.graphicsContext.translate(runwayRect.getX() + runwayRect.getWidth() - runwayRenderParams.getZebraMarginOuter() - runwayRenderParams.getZebraMarginInner() - runwayRenderParams.getZebraDashLength() - runwayRenderParams.getIdentifierMargin(), runwayRect.getY());
        this.graphicsContext.rotate(-90);
        this.graphicsContext.fillText(runwayPair.getR2().getRunwayDesignator().toString(), -70, 10);
        this.graphicsContext.restore();
        //this.graphicsContext.fillText("SEG BAFFI", 20, 20);

        //And the labels identifying the runway params
        if (renderLabelLines){
            for (Pair<Line, String> line : labelLines){
                graphicsContext.setFont(new Font(runwayRenderParams.getLabelFontSize()));
                graphicsContext.setStroke(Color.BLACK);
                renderParamLine(line);
            }
        }

        graphicsContext.restore();

        if (windAngle != -1 && renderWindCompass){
            Pair<Line, Line> lines = getWindLinePair();
            drawLine(lines.getKey(), Color.BLUE);
            drawLine(lines.getValue(), Color.RED);
            renderArrowCap((int) lines.getValue().getEndX(), (int) lines.getValue().getEndY(), windAngle - Math.PI);
        }
    }
    public void renderSideview(){
        //canvas dimensions
        int maxWidth = (int) this.graphicsContext.getCanvas().getWidth();
        double maxHeight = this.graphicsContext.getCanvas().getHeight();


        graphicsContext.clearRect(0,0,maxWidth,maxHeight);
        //set environment color
        graphicsContext.setFill(sideOnBackgroundColor);
        graphicsContext.fillRect(0, 0, maxWidth, maxHeight/2);
        graphicsContext.setFill(Color.OLDLACE);
        graphicsContext.fillRect(0, maxHeight/2, maxWidth, maxHeight);

        renderTakeOfMessages(maxWidth, (int) maxHeight);

        Rectangle runwayRect = new Rectangle(runwayRenderParams.getRunwayStartX(),runwayRenderParams.getSideOnRunwayStartY() , runwayRenderParams.getRunwayLength(), runwayRenderParams.getSideOnRunwayHeight());
        Rectangle stopAreaLeft = new Rectangle(0, maxHeight /2, runwayRenderParams.getRunwayStartX(), 7);
        Rectangle stopAreaRight = new Rectangle(runwayRenderParams.getRunwayStartX() +  runwayRenderParams.getRunwayLength() , maxHeight /2, maxWidth , 7);

        //Stopway creation
        Rectangle[] stopways = getStopways(runwayRenderParams.getSideOnRunwayStartY(), runwayRenderParams.getSideOnRunwayHeight());

        //Clearway creation
        Rectangle[] clearways = getClearways(runwayRenderParams.getSideOnRunwayStartY(), runwayRenderParams.getSideOnRunwayHeight());


        Rectangle clearAreaLeft = new Rectangle(runwayRenderParams.getRunwayStartX(), maxHeight /2, runwayRect.getX() + runwayRenderParams.getZebraMarginOuter() - runwayRenderParams.getRunwayStartX() , 7);
        Rectangle clearAreaRight = new Rectangle(runwayRenderParams.getRunwayStartX() + runwayRenderParams.getRunwayLength() - runwayRenderParams.getZebraMarginOuter(), maxHeight /2, runwayRenderParams.getZebraMarginOuter() , 7);
        drawRect(runwayRect, RUNWAY_COLOR );

        //Clearway
        for (Rectangle rect : clearways){
            drawRect(rect, Color.GREEN);
        }

        //Stopway
        for (Rectangle rect : stopways){
            drawRect(rect, Color.PINK);
        }

        //And the labels identifying the runway params
//        for (Pair<Line, String> line : labelLines){
//            graphicsContext.setFont(new Font(runwayRenderParams.getLabelFontSize()));
//            renderParamLine(line);
//        }

        this.graphicsContext.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        this.graphicsContext.setFill(Color.BLACK);
        this.graphicsContext.fillText(runwayPair.getR1().getRunwayDesignator().toString(),70 , maxHeight /2 + 20);
        this.graphicsContext.fillText(runwayPair.getR2().getRunwayDesignator().toString(), graphicsContext.getCanvas().getWidth() - 90 , maxHeight /2 + 20);
    }

    private void renderTakeOfMessages(int maxWidth, int maxHeight){
        Line directionLeft = new Line(maxWidth , maxHeight - maxHeight/30 , maxWidth - maxWidth/7, maxHeight - maxHeight/30);
        this.graphicsContext.moveTo(directionLeft.getStartX(), directionLeft.getStartY());
        this.graphicsContext.lineTo(directionLeft.getEndX(), directionLeft.getEndY());
        this.graphicsContext.stroke();
        this.graphicsContext.setFill(Color.BLACK);
        renderArrowCap((int) directionLeft.getEndX(), (int) directionLeft.getEndY(), ArrowDirection.LEFT);
        this.graphicsContext.setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
        this.graphicsContext.fillText("Landing and take-off in this direction",maxWidth - maxWidth/2 ,maxHeight-maxHeight/20);

        Line directionRight = new Line(0,maxHeight/30, maxWidth/7, maxHeight/30);
        this.graphicsContext.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        this.graphicsContext.moveTo(directionRight.getStartX(), directionRight.getStartY());
        this.graphicsContext.lineTo(directionRight.getEndX(), directionRight.getEndY());
        this.graphicsContext.stroke();
        this.graphicsContext.setStroke(Color.BLACK);
        this.graphicsContext.setFill(Color.BLACK);
        renderArrowCap((int) directionRight.getEndX(), (int) directionRight.getEndY(), ArrowDirection.RIGHT);
        this.graphicsContext.setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
        this.graphicsContext.fillText("Landing and take-off in this direction",maxWidth/20 ,maxWidth/20);
    }

    private void renderParamLine(Pair<Line, String> labelLine){
        Line line = labelLine.getKey();
        int midX = (int) (line.getStartX() + line.getEndX())/2;
        int midY = (int) (line.getStartY() + line.getEndY())/2;

        if (line.getUserData() != null){
            this.graphicsContext.setLineWidth(runwayRenderParams.getHighLightWidth());
        } else {
            this.graphicsContext.setLineWidth(0.8);
        }

        // First section
        this.graphicsContext.strokeLine(line.getStartX(), line.getStartY(), midX - lableWidth, line.getEndY());
        renderArrowCap((int) line.getStartX(), (int) line.getStartY(), ArrowDirection.LEFT);

        // Text between sections
        this.graphicsContext.fillText(labelLine.getValue(), midX - lableWidth + runwayRenderParams.getLabelTextMargin(), midY + runwayRenderParams.getLabelFontSize()/2);

        // Second section
        this.graphicsContext.strokeLine(midX + lableWidth + runwayRenderParams.getLabelTextMargin(), line.getStartY(), line.getEndX(), line.getEndY());
        renderArrowCap((int) line.getEndX(), (int) line.getEndY(), ArrowDirection.RIGHT);
    }

    private void renderArrowCap(int x, int y, ArrowDirection direction){
        double angle;
        if (direction == ArrowDirection.LEFT){
            renderArrowCap(x, y, 0);
        } else {
            renderArrowCap(x, y, Math.PI);
        }
    }

    private void renderArrowCap(int x, int y, double angle){
        double arrowWideness = Math.PI/4;
        int arrowLength = 15;

        //top line of the arrow
        this.graphicsContext.strokeLine(x, y, x + Math.cos(angle - arrowWideness/2) * arrowLength, y + Math.sin(angle - arrowWideness/2) * arrowLength);

        //bottom line of the arrow
        this.graphicsContext.strokeLine(x, y, x + Math.cos(angle + arrowWideness/2) * arrowLength, y + Math.sin(angle + arrowWideness/2) * arrowLength);
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

    private Rectangle[] getStopways(int startY, int height){
        //Stopway creation
        Rectangle[] stopways = new Rectangle[2];
        int leftStopwayLength = (int) (runwayPair.getR1().getStopwayLength() * runwayRenderParams.getRunwayLength());
        int rightStopwayLength = (int) (runwayPair.getR1().getStopwayLength() * runwayRenderParams.getRunwayLength());
        stopways[0] = new Rectangle(runwayRenderParams.getRunwayStartX() - leftStopwayLength, startY, leftStopwayLength, height);
        stopways[1] = new Rectangle(runwayRenderParams.getRunwayStartX() + runwayRenderParams.getRunwayLength(), startY, rightStopwayLength, height);
        return stopways;
    }

    private Rectangle[] getClearways(int startY, int height){
        //Clearway creation
        Rectangle[] clearways = new Rectangle[2];
        int leftClearwayLength = (int) (runwayPair.getR2().getClearwayLength() * runwayRenderParams.getRunwayLength());
        int rightClearwayLength = (int) (runwayPair.getR1().getClearwayLength() * runwayRenderParams.getRunwayLength());
        clearways[0] = new Rectangle(runwayRenderParams.getRunwayStartX() - leftClearwayLength, startY, leftClearwayLength, height);
        clearways[1] = new Rectangle(runwayRenderParams.getRunwayStartX() + runwayRenderParams.getRunwayLength(), startY, rightClearwayLength, height);
        return clearways;
    }

    private void drawRect(Rectangle rect, Color color){
        this.graphicsContext.setFill(color);
        this.graphicsContext.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }

    private void drawLine(Line line, Color color){
        this.graphicsContext.setStroke(color);
        this.graphicsContext.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
    }

    public void setCurrentlyHighlightedParam(RunwayParams currentlyHighlightedParam) {
        this.currentlyHighlightedParam = currentlyHighlightedParam;
        this.refreshLines();
        this.render();
    }

    private Line getWindLine(){
        double xVel = Math.cos(windAngle);
        double yVel = Math.sin(windAngle);
        double xVel2 = Math.cos(windAngle - Math.PI);
        double yVel2 = Math.sin(windAngle - Math.PI);

        int x1 = (int) (xVel*runwayRenderParams.getWindArrowLength()/2+runwayRenderParams.getWindArrowX());
        int y1 = (int) (yVel*runwayRenderParams.getWindArrowLength()/2+runwayRenderParams.getWindArrowY());
        int x2 = (int) (xVel2*runwayRenderParams.getWindArrowLength()/2+runwayRenderParams.getWindArrowX());
        int y2 = (int) (yVel2*runwayRenderParams.getWindArrowLength()/2+runwayRenderParams.getWindArrowY());

        return new Line(x2, y2, x1, y1);

    }

    private Pair<Line, Line> getWindLinePair(){
        double xVelForw = Math.cos(windAngle);
        double yVelForw = Math.sin(windAngle);
        double xVelPrev = Math.cos(windAngle - Math.PI);
        double yVelPrev = Math.sin(windAngle - Math.PI);

        int xPrev = (int) (xVelPrev*runwayRenderParams.getWindArrowLength()/2+runwayRenderParams.getWindArrowX());
        int yPrev = (int) (yVelPrev*runwayRenderParams.getWindArrowLength()/2+runwayRenderParams.getWindArrowY());

        int xForw = (int) (xVelForw*runwayRenderParams.getWindArrowLength()/2+runwayRenderParams.getWindArrowX());
        int yForw = (int) (yVelForw*runwayRenderParams.getWindArrowLength()/2+runwayRenderParams.getWindArrowY());

        Line prevLine = new Line(xPrev, yPrev, runwayRenderParams.getWindArrowX(), runwayRenderParams.getWindArrowY());
        Line forwLine = new Line(runwayRenderParams.getWindArrowX(), runwayRenderParams.getWindArrowY(), xForw, yForw);

        return new Pair<>(prevLine, forwLine);

    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    private int getCenterX(){
        return (int) this.graphicsContext.getCanvas().getWidth()/2;
    }

    private int getCenterY(){
        return (int) this.graphicsContext.getCanvas().getHeight()/2;
    }

    public void updateZoom(int zoom) {
        this.zoomDelta = zoom;
        System.out.println("current zoomDelta is " + zoom);
        this.render();
    }

    public void setZoom(double zoom){
        this.currentZoom = zoom;
        this.render();
    }

    public void incZoom(){
        this.currentZoom += ZOOM_STEP;
        if (this.currentZoom > MAX_ZOOM) {
            this.currentZoom = MAX_ZOOM;
        }
        this.render();
    }

    public void decZoom(){
        this.currentZoom -= ZOOM_STEP;
        if (this.currentZoom < MIN_ZOOM){
            this.currentZoom = MIN_ZOOM;
        }
        this.render();
    }

    public double getZoom(){
        return currentZoom;
    }

    public int getZoomPercentage(){
        return (int) (scaleAffine.getMxx()/(MAX_ZOOM-MIN_ZOOM) * 100);
    }

    public void setMouseLocation(int x, int y) {
        this.mouseLoc.x = x;
        this.mouseLoc.y = y;
    }

    public void translate(int x, int y){
        this.translateX = -x;
        this.translateY = -y;
        this.render();
    }

    public void setWindAngle(double windAngle) {
        this.windAngle = windAngle;
        this.render();
    }

    public double getWindAngle() {
        return windAngle;
    }

    public RunwayRenderParams getRunwayRenderParams() {
        return runwayRenderParams;
    }

    public void setRenderLabelLines(boolean renderLabelLines) {
        this.renderLabelLines = renderLabelLines;
        this.render();
    }

    public void setRenderRunwayRotated(boolean renderRunwayRotated) {
        this.renderRunwayRotated = renderRunwayRotated;
        this.render();
    }

    public void setRenderWindCompass(boolean renderWindCompass) {
        this.renderWindCompass = renderWindCompass;
        this.render();
    }

    public void setTopDownBackgroundColor(Color topDownBackgroundColor) {
        this.topDownBackgroundColor = topDownBackgroundColor;
        this.render();
    }

    public void setSideOnBackgroundColor(Color sideOnBackgroundColor) {
        System.out.println("Change in side on background color");
        this.sideOnBackgroundColor = sideOnBackgroundColor;
        this.renderSideview();
    }
}
