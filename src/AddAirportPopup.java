import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

class AddAirportPopup {

    private final GUI gui;

    public AddAirportPopup(GUI gui) {
        this.gui = gui;
    }

    public Stage createAddAirportPopup() {
        Stage stage = new Stage();
        stage.getIcons().add(new Image("/rec/plane.png"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add Airport");

        //Components for the popups
        Button confirmButton = new Button("Add");
        Button cancelButton = new Button("Cancel");
        TextField airportName;
        Label airportNameLbl, airportCodeLbl;
        ListView<String> airportSuggestions;

        airportNameLbl = new Label("Airport Name");
        airportCodeLbl = new Label("Airport Code");
        airportName = new TextField();
        airportName.setEditable(false);

        airportSuggestions = new ListView<>();
        airportSuggestions.setMaxHeight(100);

        //Add auto-completion to the airport code
        gui.getAirportCode().setOnKeyReleased(event -> {
            airportSuggestions.getItems().clear();
            System.out.println("text is " + gui.getAirportCode().getText());
            if (gui.getAirportCode().getText().length() > 0){
                airportSuggestions.getItems().addAll(gui.getAirportDB().getEntries(gui.getAirportCode().getText()));
                if (airportSuggestions.getItems().size() == 1){
                    if (event.getCode() == KeyCode.BACK_SPACE && gui.getAirportCode().getText().length() != 3){
                        airportName.clear();
                        return;
                    }
                    String suggestedAirportName = (String) airportSuggestions.getItems().get(0);
                    airportName.setText((String) airportSuggestions.getItems().get(0));
                    gui.getAirportCode().setText(gui.getAirportDB().getEntryReversed(suggestedAirportName));
                    gui.getAirportCode().positionCaret(gui.getAirportCode().getText().length());
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
                gui.getAirportCode().setText(gui.getAirportDB().getEntryReversed(selectedAirportName));
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
        gridPane.add(gui.getAirportCode(), 1, 0);
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
                System.out.println("add airport with name " + airportName.getText() + " and code " + gui.getAirportCode().getText());
                AirportConfig airportConfig = new AirportConfig(airportName.getText());
                gui.getAirportConfigs().put(airportConfig.getName(), airportConfig);
                gui.updateAirportSelects();
                gui.getAddAirportPopup().hide();
                gui.getAddRunwayPopup().show();
                gui.addNotification("Added " + airportName.getText() + " to the list of airports.");
            } else {
                AirportErrorPopup.displayAirportErrorPopup();
            }
        });

        //Simply close the popup, discarding the data
        cancelButton.setOnMouseClicked(event -> gui.getAddAirportPopup().hide());

        stage.setScene(scene);
        return stage;
    }
}
