package de.timroes.dokuapp.content;

import java.io.Serializable;

/**
 *
 * @author Tim Roes
 */
public class Page implements Serializable {

	private static final long serialVersionUID = 1;
	
	private final String pagename;
	private String content;
	private PageInfo pageinfo;

	public Page(String pagename, String content) {
		this.pagename = pagename;
		this.content = content;
	}

	public String getHtml() {
		return content;
	}

	public PageInfo getPageInfo() {
		return pageinfo;
	}

	public String getPageName() {
		return pagename;
	}
	
}
