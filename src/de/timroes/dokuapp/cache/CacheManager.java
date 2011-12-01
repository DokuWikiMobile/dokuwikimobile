package de.timroes.dokuapp.cache;

import android.content.Context;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;
import de.timroes.dokuapp.content.Attachment;
import de.timroes.dokuapp.content.Page;
import de.timroes.dokuapp.content.PageInfo;
import de.timroes.dokuapp.services.PageLoadedListener;
import de.timroes.dokuapp.content.DokuwikiUrl;
import de.timroes.dokuapp.xmlrpc.DokuwikiXMLRPCClient;
import de.timroes.dokuapp.xmlrpc.callback.AttachmentCallback;
import de.timroes.dokuapp.xmlrpc.callback.ErrorCallback;
import de.timroes.dokuapp.xmlrpc.callback.PageHtmlCallback;
import de.timroes.dokuapp.xmlrpc.callback.PageInfoCallback;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Tim Roes
 */
public class CacheManager implements PageInfoCallback, PageHtmlCallback, AttachmentCallback {
	
	private Cache cache;
	private Map<String,PageLoadedListener> listeners = new HashMap<String, PageLoadedListener>();
	private Map<Long,ErrorCallback> tmpListeners = new HashMap<Long, ErrorCallback>();
	
	private CacheStrategy strategy = new NoCacheStrategy();
	
	private Map<String,PageInfo> tmpPageInfos = new HashMap<String, PageInfo>();

	private Context context;
	private DokuwikiXMLRPCClient client;

	public CacheManager(Context context, DokuwikiXMLRPCClient client) {
		cache = new Cache(context.getCacheDir().getAbsolutePath());
		this.context = context;
		this.client = client;
	}

	public long getPage(PageLoadedListener listener, String pagename) {
		
		// If cache strategy allows showing cached pages, show cached page
		if(strategy.showCachedPage()) {
			Page p = cache.getPage(pagename);
			if(p != null) {
				listener.onPageLoaded(p);
			}
		}

		// TODO: two ways to save listener = bad
		listeners.put(pagename, listener);
		long id = client.getPageInfo(this, pagename);
		tmpListeners.put(id, listener);
		return id;
		
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

		listeners.remove(pagename).onPageLoaded(p);
		tmpListeners.remove(id);
	}

	public void onAttachmentLoaded(Attachment att, long id) {
		cache.addAttachment(att);
	}

	public void onError(XMLRPCException error, long id) {
		tmpListeners.remove(id).onError(error, id);
	}

	public void onServerError(XMLRPCServerException error, long id) {
		System.out.println(error);
		tmpListeners.remove(id).onServerError(error, id);
	}

}
