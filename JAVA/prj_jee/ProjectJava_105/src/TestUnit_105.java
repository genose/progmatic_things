import org.genose.implementation.*;
import org.genose.implementation.observable.Observable;

import games.*;
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
