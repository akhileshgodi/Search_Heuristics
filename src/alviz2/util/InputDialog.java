
package alviz2.util;

import javafx.stage.Stage;
import javafx.stage.StageBuilder;
import javafx.stage.StageStyle;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.layout.Region;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import alviz2.util.IntegerRangeValidator;

public class InputDialog  {

	private InputDialog() {}

	public static Integer getIntegerInput(String title, String message, int low, int high) {
		Integer retInp = null;
		final IntegerRangeValidator validator = new IntegerRangeValidator(low, high);

		GridPane root = GridPaneBuilder.create()
									   .hgap(10)
									   .vgap(10)
									   .prefHeight(Region.USE_COMPUTED_SIZE)
									   .prefWidth(Region.USE_COMPUTED_SIZE)
									   .build();
		final Stage modalDlg = StageBuilder.create()
								     .title(title)
								     .resizable(false)
									 .style(StageStyle.UTILITY)
									 .build();
		modalDlg.initModality(Modality.APPLICATION_MODAL);
		Scene inpScene = new Scene(root);

		root.add(new Label(message), 1, 1);
		final TextField inpField = new TextField();
		root.add(inpField, 2,1);
		Button bt = new Button("Ok");
		bt.setDefaultButton(true);
		bt.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent evt) {
				try {
					Integer inp = Integer.valueOf(inpField.getText());
					if (!validator.validate(inp)) {
						inpField.setText("");
					}
					else {
						modalDlg.close();
					}
				}
				catch(NumberFormatException ex) {
					inpField.setText("");
				}
			}
		});
		root.add(bt, 3, 1);
		bt = new Button("Cancel");
		bt.setCancelButton(true);
		bt.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void	handle(ActionEvent evt) {
				inpField.setText("");
				modalDlg.close();
			}
		});
		root.add(bt, 3, 2);

		modalDlg.setScene(inpScene);
		modalDlg.showAndWait();
		try {
			retInp = Integer.valueOf(inpField.getText());
		}
		catch(NumberFormatException ex) {}

		return retInp;
	}
	
}