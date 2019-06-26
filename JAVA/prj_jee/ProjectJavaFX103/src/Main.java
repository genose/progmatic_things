
import org.genose.java.implementation.javafx.applicationtools.JFXApplication;

import javafx.stage.Stage;

public class Main extends JFXApplication {

	@Override
	public void start(Stage primaryStage) throws Exception { 
		super.start(primaryStage);
		System.out.println(" hello" );
	}

	public static void main(String[] args) {
		launch(args);
	}
}
