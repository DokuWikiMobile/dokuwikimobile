package de.timroes.dokuapp.manager;

import android.content.Context;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;
import de.timroes.dokuapp.cache.Cache;
import de.timroes.dokuapp.cache.CacheStrategy;
import de.timroes.dokuapp.cache.NoCacheStrategy;
import de.timroes.dokuapp.content.Page;
import de.timroes.dokuapp.content.PageInfo;
import de.timroes.dokuapp.services.PageLoadedListener;
import de.timroes.dokuapp.xmlrpc.DokuwikiXMLRPCClient;
import de.timroes.dokuapp.xmlrpc.callback.PageHtmlCallback;
import de.timroes.dokuapp.xmlrpc.callback.PageInfoCallback;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tim Roes
 */
public class CacheManager implements PageInfoCallback, PageHtmlCallback {
	
	private Cache cache;
	private Map<String,PageLoadedListener> listeners = new HashMap<String, PageLoadedListener>();
	
	private CacheStrategy strategy = new NoCacheStrategy();
	
	private Map<String,PageInfo> tmpPageInfos = new HashMap<String, PageInfo>();

	private Context context;
	private DokuwikiXMLRPCClient client;

	public CacheManager(Context context, DokuwikiXMLRPCClient client) {
		cache = new Cache(context.getCacheDir().getAbsolutePath());
		this.context = context;
		this.client = client;
//		cache = loadCacheFromDisk();
	}

	public long getPage(PageLoadedListener listener, String pagename) {
		
		// If cache strategy allows showing cached pages, show cached page
		if(strategy.showCachedPage()) {
			Page p = cache.getPage(pagename);
			if(p != null) {
				listener.onPageLoaded(p);
			}
		}

		listeners.put(pagename, listener);
		client.getPageInfo(this, pagename);
		return 0;
		
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
		synchronized(this) {
		try {
			wait(3000);
		} catch (InterruptedException ex) {
			Logger.getLogger(CacheManager.class.getName()).log(Level.SEVERE, null, ex);
		}
		}
		client.getPageHTML(this, info.getName());
	}

	public void onPageHtml(String pagename, String html, long id) {
		Page p = new Page(html, tmpPageInfos.remove(pagename));

		// Page should be cached
		if(strategy.cachePages()) {
			cache.addPage(p);
		}

		listeners.remove(pagename).onPageLoaded(p);
	}

	public void onError(XMLRPCException error, long id) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void onServerError(XMLRPCServerException error, long id) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
