/**
 * 
 */
package hellofx;


import org.genose.java.implementation.streams.ConsoleStream;

import javafx.scene.control.TextField;

/**
 * @author 59013-36-18
 *
 */
public class GestionnaireTextField extends ConsoleStream {

	private TextField aTextField;
	
	

	public void clear() {
		aTextField.setText("");
	}
	
	public GestionnaireTextField(TextField aTextFieldObject){
		super();
		aTextField = aTextFieldObject;
	}
	
	
	@Override
	public void println(String arg0) {
		if(aTextField != null)
			aTextField.setText(aTextField.getText()+"\n"+arg0);
		else throw new Error(this.getClass()+":: null object pointer ...");
	}

}
