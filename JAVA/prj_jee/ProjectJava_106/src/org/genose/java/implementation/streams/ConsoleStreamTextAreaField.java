/**
 * 
 */
package org.genose.java.implementation.streams;


import java.io.IOException;


import org.genose.java.implementation.streams.ConsoleStream;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * @author 59013-36-18
 *
 */
public class ConsoleStreamTextAreaField extends org.genose.java.implementation.streams.ConsoleStream {
	
	public ConsoleStreamTextAreaField(TextField aTextFieldObject) {
		consoleLogObject = aTextFieldObject;
	}
	
	
	public ConsoleStreamTextAreaField(TextArea consoleLogTextField) {
		// TODO Auto-generated constructor stub
		consoleLogObject = consoleLogTextField; 
	}
	
	@Override
	public String readLine() throws IOException {
		System.out.println(" type :: " + consoleLogObject  );
		if(consoleLogObject != null && (consoleLogObject instanceof TextArea))
			return ((TextArea)consoleLogObject).getText();
		else if(consoleLogObject != null && (consoleLogObject instanceof TextField))
			return ((TextField)consoleLogObject).getText();
		else
			return super.readLine();
	}
	@Override
	public void clear() {
		if(consoleLogObject != null && (consoleLogObject instanceof TextArea))
			((TextArea)consoleLogObject).setText("");
		else if(consoleLogObject != null && (consoleLogObject instanceof TextField))
			((TextField)consoleLogObject).setText("");
		else
			super.clear();
	}
	
	
	@Override
	public void println(String arg0) {
		if(consoleLogObject != null && (consoleLogObject instanceof TextArea)) 
		{
			((TextArea)consoleLogObject).appendText( "\n"+arg0 );
		}else if(consoleLogObject != null && (consoleLogObject instanceof TextField)) 
		{
			((TextField)consoleLogObject).appendText("\n"+arg0);
		}else
		if (consoleLogObject != null) {
		 super.println(arg0);
		} else throw new Error(this.getClass()+":: null object pointer ...");
	}

}
