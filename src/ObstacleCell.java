import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class ObstacleCell extends ListCell<Obstacle> {

    @FXML
    private HBox rootBox;

    @FXML
    private Label contentLbl;

    @FXML
    private Button editBtn;
    @FXML
    private Button deleteBtn;

    private Node view;
    private Obstacle obstacle;

    public ObstacleCell(ListView parent){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("obstacleCell.fxml"));
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        rootBox.prefWidthProperty().bind(parent.widthProperty().subtract(40));

        setupListeners();

    }

    private void setupListeners(){
        deleteBtn.setOnMouseClicked(event -> {
            System.out.println("Delete obstacle " + obstacle.getName());
            fireEvent(new DeleteEvent(obstacle, DeleteEvent.DELETE_EVENT_TYPE));
        });

        rootBox.setOnMouseEntered(event -> {
            //System.out.println("Hovering over " + obstacle.getName());
            fireEvent(new CellHoverEvent(obstacle, CellHoverEvent.CELL_HOVER_EVENT_TYPE));
        });
    }

    public Obstacle getObstacle(){
        return obstacle;
    }


    public void showButtons(){

    }

    public void hideButtons(){

    }


    public Node getView(){
        return view;
    }

    @Override
    protected void updateItem(Obstacle item, boolean empty) {
        super.updateItem(item, empty);

        if (empty){
            contentLbl.setText("EMPTY");
            setText(null);
            setGraphic(null);
        } else {
            this.obstacle = item;
            setGraphic(rootBox);
            contentLbl.setText(item.getName());
        }
    }

}
