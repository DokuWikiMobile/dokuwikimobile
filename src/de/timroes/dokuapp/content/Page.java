package de.timroes.dokuapp.content;

import de.timroes.dokuapp.cache.Cache;
import de.timroes.dokuapp.util.MimeTypeUtil;
import de.timroes.dokuapp.util.RegexReplace;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Tim Roes
 */
public class Page implements Serializable {

	private static final long serialVersionUID = 1;

	private static final Pattern EXTRACT_MEDIA = Pattern.compile("(href|src)=\"(/_media/([^\"]+))");
	private static final String REPLACE_PICTURE_SRC = "<img[^>]*src=\"(/_media/)([^\"]+)\"[^>]*>";
	private static final String REPLACE_EXTERNAL_PIC_SRC = "<img[^>]*src=\"(/lib/exe/fetch\\.php\\?hash=[0-9a-f]+&amp;media=)([^\"]*)\"[^>]*>";
	
	protected String content;
	private PageInfo pageinfo;
	private int lastChecked;

	/**
	 * Save reference to cache, to be able to load attachments when needed.
	 */
	private transient Cache cache;

	public Page(Cache cache, PageInfo pageinfo) {
		this(cache, null, pageinfo);
	}

	public Page(Cache cache, String content, PageInfo pageinfo) {
		this(cache, content, pageinfo, (int)(System.currentTimeMillis() / 1000));
	}
	
	public Page(Cache cache, String content, PageInfo pageinfo, int lastChecked) {
		this.cache = cache;
		this.content = content;
		this.pageinfo = pageinfo;
		this.lastChecked = lastChecked;
	}

	/**
	 * Returns the html content of the page. This is the raw html as it was
	 * returned by the XML-RPC interface. This is not suitable to be displayed
	 * in a web view (wrong URLs to images, etc.)
	 * Always use this method to access the content.
	 * 
	 * @return The html as returned from the server.
	 */
	public String getContent() {
		
		if(content == null) {
			content = cache.getPageContent(pageinfo.getName());
		}
		
		return content;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public String getHtml() {
		String s = insertMedia(getContent());
		return s;
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

	public List<DokuwikiUrl> getLinkedAttachments() {
		
		List<DokuwikiUrl> atts = new ArrayList<DokuwikiUrl>();

		Matcher m = EXTRACT_MEDIA.matcher(getContent());

		while(m.find()) {
			atts.add(DokuwikiUrl.parseUrl(m.group(3)));
		}
		
		return atts;
		
	}

	private String insertMedia(String html) {
		RegexReplace reg = new RegexReplace(REPLACE_PICTURE_SRC);
		html = reg.replaceAll(html, new RegexReplace.Callback() {
			
			public String replace(MatchResult match) {
				Attachment a = cache.getAttachment(DokuwikiUrl.parseUrl(match.group(2)).id);
				if(a == null)
					return match.group();
				else {
					String type = MimeTypeUtil.getFileType(a.getData());
					if(type != null)
						return match.group().substring(0, match.start(1) - match.start())
								+ "data:" + type + ";base64," + a.getBase64Data()
								+ match.group().substring(match.end(2) - match.start());
					else
						return "";
				}
			}
			
		});

		// Insert external media files
		reg = new RegexReplace(REPLACE_EXTERNAL_PIC_SRC);
		html = reg.replaceAll(html, new RegexReplace.Callback() {
			
			public String replace(MatchResult match) {
				try {
					String decodedUrl = URLDecoder.decode(match.group(2), "UTF-8");
					return match.group().substring(0, match.start(1) - match.start())
							+ decodedUrl + match.group().substring(match.end(2) - match.start());
				} catch (UnsupportedEncodingException ex) {
					Logger.getLogger(Page.class.getName()).log(Level.SEVERE, null, ex);
				}
				return match.group();
			}
			
		});

		return html;
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