package org.genose.java.implementation.javafx.applicationtools;

public enum JFXFILETYPE {

	FXML_TYPE(".fxml");
	private String value;
	private static java.util.HashMap<Object, Object> map = new java.util.HashMap<>();

	private JFXFILETYPE(String value) {
		this.value = value;
	}

	static {
		for (JFXFILETYPE addrType : JFXFILETYPE.values()) {
			map.put(addrType.value, addrType);
		}
	}

	public static JFXFILETYPE valueOf(int pageType) {
		return (JFXFILETYPE) map.get(pageType);
	}

	public String getValue() {
		return value;
	}

	public String getEnumByString(String code) {
		for (JFXFILETYPE e : JFXFILETYPE.values()) {
			if (code == String.valueOf(value))
				return e.name();
		}
		return null;
	}

	public String toString() {
		for (JFXFILETYPE e : JFXFILETYPE.values()) {
			if (value == e.value)
				return e.name();
		}
		return "";
	}

	
		

}
