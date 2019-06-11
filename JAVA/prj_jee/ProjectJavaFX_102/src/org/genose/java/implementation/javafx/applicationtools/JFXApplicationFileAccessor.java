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

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author 59013-36-18
 *
 */
public class JFXApplicationFileAccessor {

	/**
	 * 
	 * File relative
	 */
	private Map<String, String> aFileContentDescriptor;
	private File aFileDescriptor = null;
	private String aFileName = null;
	private String aFilePath = null;
	private String aFilePathAbsolute = null;
	/**
	 * 
	 * I/O relative to file descriptor
	 * 
	 */
	private BufferedReader aBufferedReader = null;
	private String aBufferedReaderLine = null;
	private Integer aBufferedReaderChar = null;

	private BufferedWriter aBufferedWriter = null;

	private FileReader aFileReader = null;
	private FileWriter aFileWriter = null;

	private JFXApplicationLogger aLogger = null;

	/* **************************************** */
	/**
	 * 
	 */
	public JFXApplicationFileAccessor() {
	}

	/* **************************************** */
	/**
	 * 
	 * @param aFileArg
	 * @throws FileNotFoundException
	 */
	public JFXApplicationFileAccessor(File aFileArg) throws FileNotFoundException {
		/* **************************************** */
		if (aFileArg == null) {
			throw new FileNotFoundException(getClass().getName() + ": argument is null or file not exists ");
		}
		/* **************************************** */
		if (!aFileArg.exists()) {
			throw new FileNotFoundException(getClass().getName() + ": argument is null or file not exists ");
		}
		/* **************************************** */
		aFileDescriptor = aFileArg;
		aFileName = aFileArg.getName();
		aFilePath = aFileArg.getPath();
		aFilePathAbsolute = aFileArg.getAbsolutePath();
		/* **************************************** */
		aLogger = new JFXApplicationLogger("" + aFileDescriptor.toString());
		/* **************************************** */
		Map<String, String> aFileContentDescriptor = new HashMap<>();
		/* **************************************** */
		BufferedReader aBufferedReader = null;
		BufferedWriter aBufferedWriter = null;
		/* **************************************** */
		FileReader aFileReader = null;
		FileWriter aFileWriter = null;
		/* **************************************** */
		String aBufferedReaderLine = null;
		Integer aBufferedReaderChar = null;

	}

	/* **************************************** */
	/**
	 * 
	 * @param aStringToAppend
	 * @return
	 * @throws IOException
	 */
	public Boolean append(String aStringToAppend) throws IOException {
		try {
			return aBufferedWriter.append(aStringToAppend) != null;
		} catch (Exception evERRFILEIO) {
			getLogger().logError(this.getClass(), evERRFILEIO);
			tragicClose();
			throw evERRFILEIO;
		}
	}

	/* **************************************** */
	/**
	 * 
	 * @param aStringToAppend
	 * @return
	 * @throws IOException
	 */
	public Boolean appendWithNewLine(String aStringToAppend) throws IOException {
		try {
			return aBufferedWriter.append(aStringToAppend) != null;
		} catch (Exception evERRFILEIO) {
			getLogger().logError(this.getClass(), evERRFILEIO);
			tragicClose();
			throw evERRFILEIO;
		}

	}

	/* **************************************** */
	/**
	 * 
	 * @param aStringToAppend to the writer buffer
	 * @return
	 * @throws IOException
	 */
	public Boolean put(String aStringToAppend) throws IOException {
		try {
			return aBufferedWriter.append(aStringToAppend) != null;
		} catch (Exception evERRFILEIO) {
			getLogger().logError(this.getClass(), evERRFILEIO);
			tragicClose();
			throw evERRFILEIO;
		}
	}

	/* **************************************** */
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public Boolean appendNewLine() throws IOException {
		try {
			aBufferedWriter.newLine();
		} catch (Exception evERRFILEIO) {
			getLogger().logError(this.getClass(), evERRFILEIO);
			tragicClose();
			throw evERRFILEIO;
		}
		return true;
	}

	/* **************************************** */
	/**
	 * 
	 * @param aParentNodeElement
	 * @return
	 * @throws JFXApplicationException 
	 */
	private Boolean appendValues(Map<String, Object> aChildNodeElement) throws JFXApplicationException {
		if(aBufferedWriter == null) {
			throw new JFXApplicationException("can t append to unintilaize buffer ... ");
		}
		/* **************************************** */
		JSONArray dataset = new JSONArray();
		
		try {
			aBufferedWriter.append( dataset.toString() );
		} catch (IOException evErrAPPENDERROR) {
			getLogger().logError(this.getClass(), evErrAPPENDERROR);
			tragicClose();
			throw new JFXApplicationException(evErrAPPENDERROR);
		}
		/* **************************************** */
		return false;
	}

