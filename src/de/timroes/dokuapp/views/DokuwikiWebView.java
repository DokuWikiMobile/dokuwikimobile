package de.timroes.dokuapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import de.timroes.dokuapp.content.Page;
import de.timroes.dokuapp.util.DokuwikiUrl;
import de.timroes.dokuapp.util.DokuwikiUtil;
import java.util.Stack;

/**
 *
 * @author Tim Roes
 */
public class DokuwikiWebView extends WebView {

	private ScrollListener scrollListener;
	private LinkLoadListener linkListener;

	private Stack<Page> history = new Stack<Page>();
	private Stack<Page> future = new Stack<Page>();

	private Page currentPage;
	
	public DokuwikiWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public DokuwikiWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public DokuwikiWebView(Context context) {
		super(context);
	}

	@Override
	public boolean canGoBack() {
		return canGoBackOrForward(-1);
	}

	@Override
	public boolean canGoBackOrForward(int steps) {

		if(steps < 0) {
			return history.size() >= (-steps);
		} else if(steps > 0) {
			return future.size() >= steps;
		} 
		
		return true;

	}

	@Override
	public boolean canGoForward() {
		return canGoBackOrForward(1);
	}

	@Override
	public void goBack() {
		goBackOrForward(-1);
	}

	@Override
	public void goBackOrForward(int steps) {
		Stack<Page> from, to;
		
		if(steps < 0) {
			from = history;	
			to = future;
		} else if(steps > 0) {
			from = future;
			to = history;
		} else {
			return;
		}

		for(int i = 0; i < Math.abs(steps); i++) {
			to.push(currentPage);
			currentPage = from.pop();
		}

		// Load page, but dont modify history again
		loadPage(currentPage, false);
	}

	@Override
	public void goForward() {
		goBackOrForward(1);
	}

	@Override
	protected void onFinishInflate() {
		setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(linkListener == null)
					return super.shouldOverrideUrlLoading(view, url);

				// Is internal link?
				if(url.startsWith("/")) {
					return linkListener.onInternalLinkLoad((DokuwikiWebView)view, DokuwikiUtil.parseUrl(url));
				} else {
					return linkListener.onExternalLinkLoad((DokuwikiWebView)view, url);
				}

			}
			
		});
	}

	/**
	 * Get the page, that is loaded currently in the webview.
	 * 
	 * @return The currently loaded page.
	 */
	public Page getPage() {
		return currentPage;
	}

	public void setScrollListener(ScrollListener listener) {
		this.scrollListener = listener;
	}

	/**
	 * Set a LinkLoadListener for this DokuwikiWebView.
	 * The LinkLoadListener will be informed, when an external or internal 
	 * link has been clicked or tries to load from the history.
	 * 
	 * @param listener The LinkLoadListener to handle the link clicks.
	 */
	public void setLinkLoadListener(LinkLoadListener listener) {
		this.linkListener = listener;
	}

	public void loadPage(Page p, boolean saveHistory) {
		if(saveHistory) {
			future.clear();
			if(currentPage != null)
				history.push(currentPage);
		}
		this.currentPage = p;
		loadDataWithBaseURL(null, p.getHtml(), "text/html", "utf-8", null);
	}
	
	public void loadPage(Page p) {
		loadPage(p, true);
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if(scrollListener != null)
			scrollListener.onScroll(l, t, oldl, oldt);
	}
	
	public static interface ScrollListener {
	
		public void onScroll(int l, int t, int oldl, int oldt);
		
	}

	public static interface LinkLoadListener {

		public boolean onInternalLinkLoad(DokuwikiWebView webview, DokuwikiUrl link);

		public boolean onExternalLinkLoad(DokuwikiWebView webview, String link);
				
	}
	
}