import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.image.Image;
import javafx.util.Duration;

public class GUI extends Application {
    //TODO add airport database
    private Button loadAirportButton, addObstacleBtn, addAirportBtn, addRunwayBtn, calculateBtn, calculationsBackBtn, printerBtn, outArrowBtn, popAddObstacleBtn,
            editObstacleBtn, deleteObstacleBtn, saveObstacleBtn, saveObstaclesBtn, highlightAsdaBtn, highlightToraBtn, highlightTodaBtn, highlightLdaBtn, saveSettingsBtn;
    private Pane calculationsPane;
    private TextField obstacleNameTxt, obstacleHeightTxt, centrelineTF, distanceFromThresholdTF;
    private ListView userDefinedObstaclesLV, predefinedObstaclesLV;
    private ComboBox thresholdSelect, addRunwayAirportSelect, airportSelect, runwaySelect, directionSelect;
    private FileChooser fileChooser;
    private FileIO fileIO;
    private Label runwayDesignatorLbl, toraLbl, todaLbl, asdaLbl, ldaLbl, centrelineDistanceLbl, runwayDesignatorCntLbl, runwayDesignatorLbl2, toraCntLbl, toraCntLbl2, todaCntLbl, todaCntLbl2, asdaCntLbl, asdaCntLbl2, ldaCntLbl, ldaCntLbl2,
            runwayThresholdLbl, breakdownCalcLbl, obstacleSelectLbl, thresholdSelectLbl, originalToda,
            originalTora, originalAsda, originalLda, recalculatedToda, recalculatedTora, recalculatedAsda, recalculatedLda, windlLbl, directionSelectLbl;
    private Label centreLineRequiredLabel, thresholdDistanceRequiredLabel, thresholdRequiredLabel, obstacleRequiredLabel;
    private GridPane calculationResultsGrid, runwayGrid;
    private TextArea calculationDetails;
    private VBox calculationsRootBox, viewCalculationResultsVBox;
    private HBox centerlineHBox, thresholdHBox, obstacleSelectHBox, thresholdSelectHBox, directionSelectHBox;
    private Map<String, AirportConfig> airportConfigs;
    private Popup addObstaclePopup;
    private Map<String, Obstacle> userDefinedObstacles, predefinedObstaclesSorted, allObstaclesSorted;
    private Stage addAirportPopup, addRunwayPopup;
    private RunwayPair currentlySelectedRunway = null;
    private Canvas canvas, sideviewCanvas;
    private TabPane tabPane;
    private Pane planePane;
    private ImageView planeImg;
    private RunwayRenderer runwayRenderer;
    private RunwayRenderer runwayRendererSideView;
    private BorderPane canvasBorderPane;
    private ComboBox obstacleSelect;
    private Boolean editingObstacle;


    @Override
    public void start(Stage primaryStage) throws Exception{
        // getClass().getResource("sample.fxml") gives me a null pointer exception - caused by the way the IDE loads the resource files
        // temporary fix for now
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("sample.fxml"));
        primaryStage.setTitle("Runway Redeclaration Tool");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
        fileChooser.setInitialDirectory(new File("."));

        fileIO = new FileIO();
        userDefinedObstacles = new TreeMap<>();
        predefinedObstaclesSorted = new TreeMap<>();
        allObstaclesSorted = new TreeMap<>();

        airportConfigs = new HashMap<>();

        addAirportPopup = createAddAirportPopup(primaryStage);
        addRunwayPopup = createAddRunwayPopup(primaryStage);
        addObstaclePopup = createAddObstaclePopup();

        //TODO add event listeners for the two new images (print and share)

        runwaySelect = (ComboBox) primaryStage.getScene().lookup("#runwaySelect");
        runwaySelect.setId("runwayComboBox");
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
                        runwayRenderer = new RunwayRenderer(currentlySelectedRunway, canvas.getGraphicsContext2D());
                        runwayRenderer.render();
                        //selectedRunwayPair.getR1().render(canvas.getGraphicsContext2D());

