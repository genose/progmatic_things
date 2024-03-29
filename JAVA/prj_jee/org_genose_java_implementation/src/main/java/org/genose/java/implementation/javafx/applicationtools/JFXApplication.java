package org.genose.java.implementation.javafx.applicationtools;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.genose.java.implementation.javafx.applicationtools.exceptionerror.JFXApplicationException;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationScene;
import org.genose.java.implementation.javafx.applicationtools.views.JFXApplicationStage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.genose.java.implementation.streams.GNSObjectMappedLogger;
import org.genose.java.implementation.tools.GNSObjectSingletonStrings;
// https://books.google.fr/books?id=LipBDwAAQBAJ&pg=PA41&lpg=PA41&dq=java+findResource(String+moduleName,+String+name)&source=bl&ots=B4gMThh0od&sig=ACfU3U37Vd0fAa1CGUkKPfQSk6TRjns4xA&hl=fr&sa=X&ved=2ahUKEwj-0Y757OPiAhVEUBoKHSmRAIYQ6AEwAnoECAgQAQ#v=onepage&q=java%20findResource(String%20moduleName%2C%20String%20name)&f=false
// https://github.com/oracle/graal/blob/master/substratevm/src/com.oracle.svm.core/src/com/oracle/svm/core/jdk/Target_java_lang_ClassLoader.java
// https://github.com/gluonhq/scenebuilder/blob/master/app/src/main/java/com/oracle/javafx/scenebuilder/app/DocumentWatchingController.java
// http://robovm.mobidevelop.com/
// https://riptutorial.com/Download/javafx.pdf
// https://stackoverflow.com/questions/46898/how-do-i-efficiently-iterate-over-each-entry-in-a-java-map
// http://launch4j.sourceforge.net/docs.html

/*
 * https://stackoverflow.com/questions/53453212/how-to-deploy-a-javafx-11-desktop-application-with-a-jre
 * https://medium.com/azulsystems/using-jlink-to-build-java-runtimes-for-non-modular-applications-9568c5e70ef4
 *  JAVAFX12_OUTPUTNAME="javafx12.0.1_jdk"
 *
 *  PATH_JAVAFX_JMODS="/c/java/javafx-jmods-12.0.1/"
 *
 * JAVA_HOME="/c/java/jdk-12.0.1/"
 * JAVA_JREMODULE_LIST="java.se"
 *
 * JAVAFX12_MODS_LIST="javafx.base.jmod,javafx.controls.jmod,javafx.fxml.jmod,javafx.graphics.jmod,javafx.media.jmod,javafx.swing.jmod,javafx.web.jmod"
 * JAVAFX12_MODS_LIST="javafx.fxml,javafx.web,javafx.media,javafx.swing"
 *
 *  $JAVA_HOME/bin/jlink --module-path $PATH_JAVAFX_JMODS --add-modules $JAVA_JREMODULE_LIST,$JAVAFX12_MODS_LIST --bind-service --output $JAVAFX12_OUTPUTNAME
 */
// https://stackoverflow.com/questions/28305571/self-contained-javafx-application-working-directory
// https://nexus.gluonhq.com/nexus/content/repositories/releases/org/javafxports/armv6hf-sdk/
// https://bitbucket.org/javafxports/javafxmobile-plugin/downloads/
// https://github.com/gluonhq/gluon-samples/tree/master/singleview-gluonvm
// https://github.com/javafxports/javafxmobile-plugin
// https://www.freecodecamp.org/news/how-to-make-a-cross-platform-mobile-app-in-java-5f8eae071ff2/
public class JFXApplication extends Application implements GNSObjectSingletonStrings {
    /**
     * ** Declare a singleton pattern thru a static reference to Application
     * instance **
     */
    private static JFXApplication opsSingletonJFXApplication = null;
    /**
     * ** Declare static Stage **
     */
    private Stage aPrimaryStage;
    /**
     * ** Declare a flag for if (class).start() was already called ... **
     */
    private static Boolean bJFXApplicationIsStarted = false;
    /**
     * ** Declare a Log wrapper ... **
     */
    private static GNSObjectMappedLogger aLogger = null;
    private static JFXApplicationException aExceptionManager;

