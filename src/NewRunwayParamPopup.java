import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

class NewRunwayParamPopup {

    private final GUI gui;

    public NewRunwayParamPopup(GUI gui) {
        this.gui = gui;
    }

    //TODO Make the Confirm/Cancel buttons work
    public void displayRunwayParametersPrompt(RunwayPair runwayPair) {
        Stage runwayWindow = new Stage();
        runwayWindow.getIcons().add(new Image("/rec/plane.png"));
        runwayWindow.initModality(Modality.APPLICATION_MODAL);
        runwayWindow.setTitle("Add Runway");

        Label summaryLbl = new Label("Summary of parameters for the new runway at " + gui.getAddRunwayPopup().getAddRunwayAirportSelect().getSelectionModel().getSelectedItem().toString() + " airport:");
        summaryLbl.getStyleClass().add("label");
        summaryLbl.getStylesheets().add("styles/global.css");

        Label confirmDetailsLbl = new Label("Do you wish to confirm these details and add the runway?");
        confirmDetailsLbl.getStyleClass().add("label");
        confirmDetailsLbl.getStylesheets().add("styles/global.css");

        // Headers of the table (column 1)
        Label runwayDesignatorHeader = new Label("Runway Designator");
        Label toraHeader = new Label("TORA (m)");
        Label todaHeader = new Label("TODA (m)");
        Label asdaHeader = new Label("ASDA (m)");
        Label ldaHeader = new Label("LDA (m)");
        Label displacedThresholdHeader = new Label("Displaced Threshold (m)");
        Label clearwayHeader = new Label("Clearway (m)");
        Label stopwayHeader = new Label("Stopway (m)");
        VBox headers = new VBox(10);
        headers.getChildren().addAll(runwayDesignatorHeader, toraHeader, todaHeader, asdaHeader, ldaHeader, displacedThresholdHeader, clearwayHeader, stopwayHeader);
        headers.getStyleClass().add("sideLabel");
        headers.getStylesheets().add("styles/global.css");

        // Values for one side of the runway (column 2)
        Label runwayDesignatorValue = new Label(runwayPair.getR1().getRunwayDesignator().toString());
        Label toraValue = new Label(Integer.toString(runwayPair.getR1().getTORA()));
        Label todaValue = new Label(Integer.toString(runwayPair.getR1().getTODA()));
        Label asdaValue = new Label(Integer.toString(runwayPair.getR1().getASDA()));
        Label ldaValue = new Label(Integer.toString(runwayPair.getR1().getLDA()));
        Label displacedThresholdValue = new Label(Integer.toString(runwayPair.getR1().getDisplacementThreshold()));
        Label clearwayValue = new Label(Integer.toString(runwayPair.getR1().getClearway()));
        Label stopwayValue = new Label(Integer.toString(runwayPair.getR1().getStopway()));
        VBox values = new VBox(10);
        values.getChildren().addAll(runwayDesignatorValue, toraValue, todaValue, asdaValue, ldaValue, displacedThresholdValue, clearwayValue, stopwayValue);
        values.getStyleClass().add("label");
        values.getStylesheets().add("styles/global.css");

        // Values for other side of the runway (column 3)
        Label runwayDesignatorValue2 = new Label(runwayPair.getR2().getRunwayDesignator().toString());
        Label toraValue2 = new Label(Integer.toString(runwayPair.getR2().getTORA()));
        Label todaValue2 = new Label(Integer.toString(runwayPair.getR2().getTODA()));
        Label asdaValue2 = new Label(Integer.toString(runwayPair.getR2().getASDA()));
        Label ldaValue2 = new Label(Integer.toString(runwayPair.getR2().getLDA()));
        Label displacedThresholdValue2 = new Label(Integer.toString(runwayPair.getR2().getDisplacementThreshold()));
        Label clearwayValue2 = new Label(Integer.toString(runwayPair.getR2().getClearway()));
        Label stopwayValue2 = new Label(Integer.toString(runwayPair.getR2().getStopway()));
        VBox values2 = new VBox(10);
        values2.getChildren().addAll(runwayDesignatorValue2, toraValue2, todaValue2, asdaValue2, ldaValue2, displacedThresholdValue2, clearwayValue2, stopwayValue2);
        values2.getStyleClass().add("label");
        values2.getStylesheets().add("styles/global.css");

        // Entire table of values
        HBox summaryTable = new HBox(20);
        summaryTable.getChildren().addAll(headers, values, values2);
        summaryTable.setAlignment(Pos.CENTER);

        // Confirm and Back buttons
        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("primaryButton");
        backBtn.getStylesheets().add("styles/global.css");
        Button confirmBtn = new Button("Confirm");
        confirmBtn.getStyleClass().add("primaryButton");
        confirmBtn.getStylesheets().add("styles/global.css");
        HBox buttonsBox = new HBox(10);
        buttonsBox.getChildren().addAll(confirmBtn, backBtn);
        buttonsBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonsBox.setPadding(new Insets(0, 15, 10, 0));

        VBox windowLayout = new VBox(20);
        windowLayout.setPadding(new Insets(10, 0, 0, 15));
        windowLayout.getChildren().addAll(summaryLbl, summaryTable, confirmDetailsLbl, buttonsBox);

        Scene scene = new Scene(windowLayout);
        runwayWindow.setScene(scene);
        runwayWindow.setWidth(700);
        runwayWindow.setHeight(430);
        runwayWindow.showAndWait();
    }
}
