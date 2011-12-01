package de.timroes.dokuapp.xmlrpc.callback;

import de.timroes.dokuapp.content.SearchResult;
import java.util.List;

/**
 *
 * @author timroes
 */
public interface SearchCallback extends ErrorCallback {

	public void onSearchResults(List<SearchResult> pages, long id);
	
}