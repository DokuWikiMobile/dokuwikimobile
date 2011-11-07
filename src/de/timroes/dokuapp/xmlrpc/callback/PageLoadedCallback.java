package de.timroes.dokuapp.xmlrpc.callback;

/**
 *
 * @author Tim Roes
 */
public interface PageLoadedCallback extends ErrorCallback {

	public void onPageLoaded(String pageHtml, long id);

}
