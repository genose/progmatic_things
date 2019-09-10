package ArticleDialog;

import java.awt.event.ActionEvent;
import java.util.Objects;

import org.genose.java.implementation.javafx.applicationtools.JFXApplication;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationDesignObjectLoad;
import org.genose.java.implementation.arraysmapslists.GNSObjectMappedObjectValue;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationDialog;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationScene;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationStage;

import MainWindow.assets.controllers.MainWindow;
import MainWindow.assets.controllers.MainWindowDetail;
import dao.DaoFactory;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.Stage;
import metier.Article;
import service.ServiceArticle;

public class ArticleDialog {

	private Article aArticle = null;
	MainWindowDetail aWindowDetailArticle = null;
	Stage aArticleDialogStage = null;
	private static JFXApplicationScene aArticleDialogScene = null;

	public static void articleDialogCreate() {
		try {

			aArticleDialogScene = JFXApplicationDesignObjectLoad.create(ArticleDialog.class.getSimpleName(),
					ArticleDialog.class.getSimpleName(), null);
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

			argArticle = Objects.requireNonNullElse(argArticle, new Article(0, Article.sDEFAULTNEWARTICLELIBELLE));
			;
			aArticle = argArticle;

			JFXApplicationScene aDetailPanelContent = JFXApplicationDesignObjectLoad
					.create(MainWindow.class.getSimpleName(), MainWindowDetail.class.getSimpleName(), null);
			MainWindowDetail aController = (MainWindowDetail) aDetailPanelContent.getRootController();

			aController.setEditable(true);
			aController.refresh(argArticle);

			aArticleDialogStage = new Stage();

			aArticleDialogStage.setTitle(String.format("%s (%s) : %s",
					((aArticle.getId() == 0) ? "Ajouter " : "Modifier "), aArticle.getId(), aArticle.getLibelle()));
			aArticleDialogStage.setScene(aDetailPanelContent);
			aArticleDialogStage.initModality(Modality.WINDOW_MODAL);

			aArticleDialogStage.setHeight(480);
			aArticleDialogStage.setWidth(640);
			aArticleDialogStage.centerOnScreen();
			aArticleDialogStage.showAndWait();

		} catch (Exception evERRException) {
			JFXApplicationException.raiseToFront(this.getClass(), evERRException, true);
		}
		return false;
	}

	@FXML
	public Boolean deleteConfirmDialog(Article argArticle) {
		try {

			if (!JFXApplicationDialog.showConfirmDialog(
					"Effacer article (" + argArticle.getLibelle() + "[" + argArticle.getId() + "])")) {
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

			if (!JFXApplicationDialog.showConfirmDialog(
					"Enregistrer les modification article (" + aArticle.getLibelle() + "[" + aArticle.getId() + "])")) {
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

	public void cancel() {
		aArticleDialogStage.close();

	}

	public void close() {
		aArticleDialogStage.close();

	}

}
