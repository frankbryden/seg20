import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.stream.Collectors;

public class ExportPopup {
    private GridPane rootPane;
    private RadioButton airportsRadio;
    private RadioButton obstaclesRadio;
    private ToggleGroup toggleGroup;
    private Stage stage;
    private enum ExportPopupStates {MENU, AIRPORTS, OBSTACLES};
    private ExportPopupStates currentExportPopupState;
    /*
    private void init(){
        rootPane = new GridPane();

        airportsRadio = new RadioButton("Airports");
        obstaclesRadio = new RadioButton("Obstacles");
        toggleGroup = new ToggleGroup();
        airportsRadio.setToggleGroup(toggleGroup);
        obstaclesRadio.setToggleGroup(toggleGroup);

        stage = new Stage();
        stage.setTitle("Export Data");

        currentExportPopupState = ExportPopupStates.MENU;

        //Components for the popup
        Button confirmButton = new Button("Next");
        Button cancelButton = new Button("Cancel");

        rootPane.add(airportsRadio, 0, 0, 2, 1);
        rootPane.add(obstaclesRadio, 0, 1, 2, 1);
        rootPane.add(confirmButton, 0, 2);
        rootPane.add(cancelButton, 1, 2);

        //Add some spacing around and in between the cells
        rootPane.setHgap(10);
        rootPane.setVgap(10);
        rootPane.setPadding(new Insets(15, 15, 15, 15));

        //Add the style sheet containing the primary button styling
        rootPane.getStylesheets().add("styles/global.css");

        //Apply the primary button class to the two buttons
        confirmButton.getStyleClass().add("primaryButton");
        cancelButton.getStyleClass().add("primaryButton");


        Scene scene = new Scene(rootPane);
        stage.setScene(scene);
        stage.setWidth(500);
        stage.setHeight(300);

        //Above is the initial menu, which then leads to one of 2 menus : Airport export, or Obstacle export

        //Airports first, they're cooler
        ComboBox airportsCombo = new ComboBox();
        airportsCombo.getItems().addAll(airportConfigs.keySet().stream().sorted().collect(Collectors.toList()));
        System.out.println(airportConfigs.keySet());

        //Obstacles second, they're just an inconvenience
        ComboBox obstaclesCombo = new ComboBox();
        obstaclesCombo.getItems().addAll(userDefinedObstacles.keySet().stream().sorted().collect(Collectors.toList()));

        confirmButton.setOnMouseClicked(event -> {
            switch (currentExportPopupState){
                case MENU:
                    rootPane.getChildren().removeAll(obstaclesRadio, airportsRadio);
                    confirmButton.setText("Export");
                    if (airportsRadio.selectedProperty().get()){
                        currentExportPopupState = GUI.ExportPopupStates.AIRPORTS;
                        rootPane.add(airportsCombo, 0, 1, 2, 1);
                    } else if (obstaclesRadio.selectedProperty().get()){
                        currentExportPopupState = GUI.ExportPopupStates.OBSTACLES;
                        rootPane.add(obstaclesCombo, 0, 1, 2, 1);
                    }
                    break;
                case AIRPORTS:
                    //TODO export airports
                    String selectedAirportName = (String) airportsCombo.getSelectionModel().getSelectedItem();
                    if (selectedAirportName != null){
                        System.out.println("Exporting airport " + selectedAirportName);
                        File outFile = fileChooser.showSaveDialog(primaryStage);
                        System.out.println("Exporting airport " + selectedAirportName + " to " + outFile.getName());
                        fileIO.write(airportConfigs.get(selectedAirportName), outFile.getPath());
                    } else {
                        //TODO show some kind of error message to user - could you do that please Jasmine? like what you did so well with the create runway and airport forms
                        System.err.println("No airport currently selected");
                    }

                    break;
                case OBSTACLES:
                    //TODO export obstacle
                    System.out.println("Exporting obstacle " + obstaclesCombo.getSelectionModel().getSelectedItem());
                    break;
            }
        });


    }

    private void setupListeners(){
        setupConfirmListener();
        setupCancelListener();
    }

    private void setupConfirmListener(){

    }

    private void setupCancelListener(){
        cancelButton.setOnMouseClicked(event -> {
            stage.hide();
        });

    }

    */
}
