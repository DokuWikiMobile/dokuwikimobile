package de.timroes.dokuapp.cache;

import android.content.Context;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;
import de.timroes.dokuapp.content.Attachment;
import de.timroes.dokuapp.content.Page;
import de.timroes.dokuapp.content.PageInfo;
import de.timroes.dokuapp.content.SearchResult;
import de.timroes.dokuapp.services.PageLoadedListener;
import de.timroes.dokuapp.content.DokuwikiUrl;
import de.timroes.dokuapp.services.LoadingListener;
import de.timroes.dokuapp.xmlrpc.DokuwikiXMLRPCClient;
import de.timroes.dokuapp.xmlrpc.DokuwikiXMLRPCClient.Canceler;
import de.timroes.dokuapp.xmlrpc.callback.AttachmentCallback;
import de.timroes.dokuapp.xmlrpc.callback.ErrorCallback;
import de.timroes.dokuapp.xmlrpc.callback.PageHtmlCallback;
import de.timroes.dokuapp.xmlrpc.callback.PageInfoCallback;
import de.timroes.dokuapp.xmlrpc.callback.SearchCallback;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author Tim Roes
 */
public class CacheManager implements PageInfoCallback, PageHtmlCallback, SearchCallback,
		AttachmentCallback {
	
	private Cache cache;

	private HashMap<Long,CallbackPair> callbacks = new HashMap<Long,CallbackPair>();
	private HashMap<Long,Long> dependentCallbacks = new HashMap<Long, Long>();

	private HashMap<String,PageInfo> tmpPageInfos = new HashMap<String, PageInfo>();
	
	private CacheStrategy strategy = new NoCacheStrategy();
	
	private Context context;
	private DokuwikiXMLRPCClient client;


	public CacheManager(Context context, DokuwikiXMLRPCClient client) {
		cache = new Cache(context.getCacheDir().getAbsolutePath());
		this.context = context;
		this.client = client;
	}

	private void put(Long id, LoadingListener listener, ErrorCallback callback) {
		callbacks.put(id, new CallbackPair(listener, callback));
	}

	public long getPage(PageLoadedListener listener, LoadingListener loading, String pagename) {
		
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

	public Canceler search(SearchCallback callback, LoadingListener loading, String query) {
			
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
		loading.startLoading(canc);
		
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
	}

	public void onPageHtml(String pagename, String html, long id) {
		Page p = new Page(cache, html, tmpPageInfos.remove(pagename));

		// Page should be cached
		if(strategy.cachePages()) {
			cache.savePage(p);
		}
		
		for(DokuwikiUrl a : p.getLinkedAttachments()) {
			client.getAttachment(this, a.id);
		}

		CallbackPair pair = removeCallback(id);
		pair.loading.endLoading();
		((PageLoadedListener)pair.callback).onPageLoaded(p);
	}

	public void onSearchResults(List<SearchResult> pages, long id) {
		CallbackPair pair = removeCallback(id);
		pair.loading.endLoading();
		((SearchCallback)pair.callback).onSearchResults(pages, id);
	}

	public void onAttachmentLoaded(Attachment att, long id) {
		cache.saveAttachment(att);
		// TODO: notify
	}

	public void onError(XMLRPCException error, long id) {
		removeCallback(id).callback.onError(error, id);
	}

	public void onServerError(XMLRPCServerException error, long id) {
		System.err.println(error);
		removeCallback(id).callback.onServerError(error, id);
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
		dependentCallbacks.put(canceler.getId(), origId);
	}
	
	/**
	 * Get a callback pair by the id of the call.
	 * If the call was a dependent call, the callback pair of the original
	 * call will be returned.
	 * 
	 * @param id The id of the call.
	 * @return The callback pair related to the call id.
	 */
	private CallbackPair getCallback(long id) {
		CallbackPair pair = callbacks.get(id);
		
		// If no callback was found with that id, search in the list of
		// dependent callbacks.
		if(pair == null) {
			Long origCall = dependentCallbacks.get(id);
			if(origCall != null) {
				pair = callbacks.get(origCall);
			}
		}

		return pair;
	}

	/**
	 * Remove a callback by its id. If the id was a dependent call, this
	 * will remove the original callback pair and all the dependent calls
	 * of it.
	 * 
	 * @param id The i of the call.
	 * @return The callback pair related to the call id.
	 */
	private CallbackPair removeCallback(long id) {
		Long origId = id;
		CallbackPair callback = callbacks.remove(id);

		if(callback == null) {
			origId = dependentCallbacks.remove(id);
			// If no original id has been found, callback doesnt exist
			if(origId == null) {
				return null;
			}
			callback = callbacks.remove(origId);
		}

		// Remove all dependent calls
		for(Entry<Long,Long> dependentCall : dependentCallbacks.entrySet()) {
			if(dependentCall.getValue().longValue() == origId.longValue()) {
				dependentCallbacks.remove(dependentCall.getKey());
			}
		}
		
		return callback;
	}

	private class CallbackPair {

		public LoadingListener loading;
		public ErrorCallback callback;

		public CallbackPair(LoadingListener loading, ErrorCallback callback) {
			this.loading = loading;
			this.callback = callback;
		}
		
	}

}