    public enum JFXFILETYPE {
        DIR_ASSETS("Assets"),
        DIR_VIEWS("Views"),
        DIR_CONTROLLERS("Controllers"),
        DIR_RESSOURCES("Ressources"),
        // ::
        // https://stackoverflow.com/questions/10143392/javafx-2-and-internationalization
        DIR_LOCALI18N("locali18n"), DIR_APPSRC("src"),
        FILETYPE_READEABLE_TEXT(".txt;.csv"),
        FILETYPE_FXML(".fxml"),
        FILETYPE_FCSS(".fcss"),
        FILETYPE_FPNG(".png");

        private String strValue;
        private static java.util.HashMap<Object, Object> map = new java.util.HashMap<>();

        /* ******************************************************* */

        /**
         * @param aStrValue
         */
        private JFXFILETYPE(String aStrValue) {
            this.strValue = aStrValue;
        }

        /* ******************************************************* */

        /**
         *
         */
        static {
            for (JFXFILETYPE aType : JFXFILETYPE.values()) {
                map.put(aType.strValue, aType);
            }
        }

        /* ******************************************************* */

        /**
         * @param pageType
         * @return
         */
        public static JFXFILETYPE valueOf(int pageType) {
            return (JFXFILETYPE) map.get(pageType);
        }

        /* ******************************************************* */

        /**
         * @return
         */
        public String getValue() {
            return strValue;
        }

        /* ******************************************************* */

        /**
         * @param strCode
         * @return
         */
        public String getEnumByString(String strCode) {
            for (JFXFILETYPE eType : JFXFILETYPE.values()) {
                if (strCode.toUpperCase().compareTo(String.valueOf(strValue.toUpperCase())) == 0)
                    return eType.name();
            }
            return null;
        }

        /* ******************************************************* */
        @Override
        public String toString() {
            for (JFXFILETYPE eType : JFXFILETYPE.values()) {
                if (strValue.compareTo(eType.strValue) == 0)
                    return eType.name();
            }
            return "";
        }

        /* ******************************************************* */

        /**
         * @return
         */
        public List<String> split() {
            ArrayList<String> aSplittedValues = new ArrayList<>();
            String[] arrayStringOfValues = strValue.split(";");
            for (int i = 0; i < arrayStringOfValues.length; i++) {
                aSplittedValues.add(arrayStringOfValues[i]);
            }
            return aSplittedValues;
        }

    }

    ;

    /**
     *
     */
    public JFXApplication() {
        super();
        singletonInstanceCreate();
        aLogger = new GNSObjectMappedLogger(getClass().getName());
        aExceptionManager = new JFXApplicationException();
        synchronized (JFXApplication.class) {
            getLogger().logInfo(getClass(), " Application class was instiated as :: " + String.valueOf(opsSingletonJFXApplication.getClass().getName()));

        }
    }

    /*
     * *****************************************************************************
     */

    /**
     * create a singleton
     */
    private void singletonInstanceCreate() {
        synchronized (JFXApplication.class) {
            if (opsSingletonJFXApplication != null) {
                throw new UnsupportedOperationException(
                        getClass() + ERRMESSAGE_SINGLETON_INSTANCAITE_TWICE);
            }

            opsSingletonJFXApplication = this;
        }
    }

    public static synchronized boolean singletonInstanceExists() {
        synchronized (JFXApplication.class) {
            return (opsSingletonJFXApplication != null);

        }
    }

    /**
     *
     */
    private static synchronized void singletonInstanceCheck() {
        synchronized (JFXApplication.class) {
            if (opsSingletonJFXApplication == null)
                throw new UnsupportedOperationException(" singleton is null");
        }
    }

    /* ****************************************************** */

    /**
     * @return {@link GNSObjectMappedLogger}
     */
    public GNSObjectMappedLogger getLogger() {
        singletonInstanceCheck();
        return aLogger;
    }

    /* ****************************************************** */

    /**
     * @param {{@link javafx.stage}
     */
    public void setPrimaryStage(Stage primaryStage) {
        if (!JFXApplication.singletonInstanceExists()) {
            singletonInstanceCreate();
        }

        aPrimaryStage = primaryStage;
    }

