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
import org.dokuwikimobile.util.HashUtil;
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

		List<String> wikiList = new ArrayList<String>();

		if(wikisString.length() > 0) {
			String[] wiki = wikisString.split(WIKILIST_SEPARATOR);
			wikiList.addAll(Arrays.asList(wiki));
		}
		
		return wikiList;
		
	}

	private static void saveWikiHashes(List<String> wikis) {
		
		Editor editor = getPrefs().edit();

		String wikisString = StringUtil.join(wikis, WIKILIST_SEPARATOR);

		editor.putString(WIKILIST, wikisString);

		editor.commit();
		
	}

	/**
	 * Read the data of a specific dokuwiki identified by its md5hash from the 
	 * preferences. If the dokuwiki could not be generated, null is returned.
	 * 
	 * @param hash The md5hash of the dokuwiki (its xmlrpc url).
	 * @return The dokuwiki or null in case of errors.
	 */
	private static Dokuwiki getByHash(String hash) {
		
		SharedPreferences prefs = getPrefs();

		try {
			URL url = new URL(prefs.getString(hash + WIKI_URL_SUFFIX, ""));
			return new Dokuwiki(url);
		} catch (MalformedURLException ex) {
			Log.e(DokuwikiApplication.LOGGER_NAME, "URL of wiki '" + hash + "' is not a valid URL.");
			deleteWiki(hash);
			return null;
		}

	}
	
	/**
	 * Deletes a wiki from the list of stored wikis.
	 * 
	 * @param wiki The wiki to be deleted from list.
	 */
	public static void deleteWiki(Dokuwiki dokuwiki) {
		deleteWiki(dokuwiki.getMd5hash());
	}
	
	/**
	 * Deletes a wiki from the list of stored wikis.
	 * 
	 * @param wiki The hash of the wiki to be deleted from list.
	 */
	private static void deleteWiki(String hash) {

		// TODO: Should also delete DOkuwikiManager and all data

		Editor editor = getPrefs().edit();

		String[] allWikiPrefs = new String[]{ WIKI_URL_SUFFIX };

		// Delete all prefs related to this wiki
		for(String pref : allWikiPrefs) {
			editor.remove(hash + pref);
		}

		editor.commit();

		// Delete the wiki from the list of wikis
		List<String> wikiHashes = getWikiHashes();
		wikiHashes.remove(hash);
		saveWikiHashes(wikiHashes);
		
	}

	public static Dokuwiki add(String wikiUrl) throws MalformedURLException {
	
		URL url = new URL(wikiUrl);

		// Only add the wiki, if there is no wiki with that url in the list
		Dokuwiki dw = getByHash(HashUtil.hash(wikiUrl, HashUtil.MD5));

		if(dw != null)
			return dw;
		
		dw = new Dokuwiki(url);

		List<String> wikiHashed = getWikiHashes();
		wikiHashed.add(dw.getMd5hash());
		saveWikiHashes(wikiHashed);

		Editor edit = getPrefs().edit();
		edit.putString(dw.getMd5hash() + WIKI_URL_SUFFIX, dw.url.toString());
		edit.commit();

		return dw;
		
	}

	/**
	 * Returns the dokuwiki for a specific dokuwiki. The dokuwiki is identified 
	 * by the md5 hash of its xmlrpc URL.
	 * 
	 * @param wikiHash The md5hash of the wiki (its xmlrpc URL).
	 * @return The dokuwiki or null, if an error occurred.
	 */
	public static Dokuwiki get(String wikiHash) {

		List<String> wikis = getWikiHashes();

		if(!wikis.contains(wikiHash)) {
			return null;
		}

		return getByHash(wikiHash);
		
	}
	
	/**
	 * Get a list of all stored dokuwikis. This list contains a link to all dokuwikis
	 * that the user added to the application.
	 * 
	 * @return List of all stored dokuwikis.
	 */
	public static List<Dokuwiki> getAll() {

		List<String> wikiHashes = getWikiHashes();

		List<Dokuwiki> wikis = new ArrayList<Dokuwiki>(wikiHashes.size());

		// Read the settings of each dokuwiki listed in the WIKILIST preferences
		for(String wikiHash : wikiHashes) {
			
			wikis.add(getByHash(wikiHash));

		}

		return wikis;

	}
	
	private URL url;
	private String md5hash;
	
	private Dokuwiki(URL url) { 
		this.url = url;
		this.md5hash = HashUtil.hash(url.toString(), HashUtil.MD5);
	}

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
