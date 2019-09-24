package org.genose.java.implementation.dataaccessobject;

import org.genose.java.implementation.exceptionerror.GNSObjectRuntimeException;
import org.genose.java.implementation.net.GNSObjectMappedParamater;
import org.genose.java.implementation.net.GNSObjectRemoteConnectionFactory;
import org.genose.java.implementation.streams.GNSObjectMappedLogger;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

abstract public class GNSObjectDAOConnexion extends GNSObjectRemoteConnectionFactory implements GNSObjectDAOStrings {
    protected Connection aLServerConnexion;

    protected  static GNSObjectDAOConnexion astServerInstanceConnexion = null;


/* ********************************************************************** */
    public GNSObjectDAOConnexion() {
        super();
        astServerInstanceConnexionSingleton();
        setConnectionName(astServerInstanceConnexion.getClass().getName()+":null");
    }

    /* ********************************************************************** */
    /**
     *
     * @param argConnexion
     */
    public GNSObjectDAOConnexion(Connection argConnexion) {
        super();
        Objects.requireNonNull(argConnexion, S_ERRMESSAGE_DAO_NULLCONNECT);
        aLServerConnexion = argConnexion;
        astServerInstanceConnexionSingleton();
        try {
            setConnectionName(String.format("%s:[%s]",astServerInstanceConnexion.getClass().getName(), argConnexion.getSchema()));
        } catch (SQLException e) {
            e.printStackTrace();
            setConnectionName(String.format("%s:[%s]",astServerInstanceConnexion.getClass().getName(),"[noname]"));
        }
    }

    /* ********************************************************************** */

    /**
     *
     */
    private void astServerInstanceConnexionSingleton(){
        synchronized (GNSObjectDAOConnexion.class){
            if(astServerInstanceConnexion == null )
                astServerInstanceConnexion = this;
        }
    }
    /* ********************************************************************** */
    /**
     *
     * @return
     */
    public Connection getServerConnexion() {
        return aLServerConnexion;
    }
    /* ********************************************************************** */
    /**
     *
     * @param aArgLServerConnexion
     */
    public void setServerConnexion(Connection aArgLServerConnexion) {
        aLServerConnexion = aArgLServerConnexion;
    }
    /* ********************************************************************** */
    /**
     *
     * @return
     */
    public static synchronized GNSObjectDAOConnexion getInstance() {
        if(astServerInstanceConnexion == null) {
         throw new GNSObjectRuntimeException(GNSObjectDAOConnexion.class, " .... Warning : NULL Object ... You should override in our instance this method and call [super.getInstance(this.class)] ");
        }
        return Objects.requireNonNullElse(astServerInstanceConnexion, null);
    }

    /* ********************************************************************** */
    /**
     *
     * @return
     */
    public static GNSObjectDAOConnexion getInstance(Class<?> cClassObject) {

        class ASTTmp extends GNSObjectDAOConnexion
        {
            // public ASTTmp(){ super();}
        }

        if(astServerInstanceConnexion == null) {
            GNSObjectMappedLogger.getLogger().logInfo (GNSObjectDAOConnexion.class ," .... Warning : NULL Object ... let's should call [super.getInstance("+cClassObject.getName()+")] ");
        }
        return Objects.requireNonNullElse(astServerInstanceConnexion, (GNSObjectDAOConnexion) (new ASTTmp()).getInstanceClass(cClassObject) );
    }

    /* ********************************************************************** */

    /**
     *
     * @param cClassObject
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    protected synchronized Object getInstanceClass(Class<?> cClassObject)  {
        Object aTMPServerInstanceConnexion = null;
        Class<?> aRefeneceClass = null;
        // TODO :: refactoring this shit mechanism "ASTTmp" thing to a more convenient solution ...
        if ( (astServerInstanceConnexion == null) || (astServerInstanceConnexion.getClass().getSimpleName().compareTo("ASTTmp") == 0 )){
            GNSObjectMappedLogger.getLogger().logInfo(" ++++++++ should using constructor for ("+cClassObject+")  .... ");
            try {

                aRefeneceClass = Class.forName(String.valueOf(cClassObject.getName()));
                Constructor<?>[] aConstructorListForRootNode = aRefeneceClass.getConstructors();
                GNSObjectMappedLogger.getLogger().logInfo(" ++++++++ "+aRefeneceClass+":Constructors ("+aConstructorListForRootNode.length+")  .... ");

                if (aConstructorListForRootNode.length > 0) {

                    for (Constructor<?> aConstructorFound : aConstructorListForRootNode) {
                        if (aConstructorFound.getParameterCount() == 0) {
                            GNSObjectMappedLogger.getLogger().logInfo(" ++++++++ Using constructor : "+aConstructorFound.getName());
                            aTMPServerInstanceConnexion =  aConstructorFound.newInstance();
                            break;
                        }
                    }
                }
               astServerInstanceConnexion = (GNSObjectDAOConnexion) aTMPServerInstanceConnexion;
            } catch (Exception evERR_GETINSTANCE) {
                GNSObjectMappedLogger.getLogger().logError(this.getClass(), evERR_GETINSTANCE);
            }
        }else{
            System.out.println(" ++++++++ got precendent constructor for ("+cClassObject+") : ("+astServerInstanceConnexion.getClass()+")  .... ");
        }
        return (astServerInstanceConnexion);
    }


}
