import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.util.ArrayList;
import java.util.Arrays;

public class AddObstaclePopup {

    private GUI gui;

    public AddObstaclePopup(GUI gui) {
        this.gui = gui;
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

        gui.getAddObstacleNameTF().getStyleClass().add("redErrorPromptText");
        gui.getAddObstacleNameTF().getStylesheets().add("styles/obstacles.css");
        gui.getAddObstacleHeightTF().getStyleClass().add("redErrorPromptText");
        gui.getAddObstacleHeightTF().getStylesheets().add("styles/obstacles.css");

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
        nameBox.getChildren().add(gui.getAddObstacleNameTF());

        heightBox.getChildren().add(heightLbl);
        heightBox.getChildren().add(heightRegion);
        heightBox.getChildren().add(gui.getAddObstacleHeightTF());

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
                if (gui.getAddObstacleNameTF().getText().isEmpty()) {
                    nameRequiredLbl.setText("                                             Enter obstacle name");
                    if (!rootBox.getChildren().contains(emptyNameBox)) {
                        rootBox.getChildren().add(0, emptyNameBox);
                    }
                    gui.getAddObstacleNameTF().setPromptText("");
                } else {
                    if (rootBox.getChildren().contains(emptyNameBox)) {
                        rootBox.getChildren().remove(emptyNameBox);
                    }
                    nameRequiredLbl.setText("");
                }
                if (gui.getAddObstacleHeightTF().getText().isEmpty()) {
                    heightRequiredLbl.setText("                                             Enter obstacle height");
                    if (rootBox.getChildren().contains(emptyNameBox)) {
                        if (!rootBox.getChildren().contains(emptyHeightBox)) {
                            rootBox.getChildren().add(2, emptyHeightBox);
                        }
                    } else {
                        if (!rootBox.getChildren().contains(emptyHeightBox)) {
                            rootBox.getChildren().add(1, emptyHeightBox);
                        }
                    }
                    gui.getAddObstacleHeightTF().setPromptText("");
                } else {
                    if (rootBox.getChildren().contains(emptyHeightBox)) {
                        rootBox.getChildren().remove(emptyHeightBox);
                    }
                    heightRequiredLbl.setText("");
                }

                // Checking for valid obstacle name and valid obstacle height
                if (gui.validateDoubleForm(new ArrayList<>(Arrays.asList(gui.getAddObstacleHeightTF().getText()))) && !gui.getAddObstacleNameTF().getText().isEmpty()) {
                    boolean matchFound = false;
                    for (String obstacleName : gui.getPredefinedObstaclesSorted().keySet()) {
                        if (gui.getAddObstacleNameTF().getText().equals(obstacleName)) {
                            gui.getOverwriteObstaclePopup().displayOverwritePrompt(obstacleName, gui.getPredefinedObstaclesSorted().get(obstacleName).getHeight(), Double.parseDouble(gui.getAddObstacleHeightTF().getText()));
                            matchFound = true;
                            break;
                        }
                    }

                    if (!matchFound) {
                        System.out.println("Add obstacle");
                        gui.addObstacle(gui.getAddObstacleNameTF().getText(), Double.parseDouble(gui.getAddObstacleHeightTF().getText()));
                        gui.addNotification("Added " + gui.getAddObstacleNameTF().getText() + " to the list of obstacles.");
                        gui.getAddObstacleNameTF().clear();
                        gui.getAddObstacleHeightTF().clear();
                        gui.getAddObstacleNameTF().setPromptText("");
                        gui.getAddObstacleHeightTF().setPromptText("");
                        gui.updateObstaclesList();
                        gui.getAddObstaclePopup().hide();

                        //notify user obstacle was added
                        gui.notifyUpdate("Obstacle added");

                    }

                } else if (!gui.getAddObstacleHeightTF().getText().isEmpty() && !gui.validateDoubleForm(new ArrayList<>(Arrays.asList(gui.getAddObstacleHeightTF().getText())))) {
                    gui.getAddObstacleHeightTF().clear();
                    gui.getAddObstacleHeightTF().setPromptText("Invalid obstacle height!");
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
                gui.getAddObstacleNameTF().clear();
                gui.getAddObstacleNameTF().setPromptText("");
                gui.getAddObstacleHeightTF().clear();
                gui.getAddObstacleHeightTF().setPromptText("");
                gui.getAddObstaclePopup().hide();
            }
        });

        popup.getContent().add(rootBox);


        return popup;
    }


}
