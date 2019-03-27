public class RunwayRenderParams {
    //Layout properties
    private int margin;
    private double halfVert;

    //Runway
    private int runwayLength;
    private int runwayStartX;
    private int runwayHeight;
    private int runwayStartY;
    private int sideOnRunwayHeight;
    private int sideOnRunwayStartY;
    private int realLifeMaxLenR1;
    private int realLifeMaxLenR2;

    //Stopway
    private int stopwayLength;

    //Clearway
    private int clearwayLength;
    private int clearwayHeight;

    //Zebra margin : margin on either side of each zebra crossing
    private int zebraMarginInner;
    private int zebraMarginOuter;

    //Identifier margin : margin after the first zebra and before the second to leave space for the runway identifier
    private int identifierMargin;

    //Zebra Crossing
    private int zebraDashLength;
    private int zebraDashOn = 4;
    private int zebraDashOff = 4;
    private int zebraVertLength;
    private int zebraDashCount;
    private int zebraDashShift;

    //Runway dashes
    private int dashOn;
    private int dashOff;
    private int dashLength;
    private int dashHeight;
    private int remainingRunwayLength;
    private int dashCount;
    private int dashShift;

    //Wind direction
    private int windArrowLength;
    private int windArrowX;
    private int windArrowY;

/*
    //Sideview parameters

    //TODA-L
    private int startLeftToda;
    private int endLeftToda;
    //TODA-R
    private int startRigtToda;
    private int endRightToda;

    //ASDA-L
    private int startLeftAsda;
    private int endLeftAsda;
    //ASDA-R
    private int startRigtAsda;
    private int endRightAsda;

    //TORA-L
    private int startLeftTora;
    private int endLeftTora;
    //TORA-R
    private int startRigtTora;
    private int endRightTora;

    */

    //Labels
    private int labelFontSize;
    //labelTextMargin : margin before and after the text, where the lines meet the text
    private int labelTextMargin;
    private int labelSpacing;
    private int highLightWidth = 3;

    public RunwayRenderParams(int maxWidth, int runwayHeight, int zebraMarginInner, int zebraMarginOuter, int identifierMargin, int zebraDashLength, int zebraDashOn, int zebraDashOff, int dashOn, int dashOff, int dashHeight) {
        //User configurable values below
        this.margin = (int) (0.01*maxWidth);
        this.halfVert = 0.5*maxWidth;
        this.runwayLength = maxWidth - margin;
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

        //initialise all the variables which depend on the parameterised values
        this.init();
    }

    public RunwayRenderParams() {

    }

    public void init(){
        //Init zebra params based on the zebra lengths
        this.zebraVertLength = zebraDashOn + zebraDashOff;
        this.zebraDashCount = runwayHeight/zebraVertLength;
        this.zebraDashShift = runwayHeight % zebraVertLength;

        //dash params
        this.dashLength = dashOn + dashOff;
        //Middle part of the runway where dashes need to be drawn
        this.remainingRunwayLength = runwayLength - 2*(zebraMarginInner + zebraMarginOuter + zebraDashLength) - 2*identifierMargin;
        this.dashCount = remainingRunwayLength/dashLength;
        //determine wasted remaining length at the end. eg runway length 10, dash length 3, we can't cover the entire runway.
        //therefore, we do 10 % 3 = 1, and we shift all the dashes to the right by that amount
        this.dashShift = remainingRunwayLength % dashLength;
        //shift the dashes by at most half the length of a dash
        if (dashShift > dashLength/2){
            this.dashShift /= 2;
        }

    }


    public int getRunwayStartY() {
        return runwayStartY;
    }

    public void setRunwayStartY(int runwayStartY) {
        this.runwayStartY = runwayStartY;
    }

    public int getRunwayCenterlineY(){
        return runwayStartY + runwayHeight/2;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public void setHalfVert(double halfVert) {
        this.halfVert = halfVert;
    }

    public void setRunwayLength(int runwayLength) {
        this.runwayLength = runwayLength;
    }

    public void setRunwayHeight(int runwayHeight) {
        this.runwayHeight = runwayHeight;
    }

    public void setRunwayStartX(int runwayStartX) {
        this.runwayStartX = runwayStartX;
    }

    public void setZebraMarginInner(int zebraMarginInner) {
        this.zebraMarginInner = zebraMarginInner;
    }

    public void setZebraMarginOuter(int zebraMarginOuter) {
        this.zebraMarginOuter = zebraMarginOuter;
    }

    public void setIdentifierMargin(int identifierMargin) {
        this.identifierMargin = identifierMargin;
    }

    public void setZebraDashOn(int zebraDashOn) {
        this.zebraDashOn = zebraDashOn;
    }

    public void setZebraDashOff(int zebraDashOff) {
        this.zebraDashOff = zebraDashOff;
    }

    public void setZebraDashLength(int zebraDashLength) {
        this.zebraDashLength = zebraDashLength;
    }

    public void setDashOn(int dashOn) {
        this.dashOn = dashOn;
    }

    public void setDashOff(int dashOff) {
        this.dashOff = dashOff;
    }

    public void setDashHeight(int dashHeight) {
        this.dashHeight = dashHeight;
    }

    public int getMargin() {
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

    public int getRunwayStartX() {
        return runwayStartX;
    }

    public int getLabelFontSize() {
        return labelFontSize;
    }

    public void setLabelFontSize(int labelFontSize) {
        this.labelFontSize = labelFontSize;
    }

    public int getLabelTextMargin() {
        return labelTextMargin;
    }

    public void setLabelTextMargin(int labelTextMargin) {
        this.labelTextMargin = labelTextMargin;
    }

    public int getLabelSpacing() {
        return labelSpacing;
    }

    public void setLabelSpacing(int labelSpacing) {
        this.labelSpacing = labelSpacing;
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

    public int getHighLightWidth() {
        return highLightWidth;
    }

    public void setHighLightWidth(int highLightWidth) {
        this.highLightWidth = highLightWidth;
    }

    public int getClearwayHeight() {
        return clearwayHeight;
    }

    public void setClearwayHeight(int clearwayHeight) {
        this.clearwayHeight = clearwayHeight;
    }

    public int getSideOnRunwayHeight() {
        return sideOnRunwayHeight;
    }

    public void setSideOnRunwayHeight(int sideOnRunwayHeight) {
        this.sideOnRunwayHeight = sideOnRunwayHeight;
    }

    public int getSideOnRunwayStartY() {
        return sideOnRunwayStartY;
    }

    public void setSideOnRunwayStartY(int sideOnRunwayStartY) {
        this.sideOnRunwayStartY = sideOnRunwayStartY;
    }

    public int getWindArrowLength() {
        return windArrowLength;
    }

    public void setWindArrowLength(int windArrowLength) {
        this.windArrowLength = windArrowLength;
    }

    public int getWindArrowX() {
        return windArrowX;
    }

    public void setWindArrowX(int windArrowX) {
        this.windArrowX = windArrowX;
    }

    public int getWindArrowY() {
        return windArrowY;
    }

    public void setWindArrowY(int windArrowY) {
        this.windArrowY = windArrowY;
    }

    public int getRealLifeMaxLenR1() {
        return realLifeMaxLenR1;
    }

    public void setRealLifeMaxLenR1(int realLifeMaxLenR1) {
        this.realLifeMaxLenR1 = realLifeMaxLenR1;
    }

    public int getRealLifeMaxLenR2() {
        return realLifeMaxLenR2;
    }

    public void setRealLifeMaxLenR2(int realLifeMaxLenR2) {
        this.realLifeMaxLenR2 = realLifeMaxLenR2;
    }
}
