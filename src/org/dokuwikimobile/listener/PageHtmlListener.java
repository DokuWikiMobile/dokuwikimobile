package org.dokuwikimobile.listener;

/**
 *
 * @author timroes
 */
public interface PageHtmlListener extends ErrorListener {
	
	public void onPageHtml(String pagename, String html, long id);
	
}
