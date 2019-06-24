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
import java.util.List;
import java.util.ResourceBundle;

import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
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
				" %n ******************************************* * BEGIN * ******************************************* * %n Error ... %n Occured in : %s %n ************************************** %n Throwed type : %s %n Message : %s  %nCause : %s %n ********************************************** %n CallStack : %n %s  %n ************************************** %n  %n Additional info : %n %s  %n ************************************** %n ******************************************* * END * ******************************************* *"),
		LOG_EXCEPTION_WITHSTACK_WITHORIGIN(
				" %n ******************************************* * BEGIN * ******************************************* * %n Error ... %n Occured in : %s %n ************************************** %n Throwed type : %s %n Message : %s %nCause : %s %n ********************************************** %n CallStack : %n %s  %n ************************************** %n Raised From previously cause %n ************************************** %n %s %n ************************************** %n  %n Additional info : %n %s  %n ************************************** %n  ******************************************* * END * ******************************************* *");

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
	}

	/**
	 * used to create a stdout
	 * 
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
		aLogger.log(level, String.format(
				"%n ************************************** %n %s %n :: %s %n :: %s %n ************************************** %n",
				aThrownedCause, bundle.toString(), message));

	}

	/* ****************************************************** */
	@Override
	public void log(Level level, ResourceBundle bundle, String format, Object... params) {
		try {
			aLogger.log(level, String.format(
					"%n ************************************** %n %s :: %s %n ************************************** %n ",
					((bundle == null) ? "[NULL BUNDLE]" : bundle.toString()), String.format(format, params)));
		} catch (Exception evERRFATALSYSLOG) {
			System.out.println(String.format(LOGGERFORMAT.LOG_EXCEPTION_WITHSTACK.getValue(),
					evERRFATALSYSLOG.toString(), Arrays.asList(JFXApplicationHelper.getStackTrace()).toString()
							.replaceAll(",", "\n").replaceAll("\\[", "").replaceAll("\\]", "")));
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
						: getClass().getName() + "[NULL CLASS] : ")
						+ "\n"
						+ ((message == null)
								? Arrays.asList(JFXApplicationHelper.getStackTrace()).toString().replaceAll(",", "\n")
										.replaceAll("\\[", "").replaceAll("\\]", "")
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
						: getClass().getName() + "[NULL CLASS] : ")
						+ "\n"
						+ ((message == null)
								? Arrays.asList(JFXApplicationHelper.getStackTrace()).toString().replaceAll(",", "\n")
										.replaceAll("\\[", "").replaceAll("\\]", "")
								: String.valueOf(message)));

	}

	/* ****************************************************** */
	/**
	 * 
	 * @param fromCurrentClass
	 * @param message
	 */
	public void logError(Class<?> fromCurrentClass, String message) {
		logError(fromCurrentClass, null, message, null);
	}

	/* ****************************************************** */
	/**
	 * 
	 * @param fromCurrentClass
	 * @param aThrowedCause
	 */
	public void logError(Class<?> fromCurrentClass, Throwable throwedEvent) {
		logError(fromCurrentClass, throwedEvent, null, null);
	}

	/* ****************************************************** */
	/**
	 * 
	 * @param fromCurrentClass
	 * @param throwedType
	 * @param message
	 * @param callStack
	 */
	public void logError(Class<?> fromCurrentClass, Throwable throwedEvent, String message) {
		logError(fromCurrentClass, throwedEvent, message, null);
	}

	public void logError(Class<?> fromCurrentClass, Throwable throwedEvent, String message, String callStackInfo) {

		StackTraceElement[] aStackTraceList = null;
		String sPreviousCause = "[NULL EVENT]";
		String sStackTraceFormatted = null;
		Throwable aEnclavedThrowable = null;

		// :: "[Previously Raised / Exception in Exception ]"
		if (throwedEvent == null) {
			aStackTraceList = JFXApplicationHelper.getStackTrace();
			sPreviousCause = String.format(
					"%n *************************** %n %s %n *************************** %n message : %s %n *************************** %n ",
					message,  sPreviousCause);

			sStackTraceFormatted = String.format(LOGGERFORMAT.LOG_EXCEPTION_WITHSTACK_WITHORIGIN.getValue(),
					String.valueOf(fromCurrentClass),
					"[NULL ORIGIN CLASS]", 
					"[NULL MESSAGE]", 
					"[NULL CAUSE]",
					JFXApplicationException.doFormattedStackTrace(aStackTraceList),
					sPreviousCause,
					String.valueOf(callStackInfo));

		} else {

			if (throwedEvent instanceof JFXApplicationException) {
				aEnclavedThrowable = ((JFXApplicationException) throwedEvent).getEncapsuledEventException();
if(aEnclavedThrowable != null) {
				sPreviousCause = String.format(LOGGERFORMAT.LOG_EXCEPTION_WITHSTACK.getValue(),
						String.valueOf(aEnclavedThrowable.getClass()),
						String.valueOf(aEnclavedThrowable.getClass()),
						String.valueOf(aEnclavedThrowable.getMessage()),
						String.valueOf(aEnclavedThrowable.getCause()),
						JFXApplicationException.doFormattedStackTrace(aEnclavedThrowable.getStackTrace()),
						String.valueOf(callStackInfo));
}else {
	sPreviousCause = String.format(LOGGERFORMAT.LOG_EXCEPTION_WITHSTACK.getValue(),
			String.valueOf(sPreviousCause),
			String.valueOf(sPreviousCause),
			String.valueOf(sPreviousCause),
			String.valueOf(sPreviousCause),
			sPreviousCause,
			String.valueOf(callStackInfo));
}
			}

			aStackTraceList = throwedEvent.getStackTrace();

			// ******** matbe is a forwarded Throwable for we pick message from both sources
			String sMessageStackedCause = String.format(
					"%n *************************** %n message %s %n *************************** %n Event message : %s %n *************************** %n ",
					message, String.valueOf(throwedEvent.getMessage()));
			// ******** final message with all informations ...
			sStackTraceFormatted = String.format(LOGGERFORMAT.LOG_EXCEPTION_WITHSTACK_WITHORIGIN.getValue(),
					String.valueOf(fromCurrentClass), 
					String.valueOf(throwedEvent.getClass()),
					sMessageStackedCause,
					String.valueOf(throwedEvent.getCause()),
					JFXApplicationException.doFormattedStackTrace(aStackTraceList),
					sPreviousCause,
					String.valueOf(callStackInfo));
		}

		log(System.Logger.Level.ERROR, sStackTraceFormatted);
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
