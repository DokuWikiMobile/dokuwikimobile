package org.dokuwikimobile.listener;

/**
 *
 * @author timroes
 */
public interface PageHtmlListener extends CancelableListener {
	
	public void onPageHtml(String pagename, String html, long id);
	
}
