/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools.files;
 
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.genose.java.implementation.javafx.applicationtools.JFXApplication.JFXFILETYPE;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationLogger;
import org.genose.java.implementation.javafx.applicationtools.arraysmapslists.JFXApplicationMappedObject;
import org.genose.java.implementation.javafx.applicationtools.arraysmapslists.JFXApplicationObjectValue;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import json.*;

import json.JSONArray;

/**
 * @author 59013-36-18
 *
 */
public class JFXApplicationFileAccessor implements Stream<JFXApplicationMappedObject> {

	/**
	 * 
	 * File relative
	 */
	protected JFXApplicationMappedObject aFileContentDescriptor;
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
	private String aBufferedReaderLineString = null;
	private Integer aBufferedReaderChar = null;

	private BufferedWriter aBufferedWriter = null;

	private FileReader aFileReader = null;
	private FileWriter aFileWriter = null;

	private JFXApplicationLogger aLogger = null;
	private ArrayList<String> aAuthourizedFileExtentions = null;

	/* **************************************** */
	/**
	 * 
	 */
	public JFXApplicationFileAccessor() throws FileNotFoundException {
		throw new FileNotFoundException(getClass().getName() + ": file argument is null or file not exists ");
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
			throw new FileNotFoundException(getClass().getName() + ": file argument is null or file not exists ");
		}
		/* **************************************** */
		if (!aFileArg.exists()) {
			throw new FileNotFoundException(getClass().getName() + ": file argument is null or file not exists ");
		}
		/* **************************************** */
		if (aFileArg.canExecute()) {

		}
		aAuthourizedFileExtentions = new ArrayList<String>();
		aAuthourizedFileExtentions.addAll(JFXFILETYPE.FILETYPE_READEABLE_TEXT.split());

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

