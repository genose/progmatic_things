package ArticleDialog;

import java.awt.event.ActionEvent;

import org.genose.java.implementation.javafx.applicationtools.JFXApplication;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationDesignObjectLoad;
import org.genose.java.implementation.javafx.applicationtools.arraysmapslists.JFXApplicationObjectValue;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationDialog;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationScene;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationStage;

import MainWindow.assets.controllers.MainWindow;
import MainWindow.assets.controllers.MainWindowDetail;
import dao.DaoFactory;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import metier.Article;
import service.ServiceArticle;

public class ArticleDialog extends AnchorPane {

	private Article aArticle = null;
	MainWindowDetail  aWindowDetailArticle = null;
	Stage ArticleDialogStage = null;
	private static JFXApplicationScene aArticleDialogScene = null;
	public static void articleDialogCreate() {
		try {
			
					 aArticleDialogScene = JFXApplicationDesignObjectLoad.create(ArticleDialog.class.getSimpleName(),
					 ArticleDialog.class.getSimpleName(), null) ;
		} catch (JFXApplicationException evERRException) { 
			evERRException.printStackTrace();
			JFXApplicationException.raiseToFront(ArticleDialog.class, evERRException, true);
		}
	}
	@FXML
	public Boolean insert(Article argArticle) {

		return update(argArticle);
	}

	@FXML
	public Boolean update(Article argArticle) {
		try {

			aArticle = argArticle;
			
			JFXApplicationScene aDetailPanelContent = JFXApplicationDesignObjectLoad.create(
					MainWindow.class.getSimpleName(),
					MainWindowDetail.class.getSimpleName(), null);
			MainWindowDetail aController  =(MainWindowDetail) aDetailPanelContent.getRootController();
		 
			aController.setEditable(true);
			aController.refresh(argArticle);
			
			ArticleDialogStage = new Stage();
	 
			ArticleDialogStage.setScene(aDetailPanelContent);
			ArticleDialogStage.initModality(Modality.WINDOW_MODAL);
			
			ArticleDialogStage.setHeight(480);
			ArticleDialogStage.setWidth(640);
			ArticleDialogStage.centerOnScreen();
			ArticleDialogStage.showAndWait();

		} catch (Exception evERRException) {
			JFXApplicationException.raiseToFront(this.getClass(), evERRException, true);
		}
		return false;
	}

	@FXML
	public Boolean deleteConfirmDialog(Article argArticle) {
		try {

			
			if(!JFXApplicationDialog.showConfirmDialog("Effacer article ("+argArticle.getLibelle()+"["+argArticle.getId()+"])"))
			{
				return false;
			} 
			Integer iInsertedObjId = DaoFactory.getArticleDAO().delete(argArticle);

			if ((iInsertedObjId != null) && (iInsertedObjId > 0)) {

			} else {
				JFXApplicationException.raiseToFront(this.getClass(),
						new JFXApplicationException(" Error lors de l operation ... "), true);
			}
			ServiceArticle.getServiceArticleSingleton().getArticleSearch().clearCriteria();
			ServiceArticle.getServiceArticleSingleton().refresh();
			return true;
		} catch (Exception evERRException) {
			JFXApplicationException.raiseToFront(this.getClass(), evERRException, true);
		}
		return false;
	}

	@FXML
	public boolean doSave() {
		try {
			
			if(!JFXApplicationDialog.showConfirmDialog("Enregistrer les modification article ("+aArticle.getLibelle()+"["+aArticle.getId()+"])"))
			{
				ServiceArticle.getServiceArticleSingleton().refresh();
				return false;
			} 
			Integer iInsertedObjId = ((aArticle.getId() > 0) ? DaoFactory.getArticleDAO().update(aArticle)
					: DaoFactory.getArticleDAO().insert(aArticle));

			if ((iInsertedObjId != null) && (iInsertedObjId > 0)) {

			} else {
				JFXApplicationException.raiseToFront(this.getClass(),
						new JFXApplicationException(" Error lors de l operation ... "), true);
			}
			ServiceArticle.getServiceArticleSingleton().getArticleSearch().clearCriteria();
			ServiceArticle.getServiceArticleSingleton().refresh();
			return true;
		} catch (Exception evERRException) {
			JFXApplicationException.raiseToFront(this.getClass(), evERRException, true);
		} finally {
			aArticle = null;
		}
		return false;
	}
	public void close() {
ArticleDialogStage.close();
		
	}
	

}
