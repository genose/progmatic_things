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
	public Boolean notifyChange(Object objNotifiedFrom) throws Exception {
		
		try {
			((Observable)objNotifiedFrom).notifyShow();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
		}

		return false;
	}

	@Override
	public Boolean notifyShow() {
		// TODO Auto-generated method stub
		return true;
	}

}
