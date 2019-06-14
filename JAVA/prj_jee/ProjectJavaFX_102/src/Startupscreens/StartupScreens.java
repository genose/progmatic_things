package StartupScreens;
 
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;

/**
 * @author xenon
 *
 */
public class StartupScreens {

	@FXML
	private Label aStartupText;
	/* ******************************************************* */
	/**
	 * 
	 * JavaFX Default init window
	 * you can t access to window / scene element until is not showed once 
	 * 
	 */
	@FXML
	public void initialize()
	{
		aStartupText.setText("Booting Up ...");
	}
}
