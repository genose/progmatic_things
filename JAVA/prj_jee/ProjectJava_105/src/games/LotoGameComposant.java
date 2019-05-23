/**
 * 
 */
package games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author 59013-36-18
 *
 */
public class LotoGameComposant implements LotoInterface {

	private int[] 	iUsableRangeNumbers 		= IntStream.range(0, 1).unordered().toArray();
	private Integer numberToUse 				= 0;
	private Integer complementNumberToUse 		= 0;
	private String 	composantName 				= "";
	
	List<Integer> shuffleableNumbers 			= new ArrayList<Integer>();
	List<Integer> shuffledNumbers 				= new ArrayList<Integer>();
	List<Integer> shuffledNumbersComplementary 	= new ArrayList<Integer>();
	
	public LotoGameComposant(String composantName, Integer numberToUse, Integer complementNumberToUse) {
		super();
		this.numberToUse = numberToUse;
		this.complementNumberToUse = complementNumberToUse;
		this.composantName = composantName;
	}
	
	public LotoGameComposant(String composantName, Integer numberToUse, Integer complementNumberToUse, int[] iUsableRangeNumbers) {
		super();
		this.numberToUse = numberToUse;
		this.complementNumberToUse = complementNumberToUse;
		this.composantName = composantName;
		this.iUsableRangeNumbers = iUsableRangeNumbers;
	}
	
	/* **
	 * 
	 * 
	 */
	@Override
	public Map<String, Object> shuffle(List<Integer> usableNumber)  throws Exception {
	
	
		/* ************ cleanUp storage of previous shuffles *************** */
		shuffledNumbers.clear();
		shuffledNumbersComplementary.clear();
		shuffleableNumbers.clear();
		/* ************** setup number shuffling *********** */
		if(usableNumber != null && usableNumber.size() >0)
		{ 
				shuffleableNumbers.addAll(usableNumber);
		}else {
		
			for (Integer shufflealeNumber : iUsableRangeNumbers) 
			{
				shuffleableNumbers.add(shufflealeNumber);
			}
		}
		
		
		/* ********************** Get primary Numbers ****************** */
		Integer iShuffledNumber = numberToUse;
		if(numberToUse >0) {

			Collections.shuffle(shuffleableNumbers);
			
			for (Integer iUsedShuffledNumber : shuffleableNumbers) {
				if(iShuffledNumber == 0) break;
				shuffledNumbers.add(iUsedShuffledNumber);
				iShuffledNumber --;
			}
		 
		}
		
		/* ********************** Get Complementary Numbers ****************** */
		Integer iComplementaryShuffledNumber = complementNumberToUse;
		if(iComplementaryShuffledNumber >0) {

			Collections.shuffle(shuffleableNumbers);
			
			for (Integer iUsedShuffledNumber : shuffleableNumbers) {
				if(iComplementaryShuffledNumber == 0) break;
				shuffledNumbersComplementary.add(iUsedShuffledNumber);
				iComplementaryShuffledNumber --;
			}
			 
		}
		
		return getShuffledNumbers();	
	}
	 
	@Override
	public void shuffle() throws Exception {
		// TODO Auto-generated method stub
		shuffle(null);
		
	}
	
	public  Map<String, Object> getShuffledNumbers()
	{
		
		Map<String, Object> tmpShuffledNumbers 			= new HashMap<String, Object>();
		/* ****************************************************** */
		if((shuffledNumbers != null) && (shuffledNumbers .size() >0) ) {
			tmpShuffledNumbers.put(composantName, shuffledNumbers);
		}
		/* ****************************************************** */
		if((shuffledNumbersComplementary != null) && (shuffledNumbersComplementary .size() >0) ) {
			tmpShuffledNumbers.put("COMPLENTAIRE", shuffledNumbersComplementary);			
		}
		/* ****************************************************** */
		return tmpShuffledNumbers;	
	}

}
