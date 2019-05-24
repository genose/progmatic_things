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

	private List<Integer> iListOfUsableNumbers = new ArrayList<Integer>();
	private Map<String, Map<String, Object>> oListTirage = new HashMap<String, Map<String, Object>>();
	private List<LotoGameComposant> oComposantTirage = new ArrayList<LotoGameComposant>();

	
	@Override
	public void shuffle(Boolean bDoNotify) throws Exception {
		// TODO Auto-generated method stub
		shuffle(iListOfUsableNumbers, bDoNotify);	
	}
	@Override
	public Map<String, Object> shuffle(List<Integer> userUsableNumber, Boolean bDoNotify) throws Exception {
		Map<String, Object> oListTirageCurrent = new HashMap<String, Object>();
		
		for (LotoGameComposant objComposantTirage : oComposantTirage) {

			if (!(objComposantTirage instanceof LotoGameComposant))
				continue;

			objComposantTirage.shuffle((((userUsableNumber != null) && (userUsableNumber.size() > 0)) ? userUsableNumber
					: (((iListOfUsableNumbers != null) && (iListOfUsableNumbers.size() > 0)) ? iListOfUsableNumbers
							: null)),
					false

			);

			oListTirageCurrent.put(objComposantTirage.getComposantName(), objComposantTirage.getShuffledNumbers());

		}
		/* *************************************************** */
		Date dateShuffle = new Date();
		String dateStr = dateShuffle.toString();
		/* *************************************************** */
		oListTirage.put(dateStr, oListTirageCurrent);
		/* *************************************************** */
		if (bDoNotify) {
			try {
				notifyChange(this);
			} catch (Exception EV_ERR_SHUFFLE_GAME) {
				// TODO Auto-generated catch block
				EV_ERR_SHUFFLE_GAME.printStackTrace();
				throw EV_ERR_SHUFFLE_GAME;
			}
		} 
		
		return oListTirageCurrent;
	}

	/*
	 * **
	 * 
	 * 
	 */
	public Map<String, Map<String, Object>> getListTirage() {
		return oListTirage;
	}



}
