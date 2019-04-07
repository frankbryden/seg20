import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

public class Notification extends Popup {
    String message;
    int showTime = 1400; //ms

    //GUI elements
    private BorderPane rootPane;
    private Label msgLbl;

    public Notification(String message){
        this.message = message;
        this.init();
    }

    private void init(){
        rootPane = new BorderPane();
        rootPane.getStylesheets().add("styles/notifications.css");
        msgLbl = new Label(this.message);
        rootPane.setPrefWidth(150);
        rootPane.setPrefHeight(70);
        rootPane.setCenter(msgLbl);
        rootPane.setId("mainPane");
        this.getScene().setFill(Color.PINK);
        this.getContent().add(rootPane);

    }

    @Override
    public void show(Window ownerWindow, double anchorX, double anchorY) {
        super.show(ownerWindow, anchorX, anchorY);
        PauseTransition delay = new PauseTransition(Duration.millis(this.showTime));
        delay.setOnFinished( event -> super.hide() );
        delay.play();
    }
}
