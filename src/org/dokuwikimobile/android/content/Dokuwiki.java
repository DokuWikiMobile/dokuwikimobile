package org.dokuwikimobile.android.content;

import java.net.MalformedURLException;
import java.net.URL;
import org.dokuwikimobile.android.util.HashUtil;

/**
 * Manages a DokuWiki
 *  
 * @author Daniel Baelz <daniel.baelz@lysandor.de>
 */
public class Dokuwiki {
	
	URL url;
	String md5hash;
	
    public Dokuwiki(String address) throws MalformedURLException {
		url = new URL(address);
		md5hash = HashUtil.hash(address, HashUtil.MD5);
    }

	/**
	 * Returns the URL of the DokuWiki
	 * @return The URL of the DokuWiki. 
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * Returns the md5hash as a string.
	 * @return The md5 hash of the DokuWiki
	 */
	public String getMd5hash() {
		return md5hash;
	}
	
}
