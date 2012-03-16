package org.dokuwikimobile.android.services;

import org.dokuwikimobile.android.content.Page;
import org.dokuwikimobile.android.xmlrpc.callback.ErrorCallback;

/**
 *
 * @author Tim Roes
 */
public interface PageLoadedListener extends ErrorCallback {

	public void onPageLoaded(Page page);

}