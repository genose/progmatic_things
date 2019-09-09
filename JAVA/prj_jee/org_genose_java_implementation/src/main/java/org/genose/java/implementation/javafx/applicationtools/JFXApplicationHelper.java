/**
 *
 */
package org.genose.java.implementation.javafx.applicationtools;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.genose.java.implementation.streams.GNSObjectMappedLogger;

/**
 * @author 59013-36-18
 *
 */
public class JFXApplicationHelper implements JFXApplicationDesignObjectLoad {

    /**
     *
     */
    public JFXApplicationHelper() {
        // TODO Auto-generated constructor stub
    }

    void loadBundleRessource() throws IOException {
        Locale locale = new Locale("en", "UK");
        ResourceBundle bundle = ResourceBundle.getBundle("strings", locale);

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("ui/main.fxml"), bundle);
    }

    /* ****************************************************** */

    /**
     *
     * @return List<StackTraceElement>
     */
    public static synchronized StackTraceElement[] getStackTrace() {
        synchronized (Thread.class) {
            String sFilejName = JFXApplicationHelper.class.getCanonicalName();
            try {

                StackTraceElement[] aStackElement = { new StackTraceElement(JFXApplicationHelper.class.getName(), "getStackTrace", sFilejName, 1)};
                Thread aThread = Thread.currentThread();
                int threadCnt = Thread.activeCount();


                if (aThread != null) {
                    // if(aThread.isDaemon())
                    aStackElement = aThread.getStackTrace();
                    // else
                    // System.out.println("ERROR CANT FETCH CURRENT THREAD WHILE FETCHING THREAD
                    // STACK ... ");
                } else {
                    System.out.println(">> ERROR CANT FETCH CURRENT THREAD ... ");
                }
                return aStackElement;
            } catch (Exception evERRSTACKTRACE) {
                System.out.println(">> ERROR WHILE FETCHING THREAD STACK ... :: thread count(" + Thread.activeCount() + ")  :::: ");
                System.out.println("StackTrace :: " + evERRSTACKTRACE);
            }

            StackTraceElement[] aStackElement = { new StackTraceElement(JFXApplicationHelper.class.getName(), "getStackTrace", sFilejName, 1)};


            return aStackElement;

        }
    }

    /* ****************************************************** */
    private StackTraceElement getLastStackTrace() {
        StackTraceElement[] aStackTrace = Thread.currentThread().getStackTrace();
        return aStackTrace[aStackTrace.length - 1];
    }

    /* ****************************************************** */
    public static Object getApplicationMain() {
        Application refApplication = null;
        Class localClass = null;
        if (JFXApplication.singletonInstanceExists()) {
            refApplication = JFXApplication.getJFXApplicationSingleton();
            localClass = refApplication.getClass();
        }else{
            StackTraceElement[] aStackElement = JFXApplicationHelper.getStackTrace();
            System.out.println(" ****** Stack trace :: "+aStackElement );
        }
        return refApplication;
    }

    /* ****************************************************** */
    public static String getApplicationBundlePath() {
        try {
            Class localClass = JFXApplicationHelper.class.getClass();
            Application refApplication = null;
            if (JFXApplication.singletonInstanceExists()) {
                refApplication = JFXApplication.getJFXApplicationSingleton();
                localClass = refApplication.getClass();
            }
            System.out.println(localClass.getPackageName()+" :: Library using Ref class APPMain as JFXApplication::" + String.valueOf(refApplication));
            String systemPathSeparator = String.valueOf(File.separatorChar);
            String packageNamed = localClass.getPackageName();
            String localPackagePath = String.valueOf(packageNamed);
            String localRunnablePathRelative = String.valueOf("." + packageNamed).replaceAll("[.]", "*")
                    .replaceAll("[^\\*]", "");
            System.out.println("Librairy path :: " + packageNamed + " :: " + localRunnablePathRelative);
            localRunnablePathRelative = localRunnablePathRelative.replaceAll("[\\*]", "..\\" + systemPathSeparator);

            return localRunnablePathRelative;
        } catch (Exception evErrPath) {
            GNSObjectMappedLogger.getLogger().logError(JFXApplicationHelper.class.getClass(), evErrPath);
        }
        return null;
    }

    public static String getApplicationRunnablePathAbsolute() {

        String systemPathSeparator = String.valueOf(File.separatorChar);
        try {

            Class localClass = JFXApplicationHelper.class.getClass();
            Application refApplication = null;
            if (JFXApplication.singletonInstanceExists()) {
                refApplication = JFXApplication.getJFXApplicationSingleton();
                localClass = refApplication.getClass();
            }

            String packageNamed = localClass.getPackageName();
            String localPackagePath = String.valueOf(packageNamed);

            String localRunnablePathRelative = String.valueOf(String.valueOf("." + localPackagePath)
                    .replaceAll("[.]", "\\" + systemPathSeparator).replaceAll("[^\\" + systemPathSeparator + "]", ""));

            localRunnablePathRelative = localRunnablePathRelative.replaceAll("[\\" + systemPathSeparator + "]", "../");
            URL aUrlClass = localClass.getResource(localRunnablePathRelative);

            if (aUrlClass != null) {
                return aUrlClass.getPath().replaceFirst("[/]", "").replaceFirst(localPackagePath, "").replaceAll("[//]",
                        "/");
            } else {
                aUrlClass = localClass.getResource(systemPathSeparator);
                if (aUrlClass != null) {
                    return aUrlClass.getPath().replaceFirst("[/]", "").replaceFirst(localPackagePath, "").replaceAll("[//]",
                            "/");
                }
            }

        } catch (Exception evErrPath) {
            GNSObjectMappedLogger.getLogger().logError(JFXApplication.class.getClass(), evErrPath);
        }

        return "/";

    }

    /**
     *
     * @param aPath
     * @return
     */
    public static boolean applicationPathExist(String aPath) {

        try {
            File localClassPath = null;
            if (aPath != null) {
                Class localClass = JFXApplicationHelper.class.getClass();
                Application refApplication = null;
                if (JFXApplication.singletonInstanceExists()) {
                    refApplication = JFXApplication.getJFXApplicationSingleton();
                    localClass = refApplication.getClass();
                }

               //  System.out.println(localClass.getPackageName()+" :: Library using Ref class APPMain as JFXApplication::" + String.valueOf(refApplication));

                String sLocalClassPackageName = localClass.getPackageName();
                Boolean bAnnonPackageName = String.valueOf(sLocalClassPackageName).isEmpty();
                String systemPathSeparator = String.valueOf(File.separatorChar);
                localClassPath = new java.io.File(String.valueOf(aPath));

                String documentPath = localClassPath.getAbsolutePath();
                String documentPathRelative = localClassPath.getPath();

                URL aPathUrl = localClass.getResource(documentPathRelative);
                URL aPathUrlAlt = localClass.getResource(aPath);

                boolean bPathIsValid = ((aPathUrl != null) && (aPathUrlAlt != null));

                boolean bPathExist = localClassPath.exists();
                boolean bPathUrlValid = (aPathUrl != null);
                /*
                 * JFXApplicationLogger.getLogger().logInfo(null, ((bPathIsValid) ? "" : "Not")
                 * + " exist :: " + documentPath + " :: ");
                 */
                return bPathIsValid;

            }
        } catch (Exception evERRFILEEXISTS) {
            GNSObjectMappedLogger.getLogger().logError(JFXApplication.getJFXApplicationSingleton().getClass(),
                    evERRFILEEXISTS);
        }

        return false;

    }

    /**
     *
     * @param sApplicationPath
     * @param sRequiredPathIn
     * @param sAltPathIfFail
     * @return
     */
    public static String resolveModulePathInsenstiveCase(String sApplicationPath, String sRequiredPathIn,
                                                         String sAltPathIfFail) {
        String sBasePath = null;
        if ((sApplicationPath != null) && (sRequiredPathIn != null)) {
            Path aPathInBundle = Paths.get(sApplicationPath, sRequiredPathIn);

            sBasePath = ((JFXApplication.applicationPathExist(aPathInBundle.toString())) ? aPathInBundle.toString()
                    : null);

            if (sBasePath == null) {
                aPathInBundle = Paths.get(sApplicationPath, String.valueOf(sRequiredPathIn).toLowerCase());

                sBasePath = ((JFXApplication.applicationPathExist(aPathInBundle.toString())) ? aPathInBundle.toString()
                        : null);
            }

            if (sBasePath == null) {
                aPathInBundle = Paths.get(sApplicationPath, String.valueOf(sRequiredPathIn).toUpperCase());

                sBasePath = ((JFXApplication.applicationPathExist(aPathInBundle.toString())) ? aPathInBundle.toString()
                        : null);
            }

        }

        if (((sAltPathIfFail != null) && String.valueOf(sAltPathIfFail).length() > 1) && (sBasePath == null)) {
            sBasePath = ((JFXApplication.applicationPathExist(sAltPathIfFail)) ? String.valueOf(sAltPathIfFail) : null);
        }

        return sBasePath;
    }

    public static String packageToPathRoot(Class<?> aObjectClass) {
        return packageToPath(aObjectClass)+""+String.valueOf(aObjectClass.getPackageName()).replaceAll("[\\.]", "/");
    }

    public static String packageToPath(Class<?> aObjectClass) {
        try {

            String sClassName = aObjectClass.getCanonicalName();
            Class<?> localClass = Class.forName(sClassName);

            String systemPathSeparator = String.valueOf(File.separatorChar);
            String packageNamed = localClass.getPackageName();
            String localPackagePath = String.valueOf(packageNamed);
            System.out.println("******* package name "+localPackagePath+" :: "+aObjectClass.getName()+" :: "+localClass.getName());
            String localRunnablePathRelative = String.valueOf("." + localPackagePath).replaceAll("[.]", "*")
                    .replaceAll("[^\\*]", "");

            localRunnablePathRelative = localRunnablePathRelative.replaceAll("[\\*]", "..\\/" );
            return localRunnablePathRelative;
        } catch (Exception evErrPath) {
            GNSObjectMappedLogger.getLogger().logError(JFXApplicationHelper.class.getClass(), evErrPath);
        }
        return null;
    }

}
