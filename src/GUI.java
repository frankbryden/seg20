import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.*;

public class GUI extends Application {
    @FXML
    private Button loadAirportButton, addObstacleBtn, addAirportBtn, addRunwayBtn, calculateBtn, calculationsBackBtn, popAddObstacleBtn,
            editObstacleBtn, saveObstacleBtn, saveObstaclesBtn, highlightAsdaBtn, highlightToraBtn, highlightTodaBtn, highlightLdaBtn, saveSettingsBtn, startBtn, manageTooltipsBtn;
    @FXML
    private Pane calculationsPane;
    private TextField obstacleNameTxt, obstacleHeightTxt, centrelineTF, distanceFromThresholdTF, addObstacleNameTF, addObstacleHeightTF, airportCode, selectedObstacleHeightTF,
            heightEditTF;
    @FXML
    private ListView userDefinedObstaclesLV, predefinedObstaclesLV;
    @FXML
    private ComboBox thresholdSelect, addRunwayAirportSelect, airportSelect, runwaySelect;
    private FileIO fileIO;
    private Label runwayDesignatorLbl, toraLbl, todaLbl, asdaLbl, ldaLbl, centrelineDistanceLbl, runwayDesignatorCntLbl, runwayDesignatorLbl2, toraCntLbl, toraCntLbl2, todaCntLbl, todaCntLbl2, asdaCntLbl, asdaCntLbl2, ldaCntLbl, ldaCntLbl2,
            runwayThresholdLbl, breakdownCalcLbl, obstacleSelectLbl, thresholdSelectLbl, originalToda,
            originalTora, originalAsda, originalLda, recalculatedToda, recalculatedTora, recalculatedAsda, recalculatedLda, windlLbl, selectedObstacleHeightLbl;
    private Label centreLineRequiredLabel, thresholdDistanceRequiredLabel, thresholdRequiredLabel, obstacleRequiredLabel;
    private GridPane calculationResultsGrid, runwayGrid;
    private TextArea calculationDetails;
    private VBox calculationsRootBox, viewCalculationResultsVBox;
    private HBox centerlineHBox, thresholdHBox, obstacleSelectHBox, thresholdSelectHBox, heightHBox;
    private Map<String, AirportConfig> airportConfigs;
    private Popup addObstaclePopup;
    private Map<String, Obstacle> userDefinedObstacles, predefinedObstaclesSorted, allObstaclesSorted;
    private Stage addAirportPopup, addRunwayPopup;
    private ExportPopup exportPopup;
    private RunwayPair currentlySelectedRunway = null;
    private Canvas canvas, sideviewCanvas;
    @FXML
    private TabPane tabPane, rootTabPane;
    private Pane planePane;
    private ImageView planeImg;
    private RunwayRenderer runwayRenderer;
    private RunwayRenderer runwayRendererSideView;
    private BorderPane canvasBorderPane;
    private ComboBox obstacleSelect;
    private Boolean editingObstacle;
    private Stage primaryStage;
    private Printer printer;
    private AirportDatabase airportDB;
    @FXML
    private CheckBox renderRunwayLabelLinesChkbx, renderRunwayRotatedChkbx, renderWindCompass;
    @FXML
    private ColorPicker topDownColorPicker, sideOnColorPicker;
    @FXML
    private Slider zoomSlider;
    private Tooltip centrelineDistTooltip, thresholdDistTooltip, obstacleHeightTooltip, airportCodeTooltip, toraButtonTooltip, todaButtonTooltip, asdaButtonTooltip, ldaButtonTooltip,
            addObstacleTooltip, importObstaclesTooltip, saveObstaclesTooltip, editObstacleHeightTooltip;
    private StackPane trackPane;
    private enum ObstacleList {USER_DEFINED, PREDEFINED}


    @Override
    public void start(Stage primaryStage) throws Exception {
        // getClass().getResource("sample.fxml") gives me a null pointer exception - caused by the way the IDE loads the resource files
        // temporary fix for now

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("sample.fxml"));
        loader.setController(this);
        Parent root = (Parent) loader.load();
        //Parent root; // = FXMLLoader.load(getClass().getClassLoader().getResource("sample.fxml"));

        primaryStage.setTitle("Runway Redeclaration Tool");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        this.primaryStage = primaryStage;


        fileIO = new FileIO();
        userDefinedObstacles = new TreeMap<>();
        predefinedObstaclesSorted = new TreeMap<>();
        allObstaclesSorted = new TreeMap<>();

        airportDB = new AirportDatabase();

        airportConfigs = new HashMap<>();

        airportConfigs.putAll(fileIO.readRunwayDB("runways.csv"));


        // Setting the texts for each tooltip
        centrelineDistTooltip = new Tooltip();
        centrelineDistTooltip.setText("Enter the obstacle's distance from the runway centreline here in metres");
        thresholdDistTooltip = new Tooltip();
        thresholdDistTooltip.setText("Enter the obstacle's distance from the selected runway threshold here in metres");
        obstacleHeightTooltip = new Tooltip();
        obstacleHeightTooltip.setText("Enter the obstacle's height here in metres");
        airportCodeTooltip = new Tooltip();
        airportCodeTooltip.setText("Enter the 3-digit IATA airport code here");
        toraButtonTooltip = new Tooltip();
        toraButtonTooltip.setText("Click here to highlight TORA on the top-down view");
        todaButtonTooltip = new Tooltip();
        todaButtonTooltip.setText("Click here to highlight TODA on the top-down view");
        asdaButtonTooltip = new Tooltip();
        asdaButtonTooltip.setText("Click here to highlight ASDA on the top-down view");
        ldaButtonTooltip = new Tooltip();
        ldaButtonTooltip.setText("Click here to highlight LDA on the top-down view");
        addObstacleTooltip = new Tooltip();
        addObstacleTooltip.setText("Add an obstacle");
        importObstaclesTooltip = new Tooltip();
        importObstaclesTooltip.setText("Import obstacles");
        saveObstaclesTooltip = new Tooltip();
        saveObstaclesTooltip.setText("Save obstacles");
        editObstacleHeightTooltip = new Tooltip();
        editObstacleHeightTooltip.setText("Enter the obstacle's height here in metres");

        addAirportPopup = createAddAirportPopup();
        addRunwayPopup = createAddRunwayPopup();
        addObstaclePopup = createAddObstaclePopup();
        exportPopup = new ExportPopup(primaryStage, airportConfigs, userDefinedObstacles, fileIO);


        rootTabPane.setVisible(false);

        //Set up color pickers in the view tab
        topDownColorPicker.setValue(Color.GOLD);
        sideOnColorPicker.setValue(Color.SKYBLUE);

        topDownColorPicker.setOnAction(event -> {
            if (runwayRenderer != null) {
                runwayRenderer.setTopDownBackgroundColor(topDownColorPicker.getValue());
            }
        });


        sideOnColorPicker.setOnAction(event -> {
            if (runwayRendererSideView != null) {
                runwayRendererSideView.setSideOnBackgroundColor(sideOnColorPicker.getValue());
            }
        });

        //Set up checkboxes in the View tab
        renderRunwayLabelLinesChkbx.setSelected(true);
        renderRunwayLabelLinesChkbx.setSelected(true);
        renderWindCompass.setSelected(true);

        renderRunwayLabelLinesChkbx.selectedProperty().addListener(state -> {
            runwayRenderer.setRenderLabelLines(renderRunwayLabelLinesChkbx.selectedProperty().get());
        });

