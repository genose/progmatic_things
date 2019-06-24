package application;

import java.io.IOException;
import java.util.Optional;

import service.ParametreBean;
import service.RepertoireBean;
import vue.ContactsController;
import vue.MenuController;
import vue.SaisieContactController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class MenuApplication extends Application
{
	private Stage primaryStage;
	private BorderPane rootLayout;
	private RepertoireBean repertoire;
	private ParametreBean parametres;
	// Point d'entrée de l'application
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage)
	{
		this.primaryStage = primaryStage;
		initialiserTitre();
		primaryStage.setOnCloseRequest(event -> sauver());
		parametres = new ParametreBean();
		showMenu();
	}

	private void initialiserTitre()
	{
		primaryStage.setTitle("Gestion de Contacts");
	}

	// accesseurs
	public Stage getPrimaryStage()
	{
		return primaryStage;
	}

	public RepertoireBean getRepertoire()
	{
		return repertoire;
	}

	public BorderPane getRootLayout()
	{
		return rootLayout;
	}

	public void setRepertoire(RepertoireBean repertoire)
	{
		this.repertoire = repertoire;
		initialiserTitre();
		primaryStage.setTitle(primaryStage.getTitle() + " - " + repertoire.getFile());
		showGestionContact();
	}

	public ParametreBean getParametres()
	{
		return parametres;
	}

	// affichage du menu
	private void showMenu()
	{
		try
		{
			// Chargement du fichier fxml
			FXMLLoader myFXMLloader = new FXMLLoader();
			myFXMLloader.setLocation(MenuApplication.class.getResource("../vue/Menu.fxml"));
			rootLayout = myFXMLloader.load();

			// Déclaration et instanciation de la scène contenant le layout
			Scene scene = new Scene(rootLayout);
			scene.getStylesheets().add(getClass().getResource("./application.css").toExternalForm());
			primaryStage.setScene(scene);

			// Déclaration et instanciation du controller, passage d'une
			// référence sur l'instance de la classe MenuApp (this)
			MenuController menuController = myFXMLloader.getController();
			menuController.setMenuApplication(this);
			// Démarrage du Stage
			primaryStage.show();
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}

	// Vue Gestion des contacts
	public void showGestionContact()
	{
		try
		{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MenuApplication.class.getResource("../vue/Contacts.fxml"));
			AnchorPane contactOverview = loader.load();
			// Chargement de la vue au center du menuLayout
			rootLayout.setCenter(contactOverview);
			// Déclaration et instanciation du controller, passage d'une
			// référence sur l'instance de la classe MenuApp (this)
			ContactsController contactsController = loader.getController();
			contactsController.setMainApp(this);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public boolean showModifContact()
	{
		try
		{
			// Load the fxml file
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MenuApplication.class.getResource("../vue/SaisieContact.fxml"));
			AnchorPane anchorPane = loader.load();

			// Creating the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Edit Person");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(anchorPane);
			dialogStage.setScene(scene);

			// sending parameters to the conroller
			SaisieContactController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setPersonne(repertoire.getPersonneSelected());
			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();
			return controller.isOkClicked();
		}
		catch (IOException e)
		{
			return false;
		}
	}

	public void sauver()
	{
		if (repertoire != null && !repertoire.isSaved())
		{
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Modifications détectées !");
			alert.setContentText(String.format(
					"Le fichier %s a été modifié, voulez-vous enregistrer les modifications ? ", repertoire.getFile()));
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK)
			{
				repertoire.sauver();
			}
		}
	}

	public void fermer()
	{
		sauver();
		initialiserTitre();
		rootLayout.setCenter(null);
	}
}
