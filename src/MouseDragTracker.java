import java.awt.*;

public class MouseDragTracker {
    private Point lastMousePos;
    private Point delta;
    private static MouseDragTracker ourInstance = new MouseDragTracker();

    public static MouseDragTracker getInstance() {
        return ourInstance;
    }

    private MouseDragTracker() {
        this.lastMousePos = new Point(0, 0);
        this.delta = new Point(0, 0);
    }

    public void startDrag(int x, int y){
        lastMousePos.x = x;
        lastMousePos.y = y;
    }

    public void dragging(int x, int y){
        delta.x = lastMousePos.x - x;
        delta.y = lastMousePos.y - y;

        lastMousePos.x = x;
        lastMousePos.y = y;
    }

    public Point getDelta(){
        return delta;
    }
}