        renderRunwayRotatedChkbx.selectedProperty().addListener(state -> {
            runwayRenderer.setRenderRunwayRotated(renderRunwayRotatedChkbx.selectedProperty().get());
        });

        renderWindCompass.selectedProperty().addListener(state -> {
            runwayRenderer.setRenderWindCompass(renderWindCompass.selectedProperty().get());
        });

        //Set up the slider controlling the zoom in the View tab
        zoomSlider.setMin(RunwayRenderer.MIN_ZOOM);
        zoomSlider.setMax(RunwayRenderer.MAX_ZOOM);
        zoomSlider.setBlockIncrement(RunwayRenderer.ZOOM_STEP);

        zoomSlider.valueProperty().addListener(event -> {
            runwayRenderer.setZoom(zoomSlider.getValue());
            /*String style = String.format("-fx-background-color: linear-gradient(to right, #1b88bb %d%%, #ffffff %d%%);", (int)zoomSlider.getValue(),(int)zoomSlider.getValue());
            trackPane.setStyle(style);*/
            notifyUpdate("Zoom : " + runwayRenderer.getZoomPercentage() + "%");
        });

        trackPane = (StackPane) zoomSlider.lookup(".track");

        zoomSlider.valueProperty().addListener((ov, old_val, new_val) -> {
            double currentVal = (double) new_val;
            double percentage = (currentVal - RunwayRenderer.MIN_ZOOM) / (RunwayRenderer.MAX_ZOOM - RunwayRenderer.MIN_ZOOM);
            int roundedPercentage = (int) (percentage * 100);
            int leftPercentage = roundedPercentage;
            int rightPercentage = 100 - leftPercentage;
            String style = String.format("-fx-background-color: linear-gradient(to right, #1b88bb %d%%, #ffffff %d%%);", leftPercentage, leftPercentage);
            trackPane.setStyle(style);
        });

        trackPane.setStyle("-fx-background-color: linear-gradient(to right, -fx-primary-color 0%, #ffffff 0%);");