    /* ****************************************************** */

    /**
     * @return javafx.Stage (JFXApplicationStage)
     */
    public Stage getPrimaryStage() {
        singletonInstanceCheck();
        return (Stage) aPrimaryStage;
    }

    /* ****************************************************** */

    /**
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        setPrimaryStage(primaryStage);
        // start((JFXApplicationStage) getPrimaryStage(), true);

    }

    /* ****************************************************** */

    /**
     * @param primaryStage
     * @throws Exception
     */
    public void start(JFXApplicationStage primaryStage) throws Exception {
        start(primaryStage, true);
    }

    public void start(JFXApplicationStage primaryStage, boolean bShowDefaultController) throws Exception {
        singletonInstanceCheck();
        setPrimaryStage(primaryStage);
        if (getPrimaryStage() != null ){
            try {

                if (getPrimaryScene() == null) {
                    JFXApplicationScene aMainScene = JFXApplicationDesignObjectLoad.create(JFXApplicationHelper.getApplicationMain().getClass().getSimpleName(), null, null);
                    if (aMainScene != null) {
                        setPrimaryScene(aMainScene);
                    }
                }
            } catch (Exception EVERR_LOAD_DEFAULT_SCENE) {
                getApplicationLogger().logError(this.getClass(), EVERR_LOAD_DEFAULT_SCENE);
            }

            if (bShowDefaultController && (getPrimaryStage() != null)) {
                getPrimaryStage().show();
            }
        }
    }


    /* ****************************************************** */

    /**
     * @return
     */
    public static JFXApplication getJFXApplicationSingleton() {
        singletonInstanceCheck();
        return opsSingletonJFXApplication;
    }

    /* ****************************************************** */

    /**
     * @param aIconPath
     * @return true when icon is set
     */
    public static boolean setIcon(String aIconPath) {
        singletonInstanceCheck();
        /*
         * *******************************************************
         * https://stackoverflow.com/questions/10121991/javafx-application-icon :: see :
         * primaryStage.getIcons().add(new
         * Image(getClass().getProtectionDomain().getCodeSource().getLocation()+
         * "/stackoverflow.jpg")); ...
         */
        Stage aPrimStage = JFXApplication.getActiveStage();
        if (aPrimStage != null) {
            Scene aScene = ((aPrimStage).getScene());

            ObservableList<Image> aStageIcons = aPrimStage.getIcons();
            if (aStageIcons.isEmpty())
                aStageIcons.set(aStageIcons.size() - 1, new Image(aIconPath));
            else
                aStageIcons.add(new Image(aIconPath));
            aScene.getRoot();
        }

        return false;
    }

    /* ****************************************************** */

    /**
     * @return
     */
    private static Stage getActiveStage() {
        singletonInstanceCheck();
        return JFXApplication.getJFXApplicationSingleton().getPrimaryStage();
    }

    /* ****************************************************** */

    /**
     * @return
     */
    private static Stage getActivePrimaryStage() {
        singletonInstanceCheck();
        return JFXApplication.getJFXApplicationSingleton().getPrimaryStage();
    }

    /* ****************************************************** */

    /**
     * @return bundle application path
     */
    public static String getApplicationRunnablePathAbsolute() {
        singletonInstanceCheck();
        return JFXApplicationHelper.getApplicationRunnablePathAbsolute();
    }

    /* ****************************************************** */