	/* **************************************** */
	/**
	 * 
	 * @param aNodeChildElementName
	 * @param aNodeChildElement
	 * @return
	 * @throws JFXApplicationException
	 */
	public Boolean appendObjectToSerializedJSON(String aNodeChildElementName, Object aNodeChildElement)
			throws JFXApplicationException {
		try {

			JSONObject dataset = new JSONObject();
			dataset.put(aNodeChildElementName, aNodeChildElement);

			aBufferedWriter.append(aNodeChildElementName);
			aBufferedWriter.append(String.valueOf(dataset.toString()));

			getLogger().getConsoleLog().println(dataset.toString());

			return true;

		} catch (Exception evERRENCODEJSON) {
			getLogger().logError(this.getClass(), evERRENCODEJSON);
			tragicClose();
			throw new JFXApplicationException(evERRENCODEJSON);
		}
	}

	/* **************************************** */
	/**
	 * 
	 * @param aNodeChildElement
	 * @return
	 * @throws JFXApplicationException
	 */
	public Boolean appendObjectToSerializedJSON(Map<String, Object> aNodeChildElement) throws JFXApplicationException {
		try {
			// prepare to tranfrom current data nodes to JSON ...
			JSONObject dataset = new JSONObject();

			if (!(aNodeChildElement instanceof Iterable<?>)) {
				throw new JFXApplicationException(getClass().getName() + ": the node class "
						+ aNodeChildElement.getClass() + " is not iterable ... ");
			}
			/* **************************************** */
			for (Entry<String, Object> aNodeChildElementEntry : aNodeChildElement.entrySet()) {

				String aNodeChildElementName = aNodeChildElementEntry.getKey();
				Object aNodeChildElementValue = aNodeChildElementEntry.getValue();
				/* **************************************** */
				if (aNodeChildElementName == null) {

					getLogger().logError(this.getClass(),
							String.valueOf(" some data could be tranfrom due to NULL KEY .... "
									+ ((aNodeChildElementValue == null) ? String.valueOf(aNodeChildElementValue)
											: aNodeChildElementValue.toString())));
					// skipping erroneous keys ... 
					continue;
				}
				/* **************************************** */
				if (aNodeChildElementValue == null ) {
					dataset.put(aNodeChildElementName, String.valueOf(aNodeChildElementValue));
				}else {
					dataset.put(aNodeChildElementName, aNodeChildElementValue);
				}

			}
			/* **************************************** */
			// put JSON STREAM inside our buffer ...
			/* **************************************** */
			aBufferedWriter.append(String.valueOf(dataset.toString()));

			getLogger().getConsoleLog().println(dataset.toString());
			return true;
		} catch (JFXApplicationException | IOException evERRENCODEJSON) {
			getLogger().logError(this.getClass(), evERRENCODEJSON);
			tragicClose();
			throw new JFXApplicationException(evERRENCODEJSON);
		}

	}

	/* **************************************** */
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public Boolean save() throws IOException {
		try {
			if (aBufferedWriter == null) {
				throw new IOException(" Invalid BufferWriter");
			} else {
				aBufferedWriter.flush();
				aBufferedWriter.close();
			}
		} catch (IOException evERRFILEIO) {
			tragicClose();
			getLogger().logError(this.getClass(), evERRFILEIO);
			throw evERRFILEIO;
		}
		return true;
	}

	/* **************************************** */
	/**
	 * 
	 * @throws IOException
	 */
	public void clearReader() throws IOException {
		try {
			aFileContentDescriptor.clear();
			if (aBufferedReader != null) {
				aBufferedReader.reset();
			}

		} catch (IOException evERRFILEIO) {
			getLogger().logError(this.getClass(), evERRFILEIO);
			tragicClose();
			throw evERRFILEIO;
		}
	}

	/* **************************************** */
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public Boolean clearWriter() throws IOException {
		try {
			if (aBufferedWriter != null) {
				aBufferedWriter = new BufferedWriter(new FileWriter(aFileDescriptor));
			}

		} catch (IOException evERRFILEIO) {
			getLogger().logError(this.getClass(), evERRFILEIO);
			tragicClose();
			throw evERRFILEIO;
		}
		return true;
	}

	/* **************************************** */
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public Boolean rewind() throws IOException {
		try {
			aBufferedReader.reset();

		} catch (IOException evERRFILEIO) {
			getLogger().logError(this.getClass(), evERRFILEIO);
			tragicClose();
			throw evERRFILEIO;
		}
		return true;
	}

