package org.dokuwikimobile.listener;

import org.dokuwikimobile.model.PageInfo;

/**
 *
 * @author Tim Roes
 */
public interface PageInfoListener extends CancelableListener {

	public void onPageInfoLoaded(PageInfo info, long id);
	
}
