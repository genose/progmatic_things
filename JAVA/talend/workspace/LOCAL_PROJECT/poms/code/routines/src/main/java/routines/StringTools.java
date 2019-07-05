package routines;

/*
 * user specification: the function's comment should contain keys as follows: 1. write about the function's comment.but
 * it must be before the "{talendTypes}" key.
 * 
 * 2. {talendTypes} 's value must be talend Type, it is required . its value should be one of: String, char | Character,
 * long | Long, int | Integer, boolean | Boolean, byte | Byte, Date, double | Double, float | Float, Object, short |
 * Short
 * 
 * 3. {Category} define a category for the Function. it is required. its value is user-defined .
 * 
 * 4. {param} 's format is: {param} <type>[(<default value or closed list values>)] <name>[ : <comment>]
 * 
 * <type> 's value should be one of: string, int, list, double, object, boolean, long, char, date. <name>'s value is the
 * Function's parameter name. the {param} is optional. so if you the Function without the parameters. the {param} don't
 * added. you can have many parameters for the Function.
 * 
 * 5. {example} gives a example for the Function. it is optional.
 */
public class StringTools {

	/**
	 * helloExample: not return value, only print "hello" + message.
	 * 
	 * 
	 * {talendTypes} String
	 * 
	 * {Category} User Defined
	 * 
	 * {param} string("world") input: The string need to be printed.
	 * 
	 * {example} helloExemple("world") # hello world !.
	 */
	public static void helloExample(String message) {
		if (message == null) {
			message = "World"; //$NON-NLS-1$
		}
		System.out.println("Hello " + message + " !"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * {talendTypes} String {Category} User Defined
	 * 
	 * @param sValueArg
	 * @return
	 */
	public static String stringValueOrNull(String sValueArg) {

		if (sValueArg != null) {
			String sValueReturn = String.valueOf(sValueArg).trim();
			if (sValueReturn.compareToIgnoreCase("NULL") == 0) {
				return null;
			} else {
				return sValueReturn;
			}
		}
		return null;
	}

	public static Double stringToFloat(String sValueArg) {

		if ((sValueArg == null) || sValueArg.trim().isEmpty()) {
			return Double.parseDouble("0.0");
		}
		String sParseableValue = String.valueOf(sValueArg).trim().replace(" ", "");
 
		if( sParseableValue.matches("^[0-9|\\.|\\,]") ) {
			return Double.parseDouble("0.0");
		}
		
		if (sParseableValue.lastIndexOf(",") != (-1)) {
			sParseableValue = sParseableValue.replace(",", ".");
		} else if (sParseableValue.lastIndexOf(",") != (-1)) {
			sParseableValue = sParseableValue.replace(",", ".");
		}

		return Double.parseDouble(sParseableValue);

	}
}
