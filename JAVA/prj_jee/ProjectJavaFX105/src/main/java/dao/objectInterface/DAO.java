package dao.objectInterface;

import org.genose.java.implementation.dataaccessobject.GNSObjectDAOAccessor;
import org.genose.java.implementation.javafx.applicationtools.JFXApplicationClassHelper;
import org.genose.java.implementation.tools.NumericRange;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public abstract class DAO<T> extends GNSObjectDAOAccessor<T> {


	public DAO(Connection argConnexion) {

		Objects.requireNonNull(argConnexion, S_ERRMESSAGE_DAO_NULLCONNECT);
		super.setServerConnexion(argConnexion);
	}


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
						if (JFXApplicationClassHelper.respondsTo(aObjectValue, aObjectMethodName)) {
							Object aObjectInnerValue = JFXApplicationClassHelper.invokeMethod(aObjectValue,
									aObjectMethodName);
							if (aObjectInnerValue != null ) {
								if (aObjectInnerValue.getClass() .equals(Integer.class.getClass())) {
									aValueForJSON.append(aKeyForJSON,((Integer) aObjectInnerValue).intValue());
								}else if (
										aObjectInnerValue.getClass() .equals(Double.class.getClass()) ||
										aObjectInnerValue.getClass() .equals(Float.class.getClass())
										) {
									aValueForJSON.append(aKeyForJSON,((Double) aObjectInnerValue).doubleValue());
								}else {
									aValueForJSON.append(aKeyForJSON,aObjectInnerValue);
								}
							}
						}
					}
					aJSONarrayRepresentation.put(aValueForJSON);
				}

				System.out.println(" JSON Criteria ::  " + aJSONarrayRepresentation.toString());
				aStatementArg.setString(iArgFieldPosition, aJSONarrayRepresentation.toString());
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

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
