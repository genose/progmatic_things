package org.genose.implementation.observable;

public interface Observer {
	public boolean notifyChange(Object objNotifiedFrom) throws Exception;
}
