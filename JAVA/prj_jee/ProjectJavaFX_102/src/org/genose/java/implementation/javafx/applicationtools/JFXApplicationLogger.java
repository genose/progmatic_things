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
 * @author 59013-36-18
 * Intend to create a console I/O loggeable activity
 *
 */
public class JFXApplicationLogger extends ConsoleStream implements System.Logger {

	private System.Logger aLogger = null;
	private String aLoggerDescription = JFXApplicationLogger.class.getName()+":System.getLogger([String])";
	private String aLoggerName = "";
	private MessageFormat aMessageFormatLogger = null;
	private List<StackTraceElement> lastStackTraceList = null;
	/**
	 * 
	 */
	public JFXApplicationLogger() {
		aLoggerName = JFXApplication.class.getClass().toString();
		aLogger = System.getLogger(aLoggerName);
		lastStackTraceList = new ArrayList<>();
	}
	
	public JFXApplicationLogger(String loggerName) {
		aLoggerName = loggerName;
		aLogger = System.getLogger(aLoggerName);
		lastStackTraceList = new ArrayList<>();
	}
	/**
	 * @param abufferedReader
	 */
	public JFXApplicationLogger(BufferedReader abufferedReader) {
		super(abufferedReader);
		lastStackTraceList = new ArrayList<>();
	}

	/**
	 * @param apPrintStream
	 */
	public JFXApplicationLogger(PrintStream apPrintStream) {
		super(apPrintStream);
		lastStackTraceList = new ArrayList<>();
	}

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

	@Override
	public void log(Level level, ResourceBundle bundle, String message, Throwable aThrownedCause) {
		aLogger.log(level,String.format("%s \n :: %s \n :: %s", aThrownedCause, bundle.toString(), message));
		
	}
	/* ****************************************************** */
	@Override
	public void log(Level level, ResourceBundle bundle, String format, Object... params) {
		aLogger.log(level,String.format("%s :: %s", bundle.toString(), String.format(format, params)));
	}
	/* ****************************************************** */
	/**
	 * 
	 * @param className
	 * @param message
	 * @param aSeverity
	 */
	public void log(String className, String message, System.Logger.Level aSeverity) {
		log( aSeverity, ((className != null && className.length()>0)?className+":":"")+message );
	}
	/* ****************************************************** */
	/**
	 * 
	 * @param currentClass
	 * @param message
	 */
	public void logInfo(Class<?> fromCurrentClass, String message) {
		String format = "******************************************* \n Occured in : %s \n  Message : %s \n********************************************** \n ";
		log(System.Logger.Level.INFO, ((fromCurrentClass != null && fromCurrentClass.getClass().getName().length()>0)?fromCurrentClass.getClass().getName()+" : ":getClass().getName()+"[NULL CLASS] : ")+
				"\n"+
				((message == null )? Arrays.deepToString(getStackTrace()) : String.valueOf(message)) );
		
	}
	/**
	 * 
	 * @param currenClass
	 * @param message
	 */
	/* ****************************************************** */
	public void logWarning(Class<?> fromCurrentClass, String message) {
		String format = "******************************************* \n Occured in : %s \n  Message : %s \n********************************************** \n ";
		log(System.Logger.Level.WARNING, ((fromCurrentClass != null && fromCurrentClass.getClass().getName().length()>0)?fromCurrentClass.getClass().getName()+" : ":getClass().getName()+"[NULL CLASS] : ")+
				"\n"+
				((message == null )? Arrays.deepToString(getStackTrace()) : String.valueOf(message)) );
		
	}
	/* ****************************************************** */
	/**
	 * 
	 * @param fromCurrentClass
	 * @param message
	 */
	public void logError(Class<?> fromCurrentClass, String message) {
		String format = "******************************************* \n Occured in : %s \n Throwed type : %s \n Message : %s \n********************************************** \n CallStack : \n %s";
		
		log(System.Logger.Level.ERROR, ((fromCurrentClass != null && fromCurrentClass.getClass().getName().length()>0)?fromCurrentClass.getClass().getName()+" : ":getClass().getName()+"[NULL CLASS] : ")+
				"\n"+
				((message == null )? Arrays.deepToString(getStackTrace()) : String.valueOf(message)) );
	}
	/* ****************************************************** */
	/**
	 * 
	 * @param fromCurrentClass
	 * @param aThrowedCause
	 */
	public void logError(Class<?> fromCurrentClass, Throwable aThrowedCause) {
		
		if( aThrowedCause == null ) {
			;;
			logError(((fromCurrentClass == null )? getClass() : fromCurrentClass),
					getClass() ,
					"Neverland happen sometime ...",
					Arrays.deepToString(getStackTrace())
				);
		} else {
			logError(((fromCurrentClass == null )? getClass() : fromCurrentClass),
					aThrowedCause.getClass() ,
					aThrowedCause.getMessage(),
					Arrays.toString(aThrowedCause.getStackTrace())
				);
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
	public void logError(Class<?> fromCurrentClass,Class<?> throwedType ,String message, String callStack) {
		
		String format = "******************************************* \n Occured in : %s \n Throwed type : %s \n Message : %s \n********************************************** \n CallStack : \n %s"; 
		
		log(System.Logger.Level.ERROR,
				String.format(format, String.valueOf(fromCurrentClass) ,
				String.valueOf(throwedType) ,
				String.valueOf(message),
				((callStack == null )? Arrays.deepToString(getStackTrace()) : String.valueOf(callStack)) ) 
				);
	}
	/* ****************************************************** */
	/**
	 * 
	 * @return List<StackTraceElement>
	 */
	private StackTraceElement[] getStackTrace() {
		 StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace(); 
		
		lastStackTraceList.clear();
		for (int i = 0; i < (stackTrace.length-1); i++) {
			lastStackTraceList.add(stackTrace[i]);
		}
		
		return stackTrace;
	}

}
