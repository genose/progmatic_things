import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.genose.java.implementation.games.PenduGame;
import org.genose.java.implementation.games.gameobserver;



public class TestUnit_106 {

	static PrintStream consoleLog = System.out;
	static BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

	static PenduGame jeudupenduGame = new PenduGame();
	
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
			jeudupenduGame.addObserver(mygameviewer);
			jeudupenduGame.initNewGameParty();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* ****************************************************** */
		do {

			
			
		consoleLog.println(" .... ");

		} while ( jeudupenduGame.jouer() == PenduGame.GAMESTATUS.GAMESTATUS_RETRYCONTINUE.getValue() );

		consoleLog.println(" .... finish ");
	}

}
