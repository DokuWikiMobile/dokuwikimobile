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

	public String getAuthor() {
		return author;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public int getVersion() {
		return version;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
		hash = 79 * hash + this.version;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final PageInfo other = (PageInfo) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		if (this.version != other.version) {
			return false;
		}
		return true;
	}

}