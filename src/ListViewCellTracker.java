import javafx.scene.control.ListCell;

import java.util.ArrayList;

public class ListViewCellTracker<T> {
    private ArrayList<T> cellValues;
    private ArrayList<ListCell<T>> cells;

    public ListViewCellTracker() {
        this.cellValues = new ArrayList<>();
        this.cells = new ArrayList<>();
    }

    public void register(ListCell cell){
        cells.add(cell);
    }

    public void unregister(ListCell cell){
        cells.remove(cell);
    }
}
