import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.Map;
import java.util.stream.Collectors;

public class ExportPopup {
    private GridPane rootPane;
    private RadioButton airportsRadio;
    private RadioButton obstaclesRadio;
    private ToggleGroup toggleGroup;
    private Stage stage;

    //Airports
    private Map<String, AirportConfig> airportConfigs;
    private ComboBox airportsCombo;

    //Obstacles
    private Map<String, Obstacle> obstacles;
    private ComboBox obstaclesCombo;

    //Buttons
    private Button confirmButton;
    private Button cancelButton;

    //Datatype and instance to keep track of state
    private enum ExportPopupStates {MENU, AIRPORTS, OBSTACLES};
    private ExportPopupStates currentExportPopupState;

    //Label for error messages
    private Label errorMessageLbl;

    //We need a handle to the primaryStage so as to block all input while the filechooser dialog is open
    private Stage primaryStage;

    //We need a fileIO instance for exporting. We'll get this from GUI during creation.
    private FileIO fileIO;

    public ExportPopup(Stage primaryStage, Map<String, AirportConfig> airportConfigs, Map<String, Obstacle> obstacles, FileIO fileIO) {
        this.primaryStage = primaryStage;
        this.airportConfigs = airportConfigs;
        this.obstacles = obstacles;
        this.fileIO = fileIO;

        this.init();
    }

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
        confirmButton = new Button("Next");
        cancelButton = new Button("Cancel");

        rootPane.add(confirmButton, 0, 2);
        rootPane.add(cancelButton, 1, 2);
        showMenuView();

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

        errorMessageLbl = new Label();
        errorMessageLbl.setStyle("-fx-text-fill: red");

        //Above is the initial menu, which then leads to one of 2 menus : Airport export, or Obstacle export

        //Airports first, they're cooler
        airportsCombo = new ComboBox();

        //Obstacles second, they're just an inconvenience
        obstaclesCombo = new ComboBox();

        setupListeners();
    }

    private void setupListeners(){
        setupConfirmListener();
        setupCancelListener();
    }

    private void setupConfirmListener(){
        confirmButton.setOnMouseClicked(event -> {
            switch (currentExportPopupState){
                case MENU:
                    clearView();
                    nextButtonToExport();
                    if (airportsRadio.selectedProperty().get()){
                        showAirportView();
                    } else if (obstaclesRadio.selectedProperty().get()){
                        showObstacleView();
                    }
                    break;
                case AIRPORTS:
                    //TODO export airports
                    String selectedAirportName = (String) airportsCombo.getSelectionModel().getSelectedItem();
                    if (selectedAirportName != null){
                        System.out.println("Exporting airport " + selectedAirportName);
                        File outFile = fileIO.fileChooser.showSaveDialog(primaryStage);
                        System.out.println("Exporting airport " + selectedAirportName + " to " + outFile.getName());
                        fileIO.write(airportConfigs.get(selectedAirportName), outFile.getPath());
                    } else {
                        //TODO show some kind of error message to user - could you do that please Jasmine? like what you did so well with the create runway and airport forms
                        //TODO acutally, could that do the job? what do you think?
                        System.err.println("No airport currently selected");
                        showErrorMessage("No airport selected.");
                    }

                    break;
                case OBSTACLES:
                    //TODO export obstacle
                    String selectedObstacleName = (String) obstaclesCombo.getSelectionModel().getSelectedItem();
                    if (selectedObstacleName != null){
                        System.out.println("Exporting obstacle " + selectedObstacleName);
                        File outFile = fileIO.fileChooser.showSaveDialog(primaryStage);
                        System.out.println("Exporting airport " + selectedObstacleName + " to " + outFile.getName());
                        fileIO.write(obstacles.get(selectedObstacleName), outFile.getPath());
                    } else {
                        System.err.println("No obstacle selected");
                        showErrorMessage("No obstacle selected.");
                    }
                    break;
            }
        });
    }

    private void setupCancelListener(){
        cancelButton.setOnMouseClicked(event -> {
            switch (currentExportPopupState){
                case MENU:
                    stage.hide();
                    break;
                case AIRPORTS:
                case OBSTACLES:
                    showMenuView();
                    break;
            }
        });

    }

    private void clearView(){
        rootPane.getChildren().removeAll(obstaclesRadio, airportsRadio, obstaclesCombo, airportsCombo, errorMessageLbl);
    }

    private void showMenuView(){
        clearView();
        currentExportPopupState = ExportPopupStates.MENU;
        rootPane.add(airportsRadio, 0, 0, 2, 1);
        rootPane.add(obstaclesRadio, 0, 1, 2, 1);
        exportButtonToNext();
        backButtonToCancel();
    }

    private void showAirportView(){
        clearView();
        currentExportPopupState = ExportPopupStates.AIRPORTS;
        rootPane.add(airportsCombo, 0, 1, 2, 1);
        cancelButtonToBack();
    }

    private void showObstacleView(){
        clearView();
        currentExportPopupState = ExportPopupStates.OBSTACLES;
        rootPane.add(obstaclesCombo, 0, 1, 2, 1);
        if (obstaclesCombo.getItems().size() == 0){
            System.err.println("User has not defined any obstacles");
            showErrorMessage("No obstacles have been defined.");
        }
        cancelButtonToBack();
    }

    private void cancelButtonToBack(){
        cancelButton.setText("Back");
    }

    private void backButtonToCancel(){
        cancelButton.setText("Cancel");
    }

    private void nextButtonToExport(){
        confirmButton.setText("Export");
    }

    private void exportButtonToNext(){
        confirmButton.setText("Next");
    }

    public void show(){
        updateAirportCombo();
        updateObstacleCombo();
        this.stage.show();
    }

    private void showErrorMessage(String message){
        errorMessageLbl.setText(message);
        rootPane.add(errorMessageLbl, 0, 3, 2, 1);
    }


    //TODO I think this method won't be needed
    private void hideErrorMessage(){
        errorMessageLbl.setText("");
        rootPane.getChildren().remove(errorMessageLbl);
    }

    public void updateAirportCombo(){
        this.airportsCombo.getItems().clear();
        this.airportsCombo.getItems().addAll(airportConfigs.keySet().stream().sorted().collect(Collectors.toList()));
    }

    public void updateObstacleCombo(){
        System.out.println("Updating the obstacles combo");
        System.out.println(obstacles.keySet());
        this.obstaclesCombo.getItems().clear();
        this.obstaclesCombo.getItems().addAll(obstacles.keySet().stream().sorted().collect(Collectors.toList()));
    }


}
