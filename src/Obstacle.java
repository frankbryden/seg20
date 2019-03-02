public class Obstacle {
    private int height;

    //I'm thinking these two attributes should be given as parameters to the recalculate method in the Calculations class
    //private int distanceFromThreshold;
    //private int distanceFromCenterLine;

    public Obstacle(int height){
        this.height = height;
    }

    public int getHeight() {
        return height;
    }
}
