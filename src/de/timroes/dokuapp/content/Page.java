package de.timroes.dokuapp.content;

import java.io.Serializable;

/**
 *
 * @author Tim Roes
 */
public class Page implements Serializable {

	private static final long serialVersionUID = 1;
	
	private String content;
	private PageInfo pageinfo;
	private int lastChecked;

	public Page(String content, PageInfo pageinfo) {
		this(content, pageinfo, (int)(System.currentTimeMillis() / 1000));
	}
	
	public Page(String content, PageInfo pageinfo, int lastChecked) {
		this.content = content;
		this.pageinfo = pageinfo;
		this.lastChecked = lastChecked;
	}

	public String getHtml() {
		return content;
	}

	public PageInfo getPageInfo() {
		return pageinfo;
	}

	public int getLastChecked() {
		return lastChecked;
	}

	public String getPageName() {
		return pageinfo.getName();
	}

	public void setLastChecked(int lastChecked) {
		this.lastChecked = lastChecked;
	}
	
}