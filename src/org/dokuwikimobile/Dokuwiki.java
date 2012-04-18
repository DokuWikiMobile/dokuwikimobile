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
import org.dokuwikimobile.listener.DokuwikiCreationListener;
import org.dokuwikimobile.listener.TitleListener;
import org.dokuwikimobile.listener.VersionListener;
import org.dokuwikimobile.util.HashUtil;
import org.dokuwikimobile.util.StringUtil;
import org.dokuwikimobile.xmlrpc.DokuwikiXMLRPCClient;
import org.dokuwikimobile.xmlrpc.DokuwikiXMLRPCClient.Canceler;
import org.dokuwikimobile.xmlrpc.ErrorCode;

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
	private static final String WIKI_TITLE_SUFFIX = "-title";

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
			String title = prefs.getString(hash + WIKI_TITLE_SUFFIX, "");
			return new Dokuwiki(title, url);
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

	private static Dokuwiki add(Dokuwiki dokuwiki) {
	
		if(getAll().contains(dokuwiki)) {
			return dokuwiki;
		}

		List<String> wikiHashed = getWikiHashes();
		wikiHashed.add(dokuwiki.getMd5hash());
		saveWikiHashes(wikiHashed);

		// Save all prefs of this wiki
		Editor edit = getPrefs().edit();
		edit.putString(dokuwiki.getMd5hash() + WIKI_URL_SUFFIX, dokuwiki.getUrl().toString());
		edit.putString(dokuwiki.getMd5hash() + WIKI_TITLE_SUFFIX, dokuwiki.getTitle());
		edit.commit();

		return dokuwiki;
		
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
	
	private String title;
	private URL url;
	private String md5hash;
	
	private Dokuwiki(String title, URL url) { 
		this.title = title;
		this.url = url;
		this.md5hash = HashUtil.hash(url.toString(), HashUtil.MD5);
	}

	/**
	 * Return the title of the DokuWiki.
	 * 
	 * @return The title of the DokuWiki.
	 */
	public String getTitle() {
		return title;
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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Dokuwiki other = (Dokuwiki) obj;
		if ((this.md5hash == null) ? (other.md5hash != null) : !this.md5hash.equals(other.md5hash)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 79 * hash + (this.md5hash != null ? this.md5hash.hashCode() : 0);
		return hash;
	}
	
	
	/**
	 * A DokuwikiCreator is responsible to check a new dokuwiki for compatibility with
	 * this app. If the DokuWiki is compatible, this class is responsible for generating
	 * an Dokuwiki class for it, that identifies the wiki, and fill it with the needed
	 * information, provided by the XMLRPC interface.
	 * 
	 * @author Tim Roes <mail@timroes.de>
	 */
	public static class Creator implements VersionListener, TitleListener {

		private DokuwikiCreationListener listener;
		private DokuwikiXMLRPCClient xmlrpcClient;

		/**
		 * Try to create a Dokuwiki instance of the given URL for the first time.
		 * This will check, if the URL points to a DokuWiki XMLRPC interface.
		 * If it does, it will check on the API version and read the title of the
		 * wiki to generate a new Dokuwiki class.
		 * 
		 * @param url The URL of the XML-RPC interface of the DokuWiki.
		 * @return The Dokuwiki, generated by that url.
		 */
		public void create(String url, DokuwikiCreationListener listener) throws MalformedURLException {

			this.listener = listener;

			URL wikiURL = new URL(url);

			xmlrpcClient = new DokuwikiXMLRPCClient(wikiURL);
			
			Canceler canceler = xmlrpcClient.getVersion(this);

			listener.onStartLoading(canceler, canceler.getId());

		}

		public void onVersion(int version, long id) {
			
			// We require at least API version 7 to work properly.
			if(version < 7) {
				listener.onError(ErrorCode.VERSION_ERROR, id);
			} else {
				// Check the title of the DokuWiki.
				Canceler canceler = xmlrpcClient.getTitel(this);
				listener.onStartLoading(canceler, canceler.getId());
			}

		}

		public void onTitle(String title, long id) {

			Dokuwiki dw = Dokuwiki.add(new Dokuwiki(title, xmlrpcClient.getURL()));
			listener.onDokuwikiCreated(dw);
			
		}
		
		public void onStartLoading(Canceler cancel, long id) {
		}

		public void onEndLoading(long id) {
		}

		public void onError(ErrorCode error, long id) {
			listener.onError(error, id);
		}


	}

}
