package games;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

import org.genose.implementation.observable.Observable;

public class LotoGame extends Observable implements LotoInterface {


	private List<Integer> iUsableNumbers 			= new ArrayList<Integer>();
	private List<Integer> iAlereadyUsedNumbers 		= new ArrayList<Integer>();
	private Map<String, ArrayList> oListTirage 		= new HashMap<String,ArrayList>();
	private List<Object> oComposantTirage 			= new ArrayList<Object>();
	
	public void shuffle() throws Exception
	{
		shuffle(null);
		try {
			notifyChange(this);
		} catch (Exception EV_ERR_SHUFFLE_GAME) {
			// TODO Auto-generated catch block
			EV_ERR_SHUFFLE_GAME.printStackTrace();
			throw EV_ERR_SHUFFLE_GAME;
		}
	}

	@Override
	public Map<String, Object> shuffle(List<Integer> usableNumber)  throws Exception {
		// TODO Auto-generated method stub
		
		ArrayList oListTirageCurrent = new ArrayList();
		for (Object objComposantTirage : oComposantTirage) {
			
			((LotoGameComposant)objComposantTirage).shuffle(usableNumber);
			
			oListTirageCurrent.add( ((LotoGameComposant) objComposantTirage).getShuffledNumbers() );
			
		}
		Date dateShuffle = new Date();
		String dateStr = dateShuffle.toString();
		
		oListTirage.add(dateStr ,  ((Object) oListTirageCurrent) );
		
		return oListTirageCurrent;
	}
	
}
