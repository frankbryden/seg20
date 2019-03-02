public class RunwayConfig {
    private int TORA;
    private int TODA;
    private int ASDA;
    private int LDA;
    private RunwayDesignator runwayDesignator;

    public RunwayConfig(RunwayDesignator runwayDesignator, int TORA, int TODA, int ASDA, int LDA){
        this.runwayDesignator = runwayDesignator;
        this.TORA = TORA;
        this.TODA = TODA;
        this.ASDA = ASDA;
        this.LDA  = LDA;
    }

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

    public RunwayDesignator getRunwayDesignator() {
        return runwayDesignator;
    }

    public void setRunwayDesignator(RunwayDesignator runwayDesignator) {
        this.runwayDesignator = runwayDesignator;
    }
}
