import javafx.event.Event;
import javafx.event.EventType;

abstract class ObstacleCellEvent extends Event {
    private final String  obstacleName;
    private final Obstacle obstacle;


    ObstacleCellEvent(Obstacle obstacle, EventType<? extends javafx.event.Event> eventType) {
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
