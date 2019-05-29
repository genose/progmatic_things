package hellofx;



import org.genose.java.implementation.games.PenduGame;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class MainWindow {

	@FXML
	private TextField consoleLogTextField;
	
	@FXML
	private TextField consoleInputField;
	
	private GestionnaireTextField fieldConsoleLog;
	
	
	private org.genose.java.implementation.games.PenduGame jeudupenduGame = new PenduGame();
	
	@FXML
	private void initialize()
	{

		fieldConsoleLog = new GestionnaireTextField(consoleLogTextField);
		jeudupenduGame.setConsoleLog(fieldConsoleLog);
		
	}
	

}