	/* **************************************** */
	/**
	 * 
	 * @return
	 */
	public Boolean forward() {
		return false;
	}

	/* **************************************** */
	/**
	 * 
	 * @return
	 */
	public Integer size() {
		return 0;

	}

	/* **************************************** */
	/**
	 * 
	 * @return EOF true when Read() or Readln() reach File EOF or can t read bytes
	 */
	public boolean isEOF() {
		return aBufferedReaderLine == null;
	}

	/* **************************************** */
	/**
	 * 
	 * @return String or NULL
	 * @throws IOException
	 */
	public String readln() throws IOException {
		try {
			aBufferedReaderLine = aBufferedReader.readLine();
			aBufferedReaderChar = 0;

		} catch (IOException evERRFILEIO) {
			getLogger().logError(this.getClass(), evERRFILEIO);
			tragicClose();
			throw evERRFILEIO;
		}
		return aBufferedReaderLine;
	}

	/* **************************************** */
	/**
	 * 
	 * @return CharBuffer
	 * @throws IOException
	 */
	public Integer read() throws IOException {

		try {
			aBufferedReaderChar = aBufferedReader.read();

			// handle EOF Mechanism
			if (aBufferedReaderChar != (-1)) {
				aBufferedReaderLine = Character.toString(aBufferedReaderChar);
			} else {
				aBufferedReaderLine = null;
			}

		} catch (IOException evERRFILEIO) {
			tragicClose();
			getLogger().logError(this.getClass(), evERRFILEIO);
			throw evERRFILEIO;
		}
		return aBufferedReaderChar;
	}

	/* **************************************** */
	/**
	 * 
	 * @return false when no data
	 * @throws IOException
	 */
	public boolean readFile() throws IOException {

		try {
			int i = 0;
			// init file reader
			initReader();
			// read until EOF Mechanism is reached ...
			while (!isEOF()) {
				// read until EOF Internal is Reached
				if (aFileContentDescriptor.put(String.valueOf(i++), readln()) == null) {
					return false;
				}
			}
			return true;
		} catch (IOException evERRFILEIO) {
			tragicClose();
			getLogger().logError(this.getClass(), evERRFILEIO);
			throw evERRFILEIO;
		}
	}

	/* **************************************** */
	/**
	 * 
	 * @throws IOException
	 */
	protected void initReader() throws IOException {
		try {
			if (aFileDescriptor == null) {
				aFileDescriptor = new File(aFileName);
				aFileReader = new FileReader(aFileDescriptor);
				aBufferedReader = new BufferedReader(aFileReader);
			} else {
				if (aFileReader == null) {
					aFileReader = new FileReader(aFileDescriptor);
				} else {
					aFileReader.close();
					aFileReader = new FileReader(aFileDescriptor);
				}
			}
			aFileContentDescriptor.clear();
		} catch (IOException evERRFILEIO) {
			tragicClose();
			getLogger().logError(this.getClass(), evERRFILEIO);
			throw evERRFILEIO;
		}

	}

	/* **************************************** */
	/**
	 * 
	 * @throws IOException
	 */
	protected void initWriter() throws IOException {

		try {
			aBufferedWriter = new BufferedWriter(new FileWriter(aFileDescriptor));
		} catch (IOException evERRFILEIO) {
			getLogger().logError(this.getClass(), evERRFILEIO);
			tragicClose();
			throw evERRFILEIO;
		}

	}

	/* **************************************** */
	/**
	 * Close ressources when Exception occur ...
	 */
	private void tragicClose() {
		try {

			if (aFileReader != null) {
				aFileReader.close();
				aFileReader = null;
			}

			if (aBufferedReader != null) {
				aBufferedReader.close();
				aBufferedReader = null;
			}

			if (aFileWriter != null) {
				aFileWriter.nullWriter();
				aFileWriter = null;
			}

			if (aBufferedWriter != null) {
				aBufferedWriter.nullWriter();
				aBufferedWriter = null;
			}
		} catch (Exception evERRFILEIO) {
			getLogger().logError(this.getClass(), evERRFILEIO);
		}
	}

	/* **************************************** */
	/**
	 * @return the aLogger
	 */
	public synchronized JFXApplicationLogger getLogger() {
		return aLogger;
	}

	/* **************************************** */
	/**
	 * @param aLogger the aLogger to set
	 */
	public synchronized void setLogger(JFXApplicationLogger aLogger) {
		this.aLogger = aLogger;
	}
	/* **************************************** */
	/**
	 * 
	 * @param aStringToAppend
	 * @return
	 * @throws IOException
	 */
	public Boolean appendLn(String aStringToAppend) throws IOException {
		return append(aStringToAppend) && appendNewLine() ;
	}

}
