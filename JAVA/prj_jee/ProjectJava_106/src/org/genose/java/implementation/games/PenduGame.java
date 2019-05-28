package org.genose.java.implementation.games;

import org.genose.java.implementation.observable.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Locale;

import org.genose.java.implementation.dicomots.DicoMots;

public class PenduGame extends Observable {

	static PrintStream consoleLog = System.out;
	static BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
	
	final  static int NBRETRY_DEFAULT = 10;
	
	static int nbRetry = NBRETRY_DEFAULT;
	
	private ArrayList<String> lsUsableChars = new ArrayList<String>();
	private  ArrayList<String> lsMatchedChars = new ArrayList<String>();
	private String sAskedWord = "LOlidoll";
	private String sAskedWordMasked = ""; // new StringBuffer(); 
	private int ciStartChar = 64;
	private int iLengthAskedWord = sAskedWord.length();
	private Boolean bCharFound = true;
	private String sCurrentCharInput = "";
	
	private DicoMots dictionnaryWordGame;
	

	/**
	 * 
	 */
	public PenduGame() {
		super();
		dictionnaryWordGame = new DicoMots(DicoMots.DEFAULT_DICO_LANG);
	}

	public void initGame() {
		
		
		sCurrentCharInput = "";
		
		bCharFound = true;
		
		sAskedWordMasked = "";
		
		lsMatchedChars.clear();
		
		lsUsableChars.clear();
		
		for (int iCharIndex = 1; iCharIndex <= 26; iCharIndex++) {
			String scurrentString = String.format("%c", ((char) (ciStartChar + iCharIndex)))
					.toLowerCase(Locale.getDefault());

			lsUsableChars.add(scurrentString.toUpperCase(Locale.getDefault()));
		}
		
	}
	
	public String nextWord() {
		
		return dictionnaryWordGame.genererMotAleatoire();
		
	}
	
	public boolean jouer()  {
		
		Boolean gameBeenStriked = false;
		bCharFound = false;
		
		
		sAskedWordMasked = sAskedWord;
		for (int iCharIndex = 1; iCharIndex <= lsUsableChars.size(); iCharIndex++) {
			String scurrentString = String.format("%c", ((char) (ciStartChar + iCharIndex)))
					.toUpperCase(Locale.getDefault());
			
			// *********************************
			// technic 1, but mostly for debug
			// :: sAskedWordMasked = sAskedWordMasked.replaceAll("["+scurrentString+"]", "!");
			// *********************************
			
			// compare user input with current char
			if (String.format("%s", sCurrentCharInput).indexOf(scurrentString) == (0)) {

				// mask used char
				lsUsableChars.set(iCharIndex - 1, "*");

				// test previous user input
				if (sCurrentCharInput.length() == 0)
					continue;
				
				// lookup for input char in asked word
				if (sAskedWord.toUpperCase(Locale.getDefault())
						.contains(sCurrentCharInput.toUpperCase(Locale.getDefault()))) {
					
					// *********************************
					// technic 1, but mostly for debug
					// :: sAskedWordMasked = sAskedWordMasked.replaceAll("[!]", " " + scurrentString + " ");
					// *********************************
					
					bCharFound = true;
					// add matched char beside asked word
					lsMatchedChars.add(scurrentString);
					
				}

			}
			
			// *********************************
			// technic 1, but mostly for debug
			// :: sAskedWordMasked = sAskedWordMasked.replaceAll("[!]", " _ ");
			// *********************************
		}
		/* ************************************* */
		if (lsMatchedChars.size() > 0) {
			String lsmatchedupper =  String.join("|", lsMatchedChars).toUpperCase();
			sAskedWordMasked = sAskedWord.toUpperCase().replaceAll(("[^" +lsmatchedupper + "]"), " _ ");
		} else {
			sAskedWordMasked = sAskedWord.replaceAll(("[^-]"), " _ ");
		}
		/* ************************************* */
		if (!bCharFound)
			--nbRetry;
		
		
		
		/* ************************************* */
		if (sAskedWord.toUpperCase().replaceAll("[ ]", "").indexOf(sAskedWordMasked.toUpperCase().replaceAll("[ ]", "")) == 0)
			gameBeenStriked = true;
		/* ************************************* */
		try {
			notifyChange(this);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return (!gameBeenStriked) && (sCurrentCharInput.compareToIgnoreCase("0") != (0)) && ((nbRetry >= 0));	
	}
	
	public void getGamerInput() {
		consoleLog.print(" Entrer votre joker N" + (nbRetry) + " : "+sAskedWord.toUpperCase().replaceAll("[ ]", "").indexOf(sAskedWordMasked.toUpperCase().replaceAll("[ ]", "")));
		try {
			sCurrentCharInput = String.format("%s", (consoleInput.readLine())).toUpperCase(Locale.getDefault());
			// :: consoleLog.println(" readed : " + sCurrentCharInput);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* ************************************* */
		consoleLog.println(" ");
		/* ************************************* */
		//if (sCurrentCharInput.length() == 0)
		//	continue;

		// if (sCurrentCharInput.contains(sAskedWord))
		//	continue;

	}
	@Override
	public void notifyShow() {
		

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
		consoleLog.println(" " + lsUsableChars);
		consoleLog.println(" ");
		/* ************************************* */
		consoleLog.println(String.format(" Essai restant : %d", nbRetry));
		consoleLog.println(" ");
		/* ************************************* */
		consoleLog.println(
				String.format("%1$" + sAskedWord.length() + "s", ((nbRetry == 0) ? sAskedWord : sAskedWordMasked)));

		getGamerInput();
	}
	
	
}
