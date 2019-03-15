package FrontEnd;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.soap.Node;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController {

    private FileChooser fileChooser;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void setFileChooser(){
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
        fileChooser.setInitialDirectory(new File("."));
    }

    @FXML
    private void loadAirportOnMouseClicked(ActionEvent e){

        setFileChooser();
        File xmlFileToLoad = fileChooser.showOpenDialog(stage);
        if (xmlFileToLoad == null){
            System.err.println("User did not select a file");
            return;
        }
    }


}

