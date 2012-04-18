package org.dokuwikimobile.model;

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
import org.dokuwikimobile.cache.Cache;
import org.dokuwikimobile.util.MimeTypeUtil;
import org.dokuwikimobile.util.RegexReplace;

/**
 *
 * @author Tim Roes
 */
public class PageOld implements Serializable {

	private static final long serialVersionUID = 1;

	private static final Pattern EXTRACT_MEDIA = Pattern.compile("(href|src)=\"(/_media/([^\"]+))");
	private static final String REPLACE_PICTURE_SRC = "<img[^>]*src=\"(/_media/)([^\"]+)\"[^>]*>";
	private static final String REPLACE_EXTERNAL_PIC_SRC = "<img[^>]*src=\"(/lib/exe/fetch\\.php\\?hash=[0-9a-f]+&amp;media=)([^\"]*)\"[^>]*>";
	
	protected String content;
	protected PageInfo pageinfo;

	/**
	 * Save reference to cache, to be able to load attachments when needed.
	 */
	private transient Cache cache;

	public PageOld(Cache cache, PageInfo pageinfo) {
		this(cache, null, pageinfo);
	}
	
	public PageOld(Cache cache, String content, PageInfo pageinfo) {
		this.cache = cache;
		this.content = content;
		this.pageinfo = pageinfo;
	}

	/**
	 * Returns the HTML content of the page. This is the raw HTML as it was
	 * returned by the XML-RPC interface. This is not suitable to be displayed
	 * in a web view (wrong URLs to images, etc.)
	 * Always use this method to access the content.
	 * 
	 * @return The HTML as returned from the server.
	 */
	public String getContent() {
		
		if(content == null) {
			content = cache.getPageContent(pageinfo.getName());
		}
		
		return content;
		
	}

	/**
	 * This delete the content of this page from memory. This should be done,
	 * when the page isn't shown anymore, to save memory.
	 */
	public void freeContent() {
		content = null;
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

	public String getPageName() {
		return pageinfo.getName();
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
					// TODO: Show error image
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
					Logger.getLogger(PageOld.class.getName()).log(Level.SEVERE, null, ex);
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
		final PageOld p = (PageOld)o;
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