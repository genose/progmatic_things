package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.genose.java.implementation.javafx.applicationtools.JFXApplication;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.tools.NumericRange;
import org.genose.java.implementation.tools.refreshableObject;

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


public class ServiceArticle implements refreshableObject<Article>
{
	
	private SortedList<Article> articleSorted = null;
	private ObservableList<Article> articleFiltred = null;
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
		
		try {
			pArticleSearchCriteria = new ArticleSearch();
			
			pArticleSearchCriteria.setCriteriaCouleur(DaoFactory.getCouleurDAO().getAll());
			
			pArticleSearchCriteria.setCriteriaType(DaoFactory.getTypeDAO().getAll());
			
			pArticleSearchCriteria.setCriteriaPays(DaoFactory.getPaysDAO().getAll());
			
			pArticleSearchCriteria.setCriteriaContinent(DaoFactory.getContinentDAO().getAll());
			
			pArticleSearchCriteria.setCriteriaFabricant(DaoFactory.getFabricantDAO().getAll());
			
			pArticleSearchCriteria.setCriteriaMarque(DaoFactory.getMarqueDAO().getAll());
			
			articleFiltred = FXCollections.observableArrayList(DaoFactory.getArticleDAO().getAll());
			
			articleSorted = new SortedList<Article>(getArticleFiltred());
			
		} catch (Exception evERRLOADSERVICEARTICLE) {

			evERRLOADSERVICEARTICLE.printStackTrace();
			JFXApplicationException.raiseToFront(this.getClass(), evERRLOADSERVICEARTICLE, true);
		}
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

	public ObservableList<Article> getArticleFiltred()
	{
		return   articleFiltred;
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

		if((articleFiltred == null)) {
			articleFiltred = (ObservableList<Article>) new ArrayList<Article>();
		}
		System.out.println("Result count "+articleFiltred.size());
		articleFiltred.clear();
		DaoFactory.getArticleDAO().select(pArticleSearchCriteria);
		articleFiltred = FXCollections.observableArrayList(pArticleSearchCriteria.getResultArticle());
		articleSorted = new SortedList<Article>(   articleFiltred);
		
	}


	public ArticleSearch getArticleSearch() {
		return pArticleSearchCriteria;
	}


	@Override
	public Boolean refresh() {
		articleFiltred.clear();
		 articleFiltred.setAll(DaoFactory.getArticleDAO().select(pArticleSearchCriteria));
		 
		return articleSorted.isEmpty();
	}


	@Override
	public Boolean refresh(Article arg0) {
		return false;
	}

 
	

}
