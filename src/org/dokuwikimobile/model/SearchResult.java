package org.dokuwikimobile.model;

import org.dokuwikimobile.util.StringUtil;

/**
 *
 * @author Tim Roes
 */
public class SearchResult {
	
	private int mtime;
	private int score;
	private int rev;
	private String id;
	private String snippet;
	private String title;

	public SearchResult(int mtime, int score, int rev, String id, String title, String snippet) {
		this.mtime = mtime;
		this.score = score;
		this.rev = rev;
		this.id = id;
		this.snippet = snippet;
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public int getMtime() {
		return mtime;
	}

	public int getRev() {
		return rev;
	}

	public int getScore() {
		return score;
	}

	public String getSnippet() {
		return snippet;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		return StringUtil.isNullOrEmpty(title) ? id : title;
	}
	
	
}