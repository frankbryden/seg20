import javafx.event.Event;
import javafx.event.EventType;

class ObstacleInfoEvent extends ObstacleCellEvent {
    public static final EventType<ObstacleInfoEvent> OBSTACLE_INFO_EVENT_TYPE = new EventType<>(EventType.ROOT, "OBSTACLE_INFO");

    public ObstacleInfoEvent(Obstacle obstacle, EventType<? extends Event> eventType) {
        super(obstacle, eventType);
    }
}
