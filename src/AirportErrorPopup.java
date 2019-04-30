import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

class AirportErrorPopup {

    public static void displayAirportErrorPopup() {
        Stage errorWindow = new Stage();
        errorWindow.getIcons().add(new Image("/rec/plane.png"));
        errorWindow.initModality(Modality.APPLICATION_MODAL);
        errorWindow.setTitle("Airport selection");

        Label errorLabel = new Label("Please enter a valid 3-digit IATA airport code and then select an airport in order to continue.");
        errorLabel.setWrapText(true);
        errorLabel.setTextAlignment(TextAlignment.CENTER);
        Button returnButton = new Button("Return");
        returnButton.getStyleClass().add("primaryButton");

        VBox windowLayout = new VBox(10);
        windowLayout.getChildren().addAll(errorLabel, returnButton);
        windowLayout.setAlignment(Pos.CENTER);
        windowLayout.getStylesheets().add("styles/global.css");

        returnButton.setOnAction(e -> errorWindow.close());

        Scene scene = new Scene(windowLayout, 400, 150);
        errorWindow.setScene(scene);
        errorWindow.showAndWait();
    }
}
