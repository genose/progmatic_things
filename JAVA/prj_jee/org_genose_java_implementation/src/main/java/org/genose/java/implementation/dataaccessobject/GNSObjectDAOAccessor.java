package org.genose.java.implementation.dataaccessobject;


import org.genose.java.implementation.applicationtools.GNSObjectClassHelper;
import org.genose.java.implementation.tools.NumericRange;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public abstract class GNSObjectDAOAccessor<T> extends GNSObjectDAOConnexion implements GNSObjectDAOStrings {
// https://community.talend.com/t5/Design-and-Development/Conditional-component-simple-if-true-commit-else-die/td-p/140416
    // https://fabrice-bouye.developpez.com/tutoriels/javafx/evenement-invalidation-modification-proprietes-expressions-javafx/
    public String sDOADefinition = null;

    public GNSObjectDAOAccessor() {
        super();
    }

    public GNSObjectDAOAccessor(Connection argConnexion) {
        super(argConnexion);
    }

    public abstract T getByID(Integer id);

    public abstract ArrayList<T> getAll();

    public abstract ArrayList<T> select(T obj);

    public abstract Integer insert(T obj);

    public abstract Integer update(T obj);

    public abstract Integer delete(T obj);

    /**
     * @param aStatementArg
     * @param iArgFieldPosition
     * @param sValue
     * @return
     */
    public boolean setOrNull(PreparedStatement aStatementArg, int iArgFieldPosition, String sValue) {
        try {
            if ((sValue == null) || (sValue.isEmpty())) {

                aStatementArg.setNull(iArgFieldPosition, Types.VARCHAR);

            } else {
                aStatementArg.setString(iArgFieldPosition, sValue);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param aStatementArg
     * @param iArgFieldPosition
     * @param iValue
     * @return
     */
    public boolean setOrNull(PreparedStatement aStatementArg, int iArgFieldPosition, Integer iValue) {
        try {
            if ((iValue == null) || (iValue <= 0)) {

                aStatementArg.setNull(iArgFieldPosition, Types.INTEGER);

            } else {
                aStatementArg.setInt(iArgFieldPosition, iValue);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param aStatementArg
     * @param iArgFieldPosition
     * @param fValue
     * @return
     */
    public boolean setOrNull(PreparedStatement aStatementArg, int iArgFieldPosition, Float fValue) {
        try {
            if ((fValue == null) || (fValue <= 0)) {

                aStatementArg.setNull(iArgFieldPosition, Types.FLOAT);

            } else {
                aStatementArg.setFloat(iArgFieldPosition, fValue);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param aStatementArg
     * @param iArgFieldPosition
     * @param aValue
     * @return
     */
    public boolean setOrNull(PreparedStatement aStatementArg, int iArgFieldPosition, ArrayList<?> aValue) {
        try {
            aStatementArg.setNull(iArgFieldPosition, Types.VARCHAR);

            if ((aValue == null) || (aValue.isEmpty())) {

                aStatementArg.setNull(iArgFieldPosition, Types.VARCHAR);

            } else {
                JSONArray aJSONarrayRepresentation = new JSONArray(aValue);

                aStatementArg.setString(iArgFieldPosition, aJSONarrayRepresentation.toString());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param aStatementArg
     * @param iArgFieldPosition
     * @param aValue
     * @param argJSONMethodMapping
     * @return
     */
    // http://blog.paumard.org/cours/java/chap10-entrees-sorties-serialization.html
    // http://www.java2novice.com/java-json/jackson/map-to-json/
    // http://www.studytrails.com/java/json/java-org-json/
    public boolean setOrNull(PreparedStatement aStatementArg, int iArgFieldPosition, ArrayList<?> aValue,
                             Map<String, String> argJSONMethodMapping) {
        try {

            if ((aValue == null) || (aValue.isEmpty())) {

                aStatementArg.setNull(iArgFieldPosition, Types.VARCHAR);

            } else {
                JSONArray aJSONarrayRepresentation = new JSONArray();
                for (Object aObjectValue : aValue) {
                    JSONObject aValueForJSON = new JSONObject();
                    for (Map.Entry<String, String> entry : argJSONMethodMapping.entrySet()) {

                        String aKeyForJSON = entry.getKey();
                        String aObjectMethodName = entry.getValue();
                        if (GNSObjectClassHelper.respondsTo(aObjectValue, aObjectMethodName)) {
                            Object aObjectInnerValue = GNSObjectClassHelper.invokeMethod(aObjectValue,
                                    aObjectMethodName);
                            if (aObjectInnerValue != null) {
                                if (aObjectInnerValue.getClass().equals(Integer.class.getClass())) {
                                    System.out.println(this.getClass().getSimpleName() + " : JSON Criteria :: adding as Integer");
                                    aValueForJSON.append(aKeyForJSON, ((Integer) aObjectInnerValue).intValue());
                                } else if (
                                        aObjectInnerValue.getClass().equals(Double.class.getClass()) ||
                                                aObjectInnerValue.getClass().equals(Float.class.getClass())
                                ) {
                                    System.out.println(this.getClass().getSimpleName() + " : JSON Criteria :: adding as Double/Float");
                                    aValueForJSON.put(aKeyForJSON, ((Double) aObjectInnerValue));
                                } else {
                                    aValueForJSON.append(aKeyForJSON, aObjectInnerValue);
                                }
                            }
                        }
                    }
                    aJSONarrayRepresentation.put(aValueForJSON);
                }

                System.out.println(this.getClass().getSimpleName() + " : JSON Criteria ::  " + aJSONarrayRepresentation.toString());
                aStatementArg.setString(iArgFieldPosition, aJSONarrayRepresentation.toString());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param aStatementArg
     * @param iArgFieldPosition
     * @param aValue
     * @return
     */
    public boolean setOrNull(PreparedStatement aStatementArg, int iArgFieldPosition, NumericRange aValue) {
        try {

            if ((aValue == null) || (aValue.isEmpty())) {

                aStatementArg.setNull(iArgFieldPosition, Types.VARCHAR);

            } else {

                JSONArray aJSONarrayRepresentation = new JSONArray();
                JSONObject aValueForJSON = new JSONObject();
                aValueForJSON.append("min", aValue.min());
                aValueForJSON.append("max", aValue.max());

                aJSONarrayRepresentation.put(aValueForJSON);
                aStatementArg.setString(iArgFieldPosition, aJSONarrayRepresentation.toString());
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
