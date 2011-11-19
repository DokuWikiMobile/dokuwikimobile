package de.timroes.dokuapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 *
 * @author Tim Roes
 */
public class DokuwikiWebView extends WebView {

	private ScrollListener listener;
	
	public DokuwikiWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public DokuwikiWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public DokuwikiWebView(Context context) {
		super(context);
	}

	public void setScrollListener(ScrollListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if(listener != null)
			listener.onScroll(l, t, oldl, oldt);
	}
	
	public static interface ScrollListener {
	
		public void onScroll(int l, int t, int oldl, int oldt);
		
	}

	
}