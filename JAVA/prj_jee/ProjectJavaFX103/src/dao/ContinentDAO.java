package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;



public class ContinentDAO extends DAO {

public ContinentDAO(Connection connexion)
{
super(connexion);
}

public ArrayList getAll()
{
ResultSet rs = null;
ArrayList liste = new ArrayList();
try
{
Statement stmt = connexion.createStatement();
String strCmd = "select continent.id_continent, nom_continent, id_pays,nom_pays from CONTINENT "
+ "join PAYS on PAYS.ID_CONTINENT = CONTINENT.ID_CONTINENT "
+ "order by NOM_CONTINENT, NOM_PAYS";
rs = stmt.executeQuery(strCmd);

Continent continentLu = new Continent();

while (rs.next())
{
if (continentLu.getId() != rs.getInt(1))
{
continentLu = new Continent(rs.getInt(1), rs.getString(2));
liste.add(continentLu);
}
continentLu.getListe().add(new Pays(rs.getInt(3), rs.getString(4)));
}

rs.close();
}
catch (Exception e)
{
e.printStackTrace();
}
return liste;
}

@Override
public Continent getByID(int id)
{
// TODO Auto-generated method stub
return null;
}

@Override
public boolean insert(Continent continent)
{
try
{
String strCmd = "insert into Continent values ?";
PreparedStatement pStmt = connexion.prepareStatement(strCmd);
pStmt.setString(1, continent.getLibelle());
pStmt.execute();
return true;
}
catch (Exception e)
{
return false;
}
}

@Override
public boolean update(Continent continent)
{
try
{
Statement stmt = connexion.createStatement();
String strCmd = "update CONTINENT set nom_continent = " + continent.getLibelle() + " where id_continent = "
+ continent.getId();
stmt.execute(strCmd);
return true;
}
catch (Exception e)
{
return false;
}
}

@Override
public boolean delete(Continent continent)
{
// TODO Auto-generated method stub
return false;
}
}
14:10

package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import metier.Continent;
import metier.Couleur;
import metier.Pays;
import service.ServiceArticle;
import javafx.scene.control.Slider;

import javafx.scene.control.ComboBox;

import javafx.scene.control.TableView;

public class SdbmController
{
@FXML
private TableView<?> beersTable;
@FXML
private ComboBox<?> comboFabricant;
@FXML
private ComboBox comboPays;
@FXML
private ComboBox comboContinent;
@FXML
private ComboBox<?> comboVolume;
@FXML
private Button supprButton;
@FXML
private Button modifierButton;
@FXML
private Button ajouterButton;
@FXML
private TextField searchField;
@FXML
private ComboBox comboCouleur;
@FXML
private ComboBox<?> comboType;
@FXML
private Slider miniSlider;
@FXML
private Slider maxiSlider;

@FXML
public void initialize()
{
ServiceArticle serviceArticle = new ServiceArticle();
comboCouleur.setItems(serviceArticle.getCouleurFiltre());
comboContinent.setItems(serviceArticle.getContinentFiltre());
comboPays.setItems(serviceArticle.getPaysFiltre());
comboContinent.valueProperty().addListener((obs, oldValue, newValue) -> {
comboPays.valueProperty().setValue(null);
if (newValue.getId() == 0)
{
comboPays.setItems(serviceArticle.getPaysFiltre());
}
else
{
// comboPays.getSelectionModel().select(null);
comboPays.setItems(FXCollections.observableArrayList(newValue.getListe()));
}
});

}

}
