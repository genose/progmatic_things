package MainWindow;



import org.genose.java.implementation.games.*;
import org.genose.java.implementation.streams.ConsoleStreamTextAreaField;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MainWindow {

	@FXML
	private TextArea consoleLogTextField;
	
	@FXML
	private TextField consoleInputField;
	@FXML
	private Button boutonEnvoyerResponse;
	
	private ConsoleStreamTextAreaField fieldConsoleLog;
	private ConsoleStreamTextAreaField fieldConsoleLogInput;
	
	
	private org.genose.java.implementation.games.PenduGame jeudupenduGame = new PenduGame();
	private org.genose.java.implementation.games.gameobserver mygameviewer = new gameobserver();
	@FXML
	public void initialize()
	{

		fieldConsoleLog = new ConsoleStreamTextAreaField(consoleLogTextField);
		fieldConsoleLogInput = new ConsoleStreamTextAreaField(consoleInputField);
	 
		jeudupenduGame.setConsoleLog(fieldConsoleLog);
		jeudupenduGame.setConsoleLogInput(fieldConsoleLogInput);
		
		 
		/* ****************************************************** */
		try {
			jeudupenduGame.addObserver(mygameviewer);
		}catch(Exception ev) {
			ev.printStackTrace();
		}

		newGameInit();
	}
	
	@FXML
	public void jouer() {
		if(consoleInputField.getLength() >0) {
			fieldConsoleLog.clear();
			
			Integer gameQuitStatus =  jeudupenduGame.getGameReasonQuit() ;
			
			if( gameQuitStatus == PenduGame.GAMESTATUS.GAMESTATUS_RETRYCONTINUE.getValue() ) {
			
				jeudupenduGame.getGamerInput();
				jeudupenduGame.jouer();
				
				
			}else{
				
				fieldConsoleLog.print(" Fin de cette partie ....");
			}
			consoleInputField.clear();
			gameQuitStatus =  jeudupenduGame.getGameReasonQuit() ; 
			
			if( gameQuitStatus == ((int)PenduGame.GAMESTATUS.GAMESTATUS_GAMEOVER.getValue())) {
				fieldConsoleLog.println("Partie Perdu ...");
			}else if( gameQuitStatus == ((int)PenduGame.GAMESTATUS.GAMESTATUS_SUCCESS.getValue())) {
					fieldConsoleLog.println("Partie Gagnee ...");
			}else if( gameQuitStatus == ((int)PenduGame.GAMESTATUS.GAMESTATUS_QUIT.getValue())) {
				fieldConsoleLog.println("Partie quit ...");
			}
			
			
		}
	
		
		
	}
	
	
	public MainWindow() {
		// initialize();
	}
	
	public void newGameInit() {
		jeudupenduGame.initNewGameParty();
		fieldConsoleLog.clear();
		fieldConsoleLog.println("Pret a jouer ... ? " );
		jeudupenduGame.jouer();
	}

}
