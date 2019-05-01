import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;

class NotificationLog {

    private final ArrayList<String> notifications;
    private final int WINDOW_WIDTH = 270;
    private Stage notifWindow;
    private double x, y;
    private VBox rootBox;

    public NotificationLog(ArrayList<String> notifications, double x, double y) {
        this.notifications = notifications;
        this.x = x;
        this.y = y;
        this.createNotifLog();
    }

    public void createNotifLog() {
        notifWindow = new Stage(StageStyle.UNDECORATED);
        notifWindow.getIcons().add(new Image("/rec/plane.png"));
        notifWindow.setTitle("Notification Log");

        ScrollPane scrollPane = new ScrollPane();

        rootBox = new VBox();
        rootBox.getStylesheets().add("styles/notifications.css");
        rootBox.setStyle("-fx-background-color: #1B88BB;");

        Label notifLbl = new Label("Notifications");
        notifLbl.setPrefWidth(WINDOW_WIDTH - 2);
        notifLbl.getStyleClass().add("notifHeader");

        rootBox.getChildren().add(notifLbl);

        scrollPane.setContent(rootBox);

        Scene scene = new Scene(scrollPane, WINDOW_WIDTH, 350);

        notifWindow.setScene(scene);

    }

    public void show(){
        notifWindow.show();
        notifWindow.setX(x - notifWindow.getWidth()/2);
        notifWindow.setY(y);

    }


    void addNotif(String notif) {
        Button notification = new Button(notif);
        notification.getStyleClass().add("notification");
        notification.setPrefWidth(WINDOW_WIDTH - 2);
        notification.setWrapText(true);
        rootBox.getChildren().add(notification);
    }

    public void hide(){
        notifWindow.hide();
    }

    public void toggle(){
        if (notifWindow.isShowing()){
            hide();
        } else {
            show();
        }
    }
}
