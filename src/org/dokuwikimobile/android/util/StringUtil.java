package org.dokuwikimobile.android.util;

/**
 *
 * @author Tim Roes
 */
public class StringUtil {
	
	private final static String[] SHORT_UNITS = new String[]{ "Bytes","kB","MB","GB","TB" };
	
	public static String humanReadable(int filesize, int decimal) {
		
		if(filesize == 0) {
			return "0 " + SHORT_UNITS[0];
		}

		int unit = (int)(Math.log(filesize)/Math.log(1024));
		double rounded = Math.round(filesize / Math.pow(1024, unit) * Math.pow(10, decimal))
				/ Math.pow(10, decimal);
		return String.valueOf(rounded) + " " + SHORT_UNITS[unit];

	}
	
}
