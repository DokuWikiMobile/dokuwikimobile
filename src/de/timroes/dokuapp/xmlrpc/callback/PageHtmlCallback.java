package de.timroes.dokuapp.xmlrpc.callback;

/**
 *
 * @author timroes
 */
public interface PageHtmlCallback extends ErrorCallback {
	
	public void onPageHtml(String html, long id);
	
}
