/**
 * 
 */
package games;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author 59013-36-18
 *
 */
public class LotoGameComposant implements LotoInterface {

	private Integer numberOfUseable = 0;
	private Integer numberToUse = 0;
	private Integer complementNumberToUse = 0;
	// name of the composant
	private String composantName = "";

	List<Integer> shuffleableNumbers = new ArrayList<Integer>();
	List<Integer> shuffledNumbers = new ArrayList<Integer>();
	List<Integer> shuffledNumbersComplementary = new ArrayList<Integer>();

	public LotoGameComposant(String composantName, Integer numberToUse, Integer complementNumberToUse) {
		super();
		this.numberToUse = numberToUse + complementNumberToUse;
		this.complementNumberToUse = complementNumberToUse;
		this.composantName = composantName;
	}

	public LotoGameComposant(String composantName, Integer nbNumberToUse, Integer nbComplementNumberToUse,
			Integer nbOfUseable) {
		super();

		this.numberOfUseable = Math.min(nbOfUseable, nbNumberToUse + nbComplementNumberToUse);

		this.numberToUse = Math.max(0, Math.min(this.numberOfUseable, this.numberOfUseable - nbNumberToUse));
		this.complementNumberToUse = Math.max(0, Math.min(this.numberOfUseable - this.numberToUse,
				this.numberOfUseable - (this.numberToUse + nbComplementNumberToUse)));
		this.composantName = composantName;
	}

	public String getComposantName() {
		return composantName;
	}

	public void setComposantName(String composantName) {
		this.composantName = composantName;
	}

	/*
	 * **
	 * 
	 * Build list of usable numbers by range this is replaceable by a combined use
	 * of ;; Random() and shuffleableNumbers.add( ([Random()]).nextInt() ) ;;
	 * 
	 */

	public void createShuffleableNumbers() throws Exception {

		if (this.numberOfUseable == 0)
			return;

		int[] iUsableRangeNumbers = IntStream.range(1, this.numberOfUseable).unordered().toArray();

		shuffleableNumbers.clear();
		for (Integer shufflealeNumber : iUsableRangeNumbers) {
			shuffleableNumbers.add(shufflealeNumber);
		}

	}

	/*
	 * **
	 * 
	 * use list of numbers defined by user ;; use List<Integer> as parameter or
	 * pre-defined by range ;; use NULL as parameter for this
	 * 
	 */
	@Override
	public Map<String, Object> shuffle(List<Integer> userUsableNumber, Boolean bDoNotify) throws Exception {

		/* ************ cleanUp storage of previous shuffles *************** */
		shuffledNumbers.clear();
		shuffledNumbersComplementary.clear();
		/* ************** setup number shuffling *********** */
		if (userUsableNumber != null && userUsableNumber.size() > 0) {
			shuffleableNumbers.clear();
			shuffleableNumbers.addAll(userUsableNumber);
		} else if (shuffleableNumbers.size() == 0) {
			return getShuffledNumbers();
		}

		/* ********************** Get primary Numbers ****************** */
		if (numberToUse > 0) {
			// Shake-up numbers
			Collections.shuffle(shuffleableNumbers);
			for (Integer iShuffledNumber = numberToUse; iShuffledNumber >=1; iShuffledNumber--) {
				if (iShuffledNumber == 0 || shuffleableNumbers.size() == 0)
					break;
				
				Random rndShufflerPosition = new Random(shuffleableNumbers.size()-1);
				int rndPosition = rndShufflerPosition.nextInt();
				shuffledNumbers.add(shuffleableNumbers.get(rndPosition));
				// get number
				shuffleableNumbers.remove(rndPosition);
				// Shake-up numbers
				Collections.shuffle(shuffleableNumbers);
				
				
				
			}

		}

		/* ********************** Get Complementary Numbers ****************** */
		if (complementNumberToUse > 0) {
			// Shake-up numbers
			Collections.shuffle(shuffleableNumbers);
			
			for (Integer iShuffledNumber = complementNumberToUse; iShuffledNumber >=1; iShuffledNumber--) {
				if (iShuffledNumber == 0 || shuffleableNumbers.size() == 0)
					break;
				Random rndShufflerPosition = new Random(shuffleableNumbers.size()-1);
				int rndPosition = rndShufflerPosition.nextInt();
				// get number
				shuffledNumbersComplementary.add(shuffleableNumbers.get(rndPosition));
				shuffleableNumbers.remove(rndPosition);
				// Shake-up numbers
				Collections.shuffle(shuffleableNumbers);
			}
			shuffleableNumbers.removeAll(shuffledNumbersComplementary);
		}
		/* *************************************************** */
		return getShuffledNumbers();
	}
	
	/* **
	 * 
	 * 
	 */
	@Override
	public void shuffle(Boolean bDoNotify) throws Exception {
		// use pre-defined numbers, this can be defined by user or by constructor
		shuffle(((shuffleableNumbers.size() == 0) ? null : shuffleableNumbers), bDoNotify);
	}

	/*
	 * **
	 * 
	 * return last shuffle() usage
	 */
	public Map<String, Object> getShuffledNumbers() {

		Map<String, Object> tmpShuffledNumbers = new HashMap<String, Object>();
		/* ****************************************************** */
		if ((shuffledNumbers != null) && (shuffledNumbers.size() > 0)) {
			tmpShuffledNumbers.put(composantName, shuffledNumbers);
		}
		/* ****************************************************** */
		if ((shuffledNumbersComplementary != null) && (shuffledNumbersComplementary.size() > 0)) {
			tmpShuffledNumbers.put("COMPLEMENTAIRE", shuffledNumbersComplementary);
		}
		/* ****************************************************** */
		return tmpShuffledNumbers;
	}

}
