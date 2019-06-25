package org.genose.java.implementation.javafx.applicationtools;

import java.io.IOException;
import java.lang.reflect.Constructor;

import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationRuntimeException;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationScene;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

public abstract interface JFXApplicationDesignObjectLoad {

	static public JFXApplicationScene create(String argModuleName, String argModuleNameFile,
			JFXApplicationCallback aFuncCallback) throws JFXApplicationException {

		return null;
	}

	/**
	 * 
	 * @return dynamic Object type in User Data ...
	 */
	public static Parent create(Class<?> aObjectClasscontrollerToCreate) {
		try {

			String sCurrentExecutionClass = JFXApplicationDesignObjectLoad.class.getCanonicalName();

			StackTraceElement[] aStackTrace = JFXApplicationHelper.getStackTrace();

			String sAclassEnclosing = null;

			if (aObjectClasscontrollerToCreate != null) {
				sAclassEnclosing = String.valueOf(
						JFXApplicationClassHelper.invokeMethod(aObjectClasscontrollerToCreate, "getCononicalName"));
			} else {
				int iIndexStack = 0;
				for (StackTraceElement aStackTraceElement : aStackTrace) { 
					System.out.println(String.format("%d :: %s", iIndexStack, aStackTraceElement.getClassName()));
					if (aStackTraceElement.getClassName().compareToIgnoreCase(sCurrentExecutionClass) == 0) {

						sAclassEnclosing = aStackTrace[(iIndexStack+1)].getClassName();
						break;
					}
					iIndexStack ++;
				}
			}
 						 
			Class<?> aRefeneceClass = Class.forName(sAclassEnclosing);
			Constructor<?>[] aConstructorList = aRefeneceClass.getConstructors();
			Object oObjectRef = null;
			if (aConstructorList.length > 0) {
				oObjectRef = aConstructorList[0].newInstance();
			}else {
				throw new JFXApplicationRuntimeException("Cant class constructor for " + sAclassEnclosing);
			}

			String sDesignFile = String.format("%s%s", oObjectRef.getClass().getSimpleName(),
					JFXApplication.JFXFILETYPE.FILETYPE_FXML.getValue());

			ClassLoader aClassLoader = oObjectRef.getClass().getClassLoader();
			if (aClassLoader == null) {
				throw new JFXApplicationRuntimeException("Cant find class loader definition for " + sAclassEnclosing);
			}
			java.net.URL aUrlDesignFile = oObjectRef.getClass().getResource(sDesignFile);

			Object aParentRootNode = null;
			Object aController = null;
			Node aRootNode = null;
			if (aUrlDesignFile != null) {
				FXMLLoader aRootNodeLoader = new FXMLLoader(aUrlDesignFile);
				Object oFXMLRootNode = aRootNodeLoader.getRoot();

				if (oFXMLRootNode == null) {
					System.out.println(" Root is Null  or Dynamic root ...");

					aController = aRootNodeLoader.getController();
					if (aController == null) {
						aController = aRefeneceClass.getSuperclass();
					}
					Object sclassnameforrootnode = JFXApplicationClassHelper.invokeMethod(aController,
							"getCanonicalName");
					Class<?> aRefeneceClassForRootNode = Class.forName(String.valueOf(sclassnameforrootnode));
					Constructor<?>[] aConstructorListForRootNode = aRefeneceClassForRootNode.getConstructors();

					if (aConstructorListForRootNode.length > 0) {
						aParentRootNode = aConstructorListForRootNode[0].newInstance();
					}

					aRootNodeLoader.setRoot(aParentRootNode);

				}
				aRootNode = aRootNodeLoader.load();

				if (aRootNode != null) {
					((Parent) aParentRootNode).setUserData(aRootNodeLoader.getController());
				} else {
					throw new JFXApplicationRuntimeException("Cant load definition for " + sAclassEnclosing);
				}

				return ((Parent) aParentRootNode);

			} else {
				JFXApplicationLogger.getLogger().logError(aRefeneceClass.getClass(), null,
						String.format("Unable to load : %s ", sDesignFile));
			}

		} catch (Exception evERRObjectCreateLoad) {
			JFXApplicationLogger.getLogger().logError(JFXApplicationDesignObjectLoad.class, evERRObjectCreateLoad);
		}
		return null;

	}

}