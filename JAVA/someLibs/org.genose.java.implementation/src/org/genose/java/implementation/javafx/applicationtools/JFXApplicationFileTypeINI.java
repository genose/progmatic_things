/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * @author 59013-36-18
 *
 */
public class JFXApplicationFileTypeINI extends JFXApplicationFileAccessor {

	private static final String DEFAULTPATHPROPERTYDELIMITER = ".";
	private Map<String,Map<String,Object>> aFileContentDescriptorIni = null;
	final String DEFAULTSECTIONVALUEDELIMITER = "=";
	final String DEFAULTSECTIONVALUESFORMAT = "%s"+DEFAULTSECTIONVALUEDELIMITER+"%s";
	final String DEFAULTSECTIONDELIMITER = "[]";
	final String DEFAULTSECTIONFORMAT = "[%s]";
	
	
	// *************************************************************************
	/**
	 * @throws FileNotFoundException 
	 * 
	 */
	public JFXApplicationFileTypeINI() throws FileNotFoundException {
		super();
	}
	
	// *************************************************************************
	/**
	 * @throws FileNotFoundException 
	 * 
	 */
	public JFXApplicationFileTypeINI(String aFileName) throws FileNotFoundException {
		super(new File(aFileName));
		try {
			readFile();
		} catch (IOException evERRREADFILEINI) {
			getLogger().logError(this.getClass(), evERRREADFILEINI);
			throw new FileNotFoundException( "\n : "+evERRREADFILEINI.getMessage()+"\n : "+evERRREADFILEINI.getCause() );
		}
	}
	// *************************************************************************
	/**
	 * 
	 */
	@Override
	public Map<Integer, Object> readlnAsMapIntegerKey() throws IOException {
		return null;
	}
	// *************************************************************************
	/**
	 * 
	 */
	@Override
	public Map<String, Object> readlnAsMapStringKey() throws IOException {
		return null;
	}
	// *************************************************************************
	@Override
	protected Integer read() throws IOException {
		return 0;
	}
	// *************************************************************************
	@Override
	protected String readln() throws IOException {
		return null;
	}
	// *************************************************************************
	@Override
	protected boolean readFile() throws IOException {

		try {
			int i = 0;
			// init file reader
			initReader();
			// read until EOF Mechanism is reached ...
			String aLineReaded = "";
			String aSectionName = "";
			String aLastSectionName = "";
			Map<String,Object> aSectionDATA = new HashMap<>();
			
			// **************************************************
			while (!isEOF()) {
				// read until EOF Internal is Reached 
				aLineReaded = readln();
				// **************************************************
				if(aLineReaded.isEmpty()) return false;
				// **************************************************				
				if(aLineReaded.startsWith(String.valueOf(DEFAULTSECTIONDELIMITER.charAt(0)), 0) ){
					aSectionName = aLineReaded.replaceAll(DEFAULTSECTIONDELIMITER, "");
					if((aLastSectionName.length() >1) && (aSectionName.compareToIgnoreCase(aLastSectionName) !=0) ) {
						// put associated data to section ...
						aFileContentDescriptor.put(aSectionName, aSectionDATA);
						aSectionDATA.clear();
						aLastSectionName = aSectionName;
						
					}
				}else {
					// **************************************************
					String[] aSplittedSectionLineValues = aLineReaded.split(DEFAULTSECTIONVALUEDELIMITER);
					// **************************************************
					String aSectionDATAKey = ((aSplittedSectionLineValues != null && aSplittedSectionLineValues.length >1)? String.valueOf(aSplittedSectionLineValues[0]):null);
					Object aSectionDATAValues = ((aSplittedSectionLineValues != null && aSplittedSectionLineValues.length >1)? String.valueOf(aSplittedSectionLineValues[0]):null);
					// **************************************************
					// invalid arguments in file ...
					if((aSectionDATAKey == null) || (String.valueOf( aSectionDATAKey).length()<2)) {
						throw new IOException(String.format("Invalid file Format after ([%s:%s]) ....", aLastSectionName, aSectionName));
					}
					// **************************************************
					// put all data in final place ... 
					aSectionDATA.put( aSectionDATAKey, aSectionDATAValues );
				}
			}
			// **************************************************
			// **************************************************
			if(!aSectionDATA.isEmpty()) {
				aFileContentDescriptor.put(aSectionName, aSectionDATA);
			}
			// **************************************************
		} catch (IOException evERRFILEIO) {
			tragicClose();
			getLogger().logError(this.getClass(), evERRFILEIO);
			throw evERRFILEIO;
		} finally {
			closeReader();
		}
		
		return false;
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
			initWriterIfNecessary();
			
			// *************************************************************************
			for (Entry<String, Object> aParentNodeElement : aFileContentDescriptor.entrySet()) {
				
				String aNodeElementName = aParentNodeElement.getKey();
				Map<String, Object> aNodeElement = (Map<String, Object>) aParentNodeElement.getValue();
				
				String sIniFileSectionName = String.format(DEFAULTSECTIONFORMAT, String.valueOf(aNodeElementName));
				
				bSaveStatus = super.append(sIniFileSectionName) && appendChildValues(aNodeElement);
				if(!bSaveStatus) { break; }
			 
				bSaveStatus = super.appendNewLine();
				if(!bSaveStatus) { break; }
			}
		} catch (IOException evERRFILE) {
			getLogger().logError(this.getClass(), evERRFILE);
			return false;
		} catch (JFXApplicationException evERRTRANSFORMDATA) {
			getLogger().logError(this.getClass(), evERRTRANSFORMDATA);
			return false;
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
	private Boolean appendChildValues(Map<String, Object> aParentNodeElement) throws JFXApplicationException {
		try {
			initWriterIfNecessary();
			Boolean bAppendStatus = true;
			String aNodeChildElementName = null;
		    Object aNodeChildElement = null;
			for (Entry<String, Object> aNodeChildElementEntry : aParentNodeElement.entrySet()) {
				
				aNodeChildElementName = aNodeChildElementEntry.getKey();
			    aNodeChildElement = aNodeChildElementEntry.getValue();
				
			    // put to the buffer 
				// *************************************************************************
				
				// value(s) contained 
				if(aNodeChildElement instanceof String) {
					// key of element
					bAppendStatus = super.appendWithNewLine(aNodeChildElementName+"="+aNodeChildElement.toString());
					if(!bAppendStatus) {
						break;
					}
					 
					
				}else if((aNodeChildElement instanceof Map)) {
					// *************************************************************************
					// key of element
					bAppendStatus = super.append(String.valueOf(aNodeChildElementName+""+DEFAULTSECTIONVALUEDELIMITER)) &&  super.appendObjectToSerializedJSON((Map<String, Object>)aNodeChildElement);
					
					if(!bAppendStatus) {
						throw new JFXApplicationException(String.format(" Something goes wrong when adding JSON on (%s)",aNodeChildElementName));
					}
				}else {
					throw new JFXApplicationException(getClass().getName()+" : Unexpected object type at index : "+aNodeChildElementName +":"+aNodeChildElement.getClass());
				}
				
				// *************************************************************************
				
			}
			if(!bAppendStatus) {
					throw new JFXApplicationException(String.format(" Something goes wrong when adding (%s)",aNodeChildElementName));
			}
			// return status
			return bAppendStatus;
		} catch (IOException | JFXApplicationException evERRAPPENDCHILD) {
			getLogger().logError(this.getClass(), evERRAPPENDCHILD);
			throw new JFXApplicationException(evERRAPPENDCHILD);
		}
		
	}
	// *************************************************************************
	public String getStringProperty(String aMatchedPathProperty) {
		String [] aPropertyPathSplitted = aMatchedPathProperty.split(DEFAULTPATHPROPERTYDELIMITER);
		
		List<String> aPropertyPathSplittedArray  = Arrays.asList(aPropertyPathSplitted);
		
		Object aObjectFoundiInPath = aFileContentDescriptor.get(aPropertyPathSplittedArray.get(0) );
		aPropertyPathSplittedArray.remove(0);
		
		for (Iterator aIteratedKeyValue = aPropertyPathSplittedArray.iterator(); aIteratedKeyValue.hasNext();) {
			String aKeyProperty = String.valueOf( ((String) aIteratedKeyValue.next()) ).toUpperCase();
			
			if( aObjectFoundiInPath == null ) {
				return null;
			}else {
				if(aObjectFoundiInPath instanceof String) {
					return ((String)aObjectFoundiInPath);
				}else {
					aObjectFoundiInPath = ((Map<?, ?>)aObjectFoundiInPath).get(aPropertyPathSplittedArray.get(0) );
				}
			}
		}
		
		
		
		return null;
	}

}
