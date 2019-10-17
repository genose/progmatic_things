#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package main.java.${package}.outils;

import org.primefaces.component.datepicker.DatePicker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class OutilsDate
{

	private OutilsDate()
	{
	}

	private static DateTimeFormatter formatNumerique = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static DateTimeFormatter formatChaine = DateTimeFormatter.ofPattern("d MMMM yyy");

	public static LocalDate stringToDate(String date)
	{
		try
		{
			return LocalDate.parse(date, formatNumerique);
		}
		catch (Exception e)
		{

			return null;

		}
	}

	public static String dateToString(LocalDate date)
	{
		if (date == null)
			return "";
		return formatNumerique.format(date);

	}

	public static String dateToLitteral(LocalDate dateNaissance)
	{
		return formatChaine.format(dateNaissance);
	}

//	public static boolean comparerDates(DatePicker saisieDateDeNaissance)
//	{
//		int dateValide = saisieDateDeNaissance.getValue().compareTo(LocalDate.now());
//		boolean dateIsOk = false;
//		if (dateValide <= 0)
//		{
//			dateIsOk = true;
//		}
//		return dateIsOk;
//	}
}
