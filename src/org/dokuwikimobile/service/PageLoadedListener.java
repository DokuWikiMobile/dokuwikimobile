package org.dokuwikimobile.service;

import org.dokuwikimobile.model.Page;
import org.dokuwikimobile.xmlrpc.callback.ErrorCallback;

/**
 *
 * @author Tim Roes
 */
public interface PageLoadedListener extends ErrorCallback {

	public void onPageLoaded(Page page);

}