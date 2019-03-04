public class Obstacle {
    private int height;
    private String name;
    //I'm thinking these two attributes should be given as parameters to the recalculate method in the Calculations class
    //private int distanceFromThreshold;
    //private int distanceFromCenterLine;

    public Obstacle(String name, int height){
        this.name = name;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }
}
