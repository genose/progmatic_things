/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

/**
 * @author 59013-36-18
 *
 */
public class JFXApplicationFileAccessor {

	private Map<String,String> aFileContentDescriptor;
	private File aFileDescriptor = null;
	private String aFileName = null;
	private String aFilePath = null;
	private String aFilePathAbsolute = null;
	
	private BufferedReader aBufferedReader = null;
	private String aBufferedReaderLine = null;
	private Integer aBufferedReaderChar = null;
	private BufferedWriter aBufferedWriter = null;
	
	private FileReader aFileReader = null;
	private FileWriter aFileWriter = null;
	
	private JFXApplicationLogger aLogger = null;
	
	
	public JFXApplicationFileAccessor() {
	}
	
	public JFXApplicationFileAccessor(File aFileArg) throws FileNotFoundException {
		
		if(aFileArg == null ) {
			throw new FileNotFoundException(getClass().getName()+": argument is null or file not exists ");
		}
		
		if( ! aFileArg.exists() ) {
			throw new FileNotFoundException(getClass().getName()+": argument is null or file not exists ");
		}
		
		aFileDescriptor = aFileArg;
		aFileName = aFileArg.getName();
		aFilePath = aFileArg.getPath();
		aFilePathAbsolute = aFileArg.getAbsolutePath();
		
		aLogger = new JFXApplicationLogger(""+aFileDescriptor.toString() );
		
		Map<String,String> aFileContentDescriptor = new HashMap<>();
		
		BufferedReader aBufferedReader = null;
		BufferedWriter aBufferedWriter = null;
		
		FileReader aFileReader = null;
		FileWriter aFileWriter = null;
		
		String aBufferedReaderLine = null;
		Integer aBufferedReaderChar = null;
		
		
	}
	
	public Boolean append(String aStringToAppend) throws IOException {
		try {
			return aBufferedWriter.append(aStringToAppend) != null;
	} catch (Exception evERRFILEIO) {
		tragicClose();
		getLogger().logError(this.getClass(), evERRFILEIO.getClass(), evERRFILEIO);
		throw evERRFILEIO;
	}
	}
	
	public Boolean appendWithNewLine(String aStringToAppend) throws IOException {
	try {
		return aBufferedWriter.append(aStringToAppend) != null;
	} catch (Exception evERRFILEIO) {
		tragicClose();
		getLogger().logError(this.getClass(), evERRFILEIO.getClass(), evERRFILEIO);
		throw evERRFILEIO;
	}

	}

	public Boolean put(String aStringToAppend) throws IOException { 
		try {
			return aBufferedWriter.append(aStringToAppend) != null;
		} catch (Exception evERRFILEIO) {
			tragicClose();
			getLogger().logError(this.getClass(), evERRFILEIO.getClass(), evERRFILEIO);
			throw evERRFILEIO;
		}
	}
	
	public Boolean appendNewLine() throws IOException {	
		try {
			aBufferedWriter.newLine();
		} catch (Exception evERRFILEIO) {
			tragicClose();
			getLogger().logError(this.getClass(), evERRFILEIO.getClass(), evERRFILEIO);
			throw evERRFILEIO;
		}
		return true;
	}
	
	private Boolean appendValues(Map<String, Object> aParentNodeElement) {
		
		return false;
	}
	
	public Boolean appendObjectToSerializedJSON(String aNodeChildElementName, Object aNodeChildElement) throws JFXApplicationException {
		try {
			
			JSONObject dataset = new JSONObject();
			dataset.put(aNodeChildElementName, aNodeChildElement);

			aBufferedWriter.append(aNodeChildElementName);
			aBufferedWriter.append( String.valueOf( dataset.toString() ) );
			
			getLogger().getConsoleLog().println( dataset.toString() );
			
			return true;
			
		} catch (Exception evERRENCODEJSON) {
			tragicClose();
			getLogger().logError(this.getClass(), evERRENCODEJSON.getClass(), evERRENCODEJSON);
			throw new JFXApplicationException( evERRENCODEJSON );
		}
	}
	
	public Boolean appendObjectToSerializedJSON(Map<String, Object> aNodeChildElement) throws JFXApplicationException {
		try {
			JSONObject dataset = new JSONObject();
			
			if( ! (aNodeChildElement instanceof Iterable<?>) ) {
				throw new JFXApplicationException ( getClass().getName()+": the node class "+aNodeChildElement.getClass()+" is not iterable ... ");
			}
			
			for (Entry<String, Object> aNodeChildElementEntry : aNodeChildElement.entrySet()) {
			
				String aNodeChildElementName = aNodeChildElementEntry.getKey();
				Object aNodeChildElementValue = aNodeChildElementEntry.getValue();
				dataset.put(aNodeChildElementName, aNodeChildElementValue);
			}
			
			aBufferedWriter.append( String.valueOf( dataset.toString() ) );
			
			getLogger().getConsoleLog().println( dataset.toString() );
		}catch (JFXApplicationException | IOException evERRENCODEJSON ) {
			tragicClose();
				getLogger().logError(this.getClass(), evERRENCODEJSON.getClass(), evERRENCODEJSON);
				throw new JFXApplicationException(evERRENCODEJSON);
		}
		return false;
	}

