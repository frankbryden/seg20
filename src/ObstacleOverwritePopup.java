import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ObstacleOverwritePopup {

    private GUI gui;

    public ObstacleOverwritePopup(GUI gui) {
        this.gui = gui;
    }

    public void displayOverwritePrompt(String obstacleName, double currentHeight, double newHeight) {
        Stage overwriteWindow = new Stage();
        overwriteWindow.initModality(Modality.APPLICATION_MODAL);
        overwriteWindow.setTitle("Overwrite Obstacle");

        // Components for the overwrite obstacle details window
        Label overwriteLabel = new Label("");
        overwriteLabel.setWrapText(true);
        overwriteLabel.setTextAlignment(TextAlignment.CENTER);
        overwriteLabel.getStyleClass().add("label");
        overwriteLabel.getStylesheets().add("styles/global.css");

        if (gui.getPredefinedObstaclesSorted().containsKey(obstacleName)) {
            overwriteLabel.setText(obstacleName + " already exists in the list of predefined obstacles. Do you wish to overwrite " +
                    "the current height of " + currentHeight + "m with a new height of " + newHeight + "m?");
        } else {
            overwriteLabel.setText(obstacleName + " already exists in the list of user-defined obstacles. Do you wish to overwrite " +
                    "the current height of " + currentHeight + "m with a new height of " + newHeight + "m?");
        }

        Button cancelOverwrite = new Button("Cancel");
        cancelOverwrite.getStyleClass().add("primaryButton");
        cancelOverwrite.getStylesheets().add("styles/global.css");
        Button confirmOverwrite = new Button("Overwrite");
        confirmOverwrite.getStyleClass().add("primaryButton");
        confirmOverwrite.getStylesheets().add("styles/global.css");

        HBox buttonsBox = new HBox(20);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.getChildren().addAll(confirmOverwrite, cancelOverwrite);
        VBox windowLayout = new VBox(10);
        windowLayout.getChildren().addAll(overwriteLabel, buttonsBox);
        windowLayout.setAlignment(Pos.CENTER);

        cancelOverwrite.setOnAction(e -> overwriteWindow.close());

        confirmOverwrite.setOnAction(e -> {

            gui.getAllObstaclesSorted().remove(obstacleName);
            Obstacle modifiedObstacle = new Obstacle(obstacleName, newHeight);
            gui.getAllObstaclesSorted().put(obstacleName, modifiedObstacle);

            if (gui.getPredefinedObstaclesSorted().containsKey(obstacleName)) {
                gui.getPredefinedObstaclesSorted().remove(obstacleName);
                gui.getPredefinedObstaclesSorted().put(obstacleName, modifiedObstacle);
            } else {
                gui.getUserDefinedObstacles().remove(obstacleName);
                gui.getUserDefinedObstacles().put(obstacleName, modifiedObstacle);
            }

            gui.updateObstaclesList();
            gui.getAddObstacleNameTF().clear();
            gui.getAddObstacleHeightTF().clear();
            gui.getAddObstaclePopup().hide();
            gui.notifyUpdate("Obstacle overwritten");
            gui.addNotification("Overwrote details of " + obstacleName + ". Modified height from " + currentHeight + "m to " + newHeight + "m.");
            overwriteWindow.close();
        });

        Scene scene = new Scene(windowLayout, 420, 170);
        overwriteWindow.setScene(scene);
        overwriteWindow.showAndWait();
    }






}
