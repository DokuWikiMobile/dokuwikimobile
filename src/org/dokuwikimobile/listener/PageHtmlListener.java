package org.dokuwikimobile.listener;

/**
 *
 * @author Tim Roes <mail@timroes.de>
 */
public interface PageHtmlListener extends CancelableListener {
	
	public void onPageHtml(String html, long id);
	
}
