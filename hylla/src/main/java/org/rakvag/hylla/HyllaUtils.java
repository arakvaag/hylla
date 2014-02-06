package org.rakvag.hylla;

public class HyllaUtils {

	public static String formaterStrengTilMaksLengde(String streng, int maksLengde) {
		if (maksLengde < 3)
			throw new IllegalArgumentException("maksLengde må være minst 3");
		
		if (streng == null)
			return "";
		
		if (streng.length() <= maksLengde)
			return streng;
		
		return streng.substring(0,  maksLengde -3) + "...";
	}
}
