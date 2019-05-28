package org.genose.java.implementation.observable;

public interface Observer {
	public boolean notifyChange(Object objNotifiedFrom) throws Exception;
	public void notifyShow();
}
