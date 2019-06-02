package MainWindow;



import org.genose.java.implementation.games.*;
import org.genose.java.implementation.streams.ConsoleStreamTextAreaField;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Window;

public class MainWindow extends Window {

	@FXML
	private TextArea consoleLogTextField;
	
	@FXML
	private TextField consoleInputField;
	
	@FXML
	private Button boutonEnvoyerResponse;
	
	@FXML
	private CheckBox checkBoxToggleUniqueCharMode;
	
	
	// this will be usable as System.out and System.in for interaction purposes
	private org.genose.java.implementation.streams.ConsoleStreamTextAreaField consoleLog;
	private org.genose.java.implementation.streams.ConsoleStreamTextAreaField consoleLogInput;
	
	// our game and its notifiers ...
	private org.genose.java.implementation.games.PenduGame aJeuDuPenduGame = null;
	private org.genose.java.implementation.games.gameobserver aGameObserver = null;

	private Boolean bplayWithOneCharMode = true;
	
	/* ******************************************************* */
	/**
	 * 
	 * default constructor
	 * 
	 */
	public MainWindow() {
		// initialize();
	}
	
	/* ******************************************************* */
	/**
	 * 
	 * @return Quit Application
	 * 
	 */
	@FXML
	public void quitApp()
	{
		System.out.println(" Quit app request ... ");
		Platform.exit();
	}
	
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

		/* ****************************************************** */
		try {
			
			
			
			
			aJeuDuPenduGame = new PenduGame();
			aGameObserver = new gameobserver();
			
			consoleLog = new ConsoleStreamTextAreaField(consoleLogTextField);
			consoleLogInput = new ConsoleStreamTextAreaField(consoleInputField);
		 
			aJeuDuPenduGame.setConsoleLog(consoleLog);
			aJeuDuPenduGame.setConsoleLogInput(consoleLogInput);
			
			 
			aJeuDuPenduGame.addObserver(aGameObserver);
			
			windowInitNewGame();
			bplayWithOneCharMode  = checkBoxToggleUniqueCharMode.isSelected();
		}catch(Exception ev) {
			ev.printStackTrace();
		}

		
	}
	/* ******************************************************* */
	/**
	 * 
	 * Step on game, all mechanism is self controlled
	 */
	@FXML
	public void jouer() {
		
		
		
		if(consoleInputField.getLength() >0) {
			consoleLog.clear();
			
			Integer gameQuitStatus =  aJeuDuPenduGame.getGameReasonQuit() ;
			
			if( gameQuitStatus == PenduGame.GAMESTATUS.GAMESTATUS_RETRYCONTINUE.getValue() ) {
			
				aJeuDuPenduGame.getGamerInput();
				aJeuDuPenduGame.jouer(); 
			}else{
				
				consoleLog.print(" Fin de cette partie ....");
			}
			// clean field user for input
			consoleInputField.clear();
			// check out if game still playable or not ...
			gameQuitStatus =  aJeuDuPenduGame.getGameReasonQuit() ; 
			// do adapted action
			if( gameQuitStatus == ((int)PenduGame.GAMESTATUS.GAMESTATUS_GAMEOVER.getValue())) {
				consoleLog.println("Partie Perdu ...");
			}else if( gameQuitStatus == ((int)PenduGame.GAMESTATUS.GAMESTATUS_SUCCESS.getValue())) {
					consoleLog.println("Partie Gagnee ...");
			}else if( gameQuitStatus == ((int)PenduGame.GAMESTATUS.GAMESTATUS_QUIT.getValue())) {
				consoleLog.println("Partie quit ...");
			} 
		} 
	}
	/** 
	 * 
	 * 
	 * 
	 * 
	 */
	
	@FXML
	public void validerReponsePressed(ActionEvent ev_source) {

		System.out.println(getClass()+" :: "+ev_source.getSource());
		
		jouer();
	}
	
	/** 
	 * 
	 * 
	 * 
	 * 
	 */
	
	@FXML
	public void validerReponseKeyPressed() {
	

		//System.out.println(getClass()+" :: "+ev_source.getSource());
		
		if(bplayWithOneCharMode) {
			jouer();
		}
	}
	/**
	 * 
	 * 
	 */
	@FXML
	public void togglePlayWithOneCharInput() {
		
		bplayWithOneCharMode  = checkBoxToggleUniqueCharMode.isSelected();
	}
	/* ******************************************************* */
	/**
	 * 
	 * init a new party
	 * 
	 */
	public void windowInitNewGame() {
		
		try {
			aJeuDuPenduGame.initNewGameParty();
			// clean interface GUI/IHM
			consoleLog.clear();
			consoleLogInput.clear();
			// print some tips 
			consoleLog.println("Pret a jouer ... ? " );
		
			aJeuDuPenduGame.notifyChange();
		} catch (Exception EV_ERR_CREATE_INIT_GAME) {
			// TODO Auto-generated catch block
			EV_ERR_CREATE_INIT_GAME.printStackTrace();
		}
	}
	

}
