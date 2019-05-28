import org.genose.java.implementation.*;

import org.genose.java.implementation.lotogames.*;

public class TestUnit_105 {

	
	
	
	
	
	public static void main(String[] args) {
		
		LotoGame lotoClassiqueGame = new LotoGame();
		
		try {
			lotoClassiqueGame.addGameComposant( new LotoGameComposant("BINGO", 10, 4) );
			lotoClassiqueGame.addGameComposant( ((LotoGameComposant) new Object()) );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}
