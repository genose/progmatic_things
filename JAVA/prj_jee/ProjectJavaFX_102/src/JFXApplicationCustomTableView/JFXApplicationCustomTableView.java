/**
 * 
 */
package JFXApplicationCustomTableView;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import org.genose.java.implementation.javafx.applicationtools.JFXApplicationClassHelper;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationDesignObjectLoad;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationHelper;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

/**
 * @author xenon
 *         https://stackoverflow.com/questions/52244810/how-to-fill-tableviews-column-with-a-values-from-an-array
 *         https://o7planning.org/fr/11079/tutoriel-javafx-tableview
 * @param <T>
 * @param <S>
 * 
 */
public class JFXApplicationCustomTableView<S, T> extends TableView implements Initializable, JFXApplicationDesignObjectLoad {
// https://o7planning.org/fr/11079/tutoriel-javafx-tableview
	private final static String message = JFXApplicationException.ERROR_MESSAGE_DESIGNLOAD;
	@FXML
	private TableView<T> pRootTableView = null;

	// Create column UserName (Data type of String).
	private ObservableList<TableColumn<S, T>> pTableComlumns = null;
 
	/**
	 * 
	 */
	public JFXApplicationCustomTableView() {
		super();
		
	}
	/**
	 * 
	 * @param aTableColumnName
	 * @return
	 */
	public TableColumn<S, T> addTableColumn(String aTableColumnName) {
		TableColumn<S, T> aNewTableColumn  = new TableColumn<>(aTableColumnName);
		
		pRootTableView.getSelectionModel();
		return aNewTableColumn;
	}
	/**
	 * 
	 * @return
	 */
	private ObservableList<T> getDataSource() {

		return (ObservableList<T>) FXCollections.observableArrayList();

	}
	/**
	 * 
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	
		doInit();
	}
	@FXML
	public void initialize() {
	
		doInit();
	}
	/**
	 * 
	 */
	private void doInit() {
		Objects.requireNonNull(pRootTableView, message);
	}
	
	/**
	 * Dynamicly create a class controller .... and load associated FXML File ...
	 * @return
	 */

	public static Parent create() {
		return JFXApplicationDesignObjectLoad.create(null);
	}


}
