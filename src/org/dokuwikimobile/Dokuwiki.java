package org.dokuwikimobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.dokuwikimobile.util.StringUtil;

/**
 * Manages a DokuWiki
 *  
 * @author Daniel Baelz <daniel.baelz@lysandor.de>
 * @author Tim Roes <mail@timroes.de>
 */
public class Dokuwiki {
	
	private static final String WIKILIST_FILE = "wikis";

	private static final String WIKILIST = "wikis";
	private static final String WIKILIST_SEPARATOR = ";";

	private static final String WIKI_URL_SUFFIX = "-url";

	private static SharedPreferences getPrefs() {
		return DokuwikiApplication.getAppContext()
				.getSharedPreferences(WIKILIST_FILE, Context.MODE_PRIVATE);
	}
	
	private static List<String> getWikiHashes() {
		
		String wikisString = getPrefs().getString(WIKILIST, "");
		String[] wiki = wikisString.split(WIKILIST_SEPARATOR);

		return Arrays.asList(wiki);
		
	}

	/**
	 * Deletes a wiki from the list of stored wikis.
	 * 
	 * @param wiki The wiki to be deleted from list.
	 */
	public static void deleteWiki(Dokuwiki wiki) {

		Editor editor = getPrefs().edit();

		String[] allWikiPrefs = new String[]{ WIKI_URL_SUFFIX };

		// Delete all prefs related to this wiki
		for(String pref : allWikiPrefs) {
			editor.remove(wiki.getMd5hash() + pref);
		}

		// Delete the wiki from the list of 
		List<String> wikiHashes = getWikiHashes();
		wikiHashes.remove(wiki.getMd5hash());

		editor.putString(WIKILIST, StringUtil.join(wikiHashes, WIKILIST_SEPARATOR));
		editor.commit();
		
	}
	
	/**
	 * Get a list of all stored dokuwikis. This list contains a link to all dokuwikis
	 * that the user added to the application.
	 * 
	 * @return List of all stored dokuwikis.
	 */
	public static List<Dokuwiki> getAll() {

		SharedPreferences prefs = DokuwikiApplication.getAppContext()
				.getSharedPreferences(WIKILIST_FILE, Context.MODE_PRIVATE);

		List<String> wikiHashes = getWikiHashes();

		List<Dokuwiki> wikis = new ArrayList<Dokuwiki>(wikiHashes.size());

		// Read the settings of each dokuwiki listed in the WIKILIST preferences
		for(String wikiHash : wikiHashes) {
			
			Dokuwiki dw = new Dokuwiki();
			dw.md5hash = wikiHash;

			try {
				dw.url = new URL(prefs.getString(wikiHash + WIKI_URL_SUFFIX, ""));
			} catch (MalformedURLException ex) {
				// If URL is not valid, delete this wiki from list.
				Log.e(DokuwikiApplication.LOGGER_NAME, "URL of wiki '" + wikiHash + "' is not a valid URL.");
				deleteWiki(dw);
				continue;
			}

			wikis.add(dw);

		}

		return wikis;

	}
	
	private URL url;
	private String md5hash;
	
	private Dokuwiki() { }

	/**
	 * Returns the URL of the DokuWiki
	 * 
	 * @return The URL of the DokuWiki. 
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * Returns the md5hash as a string.
	 * 
	 * @return The md5 hash of the DokuWiki
	 */
	public String getMd5hash() {
		return md5hash;
	}
	
}