        runwaySelect.setId("runwayComboBox");
        runwaySelect.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                System.out.println("Currently selected airport : " + airportSelect.getSelectionModel().selectedItemProperty().getValue());
                AirportConfig ac = airportConfigs.get(airportSelect.getSelectionModel().selectedItemProperty().getValue());
                for (String runwayPairName : ac.getRunways().keySet()) {
                    if (runwayPairName.equals(newValue)) {
                        RunwayPair selectedRunwayPair = ac.getRunways().get(runwayPairName);
                        updateRunwayInfoLabels(selectedRunwayPair);
                        updateThresholdList(selectedRunwayPair);
                        currentlySelectedRunway = selectedRunwayPair;
                        runwayRenderer = new RunwayRenderer(currentlySelectedRunway, canvas.getGraphicsContext2D());
                        runwayRenderer.render();
                        //selectedRunwayPair.getR1().render(canvas.getGraphicsContext2D());

                        runwayRendererSideView = new RunwayRenderer(currentlySelectedRunway, sideviewCanvas.getGraphicsContext2D(), true);
                        runwayRendererSideView.renderSideview();

                        rootTabPane.setVisible(true);
                        zoomSlider.setValue(runwayRenderer.getZoom());

                        LiveWindService liveWindService = new LiveWindService();
                        liveWindService.setLatitude(ac.getLatitude());
                        liveWindService.setLongitude(ac.getLongitude());
                        liveWindService.setOnSucceeded(t -> {
                            Map<String, Double> result = (HashMap<String, Double>) t.getSource().getValue();
                            System.out.println("We have a result!");
                            windlLbl.setText("Wind speed:  " + result.get("speed") + "km/h");
                            runwayRenderer.setWindAngle(result.get("direction"));
                        });
                        liveWindService.start();
                        break;
                    }
                }
            }
        });


        airportSelect.setId("airportComboBox");
        airportSelect.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Here");
            System.out.println((String) newValue);
            if (newValue == null) {
                System.out.println("Selection cleared");
                return;
            }
            updateRunwaySelect((String) newValue);
        });


        addObstacleBtn = new Button("Add Button");
        addObstacleBtn.setOnMouseClicked(event -> {
            System.out.println("Add obstacle");
            if (validateObstaclesForm()) {
                addObstacle(obstacleNameTxt.getText(), Double.parseDouble(obstacleHeightTxt.getText()));
                updateObstaclesList();
            }
        });

        //Icons in the obstacles tab
        int iconSize = 18;

        ImageView popAddObstacle = new ImageView(new Image(getClass().getResourceAsStream("/rec/popAddObstacle.png")));
        popAddObstacle.setFitHeight(iconSize);
        popAddObstacle.setFitWidth(iconSize);
        popAddObstacleBtn.setGraphic(popAddObstacle);

        popAddObstacleBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Bounds bounds = popAddObstacleBtn.localToScreen(popAddObstacleBtn.getBoundsInLocal());
                addObstaclePopup.show(primaryStage);
                addObstaclePopup.setAnchorX(bounds.getMaxX() - addObstaclePopup.getWidth() / 2);
                addObstaclePopup.setAnchorY(bounds.getMinY() - addObstaclePopup.getHeight());
            }
        });

        ImageView editObstacleImgView = new ImageView(new Image(getClass().getResourceAsStream("/rec/load.png")));
        editObstacleImgView.setFitWidth(iconSize);
        editObstacleImgView.setFitHeight(iconSize);
        editObstacleBtn.setGraphic(editObstacleImgView);

        editObstacleBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                File file = fileIO.fileChooser.showOpenDialog(primaryStage);
                if (file == null) {
                    return;
                }
                Collection<Obstacle> importedObstacles = fileIO.readObstacles(file.getPath());
                importedObstacles.forEach(obstacle -> {
                    addObstacle(obstacle);
                });
                updateObstaclesList();

                notifyUpdate("Obstacles loaded");
            }
        });

        ImageView deleteObstacleImgView = new ImageView(new Image(getClass().getResourceAsStream("/rec/delete.png")));
        deleteObstacleImgView.setFitWidth(iconSize);
        deleteObstacleImgView.setFitHeight(iconSize);


        ImageView saveObstacleImgView = new ImageView(new Image(getClass().getResourceAsStream("/rec/save.png")));
        saveObstacleImgView.setFitHeight(iconSize);
        saveObstacleImgView.setFitWidth(iconSize);
        saveObstacleBtn.setGraphic(saveObstacleImgView);

        //TODO only save if there are obstacles to save - also show error message or notif or something
        saveObstacleBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });

        saveObstacleBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Save obstacles");
                File file = fileIO.fileChooser.showSaveDialog(primaryStage);
                if (file != null) {
                    fileIO.write(userDefinedObstacles.values(), file.getPath());
                    notifyUpdate("Obstacles saved");
                }

                /*userDefinedObstaclesLV.getItems().forEach((name) -> {
                    System.out.println(name);
                });*/

            }
        });

        // Button in Settings tab for enabling/disabling tooltips
        manageTooltipsBtn = (Button) primaryStage.getScene().lookup("#manageTooltipsBtn");
        manageTooltipsBtn.setOnMouseClicked(event -> {
            if (manageTooltipsBtn.getText().equals("Disable tooltips")) {
                manageTooltipsBtn.setText("Enable tooltips");
                disableTooltips();
            } else {
                manageTooltipsBtn.setText("Disable tooltips");
                enableTooltips();
            }
        });


        highlightTodaBtn = (Button) primaryStage.getScene().lookup("#highlightTodaBtn");
        highlightTodaBtn.setOnMouseClicked(event -> {
            runwayRenderer.setCurrentlyHighlightedParam(RunwayRenderer.RunwayParams.TODA);

            notifyUpdate("TODA Highlighted");
        });

        highlightToraBtn = (Button) primaryStage.getScene().lookup("#highlightToraBtn");
        highlightToraBtn.setOnMouseClicked(event -> {
            runwayRenderer.setCurrentlyHighlightedParam(RunwayRenderer.RunwayParams.TORA);

            notifyUpdate("TORA Highlighted");
        });

        highlightAsdaBtn = (Button) primaryStage.getScene().lookup("#highlightAsdaBtn");
        highlightAsdaBtn.setOnMouseClicked(event -> {
            runwayRenderer.setCurrentlyHighlightedParam(RunwayRenderer.RunwayParams.ASDA);

            notifyUpdate("ASDA Highlighted");
        });

        highlightLdaBtn = (Button) primaryStage.getScene().lookup("#highlightLdaBtn");
        highlightLdaBtn.setOnMouseClicked(event -> {
            runwayRenderer.setCurrentlyHighlightedParam(RunwayRenderer.RunwayParams.LDA);

            notifyUpdate("LDA Highlighted");
        });


        addAirportBtn = (Button) primaryStage.getScene().lookup("#addAirportBtn");
        addAirportBtn.setOnMouseClicked(event -> {
            System.out.println("Add airport");
            addAirportPopup.show();
        });

        addRunwayBtn = (Button) primaryStage.getScene().lookup("#addRunwayBtn");
        addRunwayBtn.setOnMouseClicked(event -> {
            System.out.println("Add runway");
            addRunwayPopup.show();
        });

        //Settings tab
        saveSettingsBtn = (Button) primaryStage.getScene().lookup("#saveSettingsBtn");
        saveSettingsBtn.setOnMouseClicked(value -> {
            System.out.println("Clicked on save settings");
            //new Notification("hey").show(primaryStage, 10, 10);

        });


        //Calculations Pane - selection view
        final int HBOX_SPACING = 5;
        Insets calculationsInsets = new Insets(5, 20, 0, 0);
        //calculationsPane = (Pane) primaryStage.getScene().lookup("#calculationsPane");
        calculationsPane.getStylesheets().add("styles/global.css");
        calculationsPane.getStylesheets().add("styles/calculations.css");

        obstacleSelect = new ComboBox();
        obstacleSelect.setVisibleRowCount(10);
        obstacleSelect.setId("obstacleComboBox");

        obstacleSelect.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                    if (obstacleSelect.getSelectionModel().isEmpty()) {
                        selectedObstacleHeightTF.clear();
                    } else {
                        String selectedObstacleName = (String) newValue;
                        selectedObstacleHeightTF.setText(allObstaclesSorted.get(selectedObstacleName).getHeight() + "m");
                    }

                }
        );


        thresholdSelect = new ComboBox();
        thresholdSelect.setVisibleRowCount(5);
        thresholdSelect.setId("thresholdComboBox");
        centrelineDistanceLbl = new Label("Distance from runway centreline");
        runwayThresholdLbl = new Label("Distance from runway threshold");
        obstacleSelectLbl = new Label("Select obstacle");
        thresholdSelectLbl = new Label("Select threshold");

        centrelineTF = new TextField();
        distanceFromThresholdTF = new TextField();

        // Components for the selected obstacle's height in Redeclaration tab
        selectedObstacleHeightLbl = new Label("Height of the obstacle");
        selectedObstacleHeightTF = new TextField();
        selectedObstacleHeightTF.setEditable(false);

        calculateBtn = new Button("Calculate");
        calculateBtn.setId("calcButton");
        calculateBtn.getStyleClass().add("primaryButton");

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

        heightHBox = new HBox(HBOX_SPACING);
        VBox.setMargin(heightHBox, calculationsInsets);
        Region heightHBoxRegion = getHGrowingRegion();
        heightHBox.getChildren().add(selectedObstacleHeightLbl);
        heightHBox.getChildren().add(heightHBoxRegion);
        heightHBox.getChildren().add(selectedObstacleHeightTF);


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
        calculationsRootBox.getChildren().add(heightHBox);
        calculationsRootBox.getChildren().add(centerlineHBox);
        calculationsRootBox.getChildren().add(thresholdSelectHBox);
        calculationsRootBox.getChildren().add(thresholdHBox);

        calculationsRootBox.getChildren().add(calculateBtnVBox);
        calculationsRootBox.getStyleClass().add("customCol");


        centreLineRequiredLabel = (Label) primaryStage.getScene().lookup("#centreLineRequiredLabel");
        thresholdDistanceRequiredLabel = (Label) primaryStage.getScene().lookup("#thresholdDistanceRequiredLabel");
        thresholdRequiredLabel = (Label) primaryStage.getScene().lookup("#thresholdRequiredLabel");
        obstacleRequiredLabel = (Label) primaryStage.getScene().lookup("#obstacleRequiredLabel");

        calculateBtn.setOnMouseClicked(event -> {

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
                Obstacle selectedObstacle = (Obstacle) predefinedObstaclesLV.getSelectionModel().getSelectedItem();

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
                Calculations calculations2 = new Calculations(otherConfig);
                int distanceFromThreshold = Integer.valueOf(distanceFromThresholdTF.getText());
                int distanceFromCenterline = Integer.valueOf(centrelineTF.getText());

                // We take the greater TORA to be the length of the runway
                int runwayLength = 0;
                if (runwayConfig.getTORA() > otherConfig.getTORA()) {
                    runwayLength = runwayConfig.getTORA();
                } else {
                    runwayLength = otherConfig.getTORA();
                }

                int distanceFromOtherThreshold = runwayLength - runwayConfig.getDisplacementThreshold() - distanceFromThreshold - otherConfig.getDisplacementThreshold();


                // I compare the distances from each threshold. Whichever threshold the obstacle is closer to, that logical runway is used for taking off away
                CalculationResults results = null;
                CalculationResults results2 = null;
                RunwayConfig recalculatedParams = null;
                RunwayConfig recalculatedParams2 = null;
                if (distanceFromThreshold < distanceFromOtherThreshold) {
                    // Closer to runwayConfig so runwayConfig is used for taking off away
                    results = calculations.recalculateParams(currentlySelectedObstacle, distanceFromThreshold, distanceFromCenterline, "AWAY", runwayLength);
                    recalculatedParams = results.getRecalculatedParams();
                    results2 = calculations2.recalculateParams(currentlySelectedObstacle, distanceFromOtherThreshold, distanceFromCenterline, "TOWARDS", runwayLength);
                    recalculatedParams2 = results2.getRecalculatedParams();
                } else {
                    // Closer to otherConfig so otherConfig is used for taking off away
                    results = calculations.recalculateParams(currentlySelectedObstacle, distanceFromThreshold, distanceFromCenterline, "TOWARDS", runwayLength);
                    recalculatedParams = results.getRecalculatedParams();
                    results2 = calculations2.recalculateParams(currentlySelectedObstacle, distanceFromOtherThreshold, distanceFromCenterline, "AWAY", runwayLength);
                    recalculatedParams2 = results2.getRecalculatedParams();
                }

                System.out.println("calculation details");
                System.out.println(results.getCalculationDetails());
                System.out.println(results2.getCalculationDetails());

                //Generate summary string which designates the calculations (eg. A320 50m from 27R threshold)
                String summary = obstacleName + " " + distanceFromThreshold + "m from " + thresholdName + " threshold";
                System.out.println("Just performed calculations on the following situation :");
                System.out.println(summary);

                // Printing results into the breakdown of calculations text box
                String resultsDetails = results.getCalculationDetails() + "\n" + results2.getCalculationDetails();
                calculationDetails.setText(resultsDetails);
                printer.setCalculations(resultsDetails);
                printer.setCalculationsHeading(summary);

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


        });

        //Calculations Pane - calculation results view
        breakdownCalcLbl = new Label("Breakdown of the calculations");
        calculationDetails = new TextArea();
        calculationDetails.setEditable(false);
        calculationDetails.setId("calcBreakdown");
        calculationResultsGrid = new GridPane();
        calculationResultsGrid.getStylesheets().add("styles/runwayTable.css");
        calculationResultsGrid.getStyleClass().add("paintMe");
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
                if (event.getDeltaY() > 0) {
                    runwayRenderer.incZoom();
                } else if (event.getDeltaY() < 0) {
                    runwayRenderer.decZoom();
                }
                zoomSlider.setValue(runwayRenderer.getZoom());
                //runwayRenderer.updateZoom((int) (event.getDeltaY()/2));
            }
        });

        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MouseDragTracker.getInstance().startDrag((int) event.getX(), (int) event.getY());
                /*double currentAngle = runwayRenderer.getWindAngle();
                System.out.println("Current angle = " + currentAngle);
                currentAngle += Math.PI/4;
                System.out.println("After addition = " + currentAngle);
                runwayRenderer.setWindAngle(currentAngle);*/
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
            }Font.font("Verdana", FontWeight.BOLD, 14)
        });
        canvas.heightProperty().bind(canvasBorderPane.heightProperty());*/

        //calculationResultsGrid.setHgap(20);
        //Add all the labels, col by col,  to create a table
        calculationResultsGrid.setId("smallRunwayGrid");

        //Col 0 : the value names (TODA, TORA, ASDA, LDA)
        calculationResultsGrid.add(new Label(), 0, 0);
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

