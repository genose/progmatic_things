/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools;

import java.util.Iterator;
import java.util.Map;

import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 * @author 59013-36-18
 *
 */
public class JFXApplicationFileReaderTypeINI extends JFXApplicationFileReader implements Iterable<Map<String, Object>> {

	private Map<String,Object> aFileContentDescriptor = null;
	private String aFileName = null;
	private String aFilePath = null;
	
	/**
	 * 
	 */
	public JFXApplicationFileReaderTypeINI() {
		// TODO Auto-generated constructor stub
	}
	
	public boolean readln()
	{
		
		return false;
	}
	
	
	@Override
	public boolean read()
	{
		return false;
	}
	
	@Override
	public boolean save()
	{
		super.clear();
		for (String aParentNodeElementName : aFileContentDescriptor.keySet()) {
			
			String sIniFileSectionName = "["+aParentNodeElementName+"]";
			Map<String, Object> aNodeElement = (Map<String, Object>) aFileContentDescriptor.get(aParentNodeElementName);
			super.append(sIniFileSectionName);
			appendChildValues(aNodeElement);
		}
		
		// flush to the file all your modifications
		super.save();
		return false;
	}

	private boolean appendChildValues(Map<String, Object> aParentNodeElement) {

		
		for (String aNodeChildElementName : aParentNodeElement.keySet()) {
			Object aNodeChildElement = aParentNodeElement.get(aNodeChildElementName);
			
			// put to the buffer 
			super.append(aNodeChildElementName+"=");
			if(aNodeChildElement instanceof String) {
				super.append(aNodeChildElement.toString());
			}else {
				super.appendObjectToSerializedJSON(aNodeChildElement);
			}
			
		}
		return false;
	}
	
	@Override
	public Iterator<Map<String, Object>> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

}
