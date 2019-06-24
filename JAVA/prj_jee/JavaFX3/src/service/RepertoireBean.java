package service;

import java.io.File;

import dao.ContactDAO;
import model.Personne;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

/**
 * attributs et méthodes permettant de définir un répertoire
 * 
 * @author François
 *
 */
public class RepertoireBean {
	private ObservableList<Personne> allContacts;
	private FilteredList<Personne> listeFiltree;
	private SortedList<Personne> personSortedList;
	private File file;
	private ContactDAO contactDAO;
	private Personne personneSelected;
	private boolean saved;
	private Personne personneSearched;
	
	
	

	public RepertoireBean(File file) {
		personneSearched = new Personne("", "", "01/01/2018");
		this.file = file;
		contactDAO = new ContactDAO(file);
		allContacts = FXCollections.observableArrayList(contactDAO.lire());
		listeFiltree = new FilteredList<>(allContacts, null);
		personSortedList = new SortedList<>(listeFiltree);
		saved = true;
	}

	public void setNomSearched(String nomSearched) {
		personneSearched.setNom(nomSearched.toUpperCase());
		filtrerContact();
	}

	public void setPrenomSearched(String prenomSearched) {
		personneSearched.setPrenom(prenomSearched);
		filtrerContact();
	}

	public Personne getPersonneSelected() {
		return personneSelected;
	}

	public void setPersonneSelected(Personne personneSelected) {
		this.personneSelected = personneSelected;
	}

	public SortedList<Personne> getContacts() {
		return personSortedList;
	}

	public boolean isSaved() {
		return saved;
	}

	public void setSaved(boolean saved) {
		this.saved = this.saved && saved;
	}

	public File getFile() {
		return file;
	}

	public void ajouter() {
		if (allContacts.indexOf(personneSelected) == -1) {
			allContacts.add(personneSelected);
			saved = false;
		}
	}

	public void sauver() {
		contactDAO.sauver(allContacts);
		saved = true;
	}

	public void supprimer() {
		allContacts.remove(personneSelected);
		saved = false;
	}

	public void filtrerContact() {
		listeFiltree.setPredicate(personne ->
		{
			boolean c1 = personne.getNom().contains(personneSearched.getNom());
			boolean c2 = personne.getPrenom().toLowerCase().contains(personneSearched.getPrenom().toLowerCase());
			return c1 && c2;
		});
	}

	public void setFile(File selectedFile)
	{
		contactDAO = new ContactDAO(selectedFile);
		sauver();
		
	}
}
