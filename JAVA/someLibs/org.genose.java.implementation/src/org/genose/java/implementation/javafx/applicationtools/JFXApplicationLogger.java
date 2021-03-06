/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import org.genose.java.implementation.streams.ConsoleStream;

/**
 * @author 59013-36-18 Intend to create a console I/O loggeable activity
 *
 */
public class JFXApplicationLogger extends ConsoleStream implements System.Logger {
	private static JFXApplicationLogger pJFXApplicationLoggerSingleton = null;

	public enum LOGGERFORMAT {
		LOG_DEFAULT(
				"%n ************************************** %n %s %n ************************************** %n %s %n ************************************** %n "),
		LOG_ERROR(
				"%n ************************************** %n Error ... %n ************************************** %n %s %n ************************************** %n %s %n ************************************** %n "),
		LOG_EXCEPTION_WITHSTACK(
				" %n ******************************************* * %n Error ... %n Occured in : %s %n ************************************** %n Throwed type : %s %n Message : %s %n ********************************************** %n CallStack : %n %s  %n ************************************** %n");

		private String strValue;
		private static java.util.HashMap<Object, Object> map = new java.util.HashMap<>();

		/* ******************************************************* */
		/**
		 * 
		 * @param aStrValue
		 */
		private LOGGERFORMAT(String aStrValue) {
			this.strValue = aStrValue;
		}

		/* ******************************************************* */
		/**
		 * 
		 */
		static {
			for (LOGGERFORMAT aType : LOGGERFORMAT.values()) {
				map.put(aType.strValue, aType);
			}
		}

		/* ******************************************************* */
		/**
		 * 
		 * @param pageType
		 * @return
		 */
		public static LOGGERFORMAT valueOf(int pageType) {
			return (LOGGERFORMAT) map.get(pageType);
		}

		/* ******************************************************* */
		/**
		 * 
		 * @return
		 */
		public String getValue() {
			return strValue;
		}

		/* ******************************************************* */
		/**
		 * 
		 * @param strCode
		 * @return
		 */
		public String getEnumByString(String strCode) {
			for (LOGGERFORMAT eType : LOGGERFORMAT.values()) {
				if (strCode.toUpperCase().compareTo(String.valueOf(strValue.toUpperCase())) == 0)
					return eType.name();
			}
			return null;
		}

		/* ******************************************************* */
		@Override
		public String toString() {
			for (LOGGERFORMAT eType : LOGGERFORMAT.values()) {
				if (strValue.compareTo(eType.strValue) == 0)
					return eType.name();
			}
			return "";
		}

		/* ******************************************************* */
		/**
		 * 
		 * @return
		 */
		public List<String> split() {
			ArrayList<String> aSplittedValues = new ArrayList<>();
			String[] arrayStringOfValues = strValue.split("%n");
			for (int i = 0; i < arrayStringOfValues.length; i++) {
				aSplittedValues.add(arrayStringOfValues[i]);
			}
			return aSplittedValues;
		}

	};

	/*
	 * *****************************************************************************
	 * ******************
	 */

	private System.Logger aLogger = null;
	private String aLoggerDescription = JFXApplicationLogger.class.getName() + ":System.getLogger([String])";
	private String aLoggerName = "";
	private MessageFormat aMessageFormatLogger = null;
	private List<StackTraceElement> lastStackTraceList = null;

	/*
	 * *****************************************************************************
	 * ******************
	 */
	/**
	 * 
	 */
	public JFXApplicationLogger() {
		super();
		aLoggerName = JFXApplication.class.getClass().toString();
		aLogger = System.getLogger(aLoggerName);
		lastStackTraceList = new ArrayList<>();
	}

	/*
	 * *****************************************************************************
	 * ******************
	 */
	/**
	 * 
	 * @param loggerName
	 */
	public JFXApplicationLogger(String loggerName) {
		super();
		aLoggerName = loggerName;
		aLogger = System.getLogger(aLoggerName);
		lastStackTraceList = new ArrayList<>();
	}

