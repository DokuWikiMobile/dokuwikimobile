package org.dokuwikimobile.xmlrpc.callback;

import org.dokuwikimobile.model.PageInfo;

/**
 *
 * @author Tim Roes
 */
public interface PageInfoCallback extends ErrorCallback {

	public void onPageInfoLoaded(PageInfo info, long id);
	
}
