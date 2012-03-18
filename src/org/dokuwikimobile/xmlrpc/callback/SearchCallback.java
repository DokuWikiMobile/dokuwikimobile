package org.dokuwikimobile.xmlrpc.callback;

import org.dokuwikimobile.model.SearchResult;
import java.util.List;

/**
 *
 * @author timroes
 */
public interface SearchCallback extends ErrorCallback {

	public void onSearchResults(List<SearchResult> pages, long id);
	
}