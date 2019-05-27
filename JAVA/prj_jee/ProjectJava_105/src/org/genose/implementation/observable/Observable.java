/**
 * 
 */
package org.genose.implementation.observable;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.genose.implementation.observable.*;

/**
 * @author 59013-36-18
 * @param <T>
 *
 */
public class Observable implements Observer {

	private List<Observer> observerObjectsToNotify = new ArrayList<>();
	public boolean addObserver(Observer obj) throws Exception
	{
		boolean bObjectAdded = false;
		if( obj instanceof Observer)
		{
			if(observerObjectsToNotify.contains(obj)) {
				bObjectAdded = observerObjectsToNotify.add(obj);
			}else{
				bObjectAdded = true;
			}
			
		}else {
			throw new InvalidParameterException(" ***** ERROR ****** argument is not an Observer ... ");
		}
		return bObjectAdded;
		
	}
	/* **
	 * 
	 * 
	 */
	@Override
	public boolean notifyChange(Object objNotifiedFrom) throws Exception {
		
		if(objNotifiedFrom instanceof Observer) {
			return ((Observable)objNotifiedFrom).notifyChange();
		} else {
			throw new InvalidParameterException(String.format(" ******** ERROR ******** (%s) \n Something went wrong : \n the notifier (%s :: %s) not compliant to (Observer) ... ", 
					getClass(),
					objNotifiedFrom.getClass(),
					objNotifiedFrom.getClass().getInterfaces().toString()
					) );
		}
	}
	/* **
	 * 
	 * 
	 */
	public boolean notifyChange() throws Exception {
		boolean bObserverNotified = false;
		// TODO Auto-generated method stub
		if(observerObjectsToNotify.size() >0) {
			for (Observer objObserver : observerObjectsToNotify) {
				if(! objObserver.notifyChange(this) ) {
					throw new Exception(String.format(" Something went wrong when notify ... (%s)", objObserver) );
				}else{
					bObserverNotified =	true;
				} 
			}
		}else {
			System.out.println(this.getClass()+":: No subscriber to notify ... ");
		}
		
		return bObserverNotified;
	}

	  
}
