package org.dokuwikimobile.cache;

import android.content.Context;
import de.timroes.axmlrpc.XMLRPCServerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.dokuwikimobile.listener.*;
import org.dokuwikimobile.model.*;
import org.dokuwikimobile.xmlrpc.DokuwikiXMLRPCClient;
import org.dokuwikimobile.xmlrpc.DokuwikiXMLRPCClient.Canceler;
import org.dokuwikimobile.xmlrpc.ErrorCode;

/**
 * TODO: This class will be removed and all its functionality will be in the DokuwikiManager.
 * @author Tim Roes
 */
public class CacheManager implements PageInfoListener, PageHtmlListener, SearchListener,
		AttachmentListener {
	
	private Cache cache;

	private HashMap<Long,CallbackPair> callbacks = new HashMap<Long,CallbackPair>();

	private HashMap<String,PageInfo> tmpPageInfos = new HashMap<String, PageInfo>();
	
	private CacheStrategy strategy = new NoCacheStrategy();
	
	private Context context;
	private DokuwikiXMLRPCClient client;


	public CacheManager(Context context, DokuwikiXMLRPCClient client) {
		//cache = new Cache(context.getCacheDir().getAbsolutePath());
		this.context = context;
		this.client = client;
	}

	// object = errorcallback
	private void put(Long id, CancelableListener listener, Object callback) {
		//callbacks.put(id, new CallbackPair(listener, callback));
	}

	public long getPage(PageListener listener, CancelableListener loading, String pagename) {
		
		// If cache strategy allows showing cached pages, show cached page
		if(strategy.showCachedPage()) {
			Page p = cache.getPage(pagename);
			if(p != null) {
				listener.onPageLoaded(p);
			}
		}

		Canceler canc = client.getPageInfo(this, pagename);
		put(canc.getId(), loading, listener);
		return canc.getId();
		
	}

	public Canceler search(SearchListener callback, CancelableListener loading, String query) {
			
		List<SearchResult> list = new ArrayList<SearchResult>();
		list.add(new SearchResult(100, 10, 1, "welt", "Die Welt blabla"));
		list.add(new SearchResult(100, 10, 1, "welt", "Die Welt blabla"));

		// TODO: Do search in cache
		callback.onSearchResults(list, 0);

		// Start online search
		Canceler canc = client.search(this, query);
		
		// Save the listeners
		put(canc.getId(), loading, callback);

		// Notifiy that we started loading
		loading.onStartLoading(canc, canc.getId());
		
		return canc;
		
	}

	/**
	 * Get the current needed size of the cache.
	 * 
	 * @return The size of all cache files in bytes.
	 */
	public int getCacheSize() {
		return cache.getCacheSize();
	}

	/**
	 * Clear the cache. Deletes all pages and media from the cache directory.
	 */
	public void clearCache() {
		cache.clear();
	}

	public void onPageInfoLoaded(PageInfo info, long id) {
		tmpPageInfos.put(info.getName(), info);
		Canceler c = client.getPageHTML(this, info.getName());
		// Create a depedent call for loading the html of the page
		putDependent(id, c);
		// Remove this call, since it is finished now
		callbacks.remove(id);
	}

	public void onPageHtml(String pagename, String html, long id) {
		Page p = new Page(cache, html, tmpPageInfos.remove(pagename));

		// Page should be cached
		if(strategy.cachePages()) {
			cache.savePage(p);
		}
		
		for(DokuwikiUrl a : p.getLinkedAttachments()) {
			Canceler canc = client.getAttachment(this, a.id);
			putDependent(id, canc);
		}

		CallbackPair pair = callbacks.remove(id);
		pair.loading.onEndLoading(id);
		//((PageListener)pair.callback).onPageLoaded(p);
	}

	public void onSearchResults(List<SearchResult> pages, long id) {
		CallbackPair pair = callbacks.remove(id);
		pair.loading.onEndLoading(id);
		//((SearchListener)pair.callback).onSearchResults(pages, id);
	}

	public void onAttachmentLoaded(Attachment att, long id) {
		cache.saveAttachment(att);
		//((PageLoadedListener)callbacks.remove(id)).onPageLoaded(null);
	}

	public void onError(ErrorCode error, String errorMessage, long id) {
		//callbacks.remove(id).callback.onError(error, id);
	}

	public void onServerError(XMLRPCServerException error, long id) {
		System.err.println(error);
		//callbacks.remove(id).callback.onServerError(error, id);
	}

	/**
	 * Save a dependent call. This will store the id of the parent call
	 * and the link to the new call, so that the getCallback method will be 
	 * able to find the original call.
	 * 
	 * @param origId The id of the original call made outside the CacheManager.
	 * @param canceler The canceler of the internal call made by this class.
	 */
	private void putDependent(long origId, Canceler canceler) {
		callbacks.put(canceler.getId(), callbacks.get(origId));
	}

	public void onStartLoading(Canceler cancel, long id) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void onEndLoading(long id) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	private class CallbackPair {

		public CancelableListener loading;
		//public ErrorListener callback;

		// object = errorListener
		public CallbackPair(CancelableListener loading, Object callback) {
			this.loading = loading;
//			this.callback = callback;
		}
		
	}

}