	/*
	 * *****************************************************************************
	 * ******************
	 */
	/**
	 * @param abufferedReader
	 */
	public JFXApplicationLogger(BufferedReader abufferedReader) {
		super(abufferedReader);
		lastStackTraceList = new ArrayList<>();
	}

	/*
	 * *****************************************************************************
	 * ******************
	 */
	/**
	 * @param apPrintStream
	 */
	public JFXApplicationLogger(PrintStream apPrintStream) {
		super(apPrintStream);
		lastStackTraceList = new ArrayList<>();
	}

	/**
	 * used to create a stdout
	 * @throws UnsupportedOperationException
	 */
	private void singletonInstanceCreate() throws UnsupportedOperationException {
		synchronized (JFXApplicationLogger.class) {
			if (pJFXApplicationLoggerSingleton != null)
				throw new UnsupportedOperationException(
						getClass() + " is singleton but constructor called more than once");
			pJFXApplicationLoggerSingleton = this;
		}
	}

	/*
	 * *****************************************************************************
	 * ******************
	 */
	@Override
	public ConsoleStream getConsoleLog() {
		return super.getConsoleLog();
	}

	@Override
	public void setConsoleLogStream(PrintStream aconsoleLogStream) {
		super.setConsoleLogStream(aconsoleLogStream);
	}

	@Override
	public Object getConsoleLogObject() {
		return super.getConsoleLogObject();
	}

	@Override
	public void setConsoleLogObject(Object aconsoleLogObject) {
		super.setConsoleLogObject(aconsoleLogObject);
	}

	@Override
	public void println(String arg0) {
		super.println(arg0);
	}

	@Override
	public void print(String arg0) {
		super.print(arg0);
	}

	@Override
	public String readLine() throws IOException {
		return super.readLine();
	}

	@Override
	public void clear() {
		super.clear();
	}

	@Override
	public String getName() {
		return aLoggerName;
	}

	@Override
	public boolean isLoggable(Level level) {
		return false;
	}

	/*
	 * *****************************************************************************
	 */
	@Override
	/**
	 * 
	 */
	public void log(Level level, ResourceBundle bundle, String message, Throwable aThrownedCause) {
		aLogger.log(level, String.format("%n ************************************** %n %s %n :: %s %n :: %s %n ************************************** %n", aThrownedCause, bundle.toString(), message));

	}

	/* ****************************************************** */
	@Override
	public void log(Level level, ResourceBundle bundle, String format, Object... params) {
		try {
			aLogger.log(level, String.format("%n ************************************** %n %s :: %s %n ************************************** %n ", ((bundle == null) ? "[NULL BUNDLE]" : bundle.toString()),
					String.format(format, params)));
		} catch (Exception evERRFATALSYSLOG) {
			System.out.println(String.format(LOGGERFORMAT.LOG_EXCEPTION_WITHSTACK.getValue(),
					evERRFATALSYSLOG.toString(), Arrays.asList(getStackTrace()).toString().replaceAll(",", "\n")));
		}
	}

	/* ****************************************************** */
	/**
	 * 
	 * @param className
	 * @param message
	 * @param aSeverity
	 */
	public void log(String className, String message, System.Logger.Level aSeverity) {
		log(aSeverity, ((className != null && className.length() > 0) ? className + ":" : "") + message);
	}

	/* ****************************************************** */
	/**
	 * 
	 * @param message
	 */
	public void logInfo(String message) {
		logInfo(this.getClass(), message);
	}

	/**
	 * 
	 * @param fromCurrentClass
	 * @param message
	 */
	public void logInfo(Class<?> fromCurrentClass, String message) {

		log(System.Logger.Level.INFO,
				((fromCurrentClass != null && fromCurrentClass.getClass().getName().length() > 0)
						? fromCurrentClass.getClass().getName() + " : "
						: getClass().getName() + "[NULL CLASS] : ") + "\n"
						+ ((message == null) ? Arrays.asList(getStackTrace()).toString().replaceAll(",", "\n")
								: String.valueOf(message)));

	}

