/**
 * 
 */
package org.genose.java.implementation.observable;

import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * @author 59013-36-18
 * @param <T>
 *
 */
public class Observable implements Observer {

	protected ArrayList<Observer> observerObjectsToNotify = new ArrayList<>();
	public boolean addObserver(Observer objObserver) throws Exception
	{
		boolean bObjectAdded = false;
		if( objObserver instanceof Observer)
		{
			System.out.println("Observable.addObserver()::"+observerObjectsToNotify.size()+"::"+observerObjectsToNotify.toString());
			if( ! observerObjectsToNotify.contains(objObserver)) {
				bObjectAdded = observerObjectsToNotify.add(objObserver);
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
	 * notify one/chaining observer/observable
	 */
 
	@Override
	public Boolean notifyChange(Object objNotifiedFrom) throws Exception {
		
		if(objNotifiedFrom instanceof Observer) {
			return ((Observable)objNotifiedFrom).notifyChange();
		} else {
			throw new InvalidParameterException(String.format(" ******** ERROR ******** (%s) %n Something went wrong : %n the notifier (%s :: %s) not compliant to (Observer) ... ", 
					getClass(),
					objNotifiedFrom.getClass(),
					objNotifiedFrom.getClass().getInterfaces().toString()
					) );
		}
	}
	/* **
	 * notify all observers ...
	 * 
	 */
	public boolean notifyChange() throws Exception {
		boolean bObserverNotified = false;
		
		if(observerObjectsToNotify.size() >0) {
			for (Observer objObserver : observerObjectsToNotify) {
				Boolean bobjToNotify = objObserver.notifyChange(this); 
				if(! bobjToNotify ) {
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
	
	@Override
	public Boolean notifyShow() {
		System.err.println(getClass()+":Base notifyShow return false ...");
		return false;
	}

	  
}
