package de.timroes.dokuapp.content;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Tim Roes
 */
public class PageInfo implements Serializable {

	private static final long serialVersionUID = 1;
	
	private String name;
	private Date lastModified;
	private String author;
	private int version;

	public PageInfo(String name, Date lastModified, String author, int version) {
		this.name = name;
		this.lastModified = lastModified;
		this.author = author;
		this.version = version;
	}

	@Override
	public String toString() {
		return "PageInfo [" + name + "," + lastModified + "," + author + "," + version + "]";
	}

	public String getName() {
		return name;
	}

}