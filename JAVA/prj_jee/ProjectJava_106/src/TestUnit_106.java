import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.genose.java.implementation.games.PenduGame;
import org.genose.java.implementation.games.gameobserver;



public class TestUnit_106 {

	static PrintStream consoleLog = System.out;
	static BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

	static org.genose.java.implementation.games.PenduGame aJeuDuPenduGame = new org.genose.java.implementation.games.PenduGame();
	
	public static void main(String[] args) {


		/*
	    byte[] invalidBytes = " ".getBytes();
	    byte[] validBytes = "(c)".getBytes();
	    CharsetDecoder decoder = Charset.forName("US-ASCII").newDecoder();
	    CharBuffer buffer =null;
		try {
			buffer = decoder.decode(ByteBuffer.wrap(validBytes));
		} catch (CharacterCodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    System.out.println(Arrays.toString(buffer.array()));
	    

	    try {
			buffer = decoder.decode(ByteBuffer.wrap(invalidBytes));
		} catch (CharacterCodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    System.out.println(Arrays.toString(buffer.array()));
		*/
		/* ****************************************************** */
		
		gameobserver mygameviewer = new gameobserver();
		/* ****************************************************** */
		try {
			aJeuDuPenduGame.addObserver(mygameviewer);
			aJeuDuPenduGame.initNewGameParty();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* ****************************************************** */
		try {
			Boolean bnotified = aJeuDuPenduGame.notifyChange(aJeuDuPenduGame);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		do {
			aJeuDuPenduGame.getGamerInput();
			aJeuDuPenduGame.jouer();
			
			
			consoleLog.println(" .... ");

		} while ( aJeuDuPenduGame.getGameReasonQuit() == PenduGame.GAMESTATUS.GAMESTATUS_RETRYCONTINUE.getValue() );
		
		Integer gameQuitStatus =  aJeuDuPenduGame.getGameReasonQuit() ; 
		// do adapted action
		if( gameQuitStatus == ((int)PenduGame.GAMESTATUS.GAMESTATUS_GAMEOVER.getValue())) {
			consoleLog.println("Partie Perdu ...");
		}else if( gameQuitStatus == ((int)PenduGame.GAMESTATUS.GAMESTATUS_SUCCESS.getValue())) {
				consoleLog.println("Partie Gagnee ...");
		}else if( gameQuitStatus == ((int)PenduGame.GAMESTATUS.GAMESTATUS_QUIT.getValue())) {
			consoleLog.println("Partie quit ...");
		} 
		
		consoleLog.println(" .... finish ");
	}

}
