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
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.stream.Collectors;

class ObstacleCell extends ListCell<Obstacle> {

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
        styleComponents();

    }

    private void styleComponents(){
        contentLbl.setFont(new Font(9));
        System.out.println(rootBox.getParent());
    }

    private void setupListeners(){
        deleteBtn.setOnMouseClicked(event -> {
            System.out.println("Delete obstacle " + obstacle.getName());
            fireEvent(new DeleteEvent(obstacle, DeleteEvent.DELETE_EVENT_TYPE));
        });

        editBtn.setOnMouseClicked(event -> {
            System.out.println("Info requested for obstacle with name " + obstacle.getName());
            fireEvent(new ObstacleInfoEvent(obstacle, ObstacleInfoEvent.OBSTACLE_INFO_EVENT_TYPE));
        });

        rootBox.setOnMouseEntered(event -> {
            //System.out.println("Hovering over " + obstacle.getName());
            rootBox.getStyleClass().add(":selected");
            fireEvent(new CellHoverEvent(obstacle, CellHoverEvent.CELL_HOVER_EVENT_TYPE));
        });
        rootBox.setOnMouseExited(event -> {
            rootBox.getStyleClass().remove(":selected");
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
            contentLbl.setFont(new Font(9));
            contentLbl.setText(item.getName());
        }
    }

}