	public Boolean save() throws IOException {
		try {
			if(aBufferedWriter == null) {
				throw new IOException(" Invalid BufferWriter");
			}else {
				aBufferedWriter.flush();
				aBufferedWriter.close();
			}
		} catch (IOException evERRFILEIO) {
			tragicClose();
			getLogger().logError(this.getClass(), evERRFILEIO.getClass(), evERRFILEIO);
			throw evERRFILEIO;
		}
		return true;
	}

	public void clearReader() throws IOException {
		try {
			aFileContentDescriptor.clear();
			if(aBufferedReader != null) {
				aBufferedReader.reset();
			}
				
		} catch (IOException evERRFILEIO) {
			tragicClose();
			getLogger().logError(this.getClass(), evERRFILEIO.getClass(), evERRFILEIO);
			throw evERRFILEIO;
		}
	}
	
	public Boolean clearWriter() throws IOException {
		try {
			if(aBufferedWriter != null) {
				 aBufferedWriter = new BufferedWriter( new FileWriter( aFileDescriptor ) );
			}
		
		} catch (IOException evERRFILEIO) {
			tragicClose();
			getLogger().logError(this.getClass(), evERRFILEIO.getClass(), evERRFILEIO);
			throw evERRFILEIO;
		}
		return true;
	}

	public Boolean rewind() throws IOException {
		try {
		aBufferedReader.reset();
		
		} catch (IOException evERRFILEIO) {
			tragicClose();
			getLogger().logError(this.getClass(), evERRFILEIO.getClass(), evERRFILEIO);
			throw evERRFILEIO;
		}
		return true;
	}

	public Boolean forward() {
		return false;
	}

	public Integer size() {
		return 0;
		
	}

	public boolean isEOF() {
		return aBufferedReaderLine == null;
	}
	
	public String readln() throws IOException {
		try {
		 aBufferedReaderLine = aBufferedReader.readLine();
		 aBufferedReaderChar = 0;
			
		} catch (IOException evERRFILEIO) {
			tragicClose();
			getLogger().logError(this.getClass(), evERRFILEIO.getClass(), evERRFILEIO);
			throw evERRFILEIO;
		} 
		return aBufferedReaderLine;
	}
	
	public Integer read() throws IOException {
		
		try {
			aBufferedReaderChar = aBufferedReader.read();
			
			// handle EOF
			if(aBufferedReaderChar != (-1)) {
				aBufferedReaderLine = Character.toString( aBufferedReaderChar );
			}else {
				aBufferedReaderLine = null;
			}
			
		} catch (IOException evERRFILEIO) {
			tragicClose();
			getLogger().logError(this.getClass(), evERRFILEIO.getClass(), evERRFILEIO);
			throw evERRFILEIO;
		} 
		return aBufferedReaderChar;
	}
	/**
	 * 
	 * @return false when no data 
	 * @throws IOException
	 */
	public boolean readFile() throws IOException {
	
		try {
			int i = 0;
			initReader();
			while(!isEOF()) {
				if( aFileContentDescriptor.put( String.valueOf(i++) , readln() ) == null)
					return false;
			}
			return true;	
		} catch (IOException evERRFILEIO) {
			tragicClose();
			getLogger().logError(this.getClass(), evERRFILEIO.getClass(), evERRFILEIO);
			throw evERRFILEIO;
		}		
	}
	/**
	 * 
	 * @throws IOException
	 */
	protected void initReader() throws IOException {
		try {
			aFileReader = new FileReader(aFileDescriptor);
			aBufferedReader = new BufferedReader(aFileReader);
			aFileContentDescriptor.clear();
		} catch (IOException evERRFILEIO) {
			tragicClose();
			getLogger().logError(this.getClass(), evERRFILEIO.getClass(), evERRFILEIO);
			throw evERRFILEIO;
		}
		
	}
	/**
	 * 
	 * @throws IOException
	 */
	protected void initWriter() throws IOException {

		
		try {
			aBufferedWriter = new BufferedWriter(new FileWriter(aFileDescriptor));
		} catch (IOException evERRFILEIO) {
			tragicClose();
			getLogger().logError(this.getClass(), evERRFILEIO.getClass(), evERRFILEIO);
			throw evERRFILEIO;
		}
		
	}
	/**
	 * Close ressources when Exception occur ...
	 */
	private void tragicClose() {
		try {
			
			if(aFileReader != null) { 
				aFileReader.close();
				aFileReader = null;
			}
			
			if(aBufferedReader != null) {
				aBufferedReader.close();
				aBufferedReader = null;
			}
			
			if(aFileWriter != null) { 
				aFileWriter.nullWriter();
				aFileWriter = null;
			}
			
			if(aBufferedWriter != null) {
				aBufferedWriter.nullWriter();
				aBufferedWriter = null;
			}
		} catch (Exception evERRFILEIO) {
			getLogger().logError(this.getClass(), evERRFILEIO.getClass(), evERRFILEIO);
		}
	}
	/**
	 * @return the aLogger
	 */
	public synchronized JFXApplicationLogger getLogger() {
		return aLogger;
	}

	/**
	 * @param aLogger the aLogger to set
	 */
	public synchronized void setLogger(JFXApplicationLogger aLogger) {
		this.aLogger = aLogger;
	}


}