/*
        //Col 0 : the value names (TODA, TORA, ASDA, LDA)
        calculationResultsGrid.add(new Pane(toraRowLbl), 0, 1);
        calculationResultsGrid.add(new Pane(todaRowLbl), 0, 2);
        calculationResultsGrid.add(asdaRowLbl, 0, 3);
        calculationResultsGrid.add(ldaRowLbl, 0, 4);

        //Col 1 : the original values
        calculationResultsGrid.add(new Pane(originalValuesGridLbl), 1, 0);
        calculationResultsGrid.add(new Pane(originalTora), 1, 1);
        calculationResultsGrid.add(new Pane(originalToda), 1, 2);
        calculationResultsGrid.add(originalAsda, 1, 3);
        calculationResultsGrid.add(originalLda, 1, 4);

        //Col 2 : the recalculated values
        calculationResultsGrid.add(new Pane(recalculatedlValuesGridLbl), 2, 0);
        calculationResultsGrid.add(new Pane(recalculatedTora), 2, 1);
        calculationResultsGrid.add(new Pane(recalculatedToda), 2, 2);
        calculationResultsGrid.add(recalculatedAsda, 2, 3);
        calculationResultsGrid.add(recalculatedLda, 2, 4);

*/
        //Style the newly created and populated table
        ArrayList<Pair<Node, Point>> wrappedUpNodes = new ArrayList<>();
        ObservableList<Node> nodesObservable = calculationResultsGrid.getChildrenUnmodifiable();
        List<Node> nodes = nodesObservable.subList(0, nodesObservable.size());
        for (Node node : nodes) {
            int rowIndex = GridPane.getRowIndex(node);


            wrappedUpNodes.add(new Pair<>(node, new Point(GridPane.getColumnIndex(node), GridPane.getRowIndex(node))));

            if (rowIndex == 0) {
                node.getStyleClass().add("dark");
            } else if (rowIndex % 2 == 0) {
                node.getStyleClass().add("lighter");
            } else {
                node.getStyleClass().add("light");
            }
        }


        for (Pair<Node, Point> wrappedUpNodeToAdd : wrappedUpNodes) {
            //Remove this simple labels and wrap them in expanding panes
            Pane wrapper = new Pane(wrappedUpNodeToAdd.getKey());

            //style wrapper using the node's style
            wrapper.getStyleClass().addAll(wrappedUpNodeToAdd.getKey().getStyleClass());

            //Grow the panes to eliminate gaps between cells of the gridpane
            GridPane.setHgrow(wrapper, Priority.ALWAYS);

            calculationResultsGrid.add(wrapper, wrappedUpNodeToAdd.getValue().x, wrappedUpNodeToAdd.getValue().y);
        }

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

        predefinedObstaclesLV.getStyleClass().add("obstacleList");
        predefinedObstaclesLV.setStyle("-fx-font-size: 1.2em ;");
        userDefinedObstaclesLV.getStyleClass().add("obstacleList");
        userDefinedObstaclesLV.setStyle("-fx-font-size: 1.2em ;");

        predefinedObstaclesLV.setCellFactory(lv -> new ObstacleCell(predefinedObstaclesLV));
        userDefinedObstaclesLV.setCellFactory(lv -> new ObstacleCell(userDefinedObstaclesLV));

        predefinedObstaclesLV.addEventHandler(DeleteEvent.DELETE_EVENT_TYPE, event -> {
            System.out.println("List view got a delete for obstacle " + event.getObstacleName());
            displayDeletePrompt(event.getObstacle(), ObstacleList.PREDEFINED);
        });

        predefinedObstaclesLV.addEventHandler(ObstacleInfoEvent.OBSTACLE_INFO_EVENT_TYPE, event -> {
            System.out.println("Obstacle info request received for obstacle " + event.getObstacleName());
            showObstacleDetails(event.getObstacle(), predefinedObstaclesLV, null, primaryStage, ObstacleList.PREDEFINED);
        });

        userDefinedObstaclesLV.addEventHandler(DeleteEvent.DELETE_EVENT_TYPE, event -> {
            System.out.println("User defined list view got a delete for obstacle " + event.getObstacleName());
            displayDeletePrompt(event.getObstacle(), ObstacleList.USER_DEFINED);
        });

        userDefinedObstaclesLV.addEventHandler(ObstacleInfoEvent.OBSTACLE_INFO_EVENT_TYPE, event -> {
            System.out.println("Obstacle info request received for obstacle " + event.getObstacleName());
            showObstacleDetails(event.getObstacle(), userDefinedObstaclesLV, null, primaryStage, ObstacleList.USER_DEFINED);
        });

        predefinedObstaclesLV.addEventHandler(CellHoverEvent.CELL_HOVER_EVENT_TYPE, event -> {

        });

        predefinedObstaclesLV.setOnMouseClicked(click -> {

            if (!userDefinedObstaclesLV.getSelectionModel().isEmpty()) {
                int selectedUserItem = userDefinedObstaclesLV.getSelectionModel().getSelectedIndex();
                userDefinedObstaclesLV.getSelectionModel().clearSelection(selectedUserItem);
                System.out.println("Obstacle in user-defined list was selected, has now been deselected");
            }

            obstacleSelect.setValue(((Obstacle) predefinedObstaclesLV.getSelectionModel().getSelectedItem()).getName());

        });

        userDefinedObstaclesLV.setOnMouseClicked(click -> {

            if (!predefinedObstaclesLV.getSelectionModel().isEmpty()) {
                int selectedUserItem = predefinedObstaclesLV.getSelectionModel().getSelectedIndex();
                predefinedObstaclesLV.getSelectionModel().clearSelection(selectedUserItem);
                System.out.println("Obstacle in predefined list was selected, has now been deselected");
            }

            obstacleSelect.setValue(((Obstacle) userDefinedObstaclesLV.getSelectionModel().getSelectedItem()).getName());

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
                if (newValue == Animation.Status.STOPPED) {
                    tabPane.getSelectionModel().select(1);
                }
            }
        });

        tabPane.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if ((int) newValue == 0) {
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
                planeImg.setLayoutY(planePane.getHeight() - 1.4 * planeImg.getFitHeight());
                System.out.println(planeImg.getY());
            }
        });


        //Listeners for the print and export button on the top right side of the GUI
        Pane printBtnPane = (Pane) primaryStage.getScene().lookup("#printBtnPane");
        Pane exportBtnPane = (Pane) primaryStage.getScene().lookup("#exportBtnPane");

        //set cursor to make pane look like a button - not sure what the difference between HAND and OPEN_HAND is, look the same under xfce ubuntu
        printBtnPane.setCursor(Cursor.HAND);
        exportBtnPane.setCursor(Cursor.OPEN_HAND);

        printBtnPane.setOnMouseClicked(event -> {
            System.out.println("Print report");
            printer.print();
        });

        exportBtnPane.setOnMouseClicked(event -> {
            //TODO implement exporting, and link back to here when done
            System.out.println("Export data");
            exportPopup.show();
        });


        loadAirportButton = (Button) primaryStage.getScene().lookup("#loadAirportBtn");
        /*loadAirportButton.getStyleClass().add("loadBtn");
        loadAirportButton.getStylesheets().add("styles/fileTab.css");*/
        loadAirportButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Click !");
                File xmlFileToLoad = fileIO.fileChooser.showOpenDialog(primaryStage);
                if (xmlFileToLoad == null) {
                    System.err.println("User did not select a file");
                    return;
                }
                //setTabMinHeight(double v)
                takeOffTransition.play();
                System.out.println("Loading " + xmlFileToLoad.getName());
                AirportConfig ac = fileIO.read(xmlFileToLoad.getPath());
                airportConfigs.put(ac.getName(), ac);
                updateAirportSelects();

                notifyUpdate("Airport config loaded");
                //tabPane.getSelectionModel().select(1);
            }
        });

        startBtn = (Button) primaryStage.getScene().lookup("#startBtn");
        /*startBtn.getStyleClass().add("loadBtn");
        startBtn.getStylesheets().add("styles/fileTab.css");*/
        startBtn.setOnMouseClicked(event -> {
            System.out.println("Tab Switched!");
            takeOffTransition.play();
            //tabPane.getSelectionModel().select(1);
        });

        planePane.hoverProperty().addListener((observable, oldValue, rotate) -> {
            //Ignore the hover property when the plane is taking off or landing
            if (takeOffTransition.statusProperty().get().equals(Animation.Status.RUNNING) || landTransition.statusProperty().get().equals(Animation.Status.RUNNING)) {
                return;
            }

            //Rotate on mouse over, return to original state on mouse leave
            if (rotate) {
                rotateTransition.play();
            } else {
                reverseRotateTransition.play();
            }
        });

        updateAirportSelects();

        printer = new Printer(primaryStage);
        printer.setRunway(canvas);
        printer.setOriginalRecalculatedPane(new Pair<>(viewCalculationResultsVBox, calculationResultsGrid));

        heightEditTF = new TextField();

        enableTooltips();
    }

    private Region getHGrowingRegion() {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }

    public void updateAirportSelects() {
        runwaySelect.getItems().clear();
        airportSelect.getItems().clear();
        addRunwayAirportSelect.getItems().clear();

        String[] names = airportConfigs.keySet().toArray(new String[0]);
        Arrays.sort(names);

        for (String name : names) {
            AirportConfig ac = airportConfigs.get(name);
            airportSelect.getItems().add(name);
            addRunwayAirportSelect.getItems().add(name);
        }
    }

    public void updateRunwaySelect(String airportName) {
        runwaySelect.getItems().clear();
        AirportConfig ac = airportConfigs.get(airportName);
        for (String runwayPairName : ac.getRunways().keySet()) {
            runwaySelect.getItems().add(runwayPairName);
        }
    }

    public Popup createAddObstaclePopup() {
        Popup popup = new Popup();

        VBox rootBox = new VBox(20);
        rootBox.getStylesheets().add("styles/global.css");
        rootBox.getStylesheets().add("styles/layoutStyles.css");
        rootBox.getStylesheets().add("styles/obstacles.css");
        HBox emptyNameBox = new HBox();
        HBox emptyHeightBox = new HBox();
        HBox nameBox = new HBox(20);
        HBox heightBox = new HBox(20);
        HBox buttonsBox = new HBox();
        Region nameRegion = new Region();
        Region heightRegion = new Region();
        Region buttonsRegion = new Region();
        Label nameLbl, heightLbl, nameRequiredLbl, heightRequiredLbl;
        Button addObstacleBtn, cancelBtn;

        nameLbl = new Label("Name");
        nameLbl.getStyleClass().add("popUpTitles");
        nameLbl.getStylesheets().add("styles/layoutStyles.css");

        heightLbl = new Label("Height");
        heightLbl.getStyleClass().add("popUpTitles");
        heightLbl.getStylesheets().add("styles/layoutStyles.css");

        addObstacleNameTF = new TextField();
        addObstacleHeightTF = new TextField();


        addObstacleNameTF.getStyleClass().add("redErrorPromptText");
        addObstacleNameTF.getStylesheets().add("styles/obstacles.css");
        addObstacleHeightTF.getStyleClass().add("redErrorPromptText");
        addObstacleHeightTF.getStylesheets().add("styles/obstacles.css");

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
        nameBox.getChildren().add(addObstacleNameTF);

        heightBox.getChildren().add(heightLbl);
        heightBox.getChildren().add(heightRegion);
        heightBox.getChildren().add(addObstacleHeightTF);

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
                if (addObstacleNameTF.getText().isEmpty()) {
                    nameRequiredLbl.setText("                                             This field is required");
                    if (!rootBox.getChildren().contains(emptyNameBox)) {
                        rootBox.getChildren().add(0, emptyNameBox);
                    }
                    addObstacleNameTF.setPromptText("");
                } else {
                    if (rootBox.getChildren().contains(emptyNameBox)) {
                        rootBox.getChildren().remove(emptyNameBox);
                    }
                    nameRequiredLbl.setText("");
                }
                if (addObstacleHeightTF.getText().isEmpty()) {
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
                    addObstacleHeightTF.setPromptText("");
                } else {
                    if (rootBox.getChildren().contains(emptyHeightBox)) {
                        rootBox.getChildren().remove(emptyHeightBox);
                    }
                    heightRequiredLbl.setText("");
                }

                // Checking for valid obstacle name and valid obstacle height
                if (validateDoubleForm(new ArrayList<>(Arrays.asList(addObstacleHeightTF.getText()))) && !addObstacleNameTF.getText().isEmpty()) {
                    boolean matchFound = false;
                    for (String obstacleName : allObstaclesSorted.keySet()) {
                        if (addObstacleNameTF.getText().equals(obstacleName)) {
                            displayOverwritePrompt(obstacleName, allObstaclesSorted.get(obstacleName).getHeight(), Double.parseDouble(addObstacleHeightTF.getText()));
                            matchFound = true;
                            break;
                        }
                    }

                    if (!matchFound) {
                        System.out.println("Add obstacle");
                        addObstacle(addObstacleNameTF.getText(), Double.parseDouble(addObstacleHeightTF.getText()));
                        addObstacleNameTF.clear();
                        addObstacleHeightTF.clear();
                        addObstacleNameTF.setPromptText("");
                        addObstacleHeightTF.setPromptText("");
                        updateObstaclesList();
                        addObstaclePopup.hide();

                        //notify user obstacle was added
                        notifyUpdate("Obstacle added");
                    }

                } else if (!addObstacleHeightTF.getText().isEmpty() && !validateDoubleForm(new ArrayList<>(Arrays.asList(addObstacleHeightTF.getText())))) {
                    addObstacleHeightTF.clear();
                    addObstacleHeightTF.setPromptText("Invalid obstacle height!");
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
                addObstacleNameTF.clear();
                addObstacleNameTF.setPromptText("");
                addObstacleHeightTF.clear();
                addObstacleHeightTF.setPromptText("");
                addObstaclePopup.hide();
            }
        });

        popup.getContent().add(rootBox);


        return popup;
    }


    private void showObstacleDetails(Obstacle obstacle, ListView listView, MouseEvent event, Stage primaryStage, ObstacleList sourceList) {

        editingObstacle = false;

        //TODO rework sorting of the obstacles

        Popup detailsPopUp = new Popup();

        VBox box = new VBox(100);
        box.getStyleClass().add("popup");
        box.getStylesheets().add("styles/global.css");
        box.getStylesheets().add("styles/layoutStyles.css");

        HBox subBox = new HBox(100);

        Label detailsLabel = new Label("Overview of obstacle details");
        Label nameLabel = new Label("Name:");
        Label nameContentLabel = new Label(obstacle.getName());
        TextField nameEditTF = new TextField();
        nameEditTF.setPrefWidth(240);
        Label heightLabel = new Label("Height:");
        Label heightContentLabel = new Label(obstacle.getHeight() + "m");
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

        /*Node eventSource = (Node) event.getSource();
        Bounds sourceNodeBounds = eventSource.localToScreen(eventSource.getBoundsInLocal());*/
        /*detailsPopUp.setX(sourceNodeBounds.getMinX() - 310.0);
        detailsPopUp.setY(sourceNodeBounds.getMaxY() - 180.0);*/
        detailsPopUp.setX(primaryStage.getWidth() / 2);
        detailsPopUp.setY(primaryStage.getHeight() / 2);
        System.out.println("Size of window is " + primaryStage.getWidth() + " by " + primaryStage.getHeight());
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
                if (validateDoubleForm(new ArrayList<>(Arrays.asList(heightEditTF.getText()))) && !nameEditTF.getText().isEmpty()) {
                    System.out.println("Add -edited- obstacle");
                    if (sourceList.equals("predefined")) {
                        predefinedObstaclesSorted.remove(obstacle.getName());
                        allObstaclesSorted.remove(obstacle.getName());
                        obstacle.setName(nameEditTF.getText());
                        obstacle.setHeight(Double.valueOf(heightEditTF.getText()));
                        predefinedObstaclesSorted.put(obstacle.getName(), obstacle);
                        allObstaclesSorted.put(obstacle.getName(), obstacle);

                        nameContentLabel.setText(obstacle.getName());
                        heightContentLabel.setText(Double.toString(obstacle.getHeight()));

                        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
                        listView.getItems().set(selectedIndex, obstacle);

                        updateObstaclesList();
                        detailsPopUp.hide();
                    } else if (sourceList.equals("userDefined")) {
                        userDefinedObstacles.remove(obstacle.getName());
                        allObstaclesSorted.remove(obstacle.getName());
                        obstacle.setName(nameEditTF.getText());
                        obstacle.setHeight(Double.valueOf(heightEditTF.getText()));
                        userDefinedObstacles.put(obstacle.getName(), obstacle);
                        allObstaclesSorted.put(obstacle.getName(), obstacle);

                        nameContentLabel.setText(obstacle.getName());
                        heightContentLabel.setText(Double.toString(obstacle.getHeight()));

                        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
                        listView.getItems().set(selectedIndex, obstacle);

                        updateObstaclesList();
                        detailsPopUp.hide();
                    }

                } else if (!heightEditTF.getText().isEmpty() && !validateDoubleForm(new ArrayList<>(Arrays.asList(heightEditTF.getText())))) {
                    heightEditTF.clear();
                    heightEditTF.setPromptText("Invalid obstacle height!");
                }
            }
        });

        editButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                editingObstacle = !editingObstacle;
                if (editingObstacle) {
                    //Edit mode

                    nameLabel.setText("Name");
                    heightLabel.setText("Height");

                    nameHBox.setSpacing(55);
                    heightHBox.setSpacing(46);
                    subBox.setSpacing(138);

                    nameHBox.getChildren().remove(nameContentLabel);
                    nameHBox.getChildren().add(nameEditTF);

                    heightHBox.getChildren().remove(heightContentLabel);
                    heightHBox.getChildren().add(heightEditTF);

                    nameEditTF.setText(obstacle.getName());
                    heightEditTF.setText(Double.toString(obstacle.getHeight()));

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


    public Stage createAddAirportPopup() {
        Stage stage = new Stage();
        stage.setTitle("Add Airport");

        //Components for the popups
        Button confirmButton = new Button("Add");
        Button cancelButton = new Button("Cancel");
        TextField airportName;
        Label airportNameLbl, airportCodeLbl;
        ListView airportSuggestions;
        airportNameLbl = new Label("Airport Name");
        airportCodeLbl = new Label("Airport Code");
        airportName = new TextField();
        airportName.setEditable(false);

        //Setting the tooltip
        airportCode = new TextField();
        airportCode.setTooltip(airportCodeTooltip);

        airportSuggestions = new ListView();
        airportSuggestions.setMaxHeight(100);

        //Add auto-completion to the airport code
        airportCode.setOnKeyReleased(event -> {
            airportSuggestions.getItems().clear();
            System.out.println("text is " + airportCode.getText());
            if (airportCode.getText().length() > 0){
                airportSuggestions.getItems().addAll(airportDB.getEntries(airportCode.getText()));
                if (airportSuggestions.getItems().size() == 1){
                    if (event.getCode() == KeyCode.BACK_SPACE && airportCode.getText().length() != 3){
                        airportName.clear();
                        return;
                    }
                    String suggestedAirportName = (String) airportSuggestions.getItems().get(0);
                    airportName.setText((String) airportSuggestions.getItems().get(0));
                    airportCode.setText(airportDB.getEntryReversed(suggestedAirportName));
                    airportCode.positionCaret(airportCode.getText().length());
                } else {
                    airportName.clear();
                }
            } else {
                airportSuggestions.getItems().clear();
                airportName.clear();
            }
        });

        //Single-click on the airport suggestions box
        airportSuggestions.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                String selectedAirportName = (String) airportSuggestions.getSelectionModel().getSelectedItem();
                System.out.println(selectedAirportName);
                airportName.setText(selectedAirportName);
                airportCode.setText(airportDB.getEntryReversed(selectedAirportName));
            } else {
                System.out.println("Not a double click");
            }
        });

        //VBox containing confirm and cancel button
        HBox hbox = new HBox();
        hbox.getChildren().add(confirmButton);
        hbox.getChildren().add(cancelButton);
        hbox.setSpacing(10);

        //GridPane - root of the popup
        GridPane gridPane = new GridPane();

        gridPane.getStylesheets().add("styles/global.css");

        gridPane.add(airportCodeLbl, 0, 0);
        gridPane.add(airportCode, 1, 0);
        gridPane.add(airportNameLbl, 0, 1);
        gridPane.add(airportName, 1, 1);
        gridPane.add(airportSuggestions, 1, 2, 2, 1);
        gridPane.add(hbox, 1, 3);
        Scene scene = new Scene(gridPane);

        //Add some spacing around and in between the cells
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(5, 5, 5, 5));

        confirmButton.getStyleClass().add("primaryButton");
        cancelButton.getStyleClass().add("primaryButton");

        //On confirm button, add the airport to the list of known airports
        confirmButton.setOnMouseClicked(event -> {

            if (!airportSuggestions.getSelectionModel().isEmpty()) {
                System.out.println("add airport with name " + airportName.getText() + " and code " + airportCode.getText());
                AirportConfig airportConfig = new AirportConfig(airportName.getText());
                airportConfigs.put(airportConfig.getName(), airportConfig);
                updateAirportSelects();
                addAirportPopup.hide();
                addRunwayPopup.show();
            } else {
                displayAirportErrorPopup();
            }
        });

        //Simply close the popup, discarding the data
        cancelButton.setOnMouseClicked(event -> addAirportPopup.hide());

        stage.setScene(scene);
        return stage;
    }


    public Stage createAddRunwayPopup() {
        Stage stage = new Stage();
        stage.setTitle("Add Runway");

        //Components for the popups
        Label selectAirportLbl = new Label("Select airport");
        Button confirmButton = new Button("Continue");
        confirmButton.getStyleClass().add("primaryButton");
        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("primaryButton");
        TextField runwayDesignatorTF, toraTF, clearwayTF, stopwayTF, displacementThresholdTF, runwayDesignatorTF2, toraTF2, clearwayTF2, stopwayTF2, displacementThresholdTF2;
        Label runwayDesignatorLbl, toraLbl, clearwayLbl, stopwayLbl, displacementThresholdLbl;

        runwayDesignatorLbl = new Label("Runway Designator");
        toraLbl = new Label("TORA");
        clearwayLbl = new Label("Clearway");
        stopwayLbl = new Label("Stopway");
        displacementThresholdLbl = new Label("Displaced Threshold");
        addRunwayAirportSelect = new ComboBox();
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
                runwayDesignatorTF2.setText(getReverseRunwayName(contents));
            } else if (contents.length() == 3) {
                String designatorSide = contents.substring(2, 3).toUpperCase();
                String otherSide = "C";
                System.out.println("this is on side " + designatorSide);
                if (designatorSide.equals("L")) {
                    otherSide = "R";
                } else if (designatorSide.equals("R")) {
                    otherSide = "L";
                }
                runwayDesignatorTF2.setText(getReverseRunwayName(contents.substring(0, 2)) + otherSide);
            }
        });

        //On confirm button, add the airport to the list of known airports
        confirmButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if (validateIntForm(new ArrayList<String>(Arrays.asList(toraTF.getText(), clearwayTF.getText(), stopwayTF.getText(), displacementThresholdTF.getText(), toraTF2.getText(), clearwayTF2.getText(), stopwayTF2.getText(), displacementThresholdTF2.getText())))) {
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

                    RunwayConfig r1 = new RunwayConfig(new RunwayDesignator(runwayDesignator), TORA, TODA, ASDA, LDA, displacedThreshold);
                    r1.setClearway(clearway);
                    r1.setStopway(stopway);
                    RunwayConfig r2 = new RunwayConfig(new RunwayDesignator(runwayDesignator2), TORA2, TODA2, ASDA2, LDA2, displacedThreshold2);
                    r1.setClearway(clearway2);
                    r1.setStopway(stopway2);
                    RunwayPair runwayPair = new RunwayPair(r1, r2);

                    // Show the prompt that shows the summary of the runway values
                    displayRunwayParametersPrompt(runwayPair);

                    AirportConfig selectedAirport = airportConfigs.get(addRunwayAirportSelect.getSelectionModel().getSelectedItem().toString());
                    System.out.println(selectedAirport.toString());

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
        stage.setWidth(700);
        stage.setHeight(390);
        return stage;
    }

    private void displayDeletePrompt(Obstacle obstacle, ObstacleList source) {
        Stage deleteWindow = new Stage();
        deleteWindow.initModality(Modality.APPLICATION_MODAL);
        deleteWindow.setTitle("Delete Obstacle");

        // Components for the delete obstacle window

        Label confirmationLabel = new Label("Are you sure you want to delete " + obstacle.getName() + "?");
        confirmationLabel.setWrapText(true);
        confirmationLabel.setTextAlignment(TextAlignment.CENTER);
        Button cancelDeletion = new Button("Cancel");
        cancelDeletion.getStyleClass().add("primaryButton");
        Button confirmDeletion = new Button("Delete");
        confirmDeletion.getStyleClass().add("primaryButton");

        HBox buttonsBox = new HBox(20);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.getChildren().addAll(confirmDeletion, cancelDeletion);
        VBox windowLayout = new VBox(10);
        windowLayout.getChildren().addAll(confirmationLabel, buttonsBox);
        windowLayout.setAlignment(Pos.CENTER);
        windowLayout.getStylesheets().add("styles/global.css");

        cancelDeletion.setOnAction(e -> deleteWindow.close());
        confirmDeletion.setOnAction(e -> {
            removeObstacle(obstacle, source);
            updateObstaclesList();
            deleteWindow.close();
        });

        Scene scene = new Scene(windowLayout, 400, 150);
        deleteWindow.setScene(scene);
        deleteWindow.showAndWait();
    }

    private void displayOverwritePrompt(String obstacleName, double currentHeight, double newHeight) {
        Stage overwriteWindow = new Stage();
        overwriteWindow.initModality(Modality.APPLICATION_MODAL);
        overwriteWindow.setTitle("Overwrite Obstacle");

        // Components for the overwrite obstacle details window
        Label overwriteLabel = new Label("");
        overwriteLabel.setWrapText(true);
        overwriteLabel.setTextAlignment(TextAlignment.CENTER);
        overwriteLabel.getStyleClass().add("label");
        overwriteLabel.getStylesheets().add("styles/global.css");

        if (predefinedObstaclesSorted.containsKey(obstacleName)) {
            overwriteLabel.setText(obstacleName + " already exists in the list of predefined obstacles. Do you wish to overwrite " +
                    "the current height of " + currentHeight + "m with a new height of " + newHeight + "m?");
        } else {
            overwriteLabel.setText(obstacleName + " already exists in the list of user-defined obstacles. Do you wish to overwrite " +
                    "the current height of " + currentHeight + "m with a new height of " + newHeight + "m?");
        }

        Button cancelOverwrite = new Button("Cancel");
        cancelOverwrite.getStyleClass().add("primaryButton");
        cancelOverwrite.getStylesheets().add("styles/global.css");
        Button confirmOverwrite = new Button("Overwrite");
        confirmOverwrite.getStyleClass().add("primaryButton");
        confirmOverwrite.getStylesheets().add("styles/global.css");

        HBox buttonsBox = new HBox(20);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.getChildren().addAll(confirmOverwrite, cancelOverwrite);
        VBox windowLayout = new VBox(10);
        windowLayout.getChildren().addAll(overwriteLabel, buttonsBox);
        windowLayout.setAlignment(Pos.CENTER);

        cancelOverwrite.setOnAction(e -> overwriteWindow.close());

        confirmOverwrite.setOnAction(e -> {

            allObstaclesSorted.remove(obstacleName);
            Obstacle modifiedObstacle = new Obstacle(obstacleName, newHeight);
            allObstaclesSorted.put(obstacleName, modifiedObstacle);

            if (predefinedObstaclesSorted.containsKey(obstacleName)) {
                predefinedObstaclesSorted.remove(obstacleName);
                predefinedObstaclesSorted.put(obstacleName, modifiedObstacle);
            } else {
                userDefinedObstacles.remove(obstacleName);
                userDefinedObstacles.put(obstacleName, modifiedObstacle);
            }

            updateObstaclesList();
            addObstacleNameTF.clear();
            addObstacleHeightTF.clear();
            addObstaclePopup.hide();
            notifyUpdate("Obstacle overwritten");
            overwriteWindow.close();
        });

        Scene scene = new Scene(windowLayout, 420, 170);
        overwriteWindow.setScene(scene);
        overwriteWindow.showAndWait();
    }

    private void displayAirportErrorPopup() {
        Stage errorWindow = new Stage();
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

    //TODO Make the Confirm/Cancel buttons work
    //TODO other issues - deleting obstacles messes up the list of obstacles in Redeclaration,
    // "Save changes" button doesn't work when editing
    // When an obstacle is deleted, it still shows up as selected obstacle in the list of obstacles in Redeclaration
    private void displayRunwayParametersPrompt(RunwayPair runwayPair) {
        Stage runwayWindow = new Stage();
        runwayWindow.initModality(Modality.APPLICATION_MODAL);
        runwayWindow.setTitle("Add Runway");

        Label summaryLbl = new Label("Summary of parameters for the new runway at " + addRunwayAirportSelect.getSelectionModel().getSelectedItem().toString() + " airport:");
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


    private void resetCalculationsTab() {
        calculationsPane.getChildren().remove(viewCalculationResultsVBox);
        calculationsPane.getChildren().add(calculationsRootBox);
    }


    private void switchCalculationsTabToView() {
        centreLineRequiredLabel.setText("");
        thresholdDistanceRequiredLabel.setText("");
        thresholdRequiredLabel.setText("");
        obstacleRequiredLabel.setText("");
        calculationsPane.getChildren().remove(calculationsRootBox);
        calculationsPane.getChildren().add(viewCalculationResultsVBox);
    }

    private void updateRunwayInfoLabels(RunwayPair runwayPair) {
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

    public void updateCalculationResultsView(RunwayConfig original, RunwayConfig recalculated) {
        originalToda.setText(Integer.toString(original.getTODA()));
        originalTora.setText(Integer.toString(original.getTORA()));
        originalAsda.setText(Integer.toString(original.getASDA()));
        originalLda.setText(Integer.toString(original.getLDA()));

        recalculatedToda.setText(Integer.toString(recalculated.getTODA()));
        recalculatedTora.setText(Integer.toString(recalculated.getTORA()));
        recalculatedAsda.setText(Integer.toString(recalculated.getASDA()));
        recalculatedLda.setText(Integer.toString(recalculated.getLDA()));
    }

    private void updateThresholdList(RunwayPair runwayPair) {
        thresholdSelect.getItems().clear();
        thresholdSelect.getItems().add(runwayPair.getR1().getRunwayDesignator().toString());
        thresholdSelect.getItems().add(runwayPair.getR2().getRunwayDesignator().toString());
    }

    private void removeObstacle(Obstacle obstacle, ObstacleList listType) {

        Map<String, Obstacle> sourceList;
        ListView sourceLV;

        switch (listType) {
            case PREDEFINED:
                sourceList = predefinedObstaclesSorted;
                sourceLV = predefinedObstaclesLV;
                break;
            case USER_DEFINED:
                sourceList = userDefinedObstacles;
                sourceLV = userDefinedObstaclesLV;
                break;
            default:
                System.err.println("unknown origin '" + listType.toString() + "'");
                return;
        }

        sourceLV.getItems().remove(obstacle);
        obstacleSelect.getItems().remove(obstacle);

        sourceList.remove(obstacle.getName());
        allObstaclesSorted.remove(obstacle.getName());

        notifyUpdate("Obstacle removed");
    }


    private void updateObstaclesList() {
        userDefinedObstaclesLV.getItems().clear();
        userDefinedObstaclesLV.getItems().addAll(userDefinedObstacles.values());
        predefinedObstaclesLV.getItems().clear();
        predefinedObstaclesLV.getItems().addAll(predefinedObstaclesSorted.values());
        obstacleSelect.getItems().clear();
        obstacleSelect.getItems().addAll(allObstaclesSorted.values());
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

        for (Obstacle obstacle : obstacles) {
            predefinedObstaclesSorted.put(obstacle.getName(), obstacle);
            allObstaclesSorted.put(obstacle.getName(), obstacle);
        }

        //predefinedObstaclesLV.getItems().addAll(predefinedObstaclesSorted.keySet());
        predefinedObstaclesLV.getItems().addAll(obstacles);

        obstacleSelect.getItems().addAll(predefinedObstaclesSorted.keySet());


    }


    private Boolean validateObstaclesForm() {

        if (obstacleNameTxt.getText().length() < 1) {
            return false;
        }

        try {
            Double.parseDouble(obstacleHeightTxt.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }


        return true;
    }

    private Boolean validateDoubleForm(ArrayList<String> doubleVals) {
        for (String s : doubleVals) {
            try {
                Double.parseDouble(s);
            } catch (NumberFormatException e) {
                return false;
            }
            if (Double.parseDouble(s) < 1 || Double.parseDouble(s) > 9999) {
                return false;
            }
        }
        return true;
    }

    private Boolean validateIntForm(ArrayList<String> intVals) {
        for (String s : intVals) {
            try {
                Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private String getReverseRunwayName(String originalRunway) {
        return String.valueOf((Integer.parseInt(originalRunway) + 18) % 36);
    }

    public void addObstacle(String name, double height) {
        Obstacle obstacle = new Obstacle(name, height);
        addObstacle(obstacle);
    }

    private void addObstacle(Obstacle obstacle) {
        this.userDefinedObstacles.put(obstacle.getName(), obstacle);
        this.allObstaclesSorted.put(obstacle.getName(), obstacle);
    }

    private void notifyUpdate(String message) {
        new Notification(message).show(primaryStage, primaryStage.getX(), primaryStage.getY() + primaryStage.getHeight() - Notification.HEIGHT);
    }

    private void disableTooltips() {
        highlightTodaBtn.setTooltip(null);
        highlightToraBtn.setTooltip(null);
        highlightAsdaBtn.setTooltip(null);
        highlightLdaBtn.setTooltip(null);
        addObstacleHeightTF.setTooltip(null);
        centrelineTF.setTooltip(null);
        distanceFromThresholdTF.setTooltip(null);
        popAddObstacleBtn.setTooltip(null);
        editObstacleBtn.setTooltip(null);
        saveObstacleBtn.setTooltip(null);
        heightEditTF.setTooltip(null);
    }

    private void enableTooltips() {
        highlightTodaBtn.setTooltip(todaButtonTooltip);
        highlightToraBtn.setTooltip(toraButtonTooltip);
        highlightAsdaBtn.setTooltip(asdaButtonTooltip);
        highlightLdaBtn.setTooltip(ldaButtonTooltip);
        addObstacleHeightTF.setTooltip(obstacleHeightTooltip);
        centrelineTF.setTooltip(centrelineDistTooltip);
        distanceFromThresholdTF.setTooltip(thresholdDistTooltip);
        popAddObstacleBtn.setTooltip(addObstacleTooltip);
        editObstacleBtn.setTooltip(importObstaclesTooltip);
        saveObstacleBtn.setTooltip(saveObstaclesTooltip);
        heightEditTF.setTooltip(editObstacleHeightTooltip);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
