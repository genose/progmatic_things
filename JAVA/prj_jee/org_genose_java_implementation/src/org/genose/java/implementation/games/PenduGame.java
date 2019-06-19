package org.genose.java.implementation.games;

import org.genose.java.implementation.observable.*;

import org.genose.java.implementation.streams.ConsoleStream;

import genose.java.implementation.dicomots.DicoMots;

import java.io.BufferedReader; 
import java.io.InputStreamReader; 
import java.util.ArrayList;
import java.util.Locale;

import org.genose.java.implementation.games.*;

public class PenduGame extends Observable {

	// static PrintStream consoleLog = System.out;
	static ConsoleStream consoleLog;
	static ConsoleStream consoleInput; // = new BufferedReader(new InputStreamReader(System.in));

	final static int NBRETRY_DEFAULT = 10;

	static int nbRetry = NBRETRY_DEFAULT;

	private ArrayList<String> lsUsableChars = new ArrayList<String>();
	private ArrayList<String> lsEntryHistory = new ArrayList<String>();
	private ArrayList<String> lsMatchedChars = new ArrayList<String>();
	/* ************************************* */
	private String sAskedWord = "LOlidoll";
	private String sAskedWordMasked = "";
	/* ************************************* */
	private int ciStartChar = 64;
	private int iLengthAskedWord = sAskedWord.length();
	private Boolean bCharFound = true;
	/* ************************************* */
	private String sCurrentCharInput = "";
	/* ************************************* */
	private DicoMots dictionnaryWordGame;

	/* ************************************* */
	public enum GAMESTATUS {
		GAMESTATUS_QUIT(2), GAMESTATUS_GAMEOVER(-1), GAMESTATUS_RETRYCONTINUE(1), GAMESTATUS_SUCCESS(0);

		private int value;
		private static java.util.HashMap<Object, Object> map = new java.util.HashMap<>();

		private GAMESTATUS(int value) {
			this.value = value;
		}

		static {
			for (GAMESTATUS addrType : GAMESTATUS.values()) {
				map.put(addrType.value, addrType);
			}
		}

		public static GAMESTATUS valueOf(int pageType) {
			return (GAMESTATUS) map.get(pageType);
		}

		public int getValue() {
			return value;
		}

		public String getEnumByString(String code) {
			for (GAMESTATUS e : GAMESTATUS.values()) {
				if (code == String.valueOf(value))
					return e.name();
			}
			return null;
		}

		@Override
		public String toString() {
			for (GAMESTATUS e : GAMESTATUS.values()) {
				if (value == e.value)
					return e.name();
			}
			return "";
		}

	};

	/* ************************************* */
	private Integer iGameStatusOrQuitReason = GAMESTATUS.GAMESTATUS_RETRYCONTINUE.getValue();

	/* ************************************* */
	/**
	 * 
	 */
	public PenduGame() {
		super();
		dictionnaryWordGame = new DicoMots(DicoMots.DEFAULT_DICO_LANG); 

		consoleLog = new ConsoleStream(System.out);
		consoleInput = new ConsoleStream((new BufferedReader(new InputStreamReader(System.in))));
		if (dictionnaryWordGame.count() == 0) {
			throw new Error("Empty game definition .... ");
		} else if (dictionnaryWordGame.count() == 2) {
			consoleLog.println(" :: " + dictionnaryWordGame.toString());
		}
	}

	public void initNewGameParty() {

		iGameStatusOrQuitReason = GAMESTATUS.GAMESTATUS_RETRYCONTINUE.getValue();
		nbRetry = NBRETRY_DEFAULT;

		sCurrentCharInput = "";

		bCharFound = true;

		sAskedWordMasked = "";
		sAskedWord = nextWord();

		lsMatchedChars.clear();

		lsUsableChars.clear();

		lsEntryHistory.clear();

		for (int iCharIndex = 1; iCharIndex <= 26; iCharIndex++) {
			String scurrentString = String.format("%c", ((char) (ciStartChar + iCharIndex)))
					.toLowerCase(Locale.getDefault());

			lsUsableChars.add(scurrentString.toUpperCase(Locale.getDefault()));
		}

	}

	/* ************************************* */
	/**
	 * 
	 * @return String
	 */
	private String nextWord() {
		sAskedWord = dictionnaryWordGame.genererMotAleatoire();
		return sAskedWord;

	}

