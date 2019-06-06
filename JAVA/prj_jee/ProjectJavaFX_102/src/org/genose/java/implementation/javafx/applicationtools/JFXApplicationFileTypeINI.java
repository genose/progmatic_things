/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 * @author 59013-36-18
 *
 */
public class JFXApplicationFileTypeINI extends JFXApplicationFileAccessor implements Iterable<Map<String, Object>> {

	private Map<String,Map<String,Object>> aFileContentDescriptor = null;
	// *************************************************************************
	/**
	 * 
	 */
	public JFXApplicationFileTypeINI() {
		super();
	}
	
	// *************************************************************************
	/**
	 * @throws FileNotFoundException 
	 * 
	 */
	public JFXApplicationFileTypeINI(String aFileName) throws FileNotFoundException {
		super(new File(aFileName));
	}
	
	// *************************************************************************
	/**
	 * @throws IOException 
	 * @see 
	 */
	@Override
	public boolean save() throws IOException
	{
		
			super.clearWriter();
		
			super.initReader();
		
		// *************************************************************************
		for (Entry<String, Map<String, Object>> aParentNodeElement : aFileContentDescriptor.entrySet()) {
			
			String aNodeElementName = aParentNodeElement.getKey();
			Map<String, Object> aNodeElement = aParentNodeElement.getValue();
			
			String sIniFileSectionName = "["+aNodeElementName+"]";
			
			try {
				super.put(sIniFileSectionName);
			} catch (IOException evERRFILEIO) {
				getLogger().logError(this.getClass().getName(), evERRFILEIO.getMessage());
			}
			appendChildValues(aNodeElement);
			try {
				super.newLine();
			} catch (IOException evERRFILE) {
				getLogger().logError(this.getClass().getName(), evERRFILE.getMessage());
			}
		}
		
		// flush to the file all your modifications
		return super.save();
	}
	// *************************************************************************
	/**
	 * 
	 * @param aParentNodeElement
	 * @return
	 * @throws IOException 
	 */
	private boolean appendChildValues(Map<String, Object> aParentNodeElement) throws IOException {

		
		for (Entry<String, Object> aNodeChildElementEntry : aParentNodeElement.entrySet()) {
			
			String aNodeChildElementName = aNodeChildElementEntry.getKey();
		    Object aNodeChildElement = aNodeChildElementEntry.getValue();
			
		    // put to the buffer 
			try {
				super.append(aNodeChildElementName+"=");
			} catch (IOException evERRFILEIO) {
				getLogger().logError(this.getClass().getName(), evERRFILEIO.getMessage());
			}
			
			// *************************************************************************
			if(aNodeChildElement instanceof String) {
				try {
					super.append(aNodeChildElement.toString());
				} catch (IOException evERRFILEIO) {
					getLogger().logError(this.getClass().getName(), evERRFILEIO.getMessage());
				}
			}else {
				super.appendObjectToSerializedJSON(aNodeChildElement);
				super.newLine();
			}
			
		}
		return false;
	}
	// *************************************************************************
	/**
	 * 
	 */
	@Override
	public Iterator<Map<String, Object>> iterator() {
		return null;
	}

}
