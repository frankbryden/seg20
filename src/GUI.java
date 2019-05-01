import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
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
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class GUI extends Application {
    @FXML
    private Button loadAirportBtn, addObstacleBtn, addAirportBtn, addRunwayBtn, calculateBtn, calculationsBackBtn, popAddObstacleBtn,
            editObstacleBtn, saveObstacleBtn, highlightAsdaBtn, highlightToraBtn, highlightTodaBtn, highlightLdaBtn, saveSettingsBtn, startBtn, manageTooltipsBtn, viewManualBtn;
    @FXML
    private Pane calculationsPane;
    @FXML
    private TextField obstacleNameTxt, obstacleHeightTxt, centrelineTF, distanceFromThresholdTF, addObstacleNameTF, addObstacleHeightTF, airportCode, selectedObstacleHeightTF,
            heightEditTF;
    @FXML
    private ListView<Obstacle> predefinedObstaclesLV;
    @FXML
    private ComboBox<String> thresholdSelect, airportSelect, runwaySelect;
    private FileIO fileIO;
    @FXML
    private Label runwayDesignatorLbl, toraLbl, todaLbl, asdaLbl, ldaLbl, centrelineDistanceLbl, runwayDesignatorCntLbl, runwayDesignatorLbl2, toraCntLbl, toraCntLbl2, todaCntLbl, todaCntLbl2, asdaCntLbl, asdaCntLbl2, ldaCntLbl, ldaCntLbl2,
            runwayThresholdLbl, breakdownCalcLbl, obstacleSelectLbl, thresholdSelectLbl, originalToda,
            originalTora, originalAsda, originalLda, recalculatedToda, recalculatedTora, recalculatedAsda, recalculatedLda, windLbl, selectedObstacleHeightLbl,
            centreLineRequiredLabel, thresholdDistanceRequiredLabel, thresholdRequiredLabel, obstacleRequiredLabel,
            thresholdLbl;
    @FXML
    private GridPane calculationResultsGrid, runwayGrid;
    private TextArea calculationDetails;
    private VBox calculationsRootBox, viewCalculationResultsVBox;
    private HBox centerlineHBox, thresholdHBox, obstacleSelectHBox, thresholdSelectHBox, heightHBox;
    private Map<String, AirportConfig> airportConfigs;
    private Map<String, Obstacle> predefinedObstaclesSorted;
    private Stage addAirportPopup;
    private AddRunwayPopup addRunwayPopup;
    private ExportPopup exportPopup;
    private RunwayPair currentlySelectedRunway = null;
    @FXML
    private Canvas canvas, canvasSideView;
    @FXML
    private TabPane tabPane, rootTabPane;
    @FXML
    private Pane planePane;
    private ImageView planeImg;
    private RunwayRenderer runwayRenderer;
    private RunwayRenderer runwayRendererSideView;
    @FXML
    private BorderPane canvasBorderPane, tabsBox;
    private ComboBox<String> obstacleSelect;
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
    private int numOfNotifications;
    @FXML
    private Label notifCount;
    private ArrayList<String> notifList;
    private DeleteObstaclePopup delObstaclePopup;
    private ObstacleDetailsPopup obstacleDetPopup;
    private ObstacleOverwritePopup overwriteObstaclePopup;
    private AddObstaclePopup addObstaclePopup;
    private AddAirportPopup addingAirportPopup;
    private AddRunwayPopup addingRunwayPopup;
    private VBox calculateBackBtnVBox;
    public enum ObstacleList {USER_DEFINED, PREDEFINED}


    @Override
    public void start(Stage primaryStage) throws Exception {
        // getClass().getResource("sample.fxml") gives me a null pointer exception - caused by the way the IDE loads the resource files
        // temporary fix for now

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("sample.fxml"));
        loader.setController(this);
        Parent root = (Parent) loader.load();
        //Parent root; // = FXMLLoader.load(getClass().getClassLoader().getResource("sample.fxml"));

        primaryStage.setTitle("Runway Redeclaration Tool");
        primaryStage.getIcons().add(new Image("/rec/plane.png"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        this.primaryStage = primaryStage;
        addRunwayPopup = new AddRunwayPopup(this);

        primaryStage.setOnCloseRequest(event -> {
            //TODO save persistent settings here
            System.out.println("closed");
        });

        thresholdLbl = new Label();

        addObstacleNameTF = new TextField();
        addObstacleHeightTF = new TextField();
        airportCode = new TextField();
        heightEditTF = new TextField();

        notifList = new ArrayList<>();
        notifCount.setVisible(false);

        fileIO = new FileIO();

        predefinedObstaclesSorted = new TreeMap<>();

        airportDB = new AirportDatabase();

        airportConfigs = new HashMap<>();
        airportConfigs.putAll(fileIO.readRunwayDB("runways.csv"));

        createPopups();

        createTooltips();

        rootTabPane.setVisible(false);
        tabsBox.setStyle("-fx-background-color: #f4f4f4");

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
            String style = String.format("-fx-background-color: linear-gradient(to right, #1b88bb %d%%, #ffffff %d%%);", leftPercentage, leftPercentage);
            trackPane.setStyle(style);
        });

        trackPane.setStyle("-fx-background-color: linear-gradient(to right, -fx-primary-color 0%, #ffffff 0%);");


        runwaySelect.setId("runwayComboBox");

        runwaySelect.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Currently selected airport : " + airportSelect.getSelectionModel().selectedItemProperty().getValue());
            AirportConfig ac = airportConfigs.get(airportSelect.getSelectionModel().selectedItemProperty().getValue());
            for (String runwayPairName : ac.getRunways().keySet()) {
                if (runwayPairName.equals(newValue)) {
                    RunwayPair selectedRunwayPair = ac.getRunways().get(runwayPairName);
                    updateRunwayInfoLabels(selectedRunwayPair);
                    updateThresholdList(selectedRunwayPair);
                    currentlySelectedRunway = selectedRunwayPair;
                    runwayRenderer = new RunwayRenderer(currentlySelectedRunway, canvas.getGraphicsContext2D());
                    runwayRendererSideView = new RunwayRenderer(currentlySelectedRunway, canvasSideView.getGraphicsContext2D(), true);
                    setRunwayRendererParams();
                    runwayRenderer.render();

                    runwayRendererSideView.renderSideview();

                    rootTabPane.setVisible(true);
                    tabsBox.setStyle("-fx-background-color: #1b88bb");


                    LiveWindService liveWindService = new LiveWindService();
                    liveWindService.setLatitude(ac.getLatitude());
                    liveWindService.setLongitude(ac.getLongitude());
                    liveWindService.setOnSucceeded(t -> {
                        Map<String, Double> result = (Map<String, Double>) t.getSource().getValue();
                        windLbl.setText("Wind speed:  " + result.get("speed") + "km/h");
                        runwayRenderer.setWindAngle(result.get("direction"));
                    });
                    liveWindService.start();
                    break;
                }
            }
        });


        airportSelect.setId("airportComboBox");
        airportSelect.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue == null) {
                System.out.println("Selection cleared");
                return;
            }
            updateRunwaySelect(newValue);
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

        popAddObstacleBtn.setOnMouseClicked(event -> {
            Bounds bounds = popAddObstacleBtn.localToScreen(popAddObstacleBtn.getBoundsInLocal());
            addObstaclePopup.show(primaryStage);
            addObstaclePopup.center(bounds.getMaxX() - 46, bounds.getMinY() + 100);
        });

        ImageView editObstacleImgView = new ImageView(new Image(getClass().getResourceAsStream("/rec/load.png")));
        editObstacleImgView.setFitWidth(iconSize);
        editObstacleImgView.setFitHeight(iconSize);
        editObstacleBtn.setGraphic(editObstacleImgView);

        editObstacleBtn.setOnMouseClicked(event -> {
            File file = fileIO.fileChooser.showOpenDialog(primaryStage);
            if (file == null) {
                return;
            }
            Collection<Obstacle> importedObstacles = fileIO.readObstacles(file.getPath());
            importedObstacles.forEach(obstacle -> {
                addObstacle(obstacle);
            });
            updateObstaclesList();

            notifyUpdate("Obstacles imported");
            addNotification("Imported obstacles into the list of obstacles.");
        });

        ImageView deleteObstacleImgView = new ImageView(new Image(getClass().getResourceAsStream("/rec/delete.png")));
        deleteObstacleImgView.setFitWidth(iconSize);
        deleteObstacleImgView.setFitHeight(iconSize);


        ImageView saveObstacleImgView = new ImageView(new Image(getClass().getResourceAsStream("/rec/save.png")));
        saveObstacleImgView.setFitHeight(iconSize);
        saveObstacleImgView.setFitWidth(iconSize);
        saveObstacleBtn.setGraphic(saveObstacleImgView);

        saveObstacleBtn.setOnMouseClicked(event -> {
            if (!predefinedObstaclesSorted.keySet().isEmpty()) {
                System.out.println("Save obstacles");
                File file = fileIO.fileChooser.showSaveDialog(primaryStage);
                if (file != null) {
                    fileIO.write(predefinedObstaclesSorted.values(), file.getPath());
                    notifyUpdate("Obstacles saved");
                    addNotification("Saved list of obstacles.");
                }
            } else {
                System.out.println("No obstacles to save");
                SaveObstacleErrorPopup.displaySavePrompt();
            }
        });


        // Button in Settings tab for enabling/disabling tooltips
        manageTooltipsBtn.setOnMouseClicked(event -> {
            if (manageTooltipsBtn.getText().equals("Disable tooltips")) {
                manageTooltipsBtn.setText("Enable tooltips");
                disableTooltips();
            } else {
                manageTooltipsBtn.setText("Disable tooltips");
                enableTooltips();
            }
        });

        highlightTodaBtn.setOnMouseClicked(event -> {
            runwayRenderer.setCurrentlyHighlightedParam(RunwayRenderer.RunwayParams.TODA);
            notifyUpdate("TODA Highlighted");
        });

        highlightToraBtn.setOnMouseClicked(event -> {
            runwayRenderer.setCurrentlyHighlightedParam(RunwayRenderer.RunwayParams.TORA);
            notifyUpdate("TORA Highlighted");
        });

        highlightAsdaBtn.setOnMouseClicked(event -> {
            runwayRenderer.setCurrentlyHighlightedParam(RunwayRenderer.RunwayParams.ASDA);
            notifyUpdate("ASDA Highlighted");
        });

        highlightLdaBtn.setOnMouseClicked(event -> {
            runwayRenderer.setCurrentlyHighlightedParam(RunwayRenderer.RunwayParams.LDA);
            notifyUpdate("LDA Highlighted");
        });

        addAirportBtn.setOnMouseClicked(event -> {
            System.out.println("Add airport");
            addAirportPopup.show();
        });

        addRunwayBtn.setOnMouseClicked(event -> {
            System.out.println("Add runway");
            addRunwayPopup.show();
        });

        //Settings tab
        saveSettingsBtn.setOnMouseClicked(value -> {
            System.out.println("Clicked on save settings");
            //new Notification("hey").show(primaryStage, 10, 10);

        });

        createCalculationsSelectionView();
        createCalculationsResultView();
        resetCalculationsTab();

        /*canvasBorderPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double newVal = (double) newValue;
                canvas.setWidth(newVal/2);
            }Font.font("Verdana", FontWeight.BOLD, 14)
        });
        canvas.heightProperty().bind(canvasBorderPane.heightProperty());*/

        canvas.setOnScroll(event -> {
            if (runwayRenderer == null){
                return;
            }
            runwayRenderer.setMouseLocation((int) event.getX(), (int) event.getY());
            if (event.getDeltaY() > 0) {
                runwayRenderer.incZoom();
            } else if (event.getDeltaY() < 0) {
                runwayRenderer.decZoom();
            }
            zoomSlider.setValue(runwayRenderer.getZoom());
            //runwayRenderer.updateZoom((int) (event.getDeltaY()/2));
        });

        canvas.setOnMousePressed(event -> {
            MouseDragTracker.getInstance().startDrag((int) event.getX(), (int) event.getY());
            /*double currentAngle = runwayRenderer.getWindAngle();
            System.out.println("Current angle = " + currentAngle);
            currentAngle += Math.PI/4;
            System.out.println("After addition = " + currentAngle);
            runwayRenderer.setWindAngle(currentAngle);*/
        });


        canvas.setOnMouseDragged(event -> {
            if (runwayRenderer == null){
                return;
            }
            MouseDragTracker.getInstance().dragging((int) event.getX(), (int) event.getY());
            Point delta = MouseDragTracker.getInstance().getDelta();
            runwayRenderer.translate(delta.x, delta.y);
        });

        predefinedObstaclesLV.getStyleClass().add("obstacleList");
        predefinedObstaclesLV.setStyle("-fx-font-size: 1.2em ;");

        predefinedObstaclesLV.setCellFactory(lv -> new ObstacleCell(predefinedObstaclesLV));

        predefinedObstaclesLV.addEventHandler(DeleteEvent.DELETE_EVENT_TYPE, event -> {
            delObstaclePopup.displayDeletePrompt(event.getObstacle(), ObstacleList.PREDEFINED);
        });

        predefinedObstaclesLV.addEventHandler(ObstacleInfoEvent.OBSTACLE_INFO_EVENT_TYPE, event -> {
            obstacleDetPopup.showObstacleDetails(event.getObstacle(), predefinedObstaclesLV, null, primaryStage, ObstacleList.PREDEFINED);
        });

        predefinedObstaclesLV.addEventHandler(CellHoverEvent.CELL_HOVER_EVENT_TYPE, event -> {

        });

        predefinedObstaclesLV.setOnMouseClicked(click -> {
            Obstacle selectedItem = predefinedObstaclesLV.getSelectionModel().getSelectedItem();
            if(selectedItem != null){
                obstacleSelect.setValue(selectedItem.getName());
            }
        });

        populatePredefinedList();

        //Home screen plane rotation
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

        takeOffTransition.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Animation.Status.STOPPED) {
                tabPane.getSelectionModel().select(1);
            }
        });

        tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if ((int) newValue == 0) {
                landTransition.play();
            }
        });
        //planePane.setBackground(new Background(new BackgroundFill(Color.web("#ff1290"), CornerRadii.EMPTY, Insets.EMPTY)));

        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newVal = (double) newValue;
            planeImg.setLayoutX(planePane.getWidth() - planeImg.getFitWidth());
        });

        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newVal = (double) newValue;
            planeImg.setLayoutY(planePane.getHeight() - 1.4 * planeImg.getFitHeight());
        });
        //Listeners for the print and export button on the top right side of the GUI
        Pane printBtnPane = (Pane) primaryStage.getScene().lookup("#printBtnPane");
        Pane exportBtnPane = (Pane) primaryStage.getScene().lookup("#exportBtnPane");
        Pane notifBtnPane = (Pane) primaryStage.getScene().lookup("#notifBtnPane");

        //set cursor to make pane look like a button - not sure what the difference between HAND and OPEN_HAND is (there is a difference lol) look the same under xfce ubuntu
        printBtnPane.setCursor(Cursor.HAND);
        exportBtnPane.setCursor(Cursor.HAND);
        notifBtnPane.setCursor(Cursor.HAND);

        printBtnPane.setOnMouseClicked(event -> {
            System.out.println("Print report");
            printer.print();
        });

        exportBtnPane.setOnMouseClicked(event -> {
            //TODO implement exporting, and link back to here when done
            System.out.println("Export data");
            exportPopup.show();
        });

        notifBtnPane.setOnMouseClicked(event -> {
            numOfNotifications = 0;
            notifCount.setText("");
            notifCount.setVisible(false);

            NotificationLog log = new NotificationLog(notifList);
            log.createNotifLog();
        });

        loadAirportBtn.setOnMouseClicked(event -> {
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

        });

        startBtn.setOnMouseClicked(event -> {
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
        printer.setOriginalRecalculatedBackBtn(new Pair<>(viewCalculationResultsVBox, calculateBackBtnVBox));

        enableTooltips();
    }

    private Region getHGrowingRegion() {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }

    public void updateAirportSelects() {
        String selectedAirportName = (String) airportSelect.getSelectionModel().getSelectedItem();
        String selectedRunwayPair = (String) runwaySelect.getSelectionModel().getSelectedItem();
        runwaySelect.getItems().clear();
        airportSelect.getItems().clear();
        //TODO call clear method on new popup class
        addRunwayPopup.clearAirportList();

        String[] names = airportConfigs.keySet().toArray(new String[0]);
        Arrays.sort(names);

        for (String name : names) {
            AirportConfig ac = airportConfigs.get(name);
            airportSelect.getItems().add(name);
            addRunwayPopup.addAirportToList(name);
        }

        airportSelect.getSelectionModel().select(airportSelect.getItems().indexOf(selectedAirportName));
        runwaySelect.getSelectionModel().select(runwaySelect.getItems().indexOf(selectedRunwayPair));
    }

    private void updateRunwaySelect(String airportName) {
        runwaySelect.getItems().clear();
        runwaySelect.getItems().addAll(airportConfigs.get(airportName).getRunways().keySet());
    }

    private void clearErrorLabels(){
        obstacleRequiredLabel.setText("");
        thresholdRequiredLabel.setText("");
        centreLineRequiredLabel.setText("");
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

    private void updateCalculationResultsView(RunwayConfig original, RunwayConfig recalculated) {
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

    public void removeObstacle(Obstacle obstacle, ObstacleList listType) {

        Map<String, Obstacle> sourceList;
        ListView sourceLV;

        switch (listType) {
            case PREDEFINED:
                sourceList = predefinedObstaclesSorted;
                sourceLV = predefinedObstaclesLV;
                addNotification("Removed " + obstacle.getName() + " from the list of obstacles.");
                break;
            default:
                System.err.println("unknown origin '" + listType.toString() + "'");
                return;
        }

        sourceLV.getItems().remove(obstacle);
        obstacleSelect.getItems().remove(obstacle.getName());

        sourceList.remove(obstacle.getName());

        notifyUpdate("Obstacle removed");
    }


    public void updateObstaclesList() {
        predefinedObstaclesLV.getItems().clear();
        predefinedObstaclesLV.getItems().addAll(predefinedObstaclesSorted.values());
        obstacleSelect.getItems().clear();
        obstacleSelect.getItems().addAll(predefinedObstaclesSorted.keySet());
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
        }

        updateObstaclesList();

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

    // Used for obstacle heights
    public Boolean validateDoubleForm(ArrayList<String> doubleVals) {
        for (String s : doubleVals) {
            try {
                Double.parseDouble(s);
            } catch (NumberFormatException e) {
                return false;
            }
            if (Double.parseDouble(s) < 1 || Double.parseDouble(s) > 150) {
                return false;
            }
        }
        return true;
    }

    // Used for TORA, Clearway, Stopway, Displaced threshold, distance from centreline and distance from threshold
    public Boolean validateIntForm(ArrayList<String> intVals, String component) {
        for (String s : intVals) {
            try {
                Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return false;
            }
            if (component.equals("Centreline")) {
                if (Integer.parseInt(s) > 1000 || Integer.parseInt(s) < -1000) {
                    return false;
                }
            }
            if (component.equals("Threshold")) {
                if (Integer.parseInt(s) < 0 || Integer.parseInt(s) > 4000) {
                    return false;
                }
            }

        }
        return true;
    }

    public String getReverseRunwayName(String originalRunway) {
        return String.valueOf((Integer.parseInt(originalRunway) + 18) % 36);
    }

    public void addObstacle(String name, double height) {
        Obstacle obstacle = new Obstacle(name, height);
        addObstacle(obstacle);
    }

    private void addObstacle(Obstacle obstacle) {
        this.predefinedObstaclesSorted.put(obstacle.getName(), obstacle);
    }


   public void notifyUpdate(String message) {
        new Notification(message).show(primaryStage, primaryStage.getX(), primaryStage.getY() + primaryStage.getHeight() - Notification.HEIGHT);
    }


    public void addNotification(String message) {
        numOfNotifications++;
        notifList.add(0, message);
        notifCount.setVisible(true);
        notifCount.setText(Integer.toString(numOfNotifications));

        AudioClip note = new AudioClip(this.getClass().getResource("light.wav").toString());
        note.play();
    }

    private void setRunwayRendererParams(){
        if (runwayRenderer == null || runwayRendererSideView == null){
            return;
        }
        runwayRenderer.setRenderRunwayRotated(renderRunwayRotatedChkbx.selectedProperty().get());
        runwayRenderer.setZoom(zoomSlider.getValue());
        runwayRenderer.setRenderLabelLines(renderRunwayLabelLinesChkbx.selectedProperty().get());
        runwayRenderer.setRenderWindCompass(renderWindCompass.selectedProperty().get());
        runwayRenderer.setTopDownBackgroundColor(topDownColorPicker.getValue());
        runwayRendererSideView.setSideOnBackgroundColor(sideOnColorPicker.getValue());
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
        airportCode.setTooltip(null);
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
        airportCode.setTooltip(airportCodeTooltip);
    }

    private void createTooltips() {
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
    }

    private void createCalculationsSelectionView() {
        final int HBOX_SPACING = 5;
        Insets calculationsInsets = new Insets(5, 20, 0, 0);
        calculationsPane.getStylesheets().add("styles/global.css");
        calculationsPane.getStylesheets().add("styles/calculations.css");

        obstacleSelect = new ComboBox<>();
        obstacleSelect.setVisibleRowCount(10);
        obstacleSelect.setId("obstacleComboBox");

        obstacleSelect.getSelectionModel().selectedItemProperty().addListener((options, oldValue, selectedObstacleName) -> {
                    if (obstacleSelect.getSelectionModel().isEmpty()) {
                        selectedObstacleHeightTF.clear();
                    } else {
                        selectedObstacleHeightTF.setText(predefinedObstaclesSorted.get(selectedObstacleName).getHeight() + "m");
                    }

                }
        );

        thresholdSelect = new ComboBox<>();
        thresholdSelect.setVisibleRowCount(5);
        thresholdSelect.setId("thresholdComboBox");
        centrelineDistanceLbl = new Label("Distance from runway centreline");
        runwayThresholdLbl = new Label("Distance from runway threshold");
        obstacleSelectLbl = new Label("Select obstacle");
        thresholdSelectLbl = new Label("Select threshold");

        centrelineTF = new TextField();
        distanceFromThresholdTF = new TextField();

        centrelineTF.getStyleClass().add("redErrorPromptText");
        centrelineTF.getStylesheets().add("styles/obstacles.css");
        distanceFromThresholdTF.getStyleClass().add("redErrorPromptText");
        distanceFromThresholdTF.getStylesheets().add("styles/obstacles.css");

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

        calculateBtn.setOnMouseClicked(event -> {

            // Check if any of the textfields are empty or no selection is made from the comboboxes
            if (centrelineTF.getText().isEmpty() || distanceFromThresholdTF.getText().isEmpty() ||
                    thresholdSelect.getSelectionModel().isEmpty() || obstacleSelect.getSelectionModel().isEmpty()
            ) {
                if (centrelineTF.getText().isEmpty()) {
                    centrelineTF.setPromptText("");
                    centreLineRequiredLabel.setText("      Enter centreline distance");
                } else {
                    centreLineRequiredLabel.setText("");
                    if (!validateIntForm(new ArrayList<>(Arrays.asList(centrelineTF.getText())), "Centreline")) {
                        centrelineTF.clear();
                        centrelineTF.setPromptText("Invalid centreline distance!");
                    } else {
                        centrelineTF.setPromptText("");
                    }
                }
                if (distanceFromThresholdTF.getText().isEmpty()) {

                    distanceFromThresholdTF.setPromptText("");
                    thresholdDistanceRequiredLabel.setText("      Enter threshold distance");
                } else {
                    thresholdDistanceRequiredLabel.setText("");
                    if (!validateIntForm(new ArrayList<>(Arrays.asList(distanceFromThresholdTF.getText())), "Threshold")) {
                        distanceFromThresholdTF.clear();
                        distanceFromThresholdTF.setPromptText("Invalid threshold distance!");
                    } else {
                        distanceFromThresholdTF.setPromptText("");
                    }
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

                // If everything is filled in, check if the user input to distance from threshold and centreline is valid
            } else if (!validateIntForm(new ArrayList<>(Arrays.asList(distanceFromThresholdTF.getText())), "Threshold") && !validateIntForm(new ArrayList<>(Arrays.asList(centrelineTF.getText())), "Centreline")) {
                clearErrorLabels();
                centrelineTF.clear();
                distanceFromThresholdTF.clear();
                centrelineTF.setPromptText("Invalid centreline distance!");
                distanceFromThresholdTF.setPromptText("Invalid threshold distance!");
            } else if (!validateIntForm(new ArrayList<>(Arrays.asList(distanceFromThresholdTF.getText())), "Threshold")) {
                clearErrorLabels();
                thresholdDistanceRequiredLabel.setText("");
                distanceFromThresholdTF.clear();
                distanceFromThresholdTF.setPromptText("Invalid threshold distance!");
            } else if (!validateIntForm(new ArrayList<>(Arrays.asList(centrelineTF.getText())), "Centreline")) {
                clearErrorLabels();
                thresholdDistanceRequiredLabel.setText("");
                centrelineTF.clear();
                centrelineTF.setPromptText("Invalid centreline distance!");
            } else {
                // If everything is filled in and valid, start calculations

                String obstacleName = obstacleSelect.getSelectionModel().getSelectedItem();
                Obstacle currentlySelectedObstacle = predefinedObstaclesSorted.get(obstacleName);
                Obstacle selectedObstacle = (Obstacle) predefinedObstaclesLV.getSelectionModel().getSelectedItem();

                String thresholdName = thresholdSelect.getSelectionModel().getSelectedItem();
                updateThresholdLbl(thresholdName);

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
                int runwayLength;
                if (runwayConfig.getTORA() > otherConfig.getTORA()) {
                    runwayLength = runwayConfig.getTORA();
                } else {
                    runwayLength = otherConfig.getTORA();
                }

                int distanceFromOtherThreshold = runwayLength - runwayConfig.getDisplacementThreshold() - distanceFromThreshold - otherConfig.getDisplacementThreshold();

                // I compare the distances from each threshold. Whichever threshold the obstacle is closer to, that logical runway is used for taking off away
                CalculationResults results;
                CalculationResults results2;
                RunwayConfig recalculatedParams;
                RunwayConfig recalculatedParams2;
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

                RunwayPair runwayPair = new RunwayPair(recalculatedParams, recalculatedParams2);
                updateRunwayInfoLabels(runwayPair);

                System.out.println(results.getCalculationDetails());
                System.out.println(results2.getCalculationDetails());

                //Generate summary string which designates the calculations (eg. A320 50m from 27R threshold)
                String summary = obstacleName + " " + distanceFromThreshold + "m from " + thresholdName + " threshold";
                System.out.println(summary);

                // Printing results into the breakdown of calculations text box
                String resultsDetails = results.getCalculationDetails() + "\n" + results2.getCalculationDetails();
                calculationDetails.setText(summary + "\n\n" + resultsDetails);
                printer.setCalculations(resultsDetails);
                printer.setCalculationsHeading(summary);

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

                setRunwayRendererParams();

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
    }

    private void createCalculationsResultView() {
        breakdownCalcLbl = new Label("Breakdown of the calculations: ");
        calculationDetails = new TextArea();
        calculationDetails.setEditable(false);
        calculationDetails.setId("calcBreakdown");
        calculationResultsGrid = new GridPane();
        calculationResultsGrid.getStylesheets().add("styles/runwayTable.css");
        calculationResultsGrid.getStyleClass().add("paintMe");
        Label originalValuesGridLbl, recalculatedlValuesGridLbl, todaRowLbl, toraRowLbl, asdaRowLbl, ldaRowLbl;
        originalValuesGridLbl = new Label("Original\nvalues");
        recalculatedlValuesGridLbl = new Label("Recalculated\nvalues");
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

        calculateBackBtnVBox = new VBox();
        calculateBackBtnVBox.getChildren().add(calculationsBackBtn);
        calculateBackBtnVBox.setAlignment(Pos.BASELINE_RIGHT);

        //calculationResultsGrid.setHgap(20);
        //Add all the labels, col by col,  to create a table
        calculationResultsGrid.setId("smallRunwayGrid");

        //Col 0 : the value names (TODA, TORA, ASDA, LDA)
        calculationResultsGrid.add(thresholdLbl, 0, 0);
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

        calculationsBackBtn.setOnMouseClicked(event -> resetCalculationsTab());
    }

    private void createPopups() {
        delObstaclePopup = new DeleteObstaclePopup(this);
        obstacleDetPopup = new ObstacleDetailsPopup(this);
        overwriteObstaclePopup = new ObstacleOverwritePopup(this);
        addObstaclePopup = new AddObstaclePopup(this);
        addingAirportPopup = new AddAirportPopup(this);
        addingRunwayPopup = new AddRunwayPopup(this);
        addAirportPopup = addingAirportPopup.createAddAirportPopup();
        exportPopup = new ExportPopup(primaryStage, airportConfigs, predefinedObstaclesSorted, fileIO);
    }

    Boolean getEditingObstacle() {
        return editingObstacle;
    }

    void setEditingObstacle(Boolean editingObstacle) {
        this.editingObstacle = editingObstacle;
    }

    TextField getHeightEditTF() {
        return heightEditTF;
    }

    public Map<String, Obstacle> getPredefinedObstaclesSorted() {
        return predefinedObstaclesSorted;
    }

    public TextField getAddObstacleNameTF() {
        return addObstacleNameTF;
    }

    TextField getAddObstacleHeightTF() {
        return addObstacleHeightTF;
    }

    AddObstaclePopup getAddObstaclePopup() {
        return addObstaclePopup;
    }

    ObstacleOverwritePopup getOverwriteObstaclePopup() { return overwriteObstaclePopup; }

    TextField getAirportCode() { return airportCode; }

    AirportDatabase getAirportDB() { return airportDB; }

    Map<String, AirportConfig> getAirportConfigs() {return airportConfigs; }

    Stage getAddAirportPopup() {return addAirportPopup; }

    ListView getObstacleListView(){
        return predefinedObstaclesLV;
    }

    AddRunwayPopup getAddRunwayPopup() {return addRunwayPopup; }

    private void updateThresholdLbl(String thresholdName) { thresholdLbl.setText(thresholdName + "\nthreshold"); }

    public static void main(String[] args) {
        launch(args);
    }
}
