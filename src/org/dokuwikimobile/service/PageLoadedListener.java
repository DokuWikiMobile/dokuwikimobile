package org.dokuwikimobile.service;

import org.dokuwikimobile.listener.CancelableListener;
import org.dokuwikimobile.model.Page;

/**
 *
 * @author Tim Roes
 */
public interface PageLoadedListener extends CancelableListener {

	public void onPageLoaded(Page page);

}