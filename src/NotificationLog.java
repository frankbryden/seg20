import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class NotificationLog {

    private ArrayList<String> notifications;
    private final int WINDOW_WIDTH = 270;

    public NotificationLog(ArrayList<String> notifications) {
        this.notifications = notifications;
    }

    public void createNotifLog() {
        Stage notifWindow = new Stage();
        notifWindow.getIcons().add(new Image("/rec/plane.png"));
        notifWindow.setTitle("Notification Log");

        ScrollPane scrollPane = new ScrollPane();

        VBox rootBox = new VBox();
        rootBox.getStylesheets().add("styles/notifications.css");
        rootBox.setStyle("-fx-background-color: #1B88BB;");

        Label notifLbl = new Label("Notifications");
        notifLbl.setPrefWidth(WINDOW_WIDTH - 2);
        notifLbl.getStyleClass().add("notifHeader");

        rootBox.getChildren().add(notifLbl);

        for (String notif : notifications) {
            Button notification = new Button(notif);
            notification.getStyleClass().add("notification");
            notification.setPrefWidth(WINDOW_WIDTH - 2);
            notification.setWrapText(true);
            rootBox.getChildren().add(notification);
        }

        scrollPane.setContent(rootBox);

        Scene scene = new Scene(scrollPane, WINDOW_WIDTH, 350);

        notifWindow.setScene(scene);
        notifWindow.show();
    }
}
