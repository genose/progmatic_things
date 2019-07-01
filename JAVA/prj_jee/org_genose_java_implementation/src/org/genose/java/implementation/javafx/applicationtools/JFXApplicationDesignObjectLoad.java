package org.genose.java.implementation.javafx.applicationtools;

import java.io.IOException;
import java.lang.reflect.Constructor;

import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationRuntimeException;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationScene;
import org.genose.java.implementation.javafx.applicationtools.views.customviewscontroller.JFXApplicationCustomControlComboxBoxAutoFill;

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
	 * @param <T>
	 * @return dynamic Object<T<Controller>> type in User Data ...
	 */
	public static <T> Parent create(Class<T> aObjectClassControllerWhichBeCreated) {
		try {

			String sCurrentExecutionClass = JFXApplicationDesignObjectLoad.class.getCanonicalName();

			StackTraceElement[] aStackTraceElementsList = JFXApplicationHelper.getStackTrace();

			String sEnclosingClassName = null;

			Object oFXMLRootNode = null;
			Object aParentRootNode = null;
			Object aController = null;
			Node aRootNode = null;
			
			
			
			if (aObjectClassControllerWhichBeCreated != null) {
				sEnclosingClassName = String.valueOf(JFXApplicationClassHelper
						.invokeMethod(aObjectClassControllerWhichBeCreated, "getCanonicalName"));
			} else {
				int iIndexStack = 0;
				for (StackTraceElement aStackTraceElement : aStackTraceElementsList) {

					if (aStackTraceElement.getClassName().compareToIgnoreCase(sCurrentExecutionClass) == 0) {
						// take another index, because index at n+0 is the method call and index n+1 is
						// the class element which call of current wizardy came from
						sEnclosingClassName = aStackTraceElementsList[(iIndexStack + 1)].getClassName();
						System.out.println(String.format("%d :: %s :: select (%s)", iIndexStack,
								aStackTraceElement.getClassName(), sEnclosingClassName));
						break;
					}
					iIndexStack++;
				}
			}

			Class<?> aRefeneceClass = Class.forName(sEnclosingClassName);
			Constructor<?>[] aConstructorList = aRefeneceClass.getConstructors();
			Object oObjectRef = null;
			if (aConstructorList.length > 0) {
				oObjectRef = aConstructorList[0].newInstance();
			} else {
				throw new JFXApplicationRuntimeException("Cant class constructor for " + sEnclosingClassName);
			}

			String sDesignFile = String.format("%s%s", oObjectRef.getClass().getSimpleName(),
					JFXApplication.JFXFILETYPE.FILETYPE_FXML.getValue());

			ClassLoader aClassLoader = oObjectRef.getClass().getClassLoader();
			if (aClassLoader == null) {
				throw new JFXApplicationRuntimeException(
						"Cant find class loader definition for " + sEnclosingClassName);
			}
			java.net.URL aUrlDesignFile = oObjectRef.getClass().getResource(sDesignFile);

		
			if (aUrlDesignFile != null) {
				FXMLLoader aRootNodeLoader = new FXMLLoader(aUrlDesignFile);
				
				if (aRootNodeLoader != null) {
					
				
					
					oFXMLRootNode = aRootNodeLoader.getRoot();
					
					if (oFXMLRootNode == null) {
						System.out.println(" Root is Null  or Dynamic root ...");

						aController = aRootNodeLoader.getController();
						if (aController == null) {
							aController = aRefeneceClass.getSuperclass();
						
						Object sclassnameforrootnode = JFXApplicationClassHelper.invokeMethod(aController,
								"getCanonicalName");
						Class<?> aRefeneceClassForRootNode = Class.forName(String.valueOf(sclassnameforrootnode));
						Constructor<?>[] aConstructorListForRootNode = aRefeneceClassForRootNode.getConstructors();

						if (aConstructorListForRootNode.length > 0) {

							for (Constructor<?> aConstructorFound : aConstructorListForRootNode) {
								if (aConstructorFound.getParameterCount() == 0) {
									aParentRootNode = aConstructorFound.newInstance();
									break;
								}
							}
						}
}
						if (aParentRootNode != null) {
							aRootNodeLoader.setRoot(aParentRootNode);

						} else {
							throw new JFXApplicationRuntimeException(
									"Cant obtain dynamic (default) constructor definition for " + sEnclosingClassName);
						}

					}

					aRootNode = aRootNodeLoader.load();

					if ((aRootNode != null) && (aParentRootNode != null)) {
						((Parent) aParentRootNode).setUserData(aRootNodeLoader.getController());
					} else {
						throw new JFXApplicationRuntimeException("Cant load definition for " + sEnclosingClassName);
					} 
				} 
				
			
			} else {
				JFXApplicationLogger.getLogger().logError(aRefeneceClass.getClass(), new JFXApplicationRuntimeException(
						String.format("Cant obtain FXLOADER definition for %s  %n Expected Class %s", aUrlDesignFile, sEnclosingClassName )));
			}
			// may be NULL 
			return ((Parent) aParentRootNode);
		} catch (Exception evERRObjectCreateLoad) {
			JFXApplicationLogger.getLogger().logError(JFXApplicationDesignObjectLoad.class, evERRObjectCreateLoad);
			throw new RuntimeException(evERRObjectCreateLoad);
		}
		

	}

	public static <T> Object createFromTypeController(Class <T> aclass, T tt){
		Object aObject = new Object();
		return aObject;
	}

}