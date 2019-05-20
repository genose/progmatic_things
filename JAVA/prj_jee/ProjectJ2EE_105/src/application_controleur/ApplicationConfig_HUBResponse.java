/**
 * 
 */
package application_controleur;

import java.util.HashMap;
import java.util.Map;


/**
 * @author 59013-03-13
 *
 */
public enum ApplicationConfig_HUBResponse  { 
 
	APPLICATIONCONFIG_HUBRESPONSE_SUCCESS(200, "SUCCESS") ,
	APPLICATIONCONFIG_HUBRESPONSE_FAILURE(403, "FAILURE") ,
	APPLICATIONCONFIG_HUBRESPONSE_UNREACHABLE_RESSOURCE(404, "FAILURE") ,
	
	APPLICATIONCONFIG_HUBRESPONSE_AUTH_REQUIRED(401, "AUTH_REQUIRED"),
	APPLICATIONCONFIG_HUBRESPONSE_AUTH_FAILURE(403, "AUTH_FAILURE"),
	APPLICATIONCONFIG_HUBRESPONSE_AUTH_SUCCESS(202, "AUTH_SUCCESS") ;
 
	private int intValue;
	private String stringValue;
	private static Map map = new HashMap<>();
	/**
	 * 
	 * @param httpCodeEquiv
	 * @param codeNameEquiv
	 * @throws Exception
	 */
	private ApplicationConfig_HUBResponse(int httpCodeEquiv, String codeNameEquiv)  {
	    this.stringValue = (String.valueOf(codeNameEquiv).trim().length() >1)? codeNameEquiv : null;
	    this.intValue = (Integer.valueOf(httpCodeEquiv) > 0 ) ? httpCodeEquiv : (-1) ;
	    
	 /* if(this.stringValue == null) {
	    	throw new Exception(this.getClass()+"::Invalid Enum construct value(codeNameEquiv)");
	    }
	    
	    if(this.intValue == (-1)) {
	    	throw new Exception(this.getClass()+"::Invalid Enum construct value(httpCodeEquiv)");
	    }*/
	}
	/**
	 * 
	 */
	static {
	    for (ApplicationConfig_HUBResponse hubResponseType : ApplicationConfig_HUBResponse.values()) {
	        map.put(hubResponseType.intValue, hubResponseType);
	    }
	}
	/**
	 * 
	 * @param responseHttpEquivType
	 * @return
	 */
	public static ApplicationConfig_HUBResponse valueOf(int responseHttpEquivType) {
	    ApplicationConfig_HUBResponse responseMapped = (ApplicationConfig_HUBResponse) map.get(responseHttpEquivType);
	    
	    if(responseMapped == null || responseMapped.intValue == 0 ) {
	    	return (ApplicationConfig_HUBResponse) ApplicationConfig_HUBResponse.valueOf(String.valueOf( responseHttpEquivType));
	    }
	    return responseMapped;
	}
	/* @Override
	public static StringApplicationConfig_HUBResponse valueOf(String responseEquivType) {
		for(ApplicationConfig_HUBResponse eObjectVal : ApplicationConfig_HUBResponse.values()){
			if(responseEquivType == String.valueOf(eObjectVal.stringValue) && (eObjectVal.stringValue != null) && (String.valueOf(eObjectVal.stringValue).length() > 1) ) return eObjectVal;
	        if(responseEquivType == String.valueOf(eObjectVal.intValue) && (eObjectVal.intValue != 0) ) return eObjectVal;
		}
		return null;
	}*/
	
	public int getCodeHttpEquivValue() {
	    return intValue;
	}
	
	public String getCodeNameValue() {
	    return stringValue;
	}
	
	public String getEnumByString(String eObjectValCode){
	    for(ApplicationConfig_HUBResponse eObjectVal : ApplicationConfig_HUBResponse.values()){
	        if(eObjectValCode == String.valueOf(eObjectVal.stringValue) && (eObjectVal.stringValue != null) && (String.valueOf(eObjectVal.stringValue).length() > 1) ) return eObjectVal.name();
	        if(eObjectValCode == String.valueOf(eObjectVal.intValue) && (eObjectVal.intValue != 0) ) return eObjectVal.name();
	    }
	    return null;
	}
	@Override
	public String toString() {
		 for(ApplicationConfig_HUBResponse eObjectVal : ApplicationConfig_HUBResponse.values()){
	         if(stringValue == eObjectVal.stringValue) return eObjectVal.name();
	     }
		 return "";
	}
}
