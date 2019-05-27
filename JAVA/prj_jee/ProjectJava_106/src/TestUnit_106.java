import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

public class TestUnit_106 {

	static PrintStream consoleLog = System.out;
	static BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int nbRetry = 5;
		
		ArrayList<String> lsUsableChars = new ArrayList<String>();
		ArrayList<String> lsMatchedChars = new ArrayList<String>();
		String sAskedWord ="LOlidoll";
		String sAskedWordMasked = ""; // new StringBuffer();
		String sAskedWordMaskedLast ="";
		char cCharStar = 63;
		int ciStartChar = 64;// (int) Character.getNumericValue( cCharStar );
		int iLengthAskedWord = sAskedWord.length();
		Boolean bCharFound = true;
		String sCurrentCharInput = "";
		
		
		for (int iCharIndex = 1; iCharIndex <= 26 ; iCharIndex++) {
			String scurrentString = String.format("%c", ((char)(ciStartChar + iCharIndex)) ).toLowerCase(Locale.getDefault());
			
			lsUsableChars.add( scurrentString.toUpperCase(Locale.getDefault()) );
		}
		

		 sAskedWordMasked = " _ ";
		 sAskedWordMasked = sAskedWordMasked.repeat(iLengthAskedWord);
		
		
		sAskedWordMasked = sAskedWordMaskedLast = sAskedWord;
		
		
		do {
			
			
			 
			sAskedWordMasked = sAskedWord;
			for (int iCharIndex = 1; iCharIndex <= lsUsableChars.size() ; iCharIndex++) {
				String scurrentString = String.format("%c", ((char)(ciStartChar + iCharIndex)) ).toLowerCase(Locale.getDefault());
				//consoleLog.println( "set  "+scurrentString );
				
				// sAskedWordMasked = sAskedWordMasked.replaceAll("[^"+(scurrentString.toUpperCase(Locale.getDefault()))+"]", "!");
				// sAskedWordMasked = sAskedWordMasked.replaceAll("["+(scurrentString.toLowerCase(Locale.getDefault()))+"]", "!");
				
				// compare user input with current char 
				if ( String.format("%s",sCurrentCharInput).indexOf(scurrentString ) == (0) ){
					
					// mask used char
					lsUsableChars.set(iCharIndex-1, "*" );

					if(sCurrentCharInput.length() == 0) continue;
					// lookup for input char in asked word
					if(sAskedWord.contains( sCurrentCharInput.toLowerCase() ) ) {
						sAskedWordMasked = sAskedWordMasked.replaceAll("[!]", " "+scurrentString+" ");
						bCharFound = true;
						
						lsMatchedChars.add(scurrentString);
						// sAskedWordMasked = sAskedWordMasked.replaceAll("", "");
						// sAskedWordMaskedLast = sAskedWordMaskedLast.replaceAll(sAskedWordMasked.toCharArray(), "_") ;
						 
						//String res = str.substring(0, pos) + rep + str.substring(pos + 1);
					}
					
					
					
				} 
				
				sAskedWordMasked = sAskedWordMasked.replaceAll("[!]", " _ ");
			}
			sAskedWordMasked = sAskedWord.replaceAll(("[^"+lsMatchedChars.get(0)+"]"), " _ ");
			if(!bCharFound )
				--nbRetry;
			bCharFound = false;
			
			consoleLog.println("******************************************************************");
			consoleLog.println( " "+lsUsableChars );
			
			consoleLog.println( String.format(" Essai restant : %d", nbRetry) );
			consoleLog.println( " " );
			consoleLog.println( ((nbRetry<=3)? "_________   ":""));
			consoleLog.println( ((nbRetry<=3)? "=========   ":""));
			consoleLog.println( ((nbRetry<=4)? "||//    " + ((nbRetry<=3)? "|   ":" ") :" ") );
			consoleLog.println( ((nbRetry<=5)? ((nbRetry<=2)? "||/     @   " : "||/" ):" ") );
			consoleLog.println( ((nbRetry<=6)? ((nbRetry<=1)? "||     /|\\ " : "||"):" ") );
			consoleLog.println( ((nbRetry<=7)? ((nbRetry<1)? "||     / \\ " : "||" ) :" ") );
			consoleLog.println( ((nbRetry<=8)? "||__________":"") );
			consoleLog.println( ((nbRetry<=9)? "||==========":"") );
			consoleLog.println( ((nbRetry<10)? "!!        !!":"") );
			consoleLog.println( " ");
			

			
			consoleLog.println(  String.format("%1$"+sAskedWord.length()+ "s",  (( nbRetry == 0 )? sAskedWord :sAskedWordMasked) ));
			consoleLog.print(" Entrer votre joker n°"+(nbRetry)+" : ");
			consoleLog.println(" :::: "+sAskedWord.indexOf( sAskedWordMasked.replaceAll("[ ]", "")) );
			try {
			sCurrentCharInput = String.format("%s", (consoleInput.readLine() ));
			consoleLog.println(" readed : "+sCurrentCharInput);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			if(sCurrentCharInput.length() == 0) continue;
			
			if(sCurrentCharInput.contains(sAskedWord)) continue;
			
			
			
			
		} while ( (sCurrentCharInput.compareToIgnoreCase("0") != (0)) && (( nbRetry >=0) )  );
			
			

		consoleLog.println(" .... finish ");
	}

}
