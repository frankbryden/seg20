import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

class ObstacleDetailsPopup {

    private final GUI gui;

    public ObstacleDetailsPopup(GUI gui){
        this.gui = gui;
    }


    public void showObstacleDetails(Obstacle obstacle, ListView listView, MouseEvent event, Stage primaryStage, GUI.ObstacleList sourceList) {

        gui.setEditingObstacle(false);

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
        gui.getHeightEditTF().setPrefWidth(240);

        // Styling of name and height text fields to show red prompt text
        nameEditTF.getStyleClass().add("redErrorPromptText");
        nameEditTF.getStylesheets().add("styles/obstacles.css");
        gui.getHeightEditTF().getStyleClass().add("redErrorPromptText");
        gui.getHeightEditTF().getStylesheets().add("styles/obstacles.css");

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

        returnButton.setOnMouseClicked(event13 -> detailsPopUp.hide());


        saveButton.setOnMouseClicked(event12 -> {
            // Checking for empty name and height fields
            if (nameEditTF.getText().isEmpty()) {
                nameRequiredLbl.setText("                                       Enter obstacle name");
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
            if (gui.getHeightEditTF().getText().isEmpty()) {
                heightRequiredLbl.setText("                                       Enter obstacle height");
                if (box.getChildren().contains(emptyNameBox)) {
                    if (!box.getChildren().contains(emptyHeightBox)) {
                        box.getChildren().add(3, emptyHeightBox);
                    }
                } else {
                    if (!box.getChildren().contains(emptyHeightBox)) {
                        box.getChildren().add(2, emptyHeightBox);
                    }
                }
                gui.getHeightEditTF().setPromptText("");
            } else {
                if (box.getChildren().contains(emptyHeightBox)) {
                    box.getChildren().remove(emptyHeightBox);
                }
                heightRequiredLbl.setText("");
            }
            if (gui.validateDoubleForm(new ArrayList<>(Arrays.asList(gui.getHeightEditTF().getText()))) && !nameEditTF.getText().isEmpty()) {
                System.out.println("Add -edited- obstacle");
                Map<String, Obstacle> obstacleList;
                if (sourceList == GUI.ObstacleList.PREDEFINED) {
                    obstacleList = gui.getPredefinedObstaclesSorted();
                } else if (sourceList == GUI.ObstacleList.USER_DEFINED) {
                    obstacleList = gui.getUserDefinedObstacles();
                } else {
                    System.err.println("We have a problem - unknown source list " + sourceList);
                    return;
                }
                double currentHeight = obstacle.getHeight();
                double newHeight = Double.parseDouble(gui.getHeightEditTF().getText());
                obstacleList.remove(obstacle.getName());
                gui.getAllObstaclesSorted().remove(obstacle.getName());
                obstacle.setName(nameEditTF.getText());
                obstacle.setHeight(Double.valueOf(gui.getHeightEditTF().getText()));

                obstacleList.put(obstacle.getName(), obstacle);
                gui.getAllObstaclesSorted().put(obstacle.getName(), obstacle);

                nameContentLabel.setText(obstacle.getName());
                heightContentLabel.setText(Double.toString(obstacle.getHeight()));

                gui.notifyUpdate("Obstacle edited");
                gui.addNotification("Edited details of " + obstacle.getName() + ". Modified height from " + currentHeight + "m to " + newHeight + "m.");

                gui.updateObstaclesList();
                detailsPopUp.hide();

            } else if (!gui.getHeightEditTF().getText().isEmpty() && !gui.validateDoubleForm(new ArrayList<>(Arrays.asList(gui.getHeightEditTF().getText())))) {
                gui.getHeightEditTF().clear();
                gui.getHeightEditTF().setPromptText("Invalid obstacle height!");
            }
        });

        editButton.setOnMouseClicked(event1 -> {
            gui.setEditingObstacle(!gui.getEditingObstacle());
            if (gui.getEditingObstacle() == true) {
                //Edit mode

                nameLabel.setText("Name");
                heightLabel.setText("Height");

                nameHBox.setSpacing(55);
                heightHBox.setSpacing(46);
                subBox.setSpacing(138);

                nameHBox.getChildren().remove(nameContentLabel);
                nameHBox.getChildren().add(nameEditTF);

                heightHBox.getChildren().remove(heightContentLabel);
                heightHBox.getChildren().add(gui.getHeightEditTF());

                nameEditTF.setText(obstacle.getName());
                gui.getHeightEditTF().setText(Double.toString(obstacle.getHeight()));

                detailsLabel.setText("Edit obstacle details");

                subBox.getChildren().remove(returnButton);
                subBox.getChildren().remove(editButton);
                subBox.getChildren().add(saveButton);
                subBox.getChildren().add(returnButton);

                returnButton.setText("Cancel");
            }
        });


    }


}