                        runwayRendererSideView = new RunwayRenderer(currentlySelectedRunway, sideviewCanvas.getGraphicsContext2D(), true);
                        runwayRendererSideView.renderSideview();

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("we're just gonna get some data here");
                                String apiKey = "473ade203bfbbf2d4346749e61a37a95";
                                String urlString = "https://api.openweathermap.org/data/2.5/weather?lat=" + ac.getLatitude() + "&lon=" + ac.getLongitude() + "&appid=" + apiKey;
                                try {
                                    URL url = new URL(urlString);
                                    URLConnection urlConnection = url.openConnection();
                                    InputStreamReader is = new InputStreamReader(urlConnection.getInputStream());
                                    StringBuilder data = new StringBuilder();
                                    while (is.ready()){
                                        data.append((char) is.read());
                                    }
                                    System.out.println(data.toString());
                                    Pattern p = Pattern.compile("\"wind\":\\{\"speed\":([0-9]+\\.[0-9]*),\"deg\":([0-9]+).*?\\}");
                                    System.out.println(p.toString());
                                    Matcher m = p.matcher(data.toString());
                                    m.find();
                                    System.out.println("Speed extracted from response : " + m.group(1));
                                    System.out.println("Angle extracted from response : " + m.group(2));

                                    double speed = Double.valueOf(m.group(1));
                                    int angleDeg = Integer.valueOf(m.group(2));
                                    //Convert angle to radians
                                    double angleRad = angleDeg * Math.PI/180;
                                    //Add PI/2 as the 0 in the meteorological is north, whereas it is east in the trigonometry world
                                    angleRad += Math.PI/2;
                                    windlLbl.setText(String.valueOf(speed) + "km/h, ang : " + angleDeg + "/" + angleRad);
                                    runwayRenderer.setWindAngle(angleRad);


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        Platform.runLater(runnable);
                        break;
                    }
                }
            }
        });


        airportSelect = (ComboBox) primaryStage.getScene().lookup("#airportSelect");
        airportSelect.setId("airportComboBox");
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

        addObstacleBtn = new Button("Add Button");
        addObstacleBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Add obstacle");
                if (validateObstaclesForm()){
                    addObstacle(obstacleNameTxt.getText(), Double.parseDouble(obstacleHeightTxt.getText()));
                    updateObstaclesList();
                }
            }
        });

        //Icons in the obstacles tab
        int iconSize = 18;

        popAddObstacleBtn = (Button) primaryStage.getScene().lookup("#popAddObstacleBtn");
        ImageView popAddObstacle = new ImageView(new Image(getClass().getResourceAsStream("/rec/popAddObstacle.png")));
        popAddObstacle.setFitHeight(iconSize);
        popAddObstacle.setFitWidth(iconSize);
        popAddObstacleBtn.setGraphic(popAddObstacle);

        popAddObstacleBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //TODO - position according to window size
                Bounds bounds = popAddObstacleBtn.localToScreen(popAddObstacleBtn.getBoundsInLocal());
                addObstaclePopup.show(primaryStage);
                addObstaclePopup.setAnchorX(bounds.getMaxX() - addObstaclePopup.getWidth()/2);
                addObstaclePopup.setAnchorY(bounds.getMinY() - addObstaclePopup.getHeight());
            }
        });

        editObstacleBtn = (Button) primaryStage.getScene().lookup("#editObstacleBtn");
        ImageView editObstacleImgView = new ImageView(new Image(getClass().getResourceAsStream("/rec/edit.png")));
        editObstacleImgView.setFitWidth(iconSize);
        editObstacleImgView.setFitHeight(iconSize);
        editObstacleBtn.setGraphic(editObstacleImgView);

        editObstacleBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file == null){
                    return;
                }
                Collection<Obstacle> importedObstacles = fileIO.readObstacles(file.getPath());
                importedObstacles.forEach(obstacle -> {
                    addObstacle(obstacle);
                });
                updateObstaclesList();
            }
        });

        deleteObstacleBtn = (Button) primaryStage.getScene().lookup("#deleteObstacleBtn");

        ImageView deleteObstacleImgView = new ImageView(new Image(getClass().getResourceAsStream("/rec/delete.png")));
        deleteObstacleImgView.setFitWidth(iconSize);
        deleteObstacleImgView.setFitHeight(iconSize);
        deleteObstacleBtn.setGraphic(deleteObstacleImgView);

        deleteObstacleBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                removeUserObstacle();
                updateObstaclesList();
            }
        });

        saveObstacleBtn = (Button) primaryStage.getScene().lookup("#saveObstaclesBtn");

        ImageView saveObstacleImgView = new ImageView(new Image(getClass().getResourceAsStream("/rec/save.png")));
        saveObstacleImgView.setFitHeight(iconSize);
        saveObstacleImgView.setFitWidth(iconSize);
        saveObstacleBtn.setGraphic(saveObstacleImgView);

        saveObstacleBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });

        saveObstaclesBtn = (Button) primaryStage.getScene().lookup("#saveObstaclesBtn");
        saveObstaclesBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Save obstacles");
                File file = fileChooser.showSaveDialog(primaryStage);
                if (file != null){
                    fileIO.write(userDefinedObstacles.values(), file.getPath());
                }

                /*userDefinedObstaclesLV.getItems().forEach((name) -> {
                    System.out.println(name);
                });*/

            }
        });
        highlightTodaBtn = (Button) primaryStage.getScene().lookup("#highlightTodaBtn");
        highlightTodaBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                runwayRenderer.setCurrentlyHighlightedParam(RunwayRenderer.RunwayParams.TODA);
            }
        });

        highlightToraBtn = (Button) primaryStage.getScene().lookup("#highlightToraBtn");
        highlightToraBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                runwayRenderer.setCurrentlyHighlightedParam(RunwayRenderer.RunwayParams.TORA);
            }
        });

        highlightAsdaBtn = (Button) primaryStage.getScene().lookup("#highlightAsdaBtn");
        highlightAsdaBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                runwayRenderer.setCurrentlyHighlightedParam(RunwayRenderer.RunwayParams.ASDA);
            }
        });

        highlightLdaBtn = (Button) primaryStage.getScene().lookup("#highlightLdaBtn");
        highlightLdaBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                runwayRenderer.setCurrentlyHighlightedParam(RunwayRenderer.RunwayParams.LDA);
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

        //Settings tab
        saveSettingsBtn = (Button) primaryStage.getScene().lookup("#saveSettingsBtn");
        saveSettingsBtn.setOnMouseClicked(value -> {
            System.out.println("Clicked on save settings");
            new Notification("hey").show(primaryStage, 10, 10);
        });


        //Calculations Pane - selection view
        final int HBOX_SPACING = 5;
        Insets calculationsInsets = new Insets(5, 20, 0, 0);
        calculationsPane = (Pane) primaryStage.getScene().lookup("#calculationsPane");
        obstacleSelect = new ComboBox();


        obstacleSelect.setVisibleRowCount(10);
        obstacleSelect.setId("obstacleComboBox");
        thresholdSelect = new ComboBox();


        thresholdSelect.setVisibleRowCount(5);
        thresholdSelect.setId("thresholdComboBox");
        centrelineDistanceLbl = new Label("Distance from runway centreline (m)");
        runwayThresholdLbl = new Label("Distance from runway threshold (m)");
        obstacleSelectLbl = new Label("Select obstacle");
        thresholdSelectLbl = new Label("Select threshold");
        directionSelectLbl  = new Label ("Select runway direction");
        centrelineTF = new TextField();
        distanceFromThresholdTF = new TextField();
        calculateBtn = new Button("Calculate");
        calculateBtn.setId("calcButton");
        calculateBtn.getStyleClass().add("primaryButton");

        directionSelect = new ComboBox();
        directionSelect.setVisibleRowCount(2);
        directionSelect.setId("directionComboBox");
        for(Calculations.Direction direction : Calculations.Direction.values())
        {
            directionSelect.getItems().add(Calculations.directionSpecifier.get(direction));
        }

        calculationsRootBox = new VBox(20);
        calculationsRootBox.setPadding(new Insets(40, 10, 10, 10));
        obstacleSelectHBox = new HBox(HBOX_SPACING);

        VBox.setMargin(obstacleSelectHBox, calculationsInsets);
        Region obstacleSelectRegion = getHGrowingRegion();
        obstacleSelectHBox.getChildren().add(obstacleSelectLbl);
        obstacleSelectHBox.getChildren().add(obstacleSelectRegion);
        obstacleSelectHBox.getChildren().add(obstacleSelect);
        thresholdSelectHBox = new HBox(HBOX_SPACING);

        VBox.setMargin(thresholdSelectHBox, calculationsInsets);
        Region thresholdSelectRegion = getHGrowingRegion();
        thresholdSelectHBox.getChildren().add(thresholdSelectLbl);
        thresholdSelectHBox.getChildren().add(thresholdSelectRegion);
        thresholdSelectHBox.getChildren().add(thresholdSelect);

        VBox.setMargin(obstacleSelectHBox, new Insets(5, 20, 0, 0));
        centerlineHBox = new HBox(HBOX_SPACING);

        VBox.setMargin(centerlineHBox, calculationsInsets);
        Region centerlineHBoxRegion = getHGrowingRegion();
        centerlineHBox.getChildren().add(centrelineDistanceLbl);
        centerlineHBox.getChildren().add(centerlineHBoxRegion);
        centerlineHBox.getChildren().add(centrelineTF);
        thresholdHBox = new HBox(HBOX_SPACING);

        VBox.setMargin(thresholdHBox, calculationsInsets);
        Region thresholdHBoxRegion = getHGrowingRegion();
        thresholdHBox.getChildren().add(runwayThresholdLbl);
        thresholdHBox.getChildren().add(thresholdHBoxRegion);
        thresholdHBox.getChildren().add(distanceFromThresholdTF);

        directionSelectHBox = new HBox(HBOX_SPACING);
        VBox.setMargin(directionSelectHBox, calculationsInsets);
        Region directionSelectHBoxRegion = getHGrowingRegion();
        directionSelectHBox.getChildren().add(directionSelectLbl);
        directionSelectHBox.getChildren().add(directionSelectHBoxRegion);
        directionSelectHBox.getChildren().add(directionSelect);


        HBox calculateBtnVBox = new HBox();
        VBox.setMargin(calculateBtnVBox, calculationsInsets);
        Region calculateBtnRegion = getHGrowingRegion();
        calculateBtnVBox.getChildren().add(calculateBtnRegion);
        calculateBtnVBox.getChildren().add(calculateBtn);
        //calculateBtnVBox.setPadding(new Insets(0, 20, 0, 0));
        calculationsRootBox.setMinWidth(calculationsPane.getWidth());
        calculationsRootBox.getChildren().add(obstacleSelectHBox);
        calculationsRootBox.getChildren().add(centerlineHBox);
        calculationsRootBox.getChildren().add(thresholdSelectHBox);
        calculationsRootBox.getChildren().add(thresholdHBox);
        calculationsRootBox.getChildren().add(directionSelectHBox);
        calculationsRootBox.getChildren().add(calculateBtnVBox);
        calculationsRootBox.getStyleClass().add("customCol");


        centreLineRequiredLabel = (Label) primaryStage.getScene().lookup("#centreLineRequiredLabel");
        thresholdDistanceRequiredLabel = (Label) primaryStage.getScene().lookup("#thresholdDistanceRequiredLabel");
        thresholdRequiredLabel = (Label) primaryStage.getScene().lookup("#thresholdRequiredLabel");
        obstacleRequiredLabel = (Label) primaryStage.getScene().lookup("#obstacleRequiredLabel");

        calculateBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if (centrelineTF.getText().isEmpty() || distanceFromThresholdTF.getText().isEmpty() ||
                        thresholdSelect.getSelectionModel().isEmpty() || obstacleSelect.getSelectionModel().isEmpty()
                        ) {

                    if (centrelineTF.getText().isEmpty()) {
                        centreLineRequiredLabel.setText("            This field is required");
                    } else {
                        centreLineRequiredLabel.setText("");
                    }
                    if (distanceFromThresholdTF.getText().isEmpty()) {
                        thresholdDistanceRequiredLabel.setText("            This field is required");
                    } else {
                        thresholdDistanceRequiredLabel.setText("");
                    }
                    if (thresholdSelect.getSelectionModel().isEmpty()) {
                        thresholdRequiredLabel.setText("      Please select a threshold");
                    } else {
                        thresholdRequiredLabel.setText("");
                    }
                    if (obstacleSelect.getSelectionModel().isEmpty()) {
                        obstacleRequiredLabel.setText("     Please select an obstacle");
                    } else {
                        obstacleRequiredLabel.setText("");
                    }

                } else {

                    String obstacleName = obstacleSelect.getSelectionModel().getSelectedItem().toString();
                    Obstacle currentlySelectedObstacle = allObstaclesSorted.get(obstacleName);

                    String thresholdName = thresholdSelect.getSelectionModel().getSelectedItem().toString();

                    RunwayConfig runwayConfig, otherConfig;
                    RunwayPair.Side selectedSide;

                    if (currentlySelectedRunway.getR1().getRunwayDesignator().toString().equals(thresholdName)) {
                        runwayConfig = currentlySelectedRunway.getR1();
                        otherConfig = currentlySelectedRunway.getR2();
                        selectedSide = RunwayPair.Side.R1;
                    } else {
                        runwayConfig = currentlySelectedRunway.getR2();
                        otherConfig = currentlySelectedRunway.getR1();
                        selectedSide = RunwayPair.Side.R2;
                    }


                    //Perform recalculations
                    Calculations calculations = new Calculations(runwayConfig);
                    int distanceFromThreshold = Integer.valueOf(distanceFromThresholdTF.getText());
                    int distanceFromCenterline = Integer.valueOf(centrelineTF.getText());
                    Calculations.Direction runwayDirection = Calculations.getKey(directionSelect.getSelectionModel().getSelectedItem().toString());

                    Calculations.Direction otherDirection;
                    if (runwayDirection == Calculations.Direction.TOWARDS) {
                        otherDirection = Calculations.Direction.AWAY;
                    } else
                        otherDirection = Calculations.Direction.TOWARDS;

                    CalculationResults results = calculations.recalculateParams(currentlySelectedObstacle, distanceFromThreshold, distanceFromCenterline, runwayDirection);
                    RunwayConfig recalculatedParams = results.getRecalculatedParams();


                    //Fix ? perform recalculation on the other runway config
                    Calculations calculations2 = new Calculations(otherConfig);
                    CalculationResults results2 = calculations2.recalculateParams(currentlySelectedObstacle, otherConfig.getLDA() - distanceFromThreshold, distanceFromCenterline, otherDirection);
                    RunwayConfig recalculatedParams2 = results2.getRecalculatedParams();

                    System.out.println("calculation details");
                    System.out.println(results.getCalculationDetails());
                    System.out.println(results2.getCalculationDetails());

                    // Printing results into the breakdown of calculations text box
                    calculationDetails.setText(results.getCalculationDetails() + "\n" + results2.getCalculationDetails());

                    System.out.println(recalculatedParams.toString());
                    updateCalculationResultsView(runwayConfig, recalculatedParams);
                    //updateCalculationResultsView(otherConfig, recalculatedParams2);
                    switchCalculationsTabToView();

                    if (selectedSide == RunwayPair.Side.R1) {
                        runwayRenderer = new RunwayRenderer(new RunwayPair(recalculatedParams, recalculatedParams2), canvas.getGraphicsContext2D());
                        runwayRenderer.getRunwayRenderParams().setRealLifeMaxLenR1(runwayConfig.getTORA());
                        runwayRenderer.getRunwayRenderParams().setRealLifeMaxLenR2(otherConfig.getTORA());
                    } else {
                        runwayRenderer = new RunwayRenderer(new RunwayPair(recalculatedParams2, recalculatedParams), canvas.getGraphicsContext2D());
                        runwayRenderer.getRunwayRenderParams().setRealLifeMaxLenR2(runwayConfig.getTORA());
                        runwayRenderer.getRunwayRenderParams().setRealLifeMaxLenR1(otherConfig.getTORA());
                    }

                    runwayRenderer.refreshLines();
                    runwayRenderer.render();

                    String unselectedThreshold;
                    if (currentlySelectedRunway.getR1().getRunwayDesignator().toString().equals(thresholdName)) {
                        unselectedThreshold = currentlySelectedRunway.getR2().toString();
                    } else {
                        unselectedThreshold = currentlySelectedRunway.getR1().toString();
                    }

                    runwayRendererSideView.renderSideview();
                    runwayRendererSideView.drawObstacle(currentlySelectedObstacle, distanceFromThreshold, distanceFromCenterline, thresholdName, unselectedThreshold);

                }


            }
        });

        //Calculations Pane - calculation results view
        breakdownCalcLbl = new Label("Breakdown of the calculations");
        calculationDetails = new TextArea();
        calculationDetails.setEditable(false);
        calculationDetails.setId("calcBreakdown");
        calculationResultsGrid = new GridPane();
        Label originalValuesGridLbl, recalculatedlValuesGridLbl, todaRowLbl, toraRowLbl, asdaRowLbl, ldaRowLbl;
        originalValuesGridLbl = new Label("Original\nValues");
        recalculatedlValuesGridLbl = new Label("Recalculated\nValues");
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
        calculationsBackBtn.getStyleClass().add("primaryButton");
        calculationsBackBtn.getStylesheets().add("styles/global.css");

        VBox calculateBackBtnVBox = new VBox();
        calculateBackBtnVBox.getChildren().add(calculationsBackBtn);
        calculateBackBtnVBox.setAlignment(Pos.BASELINE_RIGHT);
        calculateBackBtnVBox.setPadding(new Insets(0, 20, 0, 0));

        tabPane = (TabPane) primaryStage.getScene().lookup("#tabPane");
        canvas = (Canvas) primaryStage.getScene().lookup("#canvas");
        canvas.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                runwayRenderer.setMouseLocation((int) event.getX(), (int) event.getY());
                runwayRenderer.updateZoom((int) (event.getDeltaY()/2));
            }
        });

        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MouseDragTracker.getInstance().startDrag((int) event.getX(), (int) event.getY());
                double currentAngle = runwayRenderer.getWindAngle();
                System.out.println("Current angle = " + currentAngle);
                currentAngle += Math.PI/4;
                System.out.println("After addition = " + currentAngle);
                runwayRenderer.setWindAngle(currentAngle);
            }
        });


        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MouseDragTracker.getInstance().dragging((int) event.getX(), (int) event.getY());
                Point delta = MouseDragTracker.getInstance().getDelta();
                runwayRenderer.translate(delta.x, delta.y);
            }
        });

        sideviewCanvas = (Canvas) primaryStage.getScene().lookup("#canvasSideView");

        canvasBorderPane = (BorderPane) primaryStage.getScene().lookup("#canvasBorderPane");
        /*canvasBorderPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double newVal = (double) newValue;
                canvas.setWidth(newVal/2);
            }
        });
        canvas.heightProperty().bind(canvasBorderPane.heightProperty());*/

        calculationResultsGrid.setHgap(20);
        //Add all the labels, col by col,  to create a table

        //Col 0 : the value names (TODA, TORA, ASDA, LDA)
        calculationResultsGrid.add(toraRowLbl, 0, 1);
        calculationResultsGrid.add(todaRowLbl, 0, 2);
        calculationResultsGrid.add(asdaRowLbl, 0, 3);
        calculationResultsGrid.add(ldaRowLbl, 0, 4);

        //Col 1 : the original values
        calculationResultsGrid.add(originalValuesGridLbl, 1, 0);
        calculationResultsGrid.add(originalTora, 1, 1);
        calculationResultsGrid.add(originalToda, 1, 2);
        calculationResultsGrid.add(originalAsda, 1, 3);
        calculationResultsGrid.add(originalLda, 1, 4);

        //Col 2 : the recalculated values
        calculationResultsGrid.add(recalculatedlValuesGridLbl, 2, 0);
        calculationResultsGrid.add(recalculatedTora, 2, 1);
        calculationResultsGrid.add(recalculatedToda, 2, 2);
        calculationResultsGrid.add(recalculatedAsda, 2, 3);
        calculationResultsGrid.add(recalculatedLda, 2, 4);

        viewCalculationResultsVBox = new VBox();
        viewCalculationResultsVBox.setPadding(new Insets(8, 0, 0, 0));
        breakdownCalcLbl.setPadding(new Insets(0, 0, 0, -5));
        viewCalculationResultsVBox.getChildren().add(breakdownCalcLbl);
        viewCalculationResultsVBox.getChildren().add(calculationDetails);
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
        predefinedObstaclesLV.setId("predefinedList");
        predefinedObstaclesLV.setStyle("-fx-font-size: 1.2em ;");
        userDefinedObstaclesLV = (ListView) primaryStage.getScene().lookup("#userDefinedObstaclesLV");
        userDefinedObstaclesLV.setId("userList");
        userDefinedObstaclesLV.setStyle("-fx-font-size: 1.2em ;");

        predefinedObstaclesLV.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {

                if (!userDefinedObstaclesLV.getSelectionModel().isEmpty()) {
                    int selectedUserItem = userDefinedObstaclesLV.getSelectionModel().getSelectedIndex();
                    userDefinedObstaclesLV.getSelectionModel().clearSelection(selectedUserItem);
                    System.out.println("Obstacle in user-defined list was selected, has now been deselected");
                }

                obstacleSelect.setValue(predefinedObstaclesLV.getSelectionModel().getSelectedItem());

                if (click.getClickCount() == 2) {
                    showObstacleDetails(predefinedObstaclesLV, click, primaryStage, "predefined");
                }
           }
        });

        userDefinedObstaclesLV.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent click) {

                if (!predefinedObstaclesLV.getSelectionModel().isEmpty()) {
                    int selectedUserItem = predefinedObstaclesLV.getSelectionModel().getSelectedIndex();
                    predefinedObstaclesLV.getSelectionModel().clearSelection(selectedUserItem);
                    System.out.println("Obstacle in predefined list was selected, has now been deselected");
                }

                obstacleSelect.setValue(userDefinedObstaclesLV.getSelectionModel().getSelectedItem());

                if (click.getClickCount() == 2 && !userDefinedObstaclesLV.getItems().isEmpty()) {
                    showObstacleDetails(userDefinedObstaclesLV, click, primaryStage, "userDefined");

                }
            }
        });


        populatePredefinedList();

        obstacleNameTxt = (TextField) primaryStage.getScene().lookup("#obstacleNameTxt");
        obstacleHeightTxt = (TextField) primaryStage.getScene().lookup("#obstacleHeightTxt");

        runwayGrid = (GridPane) primaryStage.getScene().lookup("#runwayGrid");
        //runwayGrid.getStyleClass().add("light");

        runwayDesignatorLbl = (Label) primaryStage.getScene().lookup("#runwayDesignatorLbl");
        toraLbl = (Label) primaryStage.getScene().lookup("#toraLbl");
        todaLbl = (Label) primaryStage.getScene().lookup("#todaLbl");
        asdaLbl = (Label) primaryStage.getScene().lookup("#asdaLbl");
        ldaLbl = (Label) primaryStage.getScene().lookup("#ldaLbl");

        runwayDesignatorCntLbl = (Label) primaryStage.getScene().lookup("#runwayDesignatorCntLbl");
        runwayDesignatorLbl2 = (Label) primaryStage.getScene().lookup("#runwayDesignatorLbl2");
        toraCntLbl = (Label) primaryStage.getScene().lookup("#toraCntLbl");
        todaCntLbl = (Label) primaryStage.getScene().lookup("#todaCntLbl");
        asdaCntLbl = (Label) primaryStage.getScene().lookup("#asdaCntLbl");
        ldaCntLbl = (Label) primaryStage.getScene().lookup("#ldaCntLbl");
        toraCntLbl2 = (Label) primaryStage.getScene().lookup("#toraCntLbl2");
        todaCntLbl2 = (Label) primaryStage.getScene().lookup("#todaCntLbl2");
        asdaCntLbl2 = (Label) primaryStage.getScene().lookup("#asdaCntLbl2");
        ldaCntLbl2 = (Label) primaryStage.getScene().lookup("#ldaCntLbl2");


        windlLbl = (Label) primaryStage.getScene().lookup("#windLbl");

        //Home screen plane rotation
        planePane = (Pane) primaryStage.getScene().lookup("#planePane");
        planePane.getStyleClass().add("myPane");
        planeImg = (ImageView) planePane.getChildren().get(0);

        //Animate one way...
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(0.3), planeImg);
        rotateTransition.setFromAngle(0);
        rotateTransition.setToAngle(-20);
        rotateTransition.setCycleCount(1);

        //...and the other !
        RotateTransition reverseRotateTransition = new RotateTransition(Duration.seconds(0.3), planeImg);
        reverseRotateTransition.setFromAngle(-20);
        reverseRotateTransition.setToAngle(0);
        reverseRotateTransition.setCycleCount(1);

        //Animate plane taking off
        TranslateTransition takeOffTransition = new TranslateTransition(Duration.seconds(0.3), planeImg);
        takeOffTransition.setFromY(0);
        takeOffTransition.setToY(-900);

        //Animate plane landing
        TranslateTransition landTransition = new TranslateTransition(Duration.seconds(0.3), planeImg);
        landTransition.setFromY(-900);
        landTransition.setToY(0);

        takeOffTransition.statusProperty().addListener(new ChangeListener<Animation.Status>() {
            @Override
            public void changed(ObservableValue<? extends Animation.Status> observable, Animation.Status oldValue, Animation.Status newValue) {
                System.out.println("Finished running take off transition");
                if (newValue == Animation.Status.STOPPED){
                    tabPane.getSelectionModel().select(1);
                }
            }
        });

        tabPane.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if ((int) newValue == 0){
                    landTransition.play();
                }
            }
        });
        //planePane.setBackground(new Background(new BackgroundFill(Color.web("#ff1290"), CornerRadii.EMPTY, Insets.EMPTY)));

        primaryStage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double newVal = (double) newValue;
                planeImg.setLayoutX(planePane.getWidth() - planeImg.getFitWidth());
                System.out.println(planeImg.getX());
            }
        });

        primaryStage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double newVal = (double) newValue;
                System.out.println("TRIGGERED old : " + oldValue + " and new val " + newValue);
                planeImg.setLayoutY(planePane.getHeight() - 1.4*planeImg.getFitHeight());
                System.out.println(planeImg.getY());
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
                //setTabMinHeight(double v)
                takeOffTransition.play();
                System.out.println("Loading " + xmlFileToLoad.getName());
                AirportConfig ac = fileIO.read(xmlFileToLoad.getPath());
                airportConfigs.put(ac.getName(), ac);
                updateAirportSelects();

                //tabPane.getSelectionModel().select(1);
            }
        });

        planePane.hoverProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean rotate) {
                //Ignore the hover property when the plane is taking off or landing
                if (takeOffTransition.statusProperty().get().equals(Animation.Status.RUNNING) || landTransition.statusProperty().get().equals(Animation.Status.RUNNING)){
                    return;
                }

                //Rotate on mouse over, return to original state on mouse leave
                if (rotate){
                    rotateTransition.play();
                } else {
                    reverseRotateTransition.play();
                }
            }
        });

        /*airportSelect.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                System.out.println("Heeere");
            }
        });*/
        Map<String, AirportConfig> airportConfigsDB = fileIO.readRunwayDB("runways.csv");
        airportConfigs.putAll(airportConfigsDB);
        updateAirportSelects();

    }

    private Region getHGrowingRegion(){
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }

    public void updateAirportSelects(){
        runwaySelect.getItems().clear();
        airportSelect.getItems().clear();
        addRunwayAirportSelect.getItems().clear();

        String[] names = airportConfigs.keySet().toArray(new String[0]);
        Arrays.sort(names);

        for (String name : names){
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

    public Popup createAddObstaclePopup(){
        Popup popup = new Popup();

        VBox rootBox = new VBox(20);
        HBox emptyNameBox = new HBox();
        HBox emptyHeightBox = new HBox();
        HBox nameBox = new HBox(20);
        HBox heightBox = new HBox(20);
        HBox buttonsBox = new HBox();
        Region nameRegion = new Region();
        Region heightRegion = new Region();
        Region buttonsRegion = new Region();
        Label nameLbl, heightLbl, nameRequiredLbl, heightRequiredLbl;
        TextField nameTF, heightTF;
        Button addObstacleBtn, cancelBtn;

        nameLbl = new Label("Name");
        nameLbl.getStyleClass().add("popUpTitles");
        nameLbl.getStylesheets().add("styles/layoutStyles.css");

        heightLbl = new Label("Height (m)");
        heightLbl.getStyleClass().add("popUpTitles");
        heightLbl.getStylesheets().add("styles/layoutStyles.css");

        nameTF = new TextField();
        heightTF = new TextField();

        nameTF.getStyleClass().add("redErrorPromptText");
        nameTF.getStylesheets().add("styles/obstacles.css");
        heightTF.getStyleClass().add("redErrorPromptText");
        heightTF.getStylesheets().add("styles/obstacles.css");

        nameRequiredLbl = new Label("");
        heightRequiredLbl = new Label("");

        nameRequiredLbl.getStyleClass().add("fieldRequiredLabel");
        nameRequiredLbl.getStylesheets().add("styles/calculations.css");
        heightRequiredLbl.getStyleClass().add("fieldRequiredLabel");
        heightRequiredLbl.getStylesheets().add("styles/calculations.css");

        addObstacleBtn = new Button("Add");
        addObstacleBtn.getStyleClass().add("primaryButton");
        addObstacleBtn.getStylesheets().add("styles/global.css");
        cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("primaryButton");
        cancelBtn.getStylesheets().add("styles/global.css");

        HBox.setHgrow(nameRegion, Priority.ALWAYS);
        HBox.setHgrow(heightRegion, Priority.ALWAYS);
        HBox.setHgrow(buttonsRegion, Priority.ALWAYS);

        emptyNameBox.getChildren().add(nameRequiredLbl);
        emptyHeightBox.getChildren().add(heightRequiredLbl);

        nameBox.getChildren().add(nameLbl);
        nameBox.getChildren().add(nameRegion);
        nameBox.getChildren().add(nameTF);

        heightBox.getChildren().add(heightLbl);
        heightBox.getChildren().add(heightRegion);
        heightBox.getChildren().add(heightTF);

        buttonsBox.getChildren().add(addObstacleBtn);
        buttonsBox.getChildren().add(buttonsRegion);
        buttonsBox.getChildren().add(cancelBtn);


        rootBox.getChildren().add(nameBox);
        rootBox.getChildren().add(heightBox);
        rootBox.getChildren().add(buttonsBox);

        rootBox.getStyleClass().add("popup");
        rootBox.getStylesheets().add("styles/layoutStyles.css");

        addObstacleBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // Checking for empty name and height fields
                if (nameTF.getText().isEmpty()) {
                    nameRequiredLbl.setText("                                             This field is required");
                    if (!rootBox.getChildren().contains(emptyNameBox)) {
                        rootBox.getChildren().add(0, emptyNameBox);
                    }
                    nameTF.setPromptText("");
                } else {
                    if (rootBox.getChildren().contains(emptyNameBox)) {
                        rootBox.getChildren().remove(emptyNameBox);
                    }
                    nameRequiredLbl.setText("");
                }
                if (heightTF.getText().isEmpty()) {
                    heightRequiredLbl.setText("                                             This field is required");
                    if (rootBox.getChildren().contains(emptyNameBox)) {
                        if (!rootBox.getChildren().contains(emptyHeightBox)) {
                            rootBox.getChildren().add(2, emptyHeightBox);
                        }
                    } else {
                        if (!rootBox.getChildren().contains(emptyHeightBox)) {
                            rootBox.getChildren().add(1, emptyHeightBox);
                        }
                    }
                    heightTF.setPromptText("");
                } else {
                    if (rootBox.getChildren().contains(emptyHeightBox)) {
                        rootBox.getChildren().remove(emptyHeightBox);
                    }
                    heightRequiredLbl.setText("");
                }
                // Checking for valid obstacle name and valid obstacle height
                if (validateDoubleForm(new ArrayList<>(Arrays.asList(heightTF.getText()))) && validateStrForm(new ArrayList<>(Arrays.asList(nameTF.getText())))) {
                    System.out.println("Add obstacle");
                    addObstacle(nameTF.getText(), Double.parseDouble(heightTF.getText()));
                    nameTF.clear();
                    heightTF.clear();
                    nameTF.setPromptText("");
                    heightTF.setPromptText("");
                    updateObstaclesList();
                    addObstaclePopup.hide();
                } else if (!heightTF.getText().isEmpty() && !validateDoubleForm(new ArrayList<>(Arrays.asList(heightTF.getText())))) {
                    heightTF.clear();
                    heightTF.setPromptText("Invalid obstacle height!");
                } else if (!nameTF.getText().isEmpty() && !validateStrForm(new ArrayList<>(Arrays.asList(nameTF.getText())))) {
                    nameTF.clear();
                    nameTF.setPromptText("Invalid obstacle name!");
                }

            }
        });

        cancelBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (rootBox.getChildren().contains(emptyNameBox)) {
                    rootBox.getChildren().remove(emptyNameBox);
                }
                if (rootBox.getChildren().contains(emptyHeightBox)) {
                    rootBox.getChildren().remove(emptyHeightBox);
                }
                nameRequiredLbl.setText("");
                heightRequiredLbl.setText("");
                nameTF.clear();
                nameTF.setPromptText("");
                heightTF.clear();
                heightTF.setPromptText("");
                addObstaclePopup.hide();
            }
        });

        popup.getContent().add(rootBox);


        return popup;
    }

    private void showObstacleDetails (ListView listView, MouseEvent event, Stage primaryStage, String typeOfList) {


        editingObstacle = false;

        String obstacleName = listView.getSelectionModel().getSelectedItem().toString();
        Obstacle selectedObstacle = allObstaclesSorted.get(obstacleName);


        Popup detailsPopUp = new Popup();

        VBox box = new VBox(100);
        box.getStyleClass().add("popup");
        box.getStylesheets().add("styles/layoutStyles.css");

        HBox subBox = new HBox(100);

        Label detailsLabel = new Label ("Overview of obstacle details");
        Label nameLabel = new Label ("Name:");
        Label nameContentLabel = new Label(selectedObstacle.getName());
        TextField nameEditTF = new TextField();
        nameEditTF.setPrefWidth(240);
        Label heightLabel = new Label ("Height:");
        Label heightContentLabel = new Label(selectedObstacle.getHeight() + "m");
        TextField heightEditTF = new TextField();
        heightEditTF.setPrefWidth(240);

        // Styling of name and height text fields to show red prompt text
        nameEditTF.getStyleClass().add("redErrorPromptText");
        nameEditTF.getStylesheets().add("styles/obstacles.css");
        heightEditTF.getStyleClass().add("redErrorPromptText");
        heightEditTF.getStylesheets().add("styles/obstacles.css");

        // Content for error messages
        Label nameRequiredLbl = new Label("");
        nameRequiredLbl.getStyleClass().add("fieldRequiredLabel");
        nameRequiredLbl.getStylesheets().add("styles/calculations.css");
        HBox emptyNameBox = new HBox();
        emptyNameBox.getChildren().add(nameRequiredLbl);
        Label heightRequiredLbl = new Label("");
        heightRequiredLbl.getStyleClass().add("fieldRequiredLabel");
        heightRequiredLbl.getStylesheets().add("styles/calculations.css");
        HBox emptyHeightBox = new HBox();
        emptyHeightBox.getChildren().add(heightRequiredLbl);

        // Styling of labels in the obstacle details popup
        detailsLabel.getStyleClass().add("popUpTitles");
        detailsLabel.getStylesheets().add("styles/layoutStyles.css");
        detailsLabel.setStyle("-fx-font-size: 16px");
        nameLabel.getStyleClass().add("popUpTitles");
        nameLabel.getStylesheets().add("styles/layoutStyles.css");
        nameContentLabel.getStyleClass().add("popUpText");
        nameContentLabel.getStylesheets().add("styles/layoutStyles.css");
        heightLabel.getStyleClass().add("popUpTitles");
        heightLabel.getStylesheets().add("styles/layoutStyles.css");
        heightContentLabel.getStyleClass().add("popUpText");
        heightContentLabel.getStylesheets().add("styles/layoutStyles.css");

        Button returnButton = new Button("Go back");
        Button editButton = new Button("Edit details");
        Button saveButton = new Button("Save changes");

        // Styling of buttons in the obstacle details popup
        returnButton.getStyleClass().add("primaryButton");
        returnButton.getStylesheets().add("styles/global.css");
        editButton.getStyleClass().add("primaryButton");
        editButton.getStylesheets().add("styles/global.css");
        saveButton.getStyleClass().add("primaryButton");
        saveButton.getStylesheets().add("styles/global.css");

        HBox nameHBox = new HBox(20);
        nameHBox.getChildren().add(nameLabel);
        nameHBox.getChildren().add(nameContentLabel);

        HBox heightHBox = new HBox(13.5);
        heightHBox.getChildren().add(heightLabel);
        heightHBox.getChildren().add(heightContentLabel);

        subBox.getChildren().add(editButton);
        subBox.getChildren().add(returnButton);
        box.getChildren().add(detailsLabel);
        box.getChildren().add(nameHBox);
        box.getChildren().add(heightHBox);
        box.getChildren().add(subBox);

        detailsPopUp.getContent().add(box);

        //TODO - position according to window size
        Node eventSource = (Node) event.getSource();
        Bounds sourceNodeBounds = eventSource.localToScreen(eventSource.getBoundsInLocal());
        detailsPopUp.setX(sourceNodeBounds.getMinX() - 310.0);
        detailsPopUp.setY(sourceNodeBounds.getMaxY() - 180.0);
        detailsPopUp.show(primaryStage);

        returnButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                detailsPopUp.hide();
            }
        });


        saveButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // Checking for empty name and height fields
                if (nameEditTF.getText().isEmpty()) {
                    nameRequiredLbl.setText("                                       This field is required");
                    if (!box.getChildren().contains(emptyNameBox)) {
                        box.getChildren().add(1, emptyNameBox);
                    }
                    nameEditTF.setPromptText("");
                } else {
                    if (box.getChildren().contains(emptyNameBox)) {
                        box.getChildren().remove(emptyNameBox);
                    }
                    nameRequiredLbl.setText("");
                }
                if (heightEditTF.getText().isEmpty()) {
                    heightRequiredLbl.setText("                                       This field is required");
                    if (box.getChildren().contains(emptyNameBox)) {
                        if (!box.getChildren().contains(emptyHeightBox)) {
                            box.getChildren().add(3, emptyHeightBox);
                        }
                    } else {
                        if (!box.getChildren().contains(emptyHeightBox)) {
                            box.getChildren().add(2, emptyHeightBox);
                        }
                    }
                    heightEditTF.setPromptText("");
                } else {
                    if (box.getChildren().contains(emptyHeightBox)) {
                        box.getChildren().remove(emptyHeightBox);
                    }
                    heightRequiredLbl.setText("");
                }
                // Checking for valid obstacle name and valid obstacle height
                if (validateDoubleForm(new ArrayList<>(Arrays.asList(heightEditTF.getText()))) && validateStrForm(new ArrayList<>(Arrays.asList(nameEditTF.getText())))) {
                    System.out.println("Add -edited- obstacle");
                    if (typeOfList.equals("predefined")) {
                        predefinedObstaclesSorted.remove(selectedObstacle.getName());
                        allObstaclesSorted.remove(selectedObstacle.getName());
                        selectedObstacle.setName(nameEditTF.getText());
                        selectedObstacle.setHeight(Double.valueOf(heightEditTF.getText()));
                        predefinedObstaclesSorted.put(selectedObstacle.getName(), selectedObstacle);
                        allObstaclesSorted.put(selectedObstacle.getName(), selectedObstacle);

                        nameContentLabel.setText(selectedObstacle.getName());
                        heightContentLabel.setText(Double.toString(selectedObstacle.getHeight()));

                        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
                        listView.getItems().set(selectedIndex, selectedObstacle.getName());

                        updateObstaclesList();
                        detailsPopUp.hide();
                    } else if (typeOfList.equals("userDefined")) {
                        userDefinedObstacles.remove(selectedObstacle.getName());
                        allObstaclesSorted.remove(selectedObstacle.getName());
                        selectedObstacle.setName(nameEditTF.getText());
                        selectedObstacle.setHeight(Double.valueOf(heightEditTF.getText()));
                        userDefinedObstacles.put(selectedObstacle.getName(), selectedObstacle);
                        allObstaclesSorted.put(selectedObstacle.getName(), selectedObstacle);

                        nameContentLabel.setText(selectedObstacle.getName());
                        heightContentLabel.setText(Double.toString(selectedObstacle.getHeight()));

                        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
                        listView.getItems().set(selectedIndex, selectedObstacle.getName());

                        updateObstaclesList();
                        detailsPopUp.hide();
                    }

                } else if (!heightEditTF.getText().isEmpty() && !validateDoubleForm(new ArrayList<>(Arrays.asList(heightEditTF.getText())))) {
                    heightEditTF.clear();
                    heightEditTF.setPromptText("Invalid obstacle height!");
                } else if (!nameEditTF.getText().isEmpty() && !validateStrForm(new ArrayList<>(Arrays.asList(nameEditTF.getText())))) {
                    nameEditTF.clear();
                    nameEditTF.setPromptText("Invalid obstacle name!");
                }
            }
        });

        editButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                editingObstacle = !editingObstacle;
                if (editingObstacle){
                    //Edit mode

                    nameLabel.setText("Name");
                    heightLabel.setText("Height (m)");

                    nameHBox.setSpacing(55);
                    subBox.setSpacing(132);

                    nameHBox.getChildren().remove(nameContentLabel);
                    nameHBox.getChildren().add(nameEditTF);

                    heightHBox.getChildren().remove(heightContentLabel);
                    heightHBox.getChildren().add(heightEditTF);

                    nameEditTF.setText(selectedObstacle.getName());
                    heightEditTF.setText(Double.toString(selectedObstacle.getHeight()));

                    detailsLabel.setText("Edit obstacle details");

                    subBox.getChildren().remove(returnButton);
                    subBox.getChildren().remove(editButton);
                    subBox.getChildren().add(saveButton);
                    subBox.getChildren().add(returnButton);

                    returnButton.setText("Cancel");
                }
            }
        });


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
                updateAirportSelects();
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
        Label selectAirportLbl = new Label("Select airport");
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
        addRunwayAirportSelect = new ComboBox();
        addRunwayAirportSelect.setId("runwayComboBox");
        addRunwayAirportSelect.setVisibleRowCount(10);

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


        HBox airportSelection = new HBox(30);
        airportSelection.setPadding(new Insets(10, 12, 15, 15));
        airportSelection.getChildren().add(selectAirportLbl);
        airportSelection.getChildren().add(addRunwayAirportSelect);


        VBox addRunwayRoot = new VBox(20);


        addRunwayRoot.getStyleClass().add("addRunwayAirportSelection");
        addRunwayRoot.getStylesheets().add("styles/layoutStyles.css");

        addRunwayRoot.getChildren().add(airportSelection);
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

                if (validateIntForm(new ArrayList<String>(Arrays.asList(toraTF.getText(), todaTF.getText(), asdaTF.getText(), ldaTF.getText(), displacementThresholdTF.getText(), toraTF2.getText(), todaTF2.getText(), asdaTF2.getText(), ldaTF2.getText(), displacementThresholdTF2.getText())))){
                    System.out.println("valid form");
                    AirportConfig selectedAirport = airportConfigs.get(addRunwayAirportSelect.getSelectionModel().getSelectedItem().toString());
                    System.out.println(selectedAirport.toString());
                    RunwayConfig r1 = new RunwayConfig(new RunwayDesignator(runwayDesignatorTF.getText()), Integer.parseInt(toraTF.getText()), Integer.parseInt(todaTF.getText()), Integer.parseInt(asdaTF.getText()), Integer.parseInt(ldaTF.getText()), Integer.parseInt(displacementThresholdTF.getText()));
                    RunwayConfig r2 = new RunwayConfig(new RunwayDesignator(runwayDesignatorTF2.getText()), Integer.parseInt(toraTF2.getText()), Integer.parseInt(todaTF2.getText()), Integer.parseInt(asdaTF2.getText()), Integer.parseInt(ldaTF2.getText()), Integer.parseInt(displacementThresholdTF2.getText()));
                    RunwayPair runwayPair = new RunwayPair(r1, r2);
                    selectedAirport.addRunwayPair(runwayPair);
                    airportConfigs.put(selectedAirport.getName(), selectedAirport);
                    System.out.println("add runway with name " + runwayDesignatorTF.getText() + " and TORA " + toraTF.getText());
                    addRunwayPopup.hide();
                    updateAirportSelects();
                } else {
                    System.err.println("Invalid form");
                }

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
        stage.setWidth(900);
        stage.setHeight(500);
        return stage;
    }

    private void resetCalculationsTab(){
        calculationsPane.getChildren().remove(viewCalculationResultsVBox);
        calculationsPane.getChildren().add(calculationsRootBox);
    }


    private void switchCalculationsTabToView(){
        centreLineRequiredLabel.setText("");
        thresholdDistanceRequiredLabel.setText("");
        thresholdRequiredLabel.setText("");
        obstacleRequiredLabel.setText("");
        calculationsPane.getChildren().remove(calculationsRootBox);
        calculationsPane.getChildren().add(viewCalculationResultsVBox);
    }

    private void updateRunwayInfoLabels(RunwayPair runwayPair){
        runwayDesignatorCntLbl.setText(" " + runwayPair.getR1().getRunwayDesignator().toString());
        runwayDesignatorLbl2.setText(" " + runwayPair.getR2().getRunwayDesignator().toString());
        toraCntLbl.setText(" " + runwayPair.getR1().getTORA());
        todaCntLbl.setText(" " + runwayPair.getR1().getTODA());
        asdaCntLbl.setText(" " + runwayPair.getR1().getASDA());
        ldaCntLbl.setText(" " + runwayPair.getR1().getLDA());
        toraCntLbl2.setText(" " + runwayPair.getR2().getTORA());
        todaCntLbl2.setText(" " + runwayPair.getR2().getTODA());
        asdaCntLbl2.setText(" " + runwayPair.getR2().getASDA());
        ldaCntLbl2.setText(" " + runwayPair.getR2().getLDA());
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

    private void removeUserObstacle() {

        if (userDefinedObstaclesLV.getItems().isEmpty()) {
            System.out.println("No obstacles to remove");
        }
        else {
            String obstacleName = userDefinedObstaclesLV.getSelectionModel().getSelectedItem().toString();
            Obstacle selectedObstacle = allObstaclesSorted.get(obstacleName);

            userDefinedObstaclesLV.getItems().remove(selectedObstacle);
            obstacleSelect.getItems().remove(selectedObstacle);

            userDefinedObstacles.remove(obstacleName);
            allObstaclesSorted.remove(obstacleName);

        }




    }


    private void updateObstaclesList(){
        userDefinedObstaclesLV.getItems().clear();
        userDefinedObstaclesLV.getItems().addAll(userDefinedObstacles.keySet());
        predefinedObstaclesLV.getItems().clear();
        predefinedObstaclesLV.getItems().addAll(predefinedObstaclesSorted.keySet());
        obstacleSelect.getItems().clear();
        obstacleSelect.getItems().addAll(allObstaclesSorted.keySet());
    }

    private void populatePredefinedList() {
        ArrayList<Obstacle> obstacles = new ArrayList<>();
        obstacles.add(new Obstacle("Boeing 747", 19.4));
        obstacles.add(new Obstacle("Boeing 767", 16.8));
        obstacles.add(new Obstacle("Boeing 777", 18.5));
        obstacles.add(new Obstacle("Boeing 787", 18.5));
        obstacles.add(new Obstacle("Airbus A320", 11.8));
        obstacles.add(new Obstacle("Airbus A330", 17.9));
        obstacles.add(new Obstacle("Airbus A340", 17.1));
        obstacles.add(new Obstacle("Airbus A350", 16.9));
        obstacles.add(new Obstacle("Airbus A380", 24.1));
        obstacles.add(new Obstacle("Telescopic handler", 12));

        for (Obstacle obstacle : obstacles){
            predefinedObstaclesSorted.put(obstacle.getName(), obstacle);
            allObstaclesSorted.put(obstacle.getName(), obstacle);
        }

        predefinedObstaclesLV.getItems().addAll(predefinedObstaclesSorted.keySet());

        obstacleSelect.getItems().addAll(predefinedObstaclesSorted.keySet());


    }


    private Boolean validateObstaclesForm(){

        if (obstacleNameTxt.getText().length() < 1){
            return false;
        }

        try {
            Double.parseDouble(obstacleHeightTxt.getText());
        } catch (NumberFormatException e){
            e.printStackTrace();
            return false;
        }



        return true;
    }

    private Boolean validateDoubleForm(ArrayList<String> doubleVals){
        for (String s : doubleVals){
            try {
                Double.parseDouble(s);
            } catch (NumberFormatException e){
                return false;
            }
            if (Double.parseDouble(s) < 1 || Double.parseDouble(s) > 100) {
                return false;
            }
        }
        return true;
    }

    private Boolean validateIntForm(ArrayList<String> intVals){
        for (String s : intVals){
            try {
                Integer.parseInt(s);
            } catch (NumberFormatException e){
                return false;
            }
        }
        return true;
    }

    private Boolean validateStrForm(ArrayList<String> strVals) {
        for (String s : strVals) {
            if (s.length() < 1) {
                return false;
            }
        }
        return true;
    }

    public void addObstacle(String name, double height) {
        Obstacle obstacle = new Obstacle(name, height);
        addObstacle(obstacle);
    }

    private void addObstacle(Obstacle obstacle) {
        this.userDefinedObstacles.put(obstacle.getName(), obstacle);
        this.allObstaclesSorted.put(obstacle.getName(), obstacle);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
