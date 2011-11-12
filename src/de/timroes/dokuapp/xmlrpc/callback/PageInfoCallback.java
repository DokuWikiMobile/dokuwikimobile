package de.timroes.dokuapp.xmlrpc.callback;

import de.timroes.dokuapp.content.PageInfo;

/**
 *
 * @author Tim Roes
 */
public interface PageInfoCallback extends ErrorCallback {

	public void onPageInfoLoaded(PageInfo info, long id);
	
}
