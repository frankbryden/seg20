import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GUI extends Application {
    private Button loadAirportButton, addObstacleBtn;
    private TextField obstacleNameTxt, obstacleHeightTxt;
    private ChoiceBox runwaySelect, airportSelect;
    private FileChooser fileChooser;
    private FileIO fileIO;
    private Label runwayDesignatorLbl, toraLbl, todaLbl, asdaLbl, ldaLbl;
    private Map<String, AirportConfig> airportConfigs;
    private ArrayList<Obstacle> obstacles;


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
        obstacles = new ArrayList<>();

        airportConfigs = new HashMap<>();

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
                for (RunwayDesignator runwayDesignator : ac.getRunwayConfigs().keySet()){
                    if (runwayDesignator.toString().equals(newValue)){
                        updateRunwayInfoLabels(ac.getRunwayConfigs().get(runwayDesignator));
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

        //                                                                  <TextField fx:id="obstacleNameTxt" layoutX="85.0" layoutY="10.0" />
        //                                                                  <TextField fx:id="obstacleHeightTxt" layoutX="85.0" layoutY="94.0" />

        addObstacleBtn = (Button) primaryStage.getScene().lookup("#addObstacleBtn");
        addObstacleBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Add obstacle");
            }
        });

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
        for (RunwayDesignator runwayDesignator : ac.getRunwayConfigs().keySet()){
            runwaySelect.getItems().add(runwayDesignator.toString());
        }
    }

    public void updateRunwayInfoLabels(RunwayConfig runwayConfig){
        runwayDesignatorLbl.setText(runwayConfig.getRunwayDesignator().toString());
        toraLbl.setText("TORA : " + runwayConfig.getTORA());
        todaLbl.setText("TODA : " + runwayConfig.getTODA());
        asdaLbl.setText("ASDA : " + runwayConfig.getASDA());
        ldaLbl.setText("LDA : " + runwayConfig.getLDA());
    }

    public Boolean validateObstaclesForm(){
        
    }

    public void addObstacle(String name, int height){
        Obstacle obstacle = new Obstacle(name, height);
        this.obstacles.add(obstacle);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
