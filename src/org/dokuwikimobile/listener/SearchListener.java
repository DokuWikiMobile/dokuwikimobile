package org.dokuwikimobile.listener;

import org.dokuwikimobile.model.SearchResult;
import java.util.List;

/**
 *
 * @author timroes
 */
public interface SearchListener extends ErrorListener {

	public void onSearchResults(List<SearchResult> pages, long id);
	
}