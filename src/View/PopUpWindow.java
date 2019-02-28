package View;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PopUpWindow {

	public static void display(String windowTitle) {
		
		Stage popUpWindow = new Stage();
		popUpWindow.initModality(Modality.APPLICATION_MODAL);
		popUpWindow.setTitle(windowTitle);
		
		VBox popUpLayout = new VBox(20);
		Scene popUpScene = new Scene(popUpLayout, 400, 200);
		popUpWindow.setScene(popUpScene);
		
		popUpWindow.show();
	}
}