	/**
	 * 
	 * @param currenClass
	 * @param message
	 */
	/* ****************************************************** */
	public void logWarning(Class<?> fromCurrentClass, String message) {

		log(System.Logger.Level.WARNING,
				((fromCurrentClass != null && fromCurrentClass.getClass().getName().length() > 0)
						? fromCurrentClass.getClass().getName() + " : "
						: getClass().getName() + "[NULL CLASS] : ") + "\n"
						+ ((message == null) ? Arrays.asList(getStackTrace()).toString().replaceAll(",", "\n")
								: String.valueOf(message)));

	}

	/* ****************************************************** */
	/**
	 * 
	 * @param fromCurrentClass
	 * @param message
	 */
	public void logError(Class<?> fromCurrentClass, String message) {

		log(System.Logger.Level.ERROR,
				((fromCurrentClass != null && fromCurrentClass.getClass().getName().length() > 0)
						? fromCurrentClass.getClass().getName() + " : "
						: getClass().getName() + "[NULL CLASS] : ") + "\n"
						+ ((message == null) ? Arrays.asList(getStackTrace()).toString().replaceAll(",", "\n")
								: String.valueOf(message)));
	}

	/* ****************************************************** */
	/**
	 * 
	 * @param fromCurrentClass
	 * @param aThrowedCause
	 */
	public void logError(Class<?> fromCurrentClass, Throwable aThrowedCause) {

		if (aThrowedCause == null) {
			logError(((fromCurrentClass == null) ? getClass() : fromCurrentClass), getClass(),
					"Neverland happen sometime ...", Arrays.asList(getStackTrace()).toString().replaceAll(",", "\n"));
		} else {
			logError(((fromCurrentClass == null) ? getClass() : fromCurrentClass), aThrowedCause.getClass(),
					aThrowedCause.getMessage(),
					Arrays.asList(aThrowedCause.getStackTrace()).toString().replaceAll(",", "\n"));
		}

	}

	/* ****************************************************** */
	/**
	 * 
	 * @param fromCurrentClass
	 * @param throwedType
	 * @param message
	 * @param callStack
	 */
	public void logError(Class<?> fromCurrentClass, Class<?> throwedType, String message, String callStack) {

		log(System.Logger.Level.ERROR,
				String.format(LOGGERFORMAT.LOG_EXCEPTION_WITHSTACK.getValue(), String.valueOf(fromCurrentClass),
						String.valueOf(throwedType), String.valueOf(message),
						((callStack == null) ? Arrays.asList(getStackTrace()).toString().replaceAll(",", "\n")
								: String.valueOf(callStack).replaceAll(",", "\n"))));
	}
	
	
	public void logError(String message, Throwable throwedEvent) {
		
		logError(this.getClass(), throwedEvent.getClass(), message, throwedEvent.getMessage()+"\n*********************\n"+throwedEvent.getCause());
	}
	/* ****************************************************** */
	/**
	 * 
	 * @return List<StackTraceElement>
	 */
	private StackTraceElement[] getStackTrace() {
		StackTraceElement[] aStackTrace = Thread.currentThread().getStackTrace();

		lastStackTraceList.clear();
		for (int i = 0; i < (aStackTrace.length - 1); i++) {
			lastStackTraceList.add(aStackTrace[i]);
		}

		return aStackTrace;
	}
	
	private StackTraceElement getLastStackTrace()
	{
		StackTraceElement[] aStackTrace = Thread.currentThread().getStackTrace();
		return aStackTrace[aStackTrace.length -2];
	}
	
	
	/* ****************************************************** */
	/**
	 * 
	 * @return
	 */
	public static JFXApplicationLogger getLogger() {
		synchronized (JFXApplicationLogger.class) {
			if (pJFXApplicationLoggerSingleton == null) {
				pJFXApplicationLoggerSingleton = new JFXApplicationLogger();
			}
			return pJFXApplicationLoggerSingleton;
		}
	}



}
