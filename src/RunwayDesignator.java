import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RunwayDesignator {
    enum Direction {L, R, C, NONE};
    public int angle;
    public Direction direction;

    public RunwayDesignator(int angle, Direction direction){
        this.angle = angle;
        this.direction = direction;
    }

    public RunwayDesignator(String designator){
        Pattern pattern = Pattern.compile("([0-9]+)([LR]?)");
        Matcher matcher = pattern.matcher(designator);
        if (matcher.find()){
            Direction direction = null;
            switch (matcher.group(2)){
                case "R":
                    direction = Direction.R;
                    break;
                case "L":
                    direction = Direction.L;
                    break;
                case "C":
                    direction = Direction.C;
                    break;
                case "":
                    direction = Direction.NONE;
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
        String s = "0" + this.angle;
        if (s.length() > 2){
            s = s.substring(1);
        }
        if (direction == Direction.NONE){
            return s;
        }
        return s + this.direction.toString();
    }
}
