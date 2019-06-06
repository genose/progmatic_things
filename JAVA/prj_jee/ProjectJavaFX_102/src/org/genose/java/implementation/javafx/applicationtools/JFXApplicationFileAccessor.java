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
		
		
	}
	

	public void append(String aStringToAppend) throws IOException {
		aBufferedWriter.append(aStringToAppend);
		aBufferedWriter.newLine();
	}

	public void put(String aStringToAppend) throws IOException { 
		aBufferedWriter.append(aStringToAppend);
	}
	
	public void newLine() throws IOException {	
		aBufferedWriter.newLine();
	}
	
	private boolean appendValues(Map<String, Object> aParentNodeElement) {
		
		return false;
	}
	
	public void appendObjectToSerializedJSON(Object aNodeChildElement) {
		
		
		
	}

	public boolean save() throws IOException {
	
	
		return false;
	}

	public void clearReader() throws IOException {

		if(aBufferedReader != null) {
			aBufferedReader.reset();
		}
	}
	
	public void clearWriter() throws IOException {
		
		if(aBufferedWriter != null) {
			aBufferedWriter.nullWriter();
		}
		
	}

	public void rewind() throws IOException {
		aBufferedReader.reset();
	}

	public void forward() {
		
	}

	public Integer size() {
		return 0;
		
	}

	public boolean isEOF() throws IOException {
		return aBufferedReaderLine == null;
	}
	
	public String readln() throws IOException {
		 aBufferedReaderLine = aBufferedReader.readLine();
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

	protected void initReader() {
		try {
			aFileReader = new FileReader(aFileDescriptor);
			aBufferedReader = new BufferedReader(aFileReader);
		} catch (IOException evERRFILEIO) {
			aLogger.logError(getClass().getName(), evERRFILEIO.getMessage());
		}
		
	}
	
	protected void initWriter() throws IOException {

		
		try {
			aBufferedWriter = new BufferedWriter(new FileWriter(aFileDescriptor));
		} catch (IOException EVERRFILE) {
			EVERRFILE.getMessage();
			throw EVERRFILE;
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
