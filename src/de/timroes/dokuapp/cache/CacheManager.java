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
import java.util.Map;

/**
 *
 * @author Tim Roes
 */
public class CacheManager implements PageInfoCallback, PageHtmlCallback, SearchCallback,
		AttachmentCallback {
	
	private Cache cache;
	private Map<String,PageLoadedListener> listeners = new HashMap<String, PageLoadedListener>();

	private HashMap<Long,CallbackPair> callbacks = new HashMap<Long,CallbackPair>();
	
	private CacheStrategy strategy = new NoCacheStrategy();
	
	private Map<String,PageInfo> tmpPageInfos = new HashMap<String, PageInfo>();

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
		list.add(new SearchResult(100, 10, 1, "welt", "Die Welt blabla"));
		list.add(new SearchResult(100, 10, 1, "welt", "Die Welt blabla"));
		list.add(new SearchResult(100, 10, 1, "welt", "Die Welt blabla"));
		list.add(new SearchResult(100, 10, 1, "welt", "Die Welt blabla"));
		list.add(new SearchResult(100, 10, 1, "welt", "Die Welt blabla"));
		list.add(new SearchResult(100, 10, 1, "welt", "Die Welt blabla"));
		list.add(new SearchResult(100, 10, 1, "welt", "Die Welt blabla"));
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
		client.getPageHTML(this, info.getName());
	}

	public void onPageHtml(String pagename, String html, long id) {
		Page p = new Page(cache, html, tmpPageInfos.remove(pagename));

		// Page should be cached
		if(strategy.cachePages()) {
			cache.addPage(p);
		}
		
		for(DokuwikiUrl a : p.getLinkedAttachments()) {
			client.getAttachment(this, a.id);
		}

		//callbacks.remove(id).onPageLoaded(p);
	}

	public void onSearchResults(List<SearchResult> pages, long id) {
		CallbackPair pair = callbacks.remove(id);
		((SearchCallback)pair.callback).onSearchResults(pages, id);
		pair.loading.endLoading();
	}

	public void onAttachmentLoaded(Attachment att, long id) {
		cache.addAttachment(att);
	}

	public void onError(XMLRPCException error, long id) {
		callbacks.remove(id).callback.onError(error, id);
	}

	public void onServerError(XMLRPCServerException error, long id) {
		System.err.println(error);
		callbacks.remove(id).callback.onServerError(error, id);
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
