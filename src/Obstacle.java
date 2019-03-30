public class Obstacle {
    private double height;
    private String name;
    //I'm thinking these two attributes should be given as parameters to the recalculate method in the Calculations class
    //private int distanceFromThreshold;
    //private int distanceFromCenterLine;


    public Obstacle(String name, double height) {
        this.height = height;
        this.name = name;
    }

    public double getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setName(String name) {
        this.name = name;
    }
}
