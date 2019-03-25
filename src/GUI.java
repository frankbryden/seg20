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
    //TODO set currently selected obstacle in the ComboBox in the calculations tab
    //TODO add airport database
    private Button loadAirportButton, addObstacleBtn, addAirportBtn, addRunwayBtn, calculateBtn, calculationsBackBtn, printerBtn, outArrowBtn, popAddObstacleBtn, editObstacleBtn, deleteObstacleBtn, saveObstacleBtn, saveObstaclesBtn, highlightAsdaBtn, highlightToraBtn, highlightTodaBtn, highlightLdaBtn;
    private Pane calculationsPane;
    private TextField obstacleNameTxt, obstacleHeightTxt, centrelineTF, distanceFromThresholdTF;
    private ListView userDefinedObstaclesLV, predefinedObstaclesLV;
    private ComboBox thresholdSelect, addRunwayAirportSelect, airportSelect, runwaySelect;
    private FileChooser fileChooser;
    private FileIO fileIO;
    private Label runwayDesignatorLbl, toraLbl, todaLbl, asdaLbl, ldaLbl, centrelineDistanceLbl, runwayThresholdLbl, originalValuesLbl, obstacleSelectLbl, thresholdSelectLbl, originalToda, originalTora, originalAsda, originalLda, recalculatedToda, recalculatedTora, recalculatedAsda, recalculatedLda, windlLbl;
    private GridPane calculationResultsGrid;
    private TextArea calculationDetails;
    private VBox calculationsRootBox, viewCalculationResultsVBox;
    private HBox centerlineHBox, thresholdHBox, obstacleSelectHBox, thresholdSelectHBox;
    private Map<String, AirportConfig> airportConfigs;
    private Popup addObstaclePopup;
    private Map<String, Obstacle> userObstaclesSorted, predefinedObstaclesSorted, allObstaclesSorted;
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
        userObstaclesSorted = new TreeMap<>();
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

                        //TODO update wind direction and speed acoordingly
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
                                    System.out.println("data is back !");
                                    System.out.println(data.toString());
                                    Pattern p = Pattern.compile("\"wind\":\\{\"speed\":([0-9]+\\.[0-9]*),\"deg\":([0-9]+).*?\\}");
                                    System.out.println(p.toString());
                                    Matcher m = p.matcher(data.toString());
                                    System.out.println(m.find());
                                    System.out.println(m.group(0));
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
                for (String name : userObstaclesSorted.keySet()){
                    fileIO.write(userObstaclesSorted.get(name), "obstacles.xml");
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
        centrelineTF = new TextField();
        distanceFromThresholdTF = new TextField();
        calculateBtn = new Button("Calculate");
        calculateBtn.setId("calcButton");
        calculationsRootBox = new VBox(20);
        calculationsRootBox.setPadding(new Insets(10, 10, 10, 10));
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
        calculationsRootBox.getChildren().add(calculateBtnVBox);
        calculationsRootBox.getStyleClass().add("customCol");

        calculateBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //Get currently selected obstacle
//HERE  can't change the textfield's width or the airport box width
                if (centrelineTF.getText().isEmpty() && distanceFromThresholdTF.getText().isEmpty()) {
                    centrelineTF.setPromptText("Invalid centreline distance!");
                    distanceFromThresholdTF.setPromptText("Invalid threshold distance!");
                } else if (distanceFromThresholdTF.getText().isEmpty()) {
                    distanceFromThresholdTF.setPromptText("Invalid threshold distance!");
                } else if (centrelineTF.getText().isEmpty()) {
                    centrelineTF.setPromptText("Invalid centreline distance!");
                } else if (thresholdSelect.getSelectionModel().isEmpty()) {
                    System.out.println("No threshold selected");
                } else if (obstacleSelect.getSelectionModel().isEmpty()){
                    System.out.println("No obstacle selected");
                }
                else {
                    String obstacleName = obstacleSelect.getSelectionModel().getSelectedItem().toString();
                    Obstacle currentlySelectedObstacle = allObstaclesSorted.get(obstacleName);


                    int distanceFromCenterline = Integer.valueOf(centrelineTF.getText());
                    String thresholdName = thresholdSelect.getSelectionModel().getSelectedItem().toString();
                    RunwayConfig runwayConfig;
                    if (currentlySelectedRunway.getR1().getRunwayDesignator().toString().equals(thresholdName)){
                        runwayConfig = currentlySelectedRunway.getR1();
                    } else {
                        runwayConfig = currentlySelectedRunway.getR2();
                    }
                    Calculations calculations = new Calculations(runwayConfig);
                    int distanceFromThreshold = Integer.valueOf(distanceFromThresholdTF.getText());
                    CalculationResults results = calculations.recalculateParams(currentlySelectedObstacle, distanceFromThreshold, distanceFromCenterline, Calculations.Direction.AWAY);
                    RunwayConfig recalculatedParams = results.getRecalculatedParams();
                    calculationDetails.setText(results.getCalculationDetails());
                    System.out.println(recalculatedParams.toString());
                    updateCalculationResultsView(runwayConfig, recalculatedParams);
                    switchCalculationsTabToView();

                    String unselectedThreshold = "";
                    if (currentlySelectedRunway.getR1().getRunwayDesignator().toString().equals(thresholdName)){
                        unselectedThreshold = currentlySelectedRunway.getR2().toString();
                    } else {
                        unselectedThreshold = currentlySelectedRunway.getR1().toString();
                    }

                    runwayRendererSideView.renderSideview();
                    runwayRendererSideView.drawObstacle((int) currentlySelectedObstacle.getHeight(),distanceFromThreshold,thresholdName,unselectedThreshold );

                }



            }
        });

        //Calculations Pane - calculation results view
        originalValuesLbl = new Label("Breakdown of the calculations");
        originalValuesLbl.setId("calcBreakdownLabel");
        originalValuesLbl.setId("breakdownTitle");
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
        VBox calculateBackBtnVBox = new VBox();
        calculateBackBtnVBox.getChildren().add(calculationsBackBtn);
        calculateBackBtnVBox.setAlignment(Pos.BASELINE_RIGHT);
        calculateBackBtnVBox.setPadding(new Insets(0, 20, 0, 0));

        tabPane = (TabPane) primaryStage.getScene().lookup("#tabPane");
        canvas = (Canvas) primaryStage.getScene().lookup("#canvas");
        canvas.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                System.out.println(event.getDeltaY());
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
                if (click.getClickCount() == 2) {
                    showObstacleDetails(predefinedObstaclesLV, click, primaryStage);
                }
           }
        });

        userDefinedObstaclesLV.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent click) {

                if (click.getClickCount() == 2 && !userDefinedObstaclesLV.getItems().isEmpty()) {

                    showObstacleDetails(userDefinedObstaclesLV, click, primaryStage);

                }
            }
        });


        populatePredefinedList();

        obstacleNameTxt = (TextField) primaryStage.getScene().lookup("#obstacleNameTxt");
        obstacleHeightTxt = (TextField) primaryStage.getScene().lookup("#obstacleHeightTxt");

        runwayDesignatorLbl = (Label) primaryStage.getScene().lookup("#runwayDesignatorLbl");
        toraLbl = (Label) primaryStage.getScene().lookup("#toraLbl");
        todaLbl = (Label) primaryStage.getScene().lookup("#todaLbl");
        asdaLbl = (Label) primaryStage.getScene().lookup("#asdaLbl");
        ldaLbl = (Label) primaryStage.getScene().lookup("#ldaLbl");

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
        HBox nameBox = new HBox(20);
        HBox heightBox = new HBox(20);
        HBox buttonsBox = new HBox();
        Region nameRegion = new Region();
        Region heightRegion = new Region();
        Region buttonsRegion = new Region();
        Label nameLbl, heightLbl;
        TextField nameTF, heightTF;
        Button addObstacleBtn, cancelBtn;

        nameLbl = new Label("Name");
        heightLbl = new Label("Height (m)");

        nameTF = new TextField();
        nameTF.setPromptText("Enter obstacle name");
        heightTF = new TextField();

        heightTF.setPromptText("Enter obstacle height");

        addObstacleBtn = new Button("Add");
        cancelBtn = new Button("Cancel");

        HBox.setHgrow(nameRegion, Priority.ALWAYS);
        HBox.setHgrow(heightRegion, Priority.ALWAYS);
        HBox.setHgrow(buttonsRegion, Priority.ALWAYS);

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

        //HERE - can't change from red to grey easily
        addObstacleBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (validateDoubleForm(new ArrayList<>(Arrays.asList(heightTF.getText()))) && validateStrForm(new ArrayList<>(Arrays.asList(nameTF.getText())))){
                    System.out.println("Add obstacle");
                    addObstacle(nameTF.getText(), Double.parseDouble(heightTF.getText()));
                    nameTF.clear();
                    heightTF.clear();
                    heightTF.setPromptText("Enter obstacle height");
                    updateObstaclesList();
                    addObstaclePopup.hide();
                } else if (!validateDoubleForm(new ArrayList<>(Arrays.asList(heightTF.getText())))) {
                    heightTF.clear();
                    heightTF.setPromptText("Invalid obstacle height!");
                }

            }
        });

        cancelBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                nameTF.clear();
                heightTF.clear();
                heightTF.setPromptText("Enter obstacle height");
                addObstaclePopup.hide();
            }
        });

        popup.getContent().add(rootBox);


        return popup;
    }

    private void showObstacleDetails (ListView listView, MouseEvent event, Stage primaryStage) {

        String obstacleName = listView.getSelectionModel().getSelectedItem().toString();
        Obstacle selectedObstacle = allObstaclesSorted.get(obstacleName);

        Popup detailsPopUp = new Popup();

        VBox box = new VBox(100);
        box.getStyleClass().add("popup");
        box.getStylesheets().add("styles/layoutStyles.css");

        VBox subBox = new VBox(100);

        Label detailsLabel = new Label ("Overview of obstacle details");
        Label nameLabel = new Label ("Name: " + selectedObstacle.getName());
        Label heightLabel = new Label ("Height (m): " + selectedObstacle.getHeight());
        Button returnButton = new Button("Go back");
        returnButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                detailsPopUp.hide();
            }
        });

        subBox.setAlignment(Pos.CENTER);
        subBox.getChildren().add(returnButton);
        box.getChildren().add(detailsLabel);
        box.getChildren().add(nameLabel);
        box.getChildren().add(heightLabel);
        box.getChildren().add(subBox);


        detailsPopUp.getContent().add(box);

        Node eventSource = (Node) event.getSource();
        Bounds sourceNodeBounds = eventSource.localToScreen(eventSource.getBoundsInLocal());
        detailsPopUp.setX(sourceNodeBounds.getMinX() - 260.0);
        detailsPopUp.setY(sourceNodeBounds.getMaxY() - 190.0);

        detailsPopUp.show(primaryStage);

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
        calculationsPane.getChildren().remove(calculationsRootBox);
        calculationsPane.getChildren().add(viewCalculationResultsVBox);
    }

    private void updateRunwayInfoLabels(RunwayPair runwayPair){
        runwayDesignatorLbl.setText(runwayPair.getName());
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

    private void removeUserObstacle() {

        if (userDefinedObstaclesLV.getItems().isEmpty()) {
            System.out.println("No obstacles to remove");
        }
        else {
            String obstacleName = userDefinedObstaclesLV.getSelectionModel().getSelectedItem().toString();
            Obstacle selectedObstacle = allObstaclesSorted.get(obstacleName);

            userDefinedObstaclesLV.getItems().remove(selectedObstacle);
            obstacleSelect.getItems().remove(selectedObstacle);

            userObstaclesSorted.remove(obstacleName);
            allObstaclesSorted.remove(obstacleName);

        }




    }


    private void updateObstaclesList(){
        userDefinedObstaclesLV.getItems().clear();
        userDefinedObstaclesLV.getItems().addAll(userObstaclesSorted.keySet());
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

    private Boolean validateStrForm(ArrayList<String> strVals){
        for (String s : strVals){
            if (s.length() < 1){
                return false;
            }
        }
        return true;
    }

    public void addObstacle(String name, double height){
        Obstacle obstacle = new Obstacle(name, height);
        this.userObstaclesSorted.put(name, obstacle);
        this.allObstaclesSorted.put(name, obstacle);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
