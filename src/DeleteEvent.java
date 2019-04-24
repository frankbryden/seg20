import javafx.event.Event;
import javafx.event.EventType;


public class DeleteEvent extends ObstacleCellEvent {
    public static final EventType<DeleteEvent> DELETE_EVENT_TYPE = new EventType<>(EventType.ROOT, "DELETE");


    public DeleteEvent(Obstacle obstacle, EventType<? extends Event> eventType) {
        super(obstacle, eventType);
    }
}
