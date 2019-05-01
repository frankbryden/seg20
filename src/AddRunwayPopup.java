import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;

class AddRunwayPopup {

    private final GUI gui;
    //TODO fix to make independent self-sufficient class
    private Stage addRunwayStage;
    private AirportConfig currentlySelectedAirportConfig;
    private ComboBox<String> addRunwayAirportSelect;


    public AddRunwayPopup(GUI gui) {
        this.gui = gui;
        this.addRunwayStage = createAddRunwayPopup();
    }

    public Stage createAddRunwayPopup() {
        Stage stage = new Stage();
        stage.getIcons().add(new Image("/rec/plane.png"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add Runway");

        //Components for the popups
        Label selectAirportLbl = new Label("Select airport");
        Button confirmButton = new Button("Continue");
        confirmButton.getStyleClass().add("primaryButton");
        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("primaryButton");
        TextField runwayDesignatorTF, toraTF, clearwayTF, stopwayTF, displacementThresholdTF, runwayDesignatorTF2, toraTF2, clearwayTF2, stopwayTF2, displacementThresholdTF2;
        Label runwayDesignatorLbl, toraLbl, clearwayLbl, stopwayLbl, displacementThresholdLbl, currentlySelectedAirportLbl;

        runwayDesignatorLbl = new Label("Runway Designator");
        toraLbl = new Label("TORA");
        clearwayLbl = new Label("Clearway");
        stopwayLbl = new Label("Stopway");
        displacementThresholdLbl = new Label("Displaced Threshold");
        currentlySelectedAirportLbl = new Label("");
        addRunwayAirportSelect = new ComboBox<>();

        addRunwayAirportSelect.setId("airportComboBox");
        addRunwayAirportSelect.setVisibleRowCount(10);

        runwayDesignatorTF = new TextField();
        toraTF = new TextField();
        clearwayTF = new TextField();
        stopwayTF = new TextField();
        displacementThresholdTF = new TextField();
        runwayDesignatorTF2 = new TextField();
        toraTF2 = new TextField();
        clearwayTF2 = new TextField();
        stopwayTF2 = new TextField();
        displacementThresholdTF2 = new TextField();

        //VBox containing confirm and cancel button
        HBox hbox = new HBox(10);
        hbox.getChildren().add(confirmButton);
        hbox.getChildren().add(cancelButton);

        //GridPane - root of the popup
        GridPane gridPane = new GridPane();

        //Left column
        gridPane.add(runwayDesignatorLbl, 0, 0);
        gridPane.add(runwayDesignatorTF, 1, 0);
        gridPane.add(toraLbl, 0, 1);
        gridPane.add(toraTF, 1, 1);
        gridPane.add(clearwayLbl, 0, 2);
        gridPane.add(clearwayTF, 1, 2);
        gridPane.add(stopwayLbl, 0, 3);
        gridPane.add(stopwayTF, 1, 3);
        gridPane.add(displacementThresholdLbl, 0, 4);
        gridPane.add(displacementThresholdTF, 1, 4);

        //Right Column
        gridPane.add(runwayDesignatorTF2, 2, 0);
        gridPane.add(toraTF2, 2, 1);
        gridPane.add(clearwayTF2, 2, 2);
        gridPane.add(stopwayTF2, 2, 3);
        gridPane.add(displacementThresholdTF2, 2, 4);

        gridPane.add(hbox, 3, 6);


        HBox airportSelection = new HBox(30);
        airportSelection.setPadding(new Insets(10, 12, 15, 15));
        airportSelection.getChildren().add(selectAirportLbl);
        airportSelection.getChildren().add(addRunwayAirportSelect);


        VBox addRunwayRoot = new VBox(20);


        addRunwayRoot.getStylesheets().add("styles/global.css");

        addRunwayRoot.getChildren().add(airportSelection);
        addRunwayRoot.getChildren().add(gridPane);
        Scene scene = new Scene(addRunwayRoot);

        //Add some spacing around and in between the cells
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(15, 15, 15, 15));

        // Auto-complete for runway designator
        runwayDesignatorTF.setOnKeyReleased(event -> {
            String contents = runwayDesignatorTF.getText();
            //If the user has typed 2 chars, we have a valid designator - fill the other side
            if (contents.length() == 2) {
                runwayDesignatorTF2.setText(gui.getReverseRunwayName(contents));
            } else if (contents.length() == 3) {
                String designatorSide = contents.substring(2, 3).toUpperCase();
                String otherSide = "C";
                System.out.println("this is on side " + designatorSide);
                if (designatorSide.equals("L")) {
                    otherSide = "R";
                } else if (designatorSide.equals("R")) {
                    otherSide = "L";
                }
                runwayDesignatorTF2.setText(gui.getReverseRunwayName(contents.substring(0, 2)) + otherSide);
            }
        });

        //On confirm button, add the airport to the list of known airports
        confirmButton.setOnMouseClicked(event -> {
            if (gui.validateIntForm(new ArrayList<>(Arrays.asList(toraTF.getText(), clearwayTF.getText(), stopwayTF.getText(), displacementThresholdTF.getText(), toraTF2.getText(), clearwayTF2.getText(), stopwayTF2.getText(), displacementThresholdTF2.getText())), "")) {
                System.out.println("valid form");

                // Working out runway values for one side of the runway
                String runwayDesignator = runwayDesignatorTF.getText();
                int TORA = Integer.parseInt(toraTF.getText());
                int TODA = Integer.parseInt(toraTF.getText()) + Integer.parseInt(clearwayTF.getText());
                int ASDA = Integer.parseInt(toraTF.getText()) + Integer.parseInt(stopwayTF.getText());
                int LDA = Integer.parseInt(toraTF.getText()) - Integer.parseInt(displacementThresholdTF.getText());
                int displacedThreshold = Integer.parseInt(displacementThresholdTF.getText());
                int clearway = Integer.parseInt(clearwayTF.getText());
                int stopway = Integer.parseInt(stopwayTF.getText());

                // Working out runway values but for the other side of the runway
                String runwayDesignator2 = runwayDesignatorTF2.getText();
                int TORA2 = Integer.parseInt(toraTF2.getText());
                int TODA2 = Integer.parseInt(toraTF2.getText()) + Integer.parseInt(clearwayTF2.getText());
                int ASDA2 = Integer.parseInt(toraTF2.getText()) + Integer.parseInt(stopwayTF2.getText());
                int LDA2 = Integer.parseInt(toraTF2.getText()) - Integer.parseInt(displacementThresholdTF2.getText());
                int displacedThreshold2 = Integer.parseInt(displacementThresholdTF2.getText());
                int clearway2 = Integer.parseInt(clearwayTF2.getText());
                int stopway2 = Integer.parseInt(stopwayTF2.getText());

                if(LDA >= 0 && LDA2 >= 0) {


                    RunwayConfig r1 = new RunwayConfig(new RunwayDesignator(runwayDesignator), TORA, TODA, ASDA, LDA, displacedThreshold);
                    r1.setClearway(clearway);
                    r1.setStopway(stopway);
                    RunwayConfig r2 = new RunwayConfig(new RunwayDesignator(runwayDesignator2), TORA2, TODA2, ASDA2, LDA2, displacedThreshold2);
                    r1.setClearway(clearway2);
                    r1.setStopway(stopway2);
                    RunwayPair runwayPair = new RunwayPair(r1, r2);

                    // Show the prompt that shows the summary of the runway values
                    NewRunwayParamPopup runwayParamPopup = new NewRunwayParamPopup(gui);
                    runwayParamPopup.displayRunwayParametersPrompt(runwayPair);

                    AirportConfig selectedAirport = gui.getAirportConfigs().get(addRunwayAirportSelect.getSelectionModel().getSelectedItem().toString());
                    System.out.println(selectedAirport.toString());

                    selectedAirport.addRunwayPair(runwayPair);
                    gui.getAirportConfigs().put(selectedAirport.getName(), selectedAirport);
                    System.out.println("add runway with name " + runwayDesignatorTF.getText() + " and TORA " + toraTF.getText());
                    gui.getAddRunwayPopup().hide();
                    gui.updateAirportSelects();
                }
            } else {
                System.err.println("Invalid form");
            }

        });

        //Simply close the popup, discarding the data
        cancelButton.setOnMouseClicked(event -> gui.getAddRunwayPopup().hide());

        stage.setScene(scene);
        stage.setWidth(700);
        stage.setHeight(390);
        return stage;
    }

    //Selects currently selected Airport if non-null
    void selectAirportConfig(){
        if (currentlySelectedAirportConfig != null){

        }
    }

    ComboBox<String> getAddRunwayAirportSelect(){
        return addRunwayAirportSelect;
    }

    void clearAirportList(){
        addRunwayAirportSelect.getItems().clear();
    }

    void addAirportToList(String name){
        addRunwayAirportSelect.getItems().add(name);
    }

    void show(){
        addRunwayStage.show();
    }

    void hide (){
        addRunwayStage.hide();
    }
}
