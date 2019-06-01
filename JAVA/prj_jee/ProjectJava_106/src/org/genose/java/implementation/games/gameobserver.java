package org.genose.java.implementation.games;
import org.genose.java.implementation.observable.Observable;
import org.genose.java.implementation.observable.Observer;

/**
 * 
 */

/**
 * @author 59013-36-18
 *
 */
public class gameobserver implements Observer {

	@Override
	public boolean notifyChange(Object objNotifiedFrom) throws Exception {
		// TODO Auto-generated method stub
		
		((Observable)objNotifiedFrom).notifyShow();
		
		
		return false;
	}

	@Override
	public void notifyShow() {
		// TODO Auto-generated method stub
		
	}

}
