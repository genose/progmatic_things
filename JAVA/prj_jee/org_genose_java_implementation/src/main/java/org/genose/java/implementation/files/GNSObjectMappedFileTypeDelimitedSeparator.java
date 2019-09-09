/**
 * 
 */
package org.genose.java.implementation.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.genose.java.implementation.streams.GNSObjectMappedLogger;
import org.genose.java.implementation.arraysmapslists.GNSObjectMappedObject;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;

/**
 * @author 59013-36-18
 *
 */
public class GNSObjectMappedFileTypeDelimitedSeparator extends GNSObjectMappedFileAccessor {

	final String DEFAULTSTRINGDELIMITER = ";";
	private String aStringDelimiterSeparator = DEFAULTSTRINGDELIMITER;
	private Boolean bFileHaveFirstLineHasKeyDescriptor = false;
	GNSObjectMappedObject aFirstLineKeyDescriptor = new GNSObjectMappedObject();

	/* **************************************** */
	/**
	 * @throws FileNotFoundException
	 */
	public GNSObjectMappedFileTypeDelimitedSeparator() throws FileNotFoundException {
		super();
	}

	/* **************************************** */
	/**
	 * @param aFileArg
	 * @throws FileNotFoundException
	 */
	public GNSObjectMappedFileTypeDelimitedSeparator(File aFileArg) throws FileNotFoundException {
		super(aFileArg);
	}

	/* **************************************** */
	/**
	 * 
	 * @param aFileArg
	 * @param aStringDelimiter
	 * @throws FileNotFoundException
	 */
	public GNSObjectMappedFileTypeDelimitedSeparator(File aFileArg, String aStringDelimiter)
			throws FileNotFoundException {
		super(aFileArg);
		if (aStringDelimiter == null)
			throw new FileNotFoundException(String.format(" can t use a null delimiter for the file (%s)", aFileArg));
		aStringDelimiterSeparator = aStringDelimiter;
	}

	/* **************************************** */
	/**
	 * @return the aStringDelimiter
	 */
	public synchronized String getStringDelimiter() {
		return aStringDelimiterSeparator;
	}

	/* **************************************** */
	/**
	 * @param aStringDelimiter the aStringDelimiter to set
	 */
	public synchronized void setStringDelimiter(String aStringDelimiter) {
		this.aStringDelimiterSeparator = aStringDelimiter;
	}

	/**
	 * 
	 * @return bFileHaveFirstLineHasKeyDescriptor
	 */
	public synchronized Boolean getFirstLineIsKeyDescriptor() {
		return bFileHaveFirstLineHasKeyDescriptor;
	}

	/**
	 * 
	 * @param bFirstLineIsKeyDescriptor
	 */
	public synchronized void setFirstLineIsKeyDescriptor(Boolean bFirstLineIsKeyDescriptor) {
		this.bFileHaveFirstLineHasKeyDescriptor = bFirstLineIsKeyDescriptor;
	}

	/* **************************************** */
	@Override
	public Boolean append(String aStringToAppend) throws IOException {
		initWriterIfNecessary();
		return super.append(String.format("%s%s", aStringToAppend, aStringDelimiterSeparator));
	}

	/* **************************************** */
	@Override
	public Boolean appendObjectToSerializedJSON(String aNodeChildElementName, Object aNodeChildElement)
			throws JFXApplicationException {

		Boolean bApppendStatus = false;
		try {
			initWriterIfNecessary();
			bApppendStatus = super.appendObjectToSerializedJSON(aNodeChildElementName, aNodeChildElement);
			if (!bApppendStatus)
				return bApppendStatus;
			bApppendStatus = super.appendWithNewLine(aStringDelimiterSeparator);
		} catch (Exception evERRENCODEJSON) {
			getLogger().logError(this.getClass(), evERRENCODEJSON);
			tragicClose();
			throw new JFXApplicationException(evERRENCODEJSON);
		}

		return bApppendStatus;
	}

	/* **************************************** */
	@Override
	public Boolean appendObjectToSerializedJSON(GNSObjectMappedObject aNodeChildElement)
			throws JFXApplicationException {

		Boolean bApppendStatus = false;
		try {
			initWriterIfNecessary();
			bApppendStatus = super.appendObjectToSerializedJSON(aNodeChildElement);
			if (!bApppendStatus)
				return bApppendStatus;
			bApppendStatus = super.appendWithNewLine(aStringDelimiterSeparator);
		} catch (Exception evERRENCODEJSON) {
			getLogger().logError(this.getClass(), evERRENCODEJSON);
			tragicClose();
			throw new JFXApplicationException(evERRENCODEJSON);
		}

		return bApppendStatus;
	}

