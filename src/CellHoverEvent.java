import javafx.event.Event;
import javafx.event.EventType;


public class CellHoverEvent extends Event {
    public static final EventType<CellHoverEvent> CELL_HOVER_EVENT_TYPE = new EventType<>(EventType.ROOT, "CELL_HOVER");
    private String  obstacleName;
    private Obstacle obstacle;


    public CellHoverEvent(Obstacle obstacle, EventType<? extends javafx.event.Event> eventType) {
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