    /**
     * @return bundle application path /
     */
    public static String getApplicationBundlePath() {
        singletonInstanceCheck();
        try {
            Class localClass = JFXApplication.getJFXApplicationSingleton().getClass();

            String systemPathSeparator = String.valueOf(File.separatorChar);
            String sLocalClassPackageName = localClass.getPackageName();

            Boolean bAnnonPackageName = String.valueOf(sLocalClassPackageName).isEmpty();

            String localPackagePath = String.valueOf(sLocalClassPackageName);
            String localRunnablePathRelative = ((bAnnonPackageName) ? "." : String.valueOf("." + sLocalClassPackageName).replaceAll("[.]", "*")
                    .replaceAll("[^\\*]", ""));

            localRunnablePathRelative = localRunnablePathRelative.replaceAll("[\\*]", "..\\" + systemPathSeparator);
            URL aUrlClass = localClass.getResource(localRunnablePathRelative);
            if (aUrlClass != null) {
                return localRunnablePathRelative;
            } else {


                aUrlClass = localClass.getResource(".");
                String slocalPathUnamedModule = aUrlClass.getPath();
                slocalPathUnamedModule = slocalPathUnamedModule.substring(0, slocalPathUnamedModule.lastIndexOf("/"));
                if (aUrlClass != null) {
                    slocalPathUnamedModule = ".." + systemPathSeparator + slocalPathUnamedModule.substring(slocalPathUnamedModule.lastIndexOf("/"), slocalPathUnamedModule.length()) + "" + systemPathSeparator;
                    slocalPathUnamedModule = slocalPathUnamedModule.replaceAll("/", "");
                    return slocalPathUnamedModule;
                }

            }
        } catch (Exception evErrPath) {
            GNSObjectMappedLogger.getLogger().logError(JFXApplication.class.getClass(), evErrPath);
        }
        return null;
    }

    /* ****************************************************** */

    /**
     * Intend to be used with (class).getRessource(String);
     *
     * @param aPath
     * @return true if path exists inside bundle app
     */
    public static boolean applicationPathExist(String aPath) {
        singletonInstanceCheck();
        return JFXApplicationHelper.applicationPathExist(aPath);
    }

    /**
     * @param jfxApplicationScene
     */
    public JFXApplication setPrimaryScene(JFXApplicationScene jfxApplicationScene) {
        singletonInstanceCheck();
        synchronized (JFXApplication.class) {
            if (aPrimaryStage != null) {
                if (jfxApplicationScene != null) {
                    aPrimaryStage.setScene(jfxApplicationScene);
                } else {
                    JFXApplicationException.raiseToFront(this.getClass(),
                            new JFXApplicationException("Try to Set Null Scene to Primary stage window ..."), true);
                }
            } else {
                JFXApplicationException.raiseToFront(this.getClass(),
                        new JFXApplicationException("Primary stage window is null ..."), true);
            }
        }
        return this;
    }

    /**
     * @param ajfxApplicationScene
     */
    public void setSecondaryScene(JFXApplicationScene ajfxApplicationScene) {
        getLogger().logInfo("setSecondary() ... ");
        JFXApplicationException.raiseToFront(this.getClass(),
                new JFXApplicationException("Unimplemented ..."), true);
    }

    /**
     * @return
     */
    public JFXApplicationScene getPrimaryScene() {
        synchronized (JFXApplication.class) {

            if (aPrimaryStage == null) {

                JFXApplicationException.raiseToFront(this.getClass(),
                        new JFXApplicationException("Primary stage window is null ..."), false);
            } else {

                return (JFXApplicationScene) aPrimaryStage.getScene();
            }
        }
        return null;
    }

    /**
     * @return
     * @throws JFXApplicationException
     */
    public static GNSObjectMappedLogger getApplicationLogger() throws JFXApplicationException {
        singletonInstanceCheck();
        synchronized (JFXApplication.class) {
            if (opsSingletonJFXApplication == null)
                throw new JFXApplicationException(" Application is not intanciated ... ");
        }
        return JFXApplication.getJFXApplicationSingleton().getLogger();

    }

    /**
     * @return
     */
    public static JFXApplicationException getExceptionManagaer() {
        synchronized (JFXApplication.class) {
            if (opsSingletonJFXApplication == null) {
                JFXApplicationException.raiseToFront(JFXApplication.class.getClass(),
                        new JFXApplicationException(" Application is not intanciated ... "), true);
            }
        }
        return JFXApplication.aExceptionManager;
    }

    /* ****************************************************** */

    /**
     * Quit Application
     */
    public void notifyQuit() {
        Platform.exit();
    }

    /* ****************************************************** */
    /*
     *
     */
    public static void notityDramaticQuit() {

        Platform.exit();
    }

}
