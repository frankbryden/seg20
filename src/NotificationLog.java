import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.ArrayList;

public class NotificationLog {

    private ArrayList<String> notifications;

    public NotificationLog(ArrayList<String> notifications) {
        this.notifications = notifications;
    }

    // TODO - add styling
    public void createNotifLog() {
        // Will use this later to create a messenger-like notification popup
        // Stage notifWindow = new Stage(StageStyle.UNDECORATED);
        Stage notifWindow = new Stage();
        notifWindow.setTitle("Notification Log");

        ScrollPane scrollPane = new ScrollPane();

        VBox rootBox = new VBox();

        for (String notif : notifications) {
            Button notification = new Button(notif);
            notification.setPrefWidth(270);
            notification.setWrapText(true);
            rootBox.getChildren().add(notification);
        }

        scrollPane.setContent(rootBox);

        Scene scene = new Scene(scrollPane, 270, 350);

        notifWindow.setScene(scene);
        notifWindow.show();
    }
}
