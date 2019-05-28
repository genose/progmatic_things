
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public enum DictionnaireDefini {
	
	DictionnaireDefini_ANGLAIS(2,"Anglais"),
	DictionnaireDefini_FRANCAIS(4,"Francais");
	

	private int intValue;
	private String stringValue;
	private static Map<Object,Object> defmap = new HashMap<>();
	/**
	 * 
	 * @param httpCodeEquiv
	 * @param codeNameEquiv
	 * @throws Exception
	 */
	private DictionnaireDefini(int definitiontype, String codeNameEquiv)  {
	    this.stringValue = (String.valueOf(codeNameEquiv).trim().length() >1)? codeNameEquiv : null;
	    this.intValue = (Integer.valueOf(definitiontype) > 0 ) ? definitiontype : (-1) ;
	    
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
	    for (DictionnaireDefini definitiontype : DictionnaireDefini.values()) {
	    	defmap.put(definitiontype.intValue, definitiontype);
	    }
	}
	/**
	 * 
	 * @param definitionid
	 * @return
	 */
	public static DictionnaireDefini valueOf(int definitionid) {
		DictionnaireDefini responseMapped = (DictionnaireDefini) defmap.get(definitionid);
	    
	    if(responseMapped == null || responseMapped.intValue == 0 ) {
	    	return (DictionnaireDefini) DictionnaireDefini.valueOf(String.valueOf( definitionid ));
	    }
	    return responseMapped;
	}
	/* ************************************************************* */
	public int getIntValue() {
	    return intValue;
	}
	/* ************************************************************* */
	public String getDictNameValue() {
	    return stringValue;
	}
	/* ************************************************************* */
	static public ArrayList<String> DictNameValues()
	{
		ArrayList<String> aTempArray = new ArrayList<>();
		
	    for(DictionnaireDefini eObjectVal : DictionnaireDefini.values()){
	        aTempArray.add(String.valueOf(eObjectVal.stringValue));	     
	    }
		
		return aTempArray;
	}
	/* ************************************************************* */
	static public ArrayList<String> DictIDValues()
	{
		ArrayList<String> aTempArray = new ArrayList<>();
		
	    for(DictionnaireDefini eObjectVal : DictionnaireDefini.values()){
	        aTempArray.add(String.valueOf(eObjectVal.intValue));	     
	    }
		
		return aTempArray;
	}
	/* ************************************************************* */
	static public Boolean contains(Integer iIDRequested) {
	
		return ((ArrayList)DictIDValues()).contains(String.valueOf(iIDRequested));
	}
	/* ************************************************************* */
	static public Boolean contains(String sNameRequested) {
	
		return ((ArrayList)DictNameValues()).contains(String.valueOf(sNameRequested));
	}
	/* ************************************************************* */
	public String getEnumByString(String eObjectValCode){
	    for(DictionnaireDefini eObjectVal : DictionnaireDefini.values()){
	        if(eObjectValCode == String.valueOf(eObjectVal.stringValue) && (eObjectVal.stringValue != null) && (String.valueOf(eObjectVal.stringValue).length() > 1) ) return eObjectVal.name();
	        if(eObjectValCode == String.valueOf(eObjectVal.intValue) && (eObjectVal.intValue != 0) ) return eObjectVal.name();
	    }
	    return null;
	}
	/* ************************************************************* */
	@Override
	public String toString() {
		 for(DictionnaireDefini eObjectVal : DictionnaireDefini.values()){
	         if(stringValue == eObjectVal.stringValue) return eObjectVal.name();
	     }
		 return "";
	}
}
