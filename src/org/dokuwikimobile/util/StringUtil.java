package org.dokuwikimobile.util;

import java.util.Iterator;

/**
 *
 * @author Tim Roes <mail@timroes.de>
 */
public class StringUtil {
	
	private final static String[] SHORT_UNITS = new String[]{ "Bytes","kB","MB","GB","TB" };
	
	/**
	 * Prints out a file size in a human readable format. This will return the
	 * file size with a trailing size unit and rounded to the given decimal places.
	 * 
	 * @param filesize The file size given in bytes.
	 * @param decimal The decimal places that should be shown.
	 * @return The {@link String} representing the file size in a human readable format.
	 */
	public static String humanReadable(int filesize, int decimal) {
		
		if(filesize == 0) {
			return "0 " + SHORT_UNITS[0];
		}

		int unit = (int)(Math.log(filesize)/Math.log(1024));
		double rounded = Math.round(filesize / Math.pow(1024, unit) * Math.pow(10, decimal))
				/ Math.pow(10, decimal);
		return String.valueOf(rounded) + " " + SHORT_UNITS[unit];

	}
	
	/**
	 * Joins a {@link String}-Array with a given sequence of characters into one
	 * string.
	 * 
	 * @param stringArray The {@link String}-Array which elements should be joined.
	 * @param joinChar The {@link String} to used for joining the elements.
	 * @return The concatenated {@link String}.
	 */
	public static String join(Iterable<String> stringArray, String joinChar) {

		StringBuilder builder = new StringBuilder();

		String string;
		for (Iterator<String> it = stringArray.iterator(); it.hasNext();) {
			string = it.next();
			builder.append(string);
			// Append joining char, if more elements exists
			if(it.hasNext()) {
				builder.append(joinChar);
			}
		}

		return builder.toString();
		
	}

	/**
	 * Checks whether the given {@link String} is null or empty.
	 * 
	 * @param str The {@link String} that is checked.
	 * @return Whether the {@link String} is null or empty.
	 */
	public static boolean isNullOrEmpty(String str) {
		return str == null || str.isEmpty();
	}
	
}
