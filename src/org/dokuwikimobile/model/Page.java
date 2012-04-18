package org.dokuwikimobile.model;

/**
 *
 * @author Tim Roes <mail@timroes.de>
 */
public class Page {

	private PageInfo pageinfo;
	private String html;
	
	public Page(String html, PageInfo pageinfo) {
		this.pageinfo = pageinfo;
		this.html = html;
	}

	public String getHtml() {
		return html;
	}

	public PageInfo getPageinfo() {
		return pageinfo;
	}
	
}