package service;

import java.util.ArrayList;

import org.genose.java.implementation.javafx.applicationtools.JFXApplication;
import org.genose.java.implementation.tools.NumericRange;

import dao.DaoFactory;
import javafx.beans.value.WritableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import metier.Article;
import metier.Continent;
import metier.Couleur;
import metier.Fabricant;
import metier.Marque;
import metier.Pays;
import metier.TypeBiere; 


public class ServiceArticle
{
	
	private SortedList<Article> articleSorted = null;
	private ArrayList<Article> articleFiltre = null;
	private static ArticleSearch pArticleSearchCriteria = null;
	
	static ServiceArticle pServiceArticleSingleton = null;
	
	/*
	 * *****************************************************************************
	 */
	/**
	 * create a singleton
	 */
	private void singletonInstanceCreate() {
		synchronized (ServiceArticle.class) {
			if (pServiceArticleSingleton != null)
				throw new UnsupportedOperationException(
						getClass() + " is singleton but constructor called more than once");
			pServiceArticleSingleton = this;
		}
	}
	
	
	/**
	 * 
	 */
	private static synchronized void singletonInstanceCheck() {
		synchronized (ServiceArticle.class) {
			if (pServiceArticleSingleton == null)
				throw new UnsupportedOperationException(" singleton is null");
		}
	}

	
	/* ****************************************************** */
	/**
	 * 
	 * @return
	 */
	public static ServiceArticle getServiceArticleSingleton() {
		singletonInstanceCheck();
		return pServiceArticleSingleton;
	}

	
	public ServiceArticle()
	{
		super();
		singletonInstanceCreate();
		
		pArticleSearchCriteria = new ArticleSearch();
		
		pArticleSearchCriteria.setCriteriaCouleur(DaoFactory.getCouleurDAO().getAll());
		
		pArticleSearchCriteria.setCriteriaType(DaoFactory.getTypeDAO().getAll());
		
		pArticleSearchCriteria.setCriteriaPays(DaoFactory.getPaysDAO().getAll());
		
		pArticleSearchCriteria.setCriteriaContinent(DaoFactory.getContinentDAO().getAll());
		
		pArticleSearchCriteria.setCriteriaFabricant(DaoFactory.getFabricantDAO().getAll());
		
		pArticleSearchCriteria.setCriteriaMarque(DaoFactory.getMarqueDAO().getAll());
		
		articleFiltre = DaoFactory.getArticleDAO().getAll();

		articleSorted = new SortedList<Article>( (ObservableList<? extends Article>) DaoFactory.getArticleDAO().select(pArticleSearchCriteria));
	}

	public ObservableList<Couleur> getCouleurFiltre()
	{
		return FXCollections.observableArrayList(pArticleSearchCriteria.getCriteriaCouleur());
	}

	public ObservableList<Continent> getContinentFiltre()
	{
		return FXCollections.observableArrayList(pArticleSearchCriteria.getCriteriaContinent());
	}

	public ObservableList<Pays> getPaysFiltre()
	{
		return  FXCollections.observableArrayList(pArticleSearchCriteria.getCriteriaPays());
	}

	public ObservableList<Marque> getMarqueFiltre()
	{
		return  FXCollections.observableArrayList(pArticleSearchCriteria.getCriteriaMarque());
	}

	public ObservableList<TypeBiere> getTypeFiltre()
	{
		return  FXCollections.observableArrayList(pArticleSearchCriteria.getCriteriaType());
	}

	public ObservableList<Fabricant> getFabricantFiltre()
	{
		return  FXCollections.observableArrayList(pArticleSearchCriteria.getCriteriaFabricant());
	}

	public ObservableList<Article> getArticleFiltre()
	{
		return  FXCollections.observableArrayList(articleFiltre);
	}

	public SortedList<Article> getArticleSorted()
	{
		return articleSorted;
	}


	public NumericRange getPrixFiltre() {

		return pArticleSearchCriteria.getCriteriaPrixRange();
	}


	public NumericRange getTitrageFiltre() {
		return pArticleSearchCriteria.getCriteriaTitrageRange();
	}


	public void search() {

		articleSorted = new SortedList<Article>( (ObservableList<? extends Article>) DaoFactory.getArticleDAO().select(pArticleSearchCriteria));
		
	}


	public  ArticleSearch getArticleSearch() { 
		return pArticleSearchCriteria;
	}


	public void refresh() {
		articleSorted = new SortedList<Article>( (ObservableList<? extends Article>) DaoFactory.getArticleDAO().select(pArticleSearchCriteria));
	}

 
	

}
