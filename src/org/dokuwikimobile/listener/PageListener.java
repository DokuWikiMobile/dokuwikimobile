package org.dokuwikimobile.listener;

import org.dokuwikimobile.model.Page;

/**
 *
 * @author Tim Roes
 */
public interface PageListener extends CancelableListener {

	public void onPageLoaded(Page page);

}