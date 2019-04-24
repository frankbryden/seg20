import javafx.event.Event;
import javafx.event.EventType;

public abstract class ObstacleCellEvent extends Event {
    private String  obstacleName;
    private Obstacle obstacle;


    public ObstacleCellEvent(Obstacle obstacle, EventType<? extends javafx.event.Event> eventType) {
        super(eventType);

        this.obstacle = obstacle;
        this.obstacleName = obstacle.getName();
    }

    public String getObstacleName() {
        return obstacleName;
    }

    public Obstacle getObstacle(){
        return obstacle;
    }

}
