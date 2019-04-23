import javafx.event.Event;
import javafx.event.EventType;


public class DeleteEvent extends Event {
    public static final EventType<DeleteEvent> DELETE_EVENT_TYPE = new EventType<>(EventType.ROOT, "DELETE");
    private String  obstacleName;
    private Obstacle obstacle;


    public DeleteEvent(Obstacle obstacle, EventType<? extends Event> eventType) {
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
