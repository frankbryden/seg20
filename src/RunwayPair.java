public class RunwayPair {
    private RunwayConfig r1, r2;
    private String name;
    public enum Side {R1, R2, Unknown}

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
        this.r1.setParent(this);
        this.r2.setParent(this);
        this.name = generatePairName();
    }

    private String generatePairName(){
        if (r1.getRunwayDesignator().angle < r2.getRunwayDesignator().angle){
            return r1.getRunwayDesignator().toString() + "/" + r2.getRunwayDesignator().toString();
        } else {
            return r2.getRunwayDesignator().toString() + "/" + r1.getRunwayDesignator().toString();
        }
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
