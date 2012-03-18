package org.dokuwikimobile.service;

import org.dokuwikimobile.model.Page;
import org.dokuwikimobile.listener.ErrorListener;

/**
 *
 * @author Tim Roes
 */
public interface PageLoadedListener extends ErrorListener {

	public void onPageLoaded(Page page);

}