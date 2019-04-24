import javafx.event.Event;
import javafx.event.EventType;


public class CellHoverEvent extends ObstacleCellEvent {
    public static final EventType<CellHoverEvent> CELL_HOVER_EVENT_TYPE = new EventType<>(EventType.ROOT, "CELL_HOVER");


    public CellHoverEvent(Obstacle obstacle, EventType<? extends javafx.event.Event> eventType) {
        super(obstacle, eventType);
    }
}
