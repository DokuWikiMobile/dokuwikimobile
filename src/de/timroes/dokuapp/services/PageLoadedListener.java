package de.timroes.dokuapp.services;

import de.timroes.dokuapp.content.Page;
import de.timroes.dokuapp.xmlrpc.callback.ErrorCallback;

/**
 *
 * @author Tim Roes
 */
public interface PageLoadedListener extends ErrorCallback {

	public void onPageLoaded(Page page, long id);

}