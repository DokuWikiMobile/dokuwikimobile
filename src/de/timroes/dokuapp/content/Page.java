package de.timroes.dokuapp.content;

import de.timroes.dokuapp.cache.AttachmentStorage;
import de.timroes.dokuapp.util.MimeTypeUtil;
import de.timroes.dokuapp.util.RegexReplace;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
	
	private String content;
	private PageInfo pageinfo;
	private int lastChecked;

	private transient AttachmentStorage attachments;

	public Page(AttachmentStorage attachments, String content, PageInfo pageinfo) {
		this(attachments, content, pageinfo, (int)(System.currentTimeMillis() / 1000));
	}
	
	public Page(AttachmentStorage attachments, String content, PageInfo pageinfo, int lastChecked) {
		this.attachments = attachments;
		this.content = content;
		this.pageinfo = pageinfo;
		this.lastChecked = lastChecked;
	}

	public void setAttachmentStorage(AttachmentStorage attachments) {
		this.attachments = attachments;
	}

	public String getHtml() {
		String s = insertMedia(content);
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

		Matcher m = EXTRACT_MEDIA.matcher(content);

		while(m.find()) {
			atts.add(DokuwikiUrl.parseUrl(m.group(3)));
		}
		
		return atts;
		
	}

	private String insertMedia(String html) {
		RegexReplace reg = new RegexReplace(REPLACE_PICTURE_SRC);
		return reg.replaceAll(html, new RegexReplace.Callback() {
			public String replace(MatchResult match) {
				Attachment a = attachments.getAttachment(DokuwikiUrl.parseUrl(match.group(2)).id);
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