	/* ************************************* */
	/**
	 * 
	 * @return enum GAMESTATUS
	 */
	public Integer jouer() {

		Boolean gameBeenStriked = false;
		Boolean bCharFoundAlreadyUsed = false;
		sAskedWordMasked = sAskedWord;
		
		
		if (sCurrentCharInput.length() > 0)
		for (int iCharIndex = 1; iCharIndex <= lsUsableChars.size(); iCharIndex++) {
			String scurrentString = String.format("%c", ((char) (ciStartChar + iCharIndex)))
					.toUpperCase(Locale.getDefault());
			Integer iCompredPosString = String.format("%s", sCurrentCharInput).indexOf(scurrentString);
			
			// System.out.println(" compare " +sCurrentCharInput+"::"+scurrentString +"::"+String.valueOf( iCompredPosString));// compare user input with current char
			if (iCompredPosString  >= (0)) {
				
				// mask used char
				
				bCharFoundAlreadyUsed = lsUsableChars.indexOf(scurrentString) == (-1);
				
				if( !bCharFoundAlreadyUsed ) {
					lsUsableChars.set(iCharIndex - 1, "*");
				}

				 
				// lookup for input char in asked word
				if ( sAskedWord.toUpperCase(Locale.getDefault()).contains(scurrentString) ) {
					bCharFound = true;
					// add matched char beside asked word
					// also it test if it already added ... 
					// System.out.println( " Add matched "+scurrentString);
					bCharFoundAlreadyUsed = ((lsMatchedChars.indexOf(scurrentString) == (-1) ) ? lsMatchedChars.add(scurrentString) : true);

				}

			}

		}
		/* ************************************* */
		if (lsMatchedChars.size() > 0) {
			String lsmatchedupper = String.join("|", lsMatchedChars).toUpperCase();
			//
			sAskedWordMasked = sAskedWord.toUpperCase().replaceAll(("[^" + lsmatchedupper + "]"), " _ ");
			// sAskedWordMasked = sAskedWord.toUpperCase().replaceAll(("[" +lsmatchedupper +
			// "]"), " $1 ");
		} else {
			sAskedWordMasked = sAskedWord.replaceAll(("[^,]"), " _ ");
		}
		System.out.println(
				" :: " + String.valueOf(String.valueOf(lsUsableChars.indexOf(sCurrentCharInput)).compareTo("*"))
						+ String.valueOf(lsUsableChars.indexOf(sCurrentCharInput)));
		

		/* ************************************* */
		if ( 
				// (sAskedWord.toUpperCase().compareToIgnoreCase( sCurrentCharInput) >0) ||
				sAskedWord.toUpperCase().replaceAll("[ ]", "")
				.indexOf(sAskedWordMasked.toUpperCase().replaceAll("[ ]", "")) == 0) {
			gameBeenStriked = true;
		}

		/* ************************************* */
		if(!gameBeenStriked) {
			/* ************************************* */
			if ( !bCharFoundAlreadyUsed && (!lsMatchedChars.contains(sCurrentCharInput))) {
				--nbRetry;
			}
			/* ************************************* */
		}
		/* ************************************* */
		if (lsEntryHistory.indexOf(sCurrentCharInput) == (-1)) {
			lsEntryHistory.add(sCurrentCharInput);
		}
		
		/* ************************************* */
		try {
			notifyChange(this);
		} catch (Exception e) {
			// TODO: handle exception
		}
		bCharFound = false;

		//
		if (sCurrentCharInput.compareToIgnoreCase("0") == (0))
			iGameStatusOrQuitReason = GAMESTATUS.GAMESTATUS_QUIT.getValue();
		else if (nbRetry == 0)
			iGameStatusOrQuitReason = GAMESTATUS.GAMESTATUS_GAMEOVER.getValue();
		else
			iGameStatusOrQuitReason = ((!gameBeenStriked) ? GAMESTATUS.GAMESTATUS_RETRYCONTINUE
					: GAMESTATUS.GAMESTATUS_SUCCESS).getValue();

		// game been stroked ;; GG ;;
		return iGameStatusOrQuitReason;
	}

