package org.genose.java.implementation.streams;

import java.io.PrintStream;

public class ConsoleStream {


	static PrintStream consoleLogStream = System.out;
	static Object consoleLogObject = null;
	
	
	
	public ConsoleStream(PrintStream out) {
		// TODO Auto-generated constructor stub
		consoleLogStream = out;
	}
	
	
	public ConsoleStream(Object out) {
		// TODO Auto-generated constructor stub
		consoleLogObject = out;
	}
	
	public void println(String arg0) {
		if(consoleLogObject != null)
		{
			((ConsoleStream)consoleLogObject).println(arg0);	
		}else {
			consoleLogStream.println(arg0);
		}
	}

	public void print(String arg0) {
		if(consoleLogObject != null)
		{
			((ConsoleStream)consoleLogObject).print(arg0);	
		}else {
			consoleLogStream.print(arg0);
		}
	}
}
