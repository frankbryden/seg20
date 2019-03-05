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
