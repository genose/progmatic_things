package vue;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import application.MenuApplication;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import service.ParametreBean;
import service.RepertoireBean;

// Controller associé au Menu.fxml
public class MenuController
{
	// Reference à l'application principale
	private MenuApplication menuApplication;
	@FXML
	private Menu menuFichiersRecents;
	private ArrayList<String> fichiersRecents;
	private ParametreBean parametres;
	@FXML
	private Label horloge;
	@FXML
	private MenuItem menuItemEnregistrer;
	@FXML
	private MenuItem menuItemEnregistrerSous;


	@FXML
	private void initialize()
	{
		desactiverItems(true);
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(900), e -> actualiserHorloge()));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}

	private void desactiverItems(Boolean bool)
	{
		menuItemEnregistrer.setDisable(bool);
		menuItemEnregistrerSous.setDisable(bool);
	}

	public void setMenuApplication(MenuApplication menuApplication)
	{
		this.menuApplication = menuApplication;
		parametres = menuApplication.getParametres();
		fichiersRecents = parametres.getFichiersRecents();
		actualiserHorloge();
		genererMenuFichiersRecents();
	}

	private void actualiserHorloge()
	{
		horloge.setText(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy   kk:mm:ss").format(LocalDateTime.now()));
	}

	private void genererMenuFichiersRecents()
	{
		menuFichiersRecents.getItems().clear();
		for (int i = 0; i < fichiersRecents.size(); i++)
		{
			menuFichiersRecents.getItems().add(new MenuItem(fichiersRecents.get(i)));
			menuFichiersRecents.getItems().get(i).setOnAction(action -> fichierOuvrirRecent(action));
		}
	}

	@FXML
	private void fichierNouveau()
	{
		menuApplication.sauver();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Contacts Files", "*.contact"));
		fileChooser.setInitialDirectory(new File(menuApplication.getParametres().getDernierRepertoire()));
		File selectedFile = fileChooser.showSaveDialog(menuApplication.getPrimaryStage());
		if (selectedFile != null)
		{
			majFichiersRecents(selectedFile);
			menuApplication.setRepertoire(new RepertoireBean(selectedFile));
		}
	}

	@FXML
	private void fichierOuvrir()
	{
		menuApplication.sauver();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Ouvrir un fichier de contacts");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Contacts Files", "*.contact"));
		fileChooser.setInitialDirectory(new File(menuApplication.getParametres().getDernierRepertoire()));
		File selectedFile = fileChooser.showOpenDialog(menuApplication.getPrimaryStage());
		if (selectedFile != null)
		{
			ouvrir(selectedFile);
		}
	}

	private void fichierOuvrirRecent(ActionEvent action)
	{
		MenuItem menuItem = (MenuItem) action.getSource();
		File selectedFile = new File(menuItem.getText());
		if (selectedFile.exists())
		{
			ouvrir(selectedFile);
		}
		else
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur : Fichier absent");
			alert.setHeaderText(selectedFile.getPath());
			alert.setContentText(
					"Le fichier n'est plus accessible, il a certainement été supprimé depuis la dernière session !!!");
			alert.showAndWait();
		}
	}

	private void ouvrir(File selectedFile)
	{
		try
		{
			RepertoireBean repertoire = new RepertoireBean(selectedFile);
			menuApplication.setRepertoire(repertoire);
			majFichiersRecents(selectedFile);
		}
		catch (Exception e)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur : Format incorrect");
			alert.setHeaderText(selectedFile.getPath());
			alert.setContentText("Le fichier n'est pas au bon format !!!");
			alert.showAndWait();
		}
	}

	private void majFichiersRecents(File selectedFile)
	{
		desactiverItems(false);
		String fichier = selectedFile.getAbsolutePath();
		parametres.setDernierRepertoire(fichier);
		int position = parametres.getFichiersRecents().indexOf(fichier);
		if (position != -1)
		{
			parametres.getFichiersRecents().remove(position);
		}
		parametres.getFichiersRecents().add(0, selectedFile.getAbsolutePath());
		parametres.sauver();
		genererMenuFichiersRecents();
	}

	@FXML
	void enregistrer(ActionEvent event)
	{
		menuApplication.getRepertoire().sauver();
	}

	@FXML
	void enregistrerSous(ActionEvent event)
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Contacts Files", "*.contact"));
		fileChooser.setInitialDirectory(new File(menuApplication.getParametres().getDernierRepertoire()));
		File selectedFile = fileChooser.showSaveDialog(menuApplication.getPrimaryStage());
		if (selectedFile != null)
		{
			majFichiersRecents(selectedFile);
			menuApplication.getRepertoire().setFile(selectedFile);
		}
	}

	@FXML
	private void fichierFermer()
	{
		menuApplication.fermer();
		desactiverItems(true);
	}

	@FXML
	private void fichierQuitter()
	{
		menuApplication.sauver();
		Platform.exit();
	}

	@FXML
	private void aideAProposDe()
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Gestion de contacts");
		alert.setHeaderText("A propos de :");
		alert.setContentText("Exemple d'application JavaFX\nAuteur : François SACKEBANDT");
		alert.showAndWait();
	}
}