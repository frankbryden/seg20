import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DeleteObstaclePopup {

    private GUI gui;

    public DeleteObstaclePopup(GUI gui) {
        this.gui = gui;
    }


    public void displayDeletePrompt(Obstacle obstacle, GUI.ObstacleList source) {
        Stage deleteWindow = new Stage();
        deleteWindow.initModality(Modality.APPLICATION_MODAL);
        deleteWindow.setTitle("Delete Obstacle");

        // Components for the delete obstacle window

        Label confirmationLabel = new Label("Are you sure you want to delete " + obstacle.getName() + "?");
        confirmationLabel.setWrapText(true);
        confirmationLabel.setTextAlignment(TextAlignment.CENTER);
        Button cancelDeletion = new Button("Cancel");
        cancelDeletion.getStyleClass().add("primaryButton");
        Button confirmDeletion = new Button("Delete");
        confirmDeletion.getStyleClass().add("primaryButton");

        HBox buttonsBox = new HBox(20);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.getChildren().addAll(confirmDeletion, cancelDeletion);
        VBox windowLayout = new VBox(10);
        windowLayout.getChildren().addAll(confirmationLabel, buttonsBox);
        windowLayout.setAlignment(Pos.CENTER);
        windowLayout.getStylesheets().add("styles/global.css");

        cancelDeletion.setOnAction(e -> deleteWindow.close());
        confirmDeletion.setOnAction(e -> {
            gui.removeObstacle(obstacle, source);
            gui.updateObstaclesList();
            deleteWindow.close();
        });

        Scene scene = new Scene(windowLayout, 400, 150);
        deleteWindow.setScene(scene);
        deleteWindow.showAndWait();
    }



}
