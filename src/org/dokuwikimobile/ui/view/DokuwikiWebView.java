package org.dokuwikimobile.ui.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.dokuwikimobile.model.DokuwikiUrl;
import org.dokuwikimobile.model.Page;
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
		build();
	}

	public DokuwikiWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		build();
	}
	
	public DokuwikiWebView(Context context) {
		super(context);
		build();
	}

	private void build() {
		setBackgroundColor(0xFFFFFFFF);
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

                // TODO: Use temporary variable [see also below]
		for(int i = 0; i < Math.abs(steps); i++) {
			to.push(currentPage);
			currentPage = from.pop();
		}

		// Notify listerner that page is loaded from history
		linkListener.onHistoryLoaded(this, new DokuwikiUrl(currentPage.getPageName()));
		
		// Load page, but dont modify history again
		loadPage(currentPage, false);
	}

	@Override
	public void goForward() {
		goBackOrForward(1);
	}

	@Override
	protected void onFinishInflate() {
		getSettings().setJavaScriptEnabled(true);
		setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(linkListener == null)
					return super.shouldOverrideUrlLoading(view, url);

				// Is internal link?
				if(url.startsWith("/")) {
					linkListener.onInternalLinkLoad((DokuwikiWebView)view, DokuwikiUrl.parseUrl(url));
				} else {
					// Send external link to listener.
					if(!linkListener.onExternalLinkLoad((DokuwikiWebView)view, url)) {
						// If listener did not handle the loading we try to open 
						// the matching app for it.
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						intent.putExtra(Browser.EXTRA_APPLICATION_ID, getContext().getPackageName());
						intent.addCategory(Intent.CATEGORY_BROWSABLE);

						getContext().startActivity(intent);

					}

				}
				
				return true;
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

		// If page is null dont do anything.
		// Also don't do anything, if we want to save history (so we didnt jump
		// back or forward) and page is equal the current page.
		// This prevent the same page to be multiple times in the history.
		
		// TODO: Remove this when used temporary variable above
		if(p == null || (saveHistory && p.equals(currentPage))) {
			return;
		}
		
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

	/**
	 * An interface for listeners, that want to be informed whenever a page has been
	 * loaded into the webview. This will be triggered whenever another class
	 * load a page into this webview or a page has been restored from the history.
	 */
	public static interface PageLoadedListener {

		/**
		 * This method is called whenever a new page has been loaded.
		 * 
		 * @param webview The webview to which the page has been loaded.
		 * @param page The page that has been loaded.
		 */
		public void onPageLoaded(DokuwikiWebView webview, Page page);
		
	}
	
	/**
	 * An interface for listeners, that want to be informed whenever a link has been
	 * clicked within this web view. The webview itself cannot load any pages,
	 * since it has no connection to the DOkuwikiService. So the LinkLoadListener
	 * is responsible for getting the page and load it into this web view.
	 */
	public static interface LinkLoadListener {

		/**
		 * This method is called whenever an internal link is clicked. The
		 * target of this link is a page within this dokuwiki.
		 * 
		 * @param webview The webview in which the link has been clicked.
		 * @param link The link that has been clicked.
		 */
		public void onInternalLinkLoad(DokuwikiWebView webview, DokuwikiUrl link);

		/**
		 * This method is called whenever an external link is clicked. The target
		 * of this link is any external resource (like a webpage, mailto or telephone
		 * number).
		 * 
		 * @param webview The webview in which the link has been clicked.
		 * @param link The link that has been clicked.
		 * @return True if the link was handled by the listener.
		 * 		False if we should try open the link in the default application for it
		 */
		public boolean onExternalLinkLoad(DokuwikiWebView webview, String link);

		/**
		 * This method is called whenever a page is loaded from history.
		 * 
		 * @param webview The webview in which the history page has been loaded.
		 * @param link The link to the page that has been loaded from history.
		 */
		public void onHistoryLoaded(DokuwikiWebView webview, DokuwikiUrl link);
				
	}
	
}