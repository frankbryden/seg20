public class RunwayRenderParams {
    //Layout properties
    private double margin;
    private double halfVert;

    //Runway
    private int runwayLength;
    private int runwayHeight;

    //Zebra margin : margin on either side of each zebra crossing
    private int zebraMarginInner;
    private int zebraMarginOuter;

    //Identifier margin : margin after the first zebra and before the second to leave space for the runway identifier
    private int identifierMargin;

    //Zebra Crossing
    private int zebraDashLength;
    private int zebraDashOn = 4;
    private int zebraDashOff = 4;
    private int zebraVertLength = zebraDashOn + zebraDashOff;
    private int zebraDashCount = runwayHeight/zebraVertLength;
    private int zebraDashShift = runwayHeight % zebraVertLength;

    //Runway dashes
    private int dashOn = 40;
    private int dashOff = 25;
    private int dashLength = dashOn + dashOff;
    private int dashHeight = 5;
    private int remainingRunwayLength = runwayLength - 2*(zebraMarginInner + zebraMarginOuter + zebraDashLength) - 2*identifierMargin;
    private int dashCount = (int) (remainingRunwayLength/dashLength);
    private int dashShift = (int) (remainingRunwayLength % dashLength);
    if (dashShift > dashLength/2){
        dashShift /= 2;
    }

    public RunwayRenderParams(int maxWidth, int runwayHeight, int zebraMarginInner, int zebraMarginOuter, int identifierMargin, int zebraDashLength, int zebraDashOn, int zebraDashOff, int dashOn, int dashOff, int dashHeight) {
        this.margin = 0.01*maxWidth;
        this.halfVert = 0.5*maxWidth;
        this.runwayLength = (int) (maxWidth - margin);
        this.runwayHeight = runwayHeight;
        this.zebraMarginInner = zebraMarginInner;
        this.zebraMarginOuter = zebraMarginOuter;
        this.identifierMargin = identifierMargin;
        this.zebraDashLength = zebraDashLength;
        this.zebraDashOn = zebraDashOn;
        this.zebraDashOff = zebraDashOff;
        this.dashOn = dashOn;
        this.dashOff = dashOff;
        this.dashHeight = dashHeight;
    }

    private void init(){
        //Init zebra params based on the zebra lengths
        zebraVertLength = zebraDashOn + zebraDashOff;
        private int zebraDashCount = runwayHeight/zebraVertLength;
        private int zebraDashShift = runwayHeight % zebraVertLength;
    }

    public double getMargin() {
        return margin;
    }

    public double getHalfVert() {
        return halfVert;
    }

    public int getRunwayLength() {
        return runwayLength;
    }

    public int getRunwayHeight() {
        return runwayHeight;
    }

    public int getZebraMarginInner() {
        return zebraMarginInner;
    }

    public int getZebraMarginOuter() {
        return zebraMarginOuter;
    }

    public int getIdentifierMargin() {
        return identifierMargin;
    }

    public int getZebraDashLength() {
        return zebraDashLength;
    }

    public int getZebraDashOn() {
        return zebraDashOn;
    }

    public int getZebraDashOff() {
        return zebraDashOff;
    }

    public int getZebraVertLength() {
        return zebraVertLength;
    }

    public int getZebraDashCount() {
        return zebraDashCount;
    }

    public int getZebraDashShift() {
        return zebraDashShift;
    }

    public int getDashOn() {
        return dashOn;
    }

    public int getDashOff() {
        return dashOff;
    }

    public int getDashLength() {
        return dashLength;
    }

    public int getDashHeight() {
        return dashHeight;
    }

    public int getRemainingRunwayLength() {
        return remainingRunwayLength;
    }

    public int getDashCount() {
        return dashCount;
    }

    public int getDashShift() {
        return dashShift;
    }
}
