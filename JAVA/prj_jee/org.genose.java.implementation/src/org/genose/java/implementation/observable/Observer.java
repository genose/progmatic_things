package org.genose.java.implementation.observable;

public interface Observer {
	public Boolean notifyChange(Object objNotifiedFrom) throws Exception;
	public Boolean notifyShow();
}
