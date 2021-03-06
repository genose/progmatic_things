package org.genose.java.implementation.streams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

public class ConsoleStream {


	protected PrintStream consoleLogStream = System.out;
	private BufferedReader consoleLogInputStream;
	
	protected Object consoleLogObject = null;
	protected Object consoleLogInputObject = null;
	
	
	public ConsoleStream() {
		super();
	} 
	
	public ConsoleStream(BufferedReader abufferedReader) {

			consoleLogInputStream = abufferedReader;
			
	}

	public ConsoleStream(PrintStream apPrintStream) {
			consoleLogStream = apPrintStream;		
	}	
	
	/**
	 * @return the consoleLogStream
	 */
	public ConsoleStream getConsoleLog() {
		return this;
	}


	/**
	 * @param consoleLogStream the consoleLogStream to set
	 */
	public void setConsoleLogStream(PrintStream aconsoleLogStream) {
		consoleLogStream = aconsoleLogStream;
	}


	/**
	 * @return the consoleLogObject
	 */
	public Object getConsoleLogObject() {
		return consoleLogObject;
	}


	/**
	 * @param consoleLogObject the consoleLogObject to set
	 */
	public void setConsoleLogObject(Object aconsoleLogObject) {
		consoleLogObject = aconsoleLogObject;
	}


	public void println(String arg0) {
		if(consoleLogObject != null)
		{
			((ConsoleStream)consoleLogObject).println(arg0);	
		}else if(consoleLogStream  != null  ) {
			consoleLogStream.println(arg0);
		}
	}

	public void print(String arg0) {
		if(consoleLogObject != null)
		{
			System.out.println("Implemantation TODO :: ConsoleStream.print() wrapped object ... ");
			consoleLogStream.println( arg0);
			//((ConsoleStream)consoleLogObject).print(arg0);	
		}else {
			consoleLogStream.print(arg0);
		}
	}

	public String readLine() throws IOException {
	
		return consoleLogInputStream.readLine();
	}

	public void clear() {
		if(consoleLogObject != null)
		{
			//((ConsoleStream)consoleLogObject).println("");	
			//((ConsoleStream)consoleLogObject).clear();	
		}else {
			consoleLogStream.println("");
		}
		
	}
}
