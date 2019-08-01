package service;

import java.util.ArrayList;

import org.genose.java.implementation.tools.NumericRange;

import javafx.collections.ObservableList;
import metier.Article;
import metier.Continent;
import metier.Couleur;
import metier.Fabricant;
import metier.Marque;
import metier.Pays;
import metier.TypeBiere;
import metier.VolumeBiere;

/**
 * 
 */

/**
 * @author 59013-36-18
 *
 */
public class ArticleSearch extends Article {

	private NumericRange aNumericRangePrix;
	private NumericRange aNumericRangeTitrage;
	private ArrayList<VolumeBiere> volumeFiltre;
	
	private ArrayList<Couleur> couleurFiltre;
	private ArrayList<Continent> continentFiltre;
	private ArrayList<Pays> paysFiltre;
	private ArrayList<Marque> marqueFiltre;
	private ArrayList<TypeBiere> typeFiltre;
	private ArrayList<Fabricant> fabricantFiltre;
	private ArrayList<Article> articleFiltre;
	
	/**
	 * 
	 */
	public ArticleSearch() {
		super();
		aNumericRangePrix = new NumericRange(0, 0);
		aNumericRangeTitrage = new NumericRange(0, 0);
		volumeFiltre = new ArrayList<>();
		
		couleurFiltre =new ArrayList<>();
		continentFiltre=new ArrayList<>();
		paysFiltre=new ArrayList<>();
		marqueFiltre=new ArrayList<>();
		typeFiltre=new ArrayList<>();
		fabricantFiltre=new ArrayList<>();
		articleFiltre=new ArrayList<>();
	}
	/**
	 * @param nom
	 */
	public ArticleSearch(String nom) {
		super(0, nom);
		aNumericRangePrix = new NumericRange(0, 0);
		aNumericRangeTitrage = new NumericRange(0, 0);
		volumeFiltre = new ArrayList<>();
		couleurFiltre =new ArrayList<>();
		continentFiltre=new ArrayList<>();
		paysFiltre=new ArrayList<>();
		marqueFiltre=new ArrayList<>();
		typeFiltre=new ArrayList<>();
		fabricantFiltre=new ArrayList<>();
		articleFiltre=new ArrayList<>();
	}
	
	
	public NumericRange getCriteriaPrixRange() {
		return aNumericRangePrix;
	}
	
	public NumericRange getCriteriaTitrageRange() {
		return aNumericRangeTitrage;
	}
	
	public ArrayList<Couleur> getCriteriaCouleur() {
		
		return couleurFiltre;
	}
	
	public ArrayList<TypeBiere> getCriteriaType() {
		return typeFiltre;
	}
	
	public ArrayList<Pays> getCriteriaPays() {
		return paysFiltre;
	}
		
	
	public ArrayList<Continent> getCriteriaContinent() {
		return continentFiltre;
	}
	
	
	public ArrayList<Fabricant> getCriteriaFabricant() {
		return fabricantFiltre;
	}
	
	
	public ArrayList<Marque> getCriteriaMarque() {
		return marqueFiltre;
	}
	
	
	public ArrayList<VolumeBiere> getCriteriaVolume() {

		return volumeFiltre;
	}
	
	public void setCriteriaPrixRange(NumericRange aRange) {
		aNumericRangePrix.setMin(aRange.min());
		aNumericRangePrix.setMax(aRange.max());
	}
	
	public void setCriteriaTitrageRange(NumericRange aRange) {
		aNumericRangeTitrage.setMin(aRange.min());
		aNumericRangeTitrage.setMax(aRange.max());
	}
	
	public void setCriteriaVolume(VolumeBiere aVolume) {
		volumeFiltre.clear();
		volumeFiltre.add(aVolume);
	}
	
	public void setCriteriaCouleur(ArrayList<Couleur> aArgList) {
		couleurFiltre.clear();
		couleurFiltre.addAll(aArgList);
	}
	
	public void setCriteriaType(ArrayList<TypeBiere> aArgList) {
		typeFiltre.clear();
		typeFiltre.addAll(aArgList);
	}
	
	public void setCriteriaPays(ArrayList<Pays> aArgList) {
		paysFiltre.clear();
		paysFiltre.addAll(aArgList);
	}
		
	
	public void setCriteriaContinent(ArrayList<Continent> aArgList) {
		continentFiltre.clear();
		continentFiltre.addAll(aArgList);
	}
	
	
	public void setCriteriaFabricant(ArrayList<Fabricant> aArgList) {
		fabricantFiltre.clear();
		fabricantFiltre.addAll(aArgList);
	}
	
	
	public void setCriteriaMarque(ArrayList<Marque> aArgList) {
		marqueFiltre.clear();
		marqueFiltre.addAll(aArgList);
	}
	
	public void clearCriteria() {
		nom_article.set("");
		aNumericRangePrix.setMin(0.0);
		aNumericRangePrix.setMax(0.0);
		aNumericRangeTitrage.setMin(0.0);
		aNumericRangeTitrage.setMax(0.0);
		volumeFiltre.clear();
		
		couleurFiltre.clear();
		continentFiltre.clear();
		paysFiltre.clear();
		marqueFiltre.clear();
		typeFiltre.clear();
		fabricantFiltre.clear();
		articleFiltre.clear();
		
	}
	
	public ArrayList<Article> getResultArticle() {
		return articleFiltre;
	}
	
	public boolean isEmpty() {

		return nom_article.get().isEmpty() && aNumericRangePrix.isEmpty() &&
		aNumericRangeTitrage.isEmpty() && 
		volumeFiltre.isEmpty() &&
		
		couleurFiltre.isEmpty() && 
		continentFiltre.isEmpty() && 
		paysFiltre.isEmpty() && 
		marqueFiltre.isEmpty() && 
		typeFiltre.isEmpty() && 
		fabricantFiltre.isEmpty() &&
		articleFiltre.isEmpty();
		
		
	}


	
	 
	
}
