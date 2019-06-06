/**
 * 
 */
package org.genose.java.implementation.javafx.applicationtools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;
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
	
	/**
	 * 
	 */
	public JFXApplicationLogger() {
		aLoggerName = JFXApplication.class.getClass().toString();
		aLogger = System.getLogger(aLoggerName);
	}
	
	public JFXApplicationLogger(String loggerName) {
		aLoggerName = loggerName;
		aLogger = System.getLogger(aLoggerName);
	}
	/**
	 * @param abufferedReader
	 */
	public JFXApplicationLogger(BufferedReader abufferedReader) {
		super(abufferedReader);
	}

	/**
	 * @param apPrintStream
	 */
	public JFXApplicationLogger(PrintStream apPrintStream) {
		super(apPrintStream);
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
		// TODO Auto-generated method stub
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
	public void log(Level level, ResourceBundle bundle, String msg, Throwable thrown) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level level, ResourceBundle bundle, String format, Object... params) {
		// TODO Auto-generated method stub
		
	}

	public void log(String className, String message, System.Logger.Level aSeverity) {
		log( aSeverity, ((className != null && className.length()>0)?className+":":"")+message );
	}
	
	public void logInfo(String className, String message) {
		log(System.Logger.Level.INFO, ((className != null && className.length()>0)?className+":":"")+message );
	}
	public void logWarning(String className, String message) {
		log(System.Logger.Level.WARNING, ((className != null && className.length()>0)?className+":":"")+message );
	}
	
	public void logError(String className, String message) {
		log(System.Logger.Level.ERROR, ((className != null && className.length()>0)?className+":":"")+message );
	}

}
