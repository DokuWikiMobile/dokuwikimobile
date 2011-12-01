package de.timroes.dokuapp.content;

/**
 *
 * @author Tim Roes
 */
public class DokuwikiUrl {

	public String id;
	public String query;
	public String anchor;

	public DokuwikiUrl(String id) {
		this(id, "", "");
	}

	public DokuwikiUrl(String id, String anchor) {
		this(id, anchor, "");
	}

	public DokuwikiUrl(String id, String anchor, String query) {
		this.id = id.startsWith(":") ? id.substring(1) : id;
		this.query = query;
		this.anchor = anchor;
	}

	@Override
	public String toString() {
		return id + "?" + query + "#" + anchor;
	}
	
}