	/* **************************************** */
	@Override
	public Boolean appendObject(Object aObjectLineToStringify) throws IOException {
		initWriterIfNecessary();
		StringBuilder aStringToAppend = new StringBuilder("");
		Boolean bAppendStatus = false;
		if ((aObjectLineToStringify instanceof String) || (aObjectLineToStringify instanceof Integer)
				|| (aObjectLineToStringify instanceof Double)) {
			bAppendStatus = super.appendWithNewLine(String.valueOf(aObjectLineToStringify));
		} else {
			Map<Object, Object> aIterableObject = new HashMap<>();
			aIterableObject.putAll((Map<Object, Object>) aObjectLineToStringify);
			for (Iterator<Entry<Object, Object>> iteratedObject = aIterableObject.entrySet().iterator(); iteratedObject
					.hasNext();) {
				aStringToAppend.append(String.format("%s%s", String.valueOf(iteratedObject.next()),
						((iteratedObject.hasNext()) ? aStringDelimiterSeparator : "")));
			}
			bAppendStatus = super.appendWithNewLine(aStringToAppend.toString());
		}

		return bAppendStatus;
	}

	@Override
	public Boolean save() throws IOException {
		initWriterIfNecessary();
		return super.save();
	}

	@Override
	protected String readln() throws IOException {
		return super.readln();
	}

	@Override
	public GNSObjectMappedObject readlnAsMapStringKey() throws IOException {
		initReaderIfNecessary();
		String aFileLineReaded = super.readln();
		
		if(isEOF()) return null;
		
		GNSObjectMappedObject aSplittedValues = new GNSObjectMappedObject();

		String[] arrayStringOfValues = aFileLineReaded.split(aStringDelimiterSeparator);
		for (int i = 0; i < arrayStringOfValues.length; i++) {
			aSplittedValues.put(String.valueOf(i), arrayStringOfValues[i]);
		}
		return aSplittedValues;
	}

	@Override
	protected Integer read() throws IOException {
		return 0;
	}

	@Override
	public boolean readFile() throws IOException {
		try {
			int i = 0;
			// init file reader
			initReaderIfNecessary();
			// read until EOF Mechanism is reached ...
			GNSObjectMappedObject aLineReaded = new GNSObjectMappedObject();
			while (!isEOF()) {
				// read until EOF Internal is Reached
				aLineReaded.clear();
				aLineReaded = readlnAsMapIntegerKey();

				if (aLineReaded.isEmpty() && isEOF())
					return true;

				if (bFileHaveFirstLineHasKeyDescriptor && aFirstLineKeyDescriptor.isEmpty()) {
					aFirstLineKeyDescriptor.putAll(aLineReaded);
				} else if (aFileContentDescriptor.put(String.valueOf(i++), aLineReaded) == null) {
					return false;
				}
			}
			return true;
		} catch (IOException evERRFILEIO) {
			super.tragicClose();
			getLogger().logError(this.getClass(), evERRFILEIO);
			throw evERRFILEIO;
		} finally {
			super.closeReader();
		}
	}

	@Override
	public GNSObjectMappedObject readlnAsMapIntegerKey() throws IOException {
		initReaderIfNecessary();
		String aFileLineReaded = super.readln();

		GNSObjectMappedObject aSplittedValues = new GNSObjectMappedObject();

		String[] arrayStringOfValues = aFileLineReaded.split(aStringDelimiterSeparator);
		for (int i = 0; i < arrayStringOfValues.length; i++) {
			aSplittedValues.put(i, arrayStringOfValues[i]);
		}
		return aSplittedValues;

	}

	public static void main(String[] args) {
		GNSObjectMappedLogger.getLogger().logInfo("Test class ... ");
		File aFilePath = new File("salaries.txt");

		GNSObjectMappedLogger.getLogger()
				.logInfo(" file :" + aFilePath.getAbsolutePath() + "::" + String.valueOf(aFilePath.exists()));

		try {
			GNSObjectMappedFileTypeDelimitedSeparator aFileAccessor = new GNSObjectMappedFileTypeDelimitedSeparator(
					aFilePath, "\\|");
			aFileAccessor.initReaderIfNecessary();
			do {
				
				GNSObjectMappedObject aMappedObjectLine = aFileAccessor.readlnAsMapStringKey();
				// 
				System.out.println(" :: "+aFileAccessor.readln());
				if(aMappedObjectLine != null)
					System.out.println(" ....  :: "+aMappedObjectLine.toAsString.apply(aMappedObjectLine) );
			} while (!aFileAccessor.isEOF());
			System.out.println(" :: .... clear ");
			
			aFileAccessor.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