	/* ************************************* */
	/**
	 * 
	 * Read user input
	 * 
	 */
	public void getGamerInput() {
		try {
			sCurrentCharInput = String.format("%s", (consoleInput.readLine())).toUpperCase(Locale.getDefault());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* ************************************* */
		consoleLog.println(" ");
		// System.out.println(" Quit readline ");
		/* ************************************* */
		// if (sCurrentCharInput.length() == 0)
		// continue;

		// if (sCurrentCharInput.contains(sAskedWord))
		// continue;

	}

	/* ************************************* */
	/**
	 * 
	 * 
	 */
	@Override
	public Boolean notifyShow() {
		/* ************************************* */
		consoleLog.println("******************************************************************");

		/* ************************************* */
		consoleLog.println(" ");
		consoleLog.println(((nbRetry <= 3) ? "_________   " : ""));
		consoleLog.println(((nbRetry <= 3) ? "=========   " : ""));
		consoleLog.println(((nbRetry <= 4) ? "||//    " + ((nbRetry <= 3) ? "|   " : " ") : " "));
		consoleLog.println(((nbRetry <= 5) ? ((nbRetry <= 2) ? "||/   \\\\|//   " : "||/") : " "));
		consoleLog.println(((nbRetry <= 5) ? ((nbRetry <= 2) ? "||    x | x   " : "||") : " "));
		consoleLog.println(((nbRetry <= 5) ? ((nbRetry <= 2) ? "||     /-\\  " : "||") : " "));
		consoleLog.println(((nbRetry <= 5) ? ((nbRetry <= 2) ? "||      |   " : "||") : " "));
		consoleLog.println(((nbRetry <= 6) ? ((nbRetry <= 1) ? "||     /|\\ " : "||") : " "));
		consoleLog.println(((nbRetry <= 6) ? ((nbRetry <= 1) ? "||    | | | " : "||") : " "));
		consoleLog.println(((nbRetry <= 6) ? ((nbRetry <= 1) ? "||      | " : "||") : " "));
		consoleLog.println(((nbRetry <= 6) ? ((nbRetry < 1) ? "||     / \\ " : "||") : " "));
		consoleLog.println(((nbRetry <= 7) ? ((nbRetry < 1) ? "||     | | " : "||") : " "));
		consoleLog.println(((nbRetry <= 7) ? ((nbRetry < 1) ? "||     / \\ " : "||") : " "));
		consoleLog.println(((nbRetry <= 7) ? "||   _____________" : ""));
		consoleLog.println(((nbRetry <= 7) ? "||   / ________  /|" : ""));
		consoleLog.println(((nbRetry <= 8) ? "||  / / / / / / / /" : ""));
		consoleLog.println(((nbRetry <= 8) ? "|| / /_/_/_/_/ / /!" : ""));
		consoleLog.println(((nbRetry <= 9) ? "||/___________/ /!!" : ""));
		consoleLog.println(((nbRetry <= 9) ? "||____________|/" : ""));
		consoleLog.println(((nbRetry < 10) ? "!! !!        !!" : ""));
		consoleLog.println(((nbRetry < 10) ? "!!            " : ""));
		consoleLog.println(" ");
		/* ************************************* */
		consoleLog.println(" " + String.join(" | ", lsUsableChars));
		consoleLog.println(" ");
		/* ************************************* */
		consoleLog.println(String.format(" Essai restant : %d", nbRetry));
		consoleLog.println(" ");
		/* ************************************* */
		consoleLog.println(
				String.format("%1$" + sAskedWord.length() + "s", ((nbRetry == 0) ? sAskedWord : sAskedWordMasked)));

		consoleLog.print(" Entrer votre joker N" + String.valueOf(nbRetry) + " : ");
		return true;
	}

	/**
	 * 
	 * @param fieldConsoleLog
	 */
	public void setConsoleLog(ConsoleStream fieldConsoleLog) {

		consoleLog = fieldConsoleLog;

	}

	/**
	 * 
	 * @param fieldConsoleLogInput
	 */
	public void setConsoleLogInput(ConsoleStream fieldConsoleLogInput) {
		// TODO Auto-generated method stub
		consoleInput = fieldConsoleLogInput;

	}

	public int getGameReasonQuit() {
		// TODO Auto-generated method stub
		return iGameStatusOrQuitReason;
	}

}
