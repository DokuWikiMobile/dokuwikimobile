package org.dokuwikimobile.listener;

import java.util.List;
import org.dokuwikimobile.model.SearchResult;

/**
 *
 * @author timroes
 */
public interface SearchListener extends CancelableListener {

	public void onSearchResults(List<SearchResult> pages, long id);
	
}