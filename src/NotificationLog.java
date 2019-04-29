import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.ArrayList;

public class NotificationLog {

    private ArrayList<String> notifications;
    private final int WINDOW_WIDTH = 270;

    public NotificationLog(ArrayList<String> notifications) {
        this.notifications = notifications;
    }

    // TODO - add styling
    public void createNotifLog() {
        //Stage notifWindow = new Stage(StageStyle.UNDECORATED);
        Stage notifWindow = new Stage();
        notifWindow.setTitle("Notification Log");

        ScrollPane scrollPane = new ScrollPane();

        VBox rootBox = new VBox();

        Label notifLbl = new Label("Notifications");

        rootBox.getChildren().add(notifLbl);

        for (String notif : notifications) {
            Button notification = new Button(notif);
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
