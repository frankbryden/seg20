import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.table.TableColumn;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GUI extends Application {
    private Button loadAirportButton, addObstacleBtn, saveObstaclesBtn, addAirportBtn, addRunwayBtn, calculateBtn, calculationsBackBtn, printerBtn, outArrowBtn, popObstacleBtn, editObstacleBtn, deleteObstacleBtn;
    private Pane calculationsPane;
    private TextField obstacleNameTxt, obstacleHeightTxt, centrelineTF, distanceFromThresholdTF;
    private ListView userDefinedObstaclesLV, predefinedObstaclesLV;
    private ChoiceBox runwaySelect, airportSelect, obstacleSelect, thresholdSelect, addRunwayAirportSelect;
    private FileChooser fileChooser;
    private FileIO fileIO;
    private Label runwayDesignatorLbl, toraLbl, todaLbl, asdaLbl, ldaLbl, centrelineDistanceLbl, runwayThresholdLbl, originalValuesLbl, obstacleSelectLbl, thresholdSelectLbl, originalToda, originalTora, originalAsda, originalLda, recalculatedToda, recalculatedTora, recalculatedAsda, recalculatedLda;
    private GridPane calculationResultsGrid;
    private TextArea originalValuesTA;
    private VBox calculationsRootBox, viewCalculationResultsVBox;
    private HBox centerlineHBox, thresholdHBox, obstacleSelectHBox, thresholdSelectHBox;
    private Map<String, AirportConfig> airportConfigs;
    private Map<String, Obstacle> obstacles;
    private Stage addAirportPopup, addRunwayPopup;
    private RunwayPair currentlySelectedRunway = null;



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

        printerBtn = (Button) primaryStage.getScene().lookup("#printerBtn");
        ImageView printerImgView = new ImageView(new Image(getClass().getResourceAsStream("/rec/printer.png")));
        printerImgView.setFitHeight(15); printerImgView.setFitWidth(20);
        printerBtn.setGraphic(printerImgView);

        printerBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });

        outArrowBtn = (Button) primaryStage.getScene().lookup("#outArrowBtn");
        ImageView outArrowImgView = new ImageView(new Image(getClass().getResourceAsStream("/rec/outArrow.png")));
        outArrowImgView.setFitHeight(15); outArrowImgView.setFitWidth(20);
        outArrowBtn.setGraphic(outArrowImgView);

        outArrowBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });

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
                        RunwayPair selectedRunwayPair = ac.getRunways().get(runwayPairName);
                        updateRunwayInfoLabels(selectedRunwayPair);
                        updateThresholdList(selectedRunwayPair);
                        currentlySelectedRunway = selectedRunwayPair;
                        break;
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
                    clearObstaclesForm();
                    updateObstaclesList();
                }
            }
        });

        popObstacleBtn = (Button) primaryStage.getScene().lookup("#popAddObstacleBtn");
        ImageView popAddObstacle = new ImageView(new Image(getClass().getResourceAsStream("/rec/popAddObstacle.png")));
        popAddObstacle.setFitHeight(15); popAddObstacle.setFitWidth(20);
        popObstacleBtn.setGraphic(popAddObstacle);

        popAddObstacle.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

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
        //Calculations Pane - selection view
        final int HBOX_SPACING = 5;
        calculationsPane = (Pane) primaryStage.getScene().lookup("#calculationsPane");
        obstacleSelect = new ChoiceBox();
        thresholdSelect = new ChoiceBox();
        centrelineDistanceLbl = new Label("Distance from runway centreline");
        runwayThresholdLbl = new Label("Distance from runway threshold");
        obstacleSelectLbl = new Label("Select obstacle");
        thresholdSelectLbl = new Label("Select threshold");
        centrelineTF = new TextField();
        distanceFromThresholdTF = new TextField();
        calculateBtn = new Button("Calculate");
        calculationsRootBox = new VBox(20);
        calculationsRootBox.setPadding(new Insets(10, 10, 10, 10));
        obstacleSelectHBox = new HBox(HBOX_SPACING);
        obstacleSelectHBox.getChildren().add(obstacleSelectLbl);
        obstacleSelectHBox.getChildren().add(obstacleSelect);
        thresholdSelectHBox = new HBox(HBOX_SPACING);
        thresholdSelectHBox.getChildren().add(thresholdSelectLbl);
        thresholdSelectHBox.getChildren().add(thresholdSelect);
        centerlineHBox = new HBox(HBOX_SPACING);
        centerlineHBox.getChildren().add(centrelineDistanceLbl);
        centerlineHBox.getChildren().add(centrelineTF);
        thresholdHBox = new HBox(HBOX_SPACING);
        thresholdHBox.getChildren().add(runwayThresholdLbl);
        thresholdHBox.getChildren().add(distanceFromThresholdTF);
        VBox calculateBtnVBox = new VBox();
        calculateBtnVBox.getChildren().add(calculateBtn);
        calculateBtnVBox.setAlignment(Pos.BASELINE_RIGHT);
        calculateBtnVBox.setPadding(new Insets(0, 20, 0, 0));
        calculationsRootBox.getChildren().add(obstacleSelectHBox);
        calculationsRootBox.getChildren().add(centerlineHBox);
        calculationsRootBox.getChildren().add(thresholdSelectHBox);
        calculationsRootBox.getChildren().add(thresholdHBox);
        calculationsRootBox.getChildren().add(calculateBtnVBox);

        calculateBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //Get currently selected obstacle
                String obstacleName = obstacleSelect.getSelectionModel().getSelectedItem().toString();
                Obstacle currentlySelectedObstacle = obstacles.get(obstacleName);
                String thresholdName = thresholdSelect.getSelectionModel().getSelectedItem().toString();
                RunwayConfig runwayConfig;
                if (currentlySelectedRunway.getR1().getRunwayDesignator().toString().equals(thresholdName)){
                    runwayConfig = currentlySelectedRunway.getR1();
                } else {
                    runwayConfig = currentlySelectedRunway.getR2();
                }
                Calculations calculations = new Calculations(runwayConfig);
                int obstacleDistance = Integer.valueOf(distanceFromThresholdTF.getText());
                RunwayConfig recalculatedParams = calculations.recalculateParams(currentlySelectedObstacle, obstacleDistance, Calculations.Direction.AWAY);
                System.out.println(recalculatedParams.toString());
                updateCalculationResultsView(runwayConfig, recalculatedParams);
                switchCalculationsTabToView();
            }
        });

        //Calculations Pane - calculation results view
        originalValuesLbl = new Label("Original Values");
        originalValuesTA = new TextArea();
        calculationResultsGrid = new GridPane();
        Label originalValuesGridLbl, recalculatedlValuesGridLbl, todaRowLbl, toraRowLbl, asdaRowLbl, ldaRowLbl;
        originalValuesGridLbl = new Label("Original Values");
        recalculatedlValuesGridLbl = new Label("Recalculated Values");
        todaRowLbl = new Label("TODA");
        toraRowLbl = new Label("TORA");
        asdaRowLbl = new Label("ASDA");
        ldaRowLbl = new Label("LDA");
        originalToda = new Label();
        originalTora = new Label();
        originalAsda = new Label();
        originalLda = new Label();
        recalculatedToda = new Label();
        recalculatedTora = new Label();
        recalculatedAsda = new Label();
        recalculatedLda = new Label();
        calculationsBackBtn = new Button("Back");
        VBox calculateBackBtnVBox = new VBox();
        calculateBackBtnVBox.getChildren().add(calculationsBackBtn);
        calculateBackBtnVBox.setAlignment(Pos.BASELINE_RIGHT);
        calculateBackBtnVBox.setPadding(new Insets(0, 20, 0, 0));

        //Add all the labels, col by col,  to create a table

        //Col 0 : the value names (TODA, TORA, ASDA, LDA)
        calculationResultsGrid.add(todaRowLbl, 0, 1);
        calculationResultsGrid.add(toraRowLbl, 0, 2);
        calculationResultsGrid.add(asdaRowLbl, 0, 3);
        calculationResultsGrid.add(ldaRowLbl, 0, 4);

        //Col 1 : the original values
        calculationResultsGrid.add(originalValuesGridLbl, 1, 0);
        calculationResultsGrid.add(originalToda, 1, 1);
        calculationResultsGrid.add(originalTora, 1, 2);
        calculationResultsGrid.add(originalAsda, 1, 3);
        calculationResultsGrid.add(originalLda, 1, 4);

        //Col 2 : the recalculated values
        calculationResultsGrid.add(recalculatedlValuesGridLbl, 2, 0);
        calculationResultsGrid.add(recalculatedToda, 2, 1);
        calculationResultsGrid.add(recalculatedTora, 2, 2);
        calculationResultsGrid.add(recalculatedAsda, 2, 3);
        calculationResultsGrid.add(recalculatedLda, 2, 4);

        viewCalculationResultsVBox = new VBox();
        viewCalculationResultsVBox.getChildren().add(originalValuesLbl);
        viewCalculationResultsVBox.getChildren().add(originalValuesTA);
        viewCalculationResultsVBox.getChildren().add(calculationResultsGrid);
        viewCalculationResultsVBox.getChildren().add(calculateBackBtnVBox);

        calculationsBackBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                resetCalculationsTab();
            }
        });


        resetCalculationsTab();

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
        addRunwayAirportSelect.getItems().clear();

        for (String name : airportConfigs.keySet()){
            AirportConfig ac = airportConfigs.get(name);
            airportSelect.getItems().add(name);
            addRunwayAirportSelect.getItems().add(name);
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
                AirportConfig airportConfig = new AirportConfig(airportName.getText());
                airportConfigs.put(airportConfig.getName(), airportConfig);
                addAirportPopup.hide();
                addRunwayPopup.show();
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
        addRunwayAirportSelect = new ChoiceBox();

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

        VBox addRunwayRoot = new VBox(20);
        addRunwayRoot.getChildren().add(addRunwayAirportSelect);
        addRunwayRoot.getChildren().add(gridPane);
        Scene scene = new Scene(addRunwayRoot);

        //Add some spacing around and in between the cells
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(15, 15, 15, 15));

        //On confirm button, add the airport to the list of known airports
        confirmButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if (validateForm(new ArrayList<String>(Arrays.asList(toraTF.getText(), todaTF.getText(), asdaTF.getText(), ldaTF.getText(), displacementThresholdTF.getText(), toraTF2.getText(), todaTF2.getText(), asdaTF2.getText(), ldaTF2.getText(), displacementThresholdTF2.getText())))){
                    System.out.println("valid form");
                } else {
                    System.err.println("Invalid form");
                }
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

    private void resetCalculationsTab(){
        calculationsPane.getChildren().remove(viewCalculationResultsVBox);
        calculationsPane.getChildren().add(calculationsRootBox);
    }

    private void switchCalculationsTabToView(){
        calculationsPane.getChildren().remove(calculationsRootBox);
        calculationsPane.getChildren().add(viewCalculationResultsVBox);
    }

    private void updateRunwayInfoLabels(RunwayPair runwayPair){
        runwayDesignatorLbl.setText(runwayPair.getName());
        System.out.println("R1 and R2");
        System.out.println(runwayPair.getR1());
        System.out.println(runwayPair.getR2());
        toraLbl.setText("TORA : " + runwayPair.getR1().getTORA() + " / " + runwayPair.getR2().getTORA());
        todaLbl.setText("TODA : " + runwayPair.getR1().getTODA() + " / " + runwayPair.getR2().getTODA());
        asdaLbl.setText("ASDA : " + runwayPair.getR1().getASDA() + " / " + runwayPair.getR2().getASDA());
        ldaLbl.setText("LDA : " + runwayPair.getR1().getLDA() + " / " + runwayPair.getR2().getLDA());
    }

    public void updateCalculationResultsView(RunwayConfig original, RunwayConfig recalculated){
        originalToda.setText(Integer.toString(original.getTODA()));
        originalTora.setText(Integer.toString(original.getTORA()));
        originalAsda.setText(Integer.toString(original.getASDA()));
        originalLda.setText(Integer.toString(original.getLDA()));

        recalculatedToda.setText(Integer.toString(recalculated.getTODA()));
        recalculatedTora.setText(Integer.toString(recalculated.getTORA()));
        recalculatedAsda.setText(Integer.toString(recalculated.getASDA()));
        recalculatedLda.setText(Integer.toString(recalculated.getLDA()));
    }

    private void updateThresholdList(RunwayPair runwayPair){
        thresholdSelect.getItems().clear();
        thresholdSelect.getItems().add(runwayPair.getR1().getRunwayDesignator().toString());
        thresholdSelect.getItems().add(runwayPair.getR2().getRunwayDesignator().toString());
    }

    private void updateObstaclesList(){
        userDefinedObstaclesLV.getItems().clear();
        userDefinedObstaclesLV.getItems().addAll(obstacles.keySet());
        obstacleSelect.getItems().clear();
        obstacleSelect.getItems().addAll(obstacles.keySet());
    }

    private Boolean validateObstaclesForm(){
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

    private Boolean validateForm(ArrayList<String> intVals){
        for (String s : intVals){
            try {
                Integer.parseInt(s);
            } catch (NumberFormatException e){
                return false;
            }
        }
        return true;
    }

    private void clearObstaclesForm(){
        obstacleNameTxt.clear();
        obstacleHeightTxt.clear();
    }

    public void addObstacle(String name, int height){
        Obstacle obstacle = new Obstacle(name, height);
        this.obstacles.put(name, obstacle);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
