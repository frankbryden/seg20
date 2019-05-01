import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SaveObstacleErrorPopup {

    private final GUI gui;

    public SaveObstacleErrorPopup (GUI gui) {
        this.gui = gui;
    }

    public static void displaySavePrompt() {
        Stage saveWindow = new Stage();
        saveWindow.getIcons().add(new Image("/rec/plane.png"));
        saveWindow.initModality(Modality.APPLICATION_MODAL);
        saveWindow.setTitle("Saving obstacles error");

        Label errorLabel = new Label("There are no obstacles to save.");
        errorLabel.setWrapText(true);
        errorLabel.setTextAlignment(TextAlignment.CENTER);
        Button returnBtn = new Button("Return");
        returnBtn.getStyleClass().add("primaryButton");

        VBox windowLayout = new VBox(10);
        windowLayout.getChildren().addAll(errorLabel,returnBtn);
        windowLayout.setAlignment(Pos.CENTER);
        windowLayout.getStylesheets().add("styles/global.css");

        returnBtn.setOnAction(e ->  saveWindow.close());

        Scene scene = new Scene(windowLayout, 400, 150);
        saveWindow.setScene(scene);
        saveWindow.showAndWait();
    }
}