	public Boolean appendObject(Object aObjectLineToStringify) throws IOException {
		StringBuilder aStringToAppend = new StringBuilder("");
		Boolean bAppendStatus = false;
		if ((aObjectLineToStringify instanceof String) || (aObjectLineToStringify instanceof Integer)
				|| (aObjectLineToStringify instanceof Double)) {
			bAppendStatus = appendWithNewLine(String.valueOf(aObjectLineToStringify));
		} else {
			Map<Object, Object> aIterableObject = new HashMap<>();
			aIterableObject.putAll((Map<Object, Object>) aObjectLineToStringify);
			for (Iterator iteratedObject = aIterableObject.entrySet().iterator(); iteratedObject.hasNext();) {
				Map.Entry<Object, Object> aEntry = (Entry<Object, Object>) iteratedObject.next();
				aStringToAppend.append(String.format("[%s]:%s%s", String.valueOf(aEntry.getKey()),
						String.valueOf(aEntry.getValue()), ((iteratedObject.hasNext()) ? " " : "")));
				bAppendStatus = appendWithNewLine(aStringToAppend.toString());
				if (!bAppendStatus)
					break;
			}
		}

		return bAppendStatus;
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
		if (aBufferedWriter == null) {
			throw new JFXApplicationException("can t append to unintilaize buffer ... ");
		}
		/* **************************************** */
		JSONArray dataset = new JSONArray(aChildNodeElement);

		try {
			aBufferedWriter.append(dataset.toString());
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
	public Boolean appendObjectToSerializedJSON(JFXApplicationMappedObject aNodeChildElement)
			throws JFXApplicationException {
		try {

			// :: https://www.baeldung.com/java-map-to-string-conversion
			// prepare to transfrom current data nodes to JSON ...
			JSONObject dataset = new JSONObject();

			if (!(aNodeChildElement instanceof JFXApplicationMappedObject)) {
				throw new JFXApplicationException(getClass().getName() + ": the node class "
						+ aNodeChildElement.getClass() + " is not iterable ... ");
			}
			/* **************************************** */
			for (Entry<String, JFXApplicationObjectValue> aNodeChildElementEntry : aNodeChildElement.entrySet()) {

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

				dataset.put(aNodeChildElementName, aNodeChildElementValue);

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
		return aBufferedReaderLineString == null;
	}

	/* **************************************** */
	/**
	 * 
	 * @return String or NULL
	 * @throws IOException
	 */
	protected String readln() throws IOException {
		try {
			if (aBufferedReader == null)
				throw new IOException("File Reader must be initilized before calling this method ... ");
			aBufferedReaderLineString = aBufferedReader.readLine();
			aBufferedReaderChar = 0;

		} catch (IOException evERRFILEIO) {
			getLogger().logError(this.getClass(), evERRFILEIO);
			tragicClose();
			throw evERRFILEIO;
		}
		return aBufferedReaderLineString;
	}

	/* **************************************** */
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public JFXApplicationMappedObject readlnAsMapIntegerKey() throws IOException {
		readln();
		JFXApplicationMappedObject aReadedLineMap = new JFXApplicationMappedObject();
		(aReadedLineMap).put(0, aBufferedReaderLineString);
		return (aReadedLineMap);
	}

	public JFXApplicationMappedObject readlnAsMapStringKey() throws IOException {
		readln();
		JFXApplicationMappedObject aReadedLineMap = new JFXApplicationMappedObject();
		(aReadedLineMap).put(0, aBufferedReaderLineString);
		return (aReadedLineMap);
	}

	/* **************************************** */
	/**
	 * 
	 * @return CharBuffer
	 * @throws IOException
	 */
	protected Integer read() throws IOException {

		try {
			if (aBufferedReader == null)
				throw new IOException("File Reader must be initilized before calling this method ... ");
			aBufferedReaderChar = aBufferedReader.read();

			// handle EOF Mechanism
			if (aBufferedReaderChar != (-1)) {
				aBufferedReaderLineString = Character.toString(aBufferedReaderChar);
			} else {
				aBufferedReaderLineString = null;
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
	protected boolean readFile() throws IOException {

		try {
			int i = 0;
			// init file reader
			initReader();
			// read until EOF Mechanism is reached ...
			String aLineReaded = "";
			while (!isEOF()) {
				// read until EOF Internal is Reached
				aLineReaded = readln();

				if (aLineReaded.isEmpty())
					return false;

				if (aFileContentDescriptor.put(String.valueOf(i++), aLineReaded) == null) {
					return false;
				}
			}
			return true;
		} catch (IOException evERRFILEIO) {
			tragicClose();
			getLogger().logError(this.getClass(), evERRFILEIO);
			throw evERRFILEIO;
		} finally {
			closeReader();
		}
	}

	/* **************************************** */
	/**
	 * 
	 * @throws IOException
	 */
	protected void initReaderIfNecessary() throws IOException {
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
	protected void initWriterIfNecessary() throws IOException {

		try {
			if (aBufferedWriter == null) {
				aFileWriter = new FileWriter(aFileDescriptor);
				aBufferedWriter = new BufferedWriter(aFileWriter);
			}

		} catch (IOException evERRFILEIO) {
			getLogger().logError(this.getClass(), evERRFILEIO);
			tragicClose();
			throw evERRFILEIO;
		}
	}

	/**
	 * 
	 * @throws IOException
	 */
	protected void initWriter() throws IOException {

		try {
			if (aBufferedWriter == null) {
				aFileWriter = new FileWriter(aFileDescriptor);
				aBufferedWriter = new BufferedWriter(aFileWriter);
			} else {
				if (aFileWriter == null) {
					aFileWriter = new FileWriter(aFileDescriptor);
				}

				aBufferedWriter = new BufferedWriter(aFileWriter);
			}

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
	protected void tragicClose() {
		closeReader();
		closeWriter();
	}

	/* **************************************** */
	/**
	 * close reader ressources
	 */
	protected void closeReader() {
		try {

			if (aFileReader != null) {
				aFileReader.close();
				aFileReader = null;
			}

			if (aBufferedReader != null) {
				aBufferedReader.close();
				aBufferedReader = null;
			}
		} catch (Exception evERRFILEIO) {
			getLogger().logError(this.getClass(), evERRFILEIO);
		}
	}

	protected void closeWriter() {
		try {
			if (aFileWriter != null) {
				Writer.nullWriter();
				aFileWriter = null;
			}

			if (aBufferedWriter != null) {
				Writer.nullWriter();
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
	/* **************************************** */
	// values accessors relatives ...
	/* **************************************** */
	/* **************************************** */

	/* **************************************** */
	/* **************************************** */
	// Iterable and Streams relatives ...
	/* **************************************** */
	/* **************************************** */

	@Override
	public Iterator<JFXApplicationMappedObject> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Spliterator<JFXApplicationMappedObject> spliterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isParallel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Stream<JFXApplicationMappedObject> sequential() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<JFXApplicationMappedObject> parallel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<JFXApplicationMappedObject> unordered() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<JFXApplicationMappedObject> onClose(Runnable closeHandler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public Stream<JFXApplicationMappedObject> filter(Predicate<? super JFXApplicationMappedObject> predicate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R> Stream<R> map(Function<? super JFXApplicationMappedObject, ? extends R> mapper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntStream mapToInt(ToIntFunction<? super JFXApplicationMappedObject> mapper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongStream mapToLong(ToLongFunction<? super JFXApplicationMappedObject> mapper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DoubleStream mapToDouble(ToDoubleFunction<? super JFXApplicationMappedObject> mapper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R> Stream<R> flatMap(Function<? super JFXApplicationMappedObject, ? extends Stream<? extends R>> mapper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntStream flatMapToInt(Function<? super JFXApplicationMappedObject, ? extends IntStream> mapper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongStream flatMapToLong(Function<? super JFXApplicationMappedObject, ? extends LongStream> mapper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DoubleStream flatMapToDouble(Function<? super JFXApplicationMappedObject, ? extends DoubleStream> mapper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<JFXApplicationMappedObject> distinct() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<JFXApplicationMappedObject> sorted() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<JFXApplicationMappedObject> sorted(Comparator<? super JFXApplicationMappedObject> comparator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<JFXApplicationMappedObject> peek(Consumer<? super JFXApplicationMappedObject> action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<JFXApplicationMappedObject> limit(long maxSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<JFXApplicationMappedObject> skip(long n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void forEach(Consumer<? super JFXApplicationMappedObject> action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void forEachOrdered(Consumer<? super JFXApplicationMappedObject> action) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <A> A[] toArray(IntFunction<A[]> generator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JFXApplicationMappedObject reduce(JFXApplicationMappedObject identity,
			BinaryOperator<JFXApplicationMappedObject> accumulator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<JFXApplicationMappedObject> reduce(BinaryOperator<JFXApplicationMappedObject> accumulator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <U> U reduce(U identity, BiFunction<U, ? super JFXApplicationMappedObject, U> accumulator,
			BinaryOperator<U> combiner) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super JFXApplicationMappedObject> accumulator,
			BiConsumer<R, R> combiner) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R, A> R collect(Collector<? super JFXApplicationMappedObject, A, R> collector) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<JFXApplicationMappedObject> min(Comparator<? super JFXApplicationMappedObject> comparator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<JFXApplicationMappedObject> max(Comparator<? super JFXApplicationMappedObject> comparator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean anyMatch(Predicate<? super JFXApplicationMappedObject> predicate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean allMatch(Predicate<? super JFXApplicationMappedObject> predicate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean noneMatch(Predicate<? super JFXApplicationMappedObject> predicate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Optional<JFXApplicationMappedObject> findFirst() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<JFXApplicationMappedObject> findAny() {
		// TODO Auto-generated method stub
		return null;
	}

}
