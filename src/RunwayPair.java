import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class RunwayPair {
    private static final Color RUNWAY_COLOR = Color.web("rgb(60, 67, 79)");
    private RunwayConfig r1, r2;
    private String name;

    public RunwayPair (RunwayConfig r1, RunwayConfig r2){
        this.r1 = r1;
        this.r2 = r2;
        this.init();
    }

    public RunwayPair(){
        this.r1 = null;
        this.r2 = null;
    }

    public void init(){
        this.name = generatePairName();
    }

    private String generatePairName(){
        if (r1.getRunwayDesignator().angle < r2.getRunwayDesignator().angle){
            return r1.getRunwayDesignator().toString() + "/" + r2.getRunwayDesignator().toString();
        } else {
            return r2.getRunwayDesignator().toString() + "/" + r1.getRunwayDesignator().toString();
        }
    }

    public void render(GraphicsContext gc){
        //canvas dimensions
        double maxWidth = gc.getCanvas().getWidth();
        double maxHeight = gc.getCanvas().getHeight();

        //Layout properties
        double margin = 0.05*maxWidth;
        double halfVert = 0.5*maxHeight;

        //Runway
        int runwayLength = (int) (maxWidth - margin);
        int runwayHeight = 100;
        Rectangle runwayRect = new Rectangle(margin, maxHeight/2 - runwayHeight/2, runwayLength, runwayHeight);

        //Zebra margin : margin on either side of each zebra crossing
        int zebraMargin = 5;

        //Identifier margin : margin after the first zebra and before the second to leave space for the runway identifier
        int identifierMargin = 20;

        //Zebra Crossing
        int zebraDashLength = 50;
        int zebraDashOn = 4;
        int zebraDashOff = 4;
        int zebraVertLength = zebraDashOn + zebraDashOff;
        int zebraDashCount = runwayHeight/zebraVertLength;
        int zebraDashShift = runwayHeight % zebraVertLength;
        Rectangle[] zebraDashes = new Rectangle[2*zebraDashCount];
        for (int i = 0; i < zebraDashCount; i++){
            zebraDashes[i] = new Rectangle(runwayRect.getX() + zebraMargin, zebraDashShift + runwayRect.getY() + zebraVertLength * i, zebraDashLength, zebraDashOn);
        }

        for (int i = zebraDashCount; i < 2*zebraDashCount; i++){
            zebraDashes[i] = new Rectangle(runwayRect.getX() + runwayRect.getWidth() - zebraMargin - zebraDashLength, zebraDashShift + runwayRect.getY() + zebraVertLength * (i - zebraDashCount), zebraDashLength, zebraDashOn);
        }

        //Runway dashes
        int dashOn = 40;
        int dashOff = 25;
        int dashLength = dashOn + dashOff;
        int dashHeight = 5;
        int remainingRunwayLength = runwayLength - 2*(2*zebraMargin + zebraDashLength) - 2*identifierMargin;
        int dashCount = (int) (remainingRunwayLength/dashLength);
        int dashShift = (int) (remainingRunwayLength % dashLength);
        Rectangle[] dashes = new Rectangle[dashCount];
        for (int i = 0; i < dashCount; i++){
            dashes[i] = new Rectangle(dashShift + 2*zebraMargin + zebraDashLength + runwayRect.getX() + i * dashLength, runwayRect.getY() + runwayRect.getHeight()/2, dashOn, dashHeight);
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
        gc.translate(runwayRect.getX() + 2*zebraMargin + zebraDashLength, runwayRect.getY() + runwayRect.getHeight()/2);
        gc.rotate(-90);
        gc.fillText(r1.getRunwayDesignator().toString(), -20, 30);
        gc.restore();
        gc.save();
        gc.translate(runwayRect.getX() + runwayRect.getWidth() - 2*zebraMargin - zebraDashLength - identifierMargin, runwayRect.getY());
        gc.rotate(-90);
        gc.fillText(r2.getRunwayDesignator().toString(), -70, 5);
        gc.restore();
        gc.fillText("hey", 20, 20);



    }

    public void drawRect(GraphicsContext gc, Rectangle rect, Color color){
        gc.setFill(color);
        System.out.println(rect.getX());
        System.out.println(rect.getY());
        System.out.println(rect.getWidth());
        System.out.println(rect.getHeight());
        gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Runway Pair : ").append(this.name);
        sb.append("\n");
        sb.append(r1.toString());
        sb.append("\n");
        sb.append(r2.toString());
        return sb.toString();
    }

    public void setR1(RunwayConfig r1) {
        this.r1 = r1;
    }

    public void setR2(RunwayConfig r2) {
        this.r2 = r2;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RunwayConfig getR1() {
        return r1;
    }

    public RunwayConfig getR2() {
        return r2;
    }

    public String getName() {
        return name;
    }
}
