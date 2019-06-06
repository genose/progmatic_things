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
import java.util.HashMap;
import java.util.Map;

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
		
		Map<String,String> aFileContentDescriptor = new HashMap<String, String>();
		
		BufferedReader aBufferedReader = null;
		BufferedWriter aBufferedWriter = null;
		
		FileReader aFileReader = null;
		FileWriter aFileWriter = null;
		
		String aBufferedReaderLine = null;
		Integer aBufferedReaderChar = null;
		
		
	}
	
	public Boolean append(String aStringToAppend) throws IOException {
		return aBufferedWriter.append(aStringToAppend) != null;
	}
	
	public Boolean appendWithNewLine(String aStringToAppend) throws IOException {
		return aBufferedWriter.append(aStringToAppend) != null;

	}

	public Boolean put(String aStringToAppend) throws IOException { 
		return aBufferedWriter.append(aStringToAppend) != null;
	}
	
	public Boolean appendNewLine() throws IOException {	
		aBufferedWriter.newLine();
		return true;
	}
	
	private Boolean appendValues(Map<String, Object> aParentNodeElement) {
		
		return false;
	}
	
	public Boolean appendObjectToSerializedJSON(Object aNodeChildElement) {
		
		
		return false;
	}

	public Boolean save() throws IOException {
		if(aBufferedWriter == null) throw new IOException(" Invalid BufferWriter");
	
		aBufferedWriter.flush();
		aBufferedWriter.close();
		return true;
	}

	public void clearReader() throws IOException {

		aFileContentDescriptor.clear();
		if(aBufferedReader != null) {
			aBufferedReader.reset();
		}
	}
	
	public Boolean clearWriter() throws IOException {
		
		if(aBufferedWriter != null) {
			 aBufferedWriter = new BufferedWriter( new FileWriter( aFileDescriptor ) );
		}
		return true;
	}

	public Boolean rewind() throws IOException {
		aBufferedReader.reset();
		return true;
	}

	public Boolean forward() {
		return false;
	}

	public Integer size() {
		return 0;
		
	}

	public boolean isEOF() throws IOException {
		return aBufferedReaderLine == null;
	}
	
	public String readln() throws IOException {
		 aBufferedReaderLine = aBufferedReader.readLine();
		 aBufferedReaderChar = 0; 
		return aBufferedReaderLine;
	}
	
	public Integer read() throws IOException {
		
		aBufferedReaderChar = aBufferedReader.read();
		
		// handle EOF
		if(aBufferedReaderChar != (-1)) {
			aBufferedReaderLine = Character.toString( aBufferedReaderChar );
		}else {
			aBufferedReaderLine = null;
		}
		
		return aBufferedReaderChar;
	}
	
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
			getLogger().logError(getClass().getName(), evERRFILEIO.getMessage());
			throw evERRFILEIO;
		}finally {
			aBufferedReader.close();
		}
		
	}
	
	protected void initReader() throws IOException {
		try {
			aFileReader = new FileReader(aFileDescriptor);
			aBufferedReader = new BufferedReader(aFileReader);
			aFileContentDescriptor.clear();
		} catch (IOException evERRFILEIO) {
			aLogger.logError(getClass().getName(), evERRFILEIO.getMessage());
			throw evERRFILEIO;
		}
		
	}
	
	protected void initWriter() throws IOException {

		
		try {
			aBufferedWriter = new BufferedWriter(new FileWriter(aFileDescriptor));
		} catch (IOException evERRFILE) {
			aLogger.logError(getClass().getName(), evERRFILE.getMessage());
			throw evERRFILE;
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
