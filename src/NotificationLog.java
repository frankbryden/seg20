import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.ArrayList;

public class NotificationLog {

    private ArrayList<String> notifications;
    private Stage primaryStage;
    private final int WINDOW_WIDTH = 270;

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
            notification.setPrefWidth(WINDOW_WIDTH);
            notification.setWrapText(true);
            rootBox.getChildren().add(notification);
        }

        scrollPane.setContent(rootBox);

        Scene scene = new Scene(scrollPane, WINDOW_WIDTH, 350);

        notifWindow.setScene(scene);
        notifWindow.show();
    }
}
