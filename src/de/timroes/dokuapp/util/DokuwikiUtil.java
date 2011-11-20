package de.timroes.dokuapp.util;

/**
 *
 * @author Tim Roes
 */
public class DokuwikiUtil {
	
	public static DokuwikiUrl parseUrl(String url) {
		String[] parts = url.split("#");
		String anchor = (parts.length > 1) ? parts[1] : "";
		parts = parts[0].split("\\?");
		String query = (parts.length > 1) ? parts[1] : "";
		url = parts[0].replace('/', ':');

		return new DokuwikiUrl(url, anchor, query);
	}
	
}
