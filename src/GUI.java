import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUI extends Application {
    private Button loadAirportButton, addObstacleBtn, saveObstaclesBtn, addAirportBtn, addRunwayBtn;
    private TextField obstacleNameTxt, obstacleHeightTxt;
    private ListView userDefinedObstaclesLV, predefinedObstaclesLV;
    private ChoiceBox runwaySelect, airportSelect;
    private FileChooser fileChooser;
    private FileIO fileIO;
    private Label runwayDesignatorLbl, toraLbl, todaLbl, asdaLbl, ldaLbl;
    private Map<String, AirportConfig> airportConfigs;
    private Map<String, Obstacle> obstacles;
    private Stage addAirportPopup, addRunwayPopup;



    @Override
    public void start(Stage primaryStage) throws Exception{
        // getClass().getResource("sample.fxml") gives me a null pointer exception - caused by the way the IDE loads the resource files
        // temporary fix for now
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("sample.fxml"));
        primaryStage.setTitle("Redeclaration Tool");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
        fileChooser.setInitialDirectory(new File("."));

        fileIO = new FileIO();
        obstacles = new HashMap<>();

        airportConfigs = new HashMap<>();

        addAirportPopup = createAddAirportPopup(primaryStage);
        addRunwayPopup = createAddRunwayPopup(primaryStage);

        loadAirportButton = (Button) primaryStage.getScene().lookup("#loadAirportBtn");
        loadAirportButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Click !");
                File xmlFileToLoad = fileChooser.showOpenDialog(primaryStage);
                if (xmlFileToLoad == null){
                    System.err.println("User did not select a file");
                    return;
                }
                System.out.println("Loading " + xmlFileToLoad.getName());
                AirportConfig ac = fileIO.read(xmlFileToLoad.getPath());
                airportConfigs.put(ac.getName(), ac);
                updateAirportSelects();
            }
        });

        runwaySelect = (ChoiceBox) primaryStage.getScene().lookup("#runwaySelect");
        runwaySelect.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                System.out.println("Currently selected airport : " + airportSelect.getSelectionModel().selectedItemProperty().getValue());
                AirportConfig ac = airportConfigs.get(airportSelect.getSelectionModel().selectedItemProperty().getValue());
                for (String runwayPairName : ac.getRunways().keySet()){
                    if (runwayPairName.equals(newValue)){
                        updateRunwayInfoLabels(ac.getRunways().get(runwayPairName));
                    }
                }
            }
        });

        airportSelect = (ChoiceBox) primaryStage.getScene().lookup("#airportSelect");
        airportSelect.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                System.out.println("Here");
                System.out.println((String) newValue);
                if (newValue == null){
                    System.out.println("Selection cleared");
                    return;
                }
                updateRunwaySelect((String) newValue);
            }
        });

        addObstacleBtn = (Button) primaryStage.getScene().lookup("#addObstacleBtn");
        addObstacleBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Add obstacle");
                if (validateObstaclesForm()){
                    addObstacle(obstacleNameTxt.getText(), Integer.parseInt(obstacleHeightTxt.getText()));
                    updateObstaclesList();
                }
            }
        });

        saveObstaclesBtn = (Button) primaryStage.getScene().lookup("#saveObstaclesBtn");
        saveObstaclesBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Save obstacles");
                for (String name : obstacles.keySet()){
                    fileIO.write(obstacles.get(name), "obstacles.xml");
                }
                /*userDefinedObstaclesLV.getItems().forEach((name) -> {
                    System.out.println(name);
                });*/
            }
        });

        addAirportBtn = (Button) primaryStage.getScene().lookup("#addAirportBtn");
        addAirportBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Add airport");
                addAirportPopup.show();
            }
        });

        addRunwayBtn = (Button) primaryStage.getScene().lookup("#addRunwayBtn");
        addRunwayBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Add airport");
                addRunwayPopup.show();
            }
        });

        predefinedObstaclesLV = (ListView) primaryStage.getScene().lookup("#predefinedObstaclesLV");
        userDefinedObstaclesLV = (ListView) primaryStage.getScene().lookup("#userDefinedObstaclesLV");

        obstacleNameTxt = (TextField) primaryStage.getScene().lookup("#obstacleNameTxt");
        obstacleHeightTxt = (TextField) primaryStage.getScene().lookup("#obstacleHeightTxt");

        runwayDesignatorLbl = (Label) primaryStage.getScene().lookup("#runwayDesignatorLbl");
        toraLbl = (Label) primaryStage.getScene().lookup("#toraLbl");
        todaLbl = (Label) primaryStage.getScene().lookup("#todaLbl");
        asdaLbl = (Label) primaryStage.getScene().lookup("#asdaLbl");
        ldaLbl = (Label) primaryStage.getScene().lookup("#ldaLbl");

        /*airportSelect.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                System.out.println("Heeere");
            }
        });*/

    }

    public void updateAirportSelects(){
        runwaySelect.getItems().clear();
        airportSelect.getItems().clear();

        for (String name : airportConfigs.keySet()){
            AirportConfig ac = airportConfigs.get(name);
            airportSelect.getItems().add(name);
        }
    }

    public void updateRunwaySelect(String airportName){
        runwaySelect.getItems().clear();
        AirportConfig ac = airportConfigs.get(airportName);
        for (String runwayPairName : ac.getRunways().keySet()){
            runwaySelect.getItems().add(runwayPairName);
        }
    }

    public Stage createAddAirportPopup(Stage primaryStage){
        Stage stage = new Stage();
        stage.setTitle("Add Airport");

        //Components for the popups
        Button confirmButton = new Button("Add");
        Button cancelButton = new Button("Cancel");
        TextField airportName, airportCode;
        Label airportNameLbl, airportCodeLbl;
        airportNameLbl = new Label("Airport Name");
        airportCodeLbl = new Label("Airport Code");
        airportName = new TextField();
        airportCode = new TextField();

        //VBox containing confirm and cancel button
        HBox hbox = new HBox();
        hbox.getChildren().add(confirmButton);
        hbox.getChildren().add(cancelButton);
        hbox.setSpacing(10);

        //GridPane - root of the popup
        GridPane gridPane = new GridPane();
        gridPane.add(airportNameLbl, 0, 0);
        gridPane.add(airportName, 1, 0);
        gridPane.add(airportCodeLbl, 0, 1);
        gridPane.add(airportCode, 1, 1);
        gridPane.add(hbox, 1, 2);
        Scene scene = new Scene(gridPane);

        //Add some spacing around and in between the cells
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(5, 5, 5, 5));

        //On confirm button, add the airport to the list of known airports
        confirmButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("add airport with name " + airportName.getText() + " and code " + airportCode.getText());
                addAirportPopup.hide();
            }
        });

        //Simply close the popup, discarding the data
        cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                addAirportPopup.hide();
            }
        });

        stage.setScene(scene);
        return stage;
    }

    public Stage createAddRunwayPopup(Stage primaryStage){
        Stage stage = new Stage();
        stage.setTitle("Add Runway");

        //Components for the popups
        Button confirmButton = new Button("Add");
        Button cancelButton = new Button("Cancel");
        TextField runwayDesignatorTF, toraTF, todaTF, asdaTF, ldaTF, displacementThresholdTF, runwayDesignatorTF2, toraTF2, todaTF2, asdaTF2, ldaTF2, displacementThresholdTF2;
        Label runwayDesignatorLbl, toraLbl, todaLbl, asdaLbl, ldaLbl, displacementThresholdLbl;
        runwayDesignatorLbl = new Label("Runway Designator");
        toraLbl = new Label("TORA");
        todaLbl = new Label("TODA");
        asdaLbl = new Label("ASDA");
        ldaLbl = new Label("LDA");
        displacementThresholdLbl = new Label("Displacement Threshold");

        runwayDesignatorTF = new TextField();
        toraTF = new TextField();
        todaTF = new TextField();
        asdaTF = new TextField();
        ldaTF = new TextField();
        displacementThresholdTF = new TextField();
        runwayDesignatorTF2 = new TextField();
        toraTF2 = new TextField();
        todaTF2 = new TextField();
        asdaTF2 = new TextField();
        ldaTF2 = new TextField();
        displacementThresholdTF2 = new TextField();

        //VBox containing confirm and cancel button
        HBox hbox = new HBox();
        hbox.getChildren().add(confirmButton);
        hbox.getChildren().add(cancelButton);
        hbox.setSpacing(10);

        //GridPane - root of the popup
        GridPane gridPane = new GridPane();

        //Left column
        gridPane.add(runwayDesignatorLbl, 0, 0);
        gridPane.add(runwayDesignatorTF, 1, 0);
        gridPane.add(toraLbl, 0, 1);
        gridPane.add(toraTF, 1, 1);
        gridPane.add(todaLbl, 0, 2);
        gridPane.add(todaTF, 1, 2);
        gridPane.add(asdaLbl, 0, 3);
        gridPane.add(asdaTF, 1, 3);
        gridPane.add(ldaLbl, 0, 4);
        gridPane.add(ldaTF, 1, 4);
        gridPane.add(displacementThresholdLbl, 0, 5);
        gridPane.add(displacementThresholdTF, 1, 5);

        //Right Column
        gridPane.add(runwayDesignatorTF2, 2, 0);
        gridPane.add(toraTF2, 2, 1);
        gridPane.add(todaTF2, 2, 2);
        gridPane.add(asdaTF2, 2, 3);
        gridPane.add(ldaTF2, 2, 4);
        gridPane.add(displacementThresholdTF2, 2, 5);

        gridPane.add(hbox, 3, 6);
        Scene scene = new Scene(gridPane);

        //Add some spacing around and in between the cells
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(15, 15, 15, 15));

        //On confirm button, add the airport to the list of known airports
        confirmButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("add runway with name " + runwayDesignatorTF.getText() + " and TORA " + toraTF.getText());
                addRunwayPopup.hide();
            }
        });

        //Simply close the popup, discarding the data
        cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                addRunwayPopup.hide();
            }
        });

        stage.setScene(scene);
        return stage;
    }

    public void updateRunwayInfoLabels(RunwayPair runwayPair){
        runwayDesignatorLbl.setText(runwayPair.getName());
        toraLbl.setText("TORA : " + runwayPair.getR1().getTORA() + " / " + runwayPair.getR2().getTORA());
        todaLbl.setText("TODA : " + runwayPair.getR1().getTODA() + " / " + runwayPair.getR2().getTODA());
        asdaLbl.setText("ASDA : " + runwayPair.getR1().getASDA() + " / " + runwayPair.getR2().getASDA());
        ldaLbl.setText("LDA : " + runwayPair.getR1().getLDA() + " / " + runwayPair.getR2().getLDA());
    }

    public void updateObstaclesList(){
        userDefinedObstaclesLV.getItems().clear();
        userDefinedObstaclesLV.getItems().addAll(obstacles.keySet());
    }

    public Boolean validateObstaclesForm(){
        if (obstacleNameTxt.getText().length() < 1){
            return false;
        }

        try {
            Integer.parseInt(obstacleHeightTxt.getText());
        } catch (NumberFormatException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void addObstacle(String name, int height){
        Obstacle obstacle = new Obstacle(name, height);
        this.obstacles.put(name, obstacle);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
