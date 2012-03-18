package org.dokuwikimobile.model;

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

	public SearchResult(int mtime, int score, int rev, String id, String snippet) {
		this.mtime = mtime;
		this.score = score;
		this.rev = rev;
		this.id = id;
		this.snippet = snippet;
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

	@Override
	public String toString() {
		return this.id;
	}
	
	
}