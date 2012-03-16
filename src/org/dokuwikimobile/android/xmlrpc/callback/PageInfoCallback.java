package org.dokuwikimobile.android.xmlrpc.callback;

import org.dokuwikimobile.android.content.PageInfo;

/**
 *
 * @author Tim Roes
 */
public interface PageInfoCallback extends ErrorCallback {

	public void onPageInfoLoaded(PageInfo info, long id);
	
}
