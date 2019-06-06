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
	public Boolean save() throws IOException
	{
		Boolean bSaveStatus = false;
		try {
			super.clearWriter();
		
			super.initReader();
			
			// *************************************************************************
			for (Entry<String, Map<String, Object>> aParentNodeElement : aFileContentDescriptor.entrySet()) {
				
				String aNodeElementName = aParentNodeElement.getKey();
				Map<String, Object> aNodeElement = aParentNodeElement.getValue();
				
				String sIniFileSectionName = "["+aNodeElementName+"]";
				
				bSaveStatus = super.append(sIniFileSectionName);
				if(!bSaveStatus) { break; }
				 
				bSaveStatus = appendChildValues(aNodeElement);
				if(!bSaveStatus) { break; }
			 
				bSaveStatus = super.appendNewLine();
				if(!bSaveStatus) { break; }
			}
		} catch (IOException evERRFILE) {
			getLogger().logError(this.getClass().getName(), evERRFILE.getMessage());
		}
		// flush to the file all your modifications, if no error occured
		return ((bSaveStatus) ? super.save() : bSaveStatus);
	}
	// *************************************************************************
	/**
	 * 
	 * @param aParentNodeElement
	 * @return
	 * @throws IOException 
	 */
	private Boolean appendChildValues(Map<String, Object> aParentNodeElement) throws IOException {

		Boolean bAppendStatus = true;
		for (Entry<String, Object> aNodeChildElementEntry : aParentNodeElement.entrySet()) {
			
			String aNodeChildElementName = aNodeChildElementEntry.getKey();
		    Object aNodeChildElement = aNodeChildElementEntry.getValue();
			
		    // put to the buffer 
			try {
				// key of element
				super.append(aNodeChildElementName+"=");
				// value(s) contained 
				if(aNodeChildElement instanceof String) {
					bAppendStatus = super.append(aNodeChildElement.toString());
					super.appendNewLine();
				}else {
					bAppendStatus = super.appendObjectToSerializedJSON(aNodeChildElement);
				}
			} catch (IOException evERRFILEIO) {
				getLogger().logError(this.getClass().getName(), evERRFILEIO.getMessage());
				bAppendStatus = false;
				break;
			}
			
		}
		return bAppendStatus;
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
