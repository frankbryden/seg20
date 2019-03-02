import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RunwayDesignator {
    enum Direction {L, R};
    public int angle;
    public Direction direction;

    public RunwayDesignator(int angle, Direction direction){
        this.angle = angle;
        this.direction = direction;
    }

    public RunwayDesignator(String designator){
        Pattern pattern = Pattern.compile("([0-9]+)([LR])");
        Matcher matcher = pattern.matcher(designator);
        if (matcher.find()){
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
            Direction direction = null;
            switch (matcher.group(2)){
                case "R":
                    direction = Direction.R;
                    break;
                case "L":
                    direction = Direction.L;
                    break;
                default:
                    System.err.println("Invalid direction letter : " + matcher.group(2));
            }
            this.angle = Integer.parseInt(matcher.group(1));
            this.direction = direction;
        } else {
            System.err.println("Invalid input string : " + designator);
        }
    }

    @Override
    public String toString(){
        return this.angle + this.direction.toString();
    }
}
