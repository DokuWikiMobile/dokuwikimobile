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

	/**
	 * Compares if this page is equal to another.
	 * Two pages are equal, if they have the same id and the same version.
	 * Actually the content could still be different, but then the server is
	 * broken in some way.
	 * 
	 * @param o The object to compare to.
	 * @return Whether the pages are the same.
	 */
	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(o == null) {
			return false;
		}
		if(getClass() != o.getClass()) {
			return false;
		}
		final Page p = (Page)o;
		if(p.getPageInfo() == null) {
			return false;
		}
		return pageinfo.getVersion() == p.pageinfo.getVersion()
				&& pageinfo.getName().equals(p.pageinfo.getName());
	}

	@Override
	public int hashCode() {
		return (pageinfo == null) ? 0 : pageinfo.hashCode();
	}
	
}