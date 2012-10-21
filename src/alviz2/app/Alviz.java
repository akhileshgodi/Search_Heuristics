
package alviz2.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.Scene;

import alviz2.app.ColorPalette;

public class Alviz extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		BorderPane rootPane = (BorderPane) FXMLLoader.load(getClass().getResource("Alviz.fxml"));
		Scene scene = new Scene(rootPane, 1000, 800);

		stage.setTitle("Alviz 2");
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
	
}