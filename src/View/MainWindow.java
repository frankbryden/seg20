package View;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainWindow extends Application {

	Stage window;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void start(Stage mainWindow) throws Exception {
		window = mainWindow;
		window.setTitle("Runway Redeclaration Tool");
		
		Button popUp = new Button("Click here to see pop-up");
		Button closeButton = new Button("Exit program");
		popUp.setOnAction(e -> PopUpWindow.display("The pop-up box"));
		closeButton.setOnAction(e -> closeProgram());
		
		window.setOnCloseRequest(e -> closeProgram());
		
		VBox mainLayout = new VBox(50);
		mainLayout.setAlignment(Pos.TOP_CENTER);
		mainLayout.getChildren().addAll(popUp, closeButton);
		Scene mainScene = new Scene(mainLayout, 1300, 800);
		window.setScene(mainScene);
		window.show();
	}
	
	private void closeProgram() {
		System.out.println("Exiting the program");
		window.close();
	}

}

