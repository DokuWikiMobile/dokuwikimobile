package org.dokuwikimobile.cache;

import android.util.Log;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import org.dokuwikimobile.DokuwikiApplication;
import org.dokuwikimobile.model.Attachment;
import org.dokuwikimobile.model.Page;
import org.dokuwikimobile.model.PageInfo;
import org.dokuwikimobile.util.FileUtil;

/**
 *
 * @author Tim Roes <mail@timroes.de>
 */
public class Cache {

	private Map<String, Page> cachedPages = new HashMap<String, Page>();

	private static final String PAGE_DIR = "pages";
	private static final String PAGE_CONTENTS_DIR = "content";
	private static final String PAGE_INFO_DIR = "info";
	private static final String MEDIA_DIR = "media";

	private final File cacheDir;
	private final File pageInfoDir;
	private final File pageContentDir;
	private final File mediaDir;

	public Cache(String cacheBaseDir) {
		this(new File(cacheBaseDir));		
	}
	
	public Cache(File cacheBaseDir) {

		cacheDir = cacheBaseDir;
		pageContentDir = new File(cacheDir, PAGE_DIR + File.separator + PAGE_CONTENTS_DIR);
		pageInfoDir = new File(cacheDir, PAGE_DIR + File.separator + PAGE_INFO_DIR);
		mediaDir = new File(cacheDir, MEDIA_DIR);

		// Create directories if they dont exist
		for(File p : new File[]{ pageContentDir, pageInfoDir, mediaDir }) {
			if(!p.exists())
				p.mkdirs();
		}

		cachedPages = loadPagesFromDisk();

	}
	
	/**
	 * Add a page to the cache. Already existing pages will be overwritten.
	 * 
	 * @param page The page to add to the cache.
	 */
	public void savePage(Page page) {
		cachedPages.put(page.getPageName(), page);
		writePageToDisk(page);
	}
	
	public Page getPage(String pagename) {
		return cachedPages.get(pagename);
	}

	public String getPageContent(String pagename) {
		String s = (String)loadObject(new File(pageContentDir, pagename));
		return (s == null) ? "" : s;
	}

	public void saveAttachment(Attachment attachment) {
		writeAttachmentToDisk(attachment);
	}

	public Attachment getAttachment(String id) {
		return loadAttachment(id);
	}
	
	/**
	 * Clear the cache. Will delete all saved cache files.
	 */
	public void clear() {
		FileUtil.clearDirectory(cacheDir);
		cachedPages.clear();
	}

	/**
	 * Get the current needed size of the cache.
	 * 
	 * @return The size of all cache files in bytes.
	 */
	public int getCacheSize() {
		return FileUtil.getDirectorySize(mediaDir)
				+ FileUtil.getDirectorySize(pageContentDir);
	}

	/**
	 * Write a specific attachment to disk. This will save the attachment object
	 * serialized to the media directory in the cache.
	 * 
	 * @param attachment The attachment that should be written to disk.
	 */
	private void writeAttachmentToDisk(Attachment attachment) {
		writeObjectToFile(new File(mediaDir, attachment.getId()), attachment);
	}
	
	/**
	 * Write a specific page to disk. This will save the content and the page
	 * info to some separated files in the cache directory.
	 * 
	 * @param page The page that should be written to disk.
	 */
	private void writePageToDisk(Page page) {
		writeObjectToFile(new File(pageContentDir, page.getPageName()), page.getContent());
		writeObjectToFile(new File(pageInfoDir, page.getPageName()), page.getPageInfo());
	}

	/**
	 * Saves an object to a file.
	 * 
	 * @param file The file where the object should be stored.
	 * @param object The object that should be saved.
	 */
	private void writeObjectToFile(File file, Object object) {

		ObjectOutputStream obj = null;
		FileOutputStream stream = null;

		try {
			stream = new FileOutputStream(file);
			obj = new ObjectOutputStream(stream);
			obj.writeObject(object);
		} catch(IOException ex) {
			Log.w(DokuwikiApplication.LOGGER_NAME, "Writing cache to disk failed.");
		} finally {
			try {
				obj.close();
				stream.close();
			} catch(Exception ex) {
				// Ignore if closing fails.		
			}
		}
		
	}

	private Page loadEmptyPage(File file) {
		return new Page(this, (PageInfo)loadObject(file));
	}

	private Attachment loadAttachment(String id) {
		return (Attachment)loadObject(new File(mediaDir, id));
	}
		
	private Object loadObject(File file) {
			
		Object object = null;
		ObjectInputStream obj = null;
		FileInputStream stream = null;

		try {
			stream = new FileInputStream(file);
			obj = new ObjectInputStream(stream);
			object = obj.readObject();
		} catch(Exception ex) {
			file.delete();
		} finally {
			try {
				obj.close();
				stream.close();
			} catch(Exception ex) {
				// Ignore if closing fails.
			}
		}

		return object;
		
	}

	private Map<String,Page> loadPagesFromDisk() {
		
		Map<String,Page> page = new HashMap<String, Page>();

		Page p;
		for(File f : pageInfoDir.listFiles()) {
			p = loadEmptyPage(f);
			p.setCache(this);
			page.put(p.getPageName(), p);
		}

		return page;
		
	}

}