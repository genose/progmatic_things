package service;

import java.util.ArrayList;

import dao.DaoFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.util.Callback;
import metier.Article;
import metier.Continent;
import metier.Couleur;
import metier.Fabricant;
import metier.Marque;
import metier.Pays;
import metier.Type;

public class ServiceArticle
{
	private ObservableList<Couleur> couleurFiltre;
	private ObservableList<Continent> continentFiltre;
	private ObservableList<Pays> paysFiltre;
	private ObservableList<Marque> marqueFiltre;
	private ObservableList<Type> typeFiltre;
	private ObservableList<Fabricant> fabricantFiltre;
	private ObservableList<Article> articleFiltre;
	private SortedList<Article> articleSorted;

	public ServiceArticle()
	{
		couleurFiltre = FXCollections.observableArrayList(DaoFactory.getCouleurDAO().getAll());
		couleurFiltre.add(0, new Couleur(0, "Couleur"));
		continentFiltre = FXCollections.observableArrayList(DaoFactory.getContinentDAO().getAll());
		continentFiltre.add(0, new Continent(0, "Continent"));
		paysFiltre = FXCollections.observableArrayList(DaoFactory.getPaysDAO().getAll());
		paysFiltre.add(0, new Pays(0, "Pays"));
		marqueFiltre = FXCollections.observableArrayList(DaoFactory.getMarqueDAO().getAll());
		marqueFiltre.add(0, new Marque(0, "Marque"));
		typeFiltre = FXCollections.observableArrayList(DaoFactory.getTypeDAO().getAll());
		typeFiltre.add(0, new Type(0, "Type"));
		fabricantFiltre = FXCollections.observableArrayList(DaoFactory.getFabricantDAO().getAll());
		fabricantFiltre.add(0, new Fabricant(0, "Fabricant"));
		articleFiltre = FXCollections.observableArrayList(DaoFactory.getArticleDAO().getAll());
//		articleFiltre.add(0,new Article(0,"Article"));
		articleSorted = new SortedList<Article>(articleFiltre);
	}

	public ObservableList<Couleur> getCouleurFiltre()
	{
		return couleurFiltre;
	}

	public ObservableList<Continent> getContinentFiltre()
	{
		return continentFiltre;
	}

	public ObservableList<Pays> getPaysFiltre()
	{
		return paysFiltre;
	}

	public ObservableList<Marque> getMarqueFiltre()
	{
		return marqueFiltre;
	}

	public ObservableList<Type> getTypeFiltre()
	{
		return typeFiltre;
	}

	public ObservableList<Fabricant> getFabricantFiltre()
	{
		return fabricantFiltre;
	}

	public ObservableList<Article> getArticleFiltre()
	{
		return articleFiltre;
	}

	public SortedList<Article> getArticleSorted()
	{
		return articleSorted;
	}
	
	

}
