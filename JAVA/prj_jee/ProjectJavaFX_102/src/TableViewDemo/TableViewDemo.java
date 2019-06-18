/**
 * 
 */
package TableViewDemo;

import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationScene;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

/**
 * @author xenon
 * https://stackoverflow.com/questions/52244810/how-to-fill-tableviews-column-with-a-values-from-an-array
 * https://o7planning.org/fr/11079/tutoriel-javafx-tableview
 * 
 */
public class TableViewDemo extends JFXApplicationScene {
// https://o7planning.org/fr/11079/tutoriel-javafx-tableview
	
	TableView<UserAccount> table = new TableView<>();
	 
    // Create column UserName (Data type of String).
    TableColumn<UserAccount, String> userNameCol //
            = new TableColumn<>("User Name");

    // Create column Email (Data type of String).
    TableColumn<UserAccount, String> emailCol//
            = new TableColumn<>("Email");

    // Create column FullName (Data type of String).
    TableColumn<UserAccount, String> fullNameCol//
            = new TableColumn<>("Full Name");

    // Create 2 sub column for FullName.
    TableColumn<UserAccount, String> firstNameCol //
            = new TableColumn<>("First Name");

    TableColumn<UserAccount, String> lastNameCol //
            = new TableColumn<>("Last Name");

    // Active Column
    TableColumn<UserAccount, Boolean> activeCol//
            = new TableColumn<>("Active");

    
    static StackPane root = new StackPane();
    
	/**
	 * 
	 */
	public TableViewDemo() {
		super(root, 450, 300);
		
		// Add sub columns to the FullName
	    fullNameCol.getColumns().addAll(firstNameCol, lastNameCol);

	      // Defines how to fill data for each cell.
	      // Get value from property of UserAccount. .
	      userNameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
	      emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
	      firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
	      lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
	      activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));
	    
	      // Set Sort type for userName column
	      userNameCol.setSortType(TableColumn.SortType.DESCENDING);
	      lastNameCol.setSortable(false);
	 
	      // Display row data
	      ObservableList<UserAccount> list = getUserList();
	      table.setItems(list);
	 
	    
	    table.getColumns().addAll(userNameCol, emailCol, fullNameCol, activeCol);

	    
	    
	      root.setPadding(new Insets(5));
	      root.getChildren().add(table);
	       
	      
	}


	  private ObservableList<UserAccount> getUserList() {
	 
	      UserAccount user1 = new UserAccount(1L, "smith", "smith@gmail.com", //
	              "Susan", "Smith", true);
	      UserAccount user2 = new UserAccount(2L, "mcneil", "mcneil@gmail.com", //
	              "Anne", "McNeil", true);
	      UserAccount user3 = new UserAccount(3L, "white", "white@gmail.com", //
	              "Kenvin", "White", false);
	 
	       return FXCollections.observableArrayList(user1, user2, user3);
	      
	  }
	 

}
