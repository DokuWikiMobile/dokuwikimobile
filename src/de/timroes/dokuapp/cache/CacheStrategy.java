package de.timroes.dokuapp.cache;

/**
 *
 * @author Tim Roes
 */
public interface CacheStrategy {
	
	/**
	 * Whether the CacheManager should show cached pages at all.
	 * If this method returns false, never any cached page will be shown,
	 * so you should most surly also return false in cachePages and cacheMedia
	 * to disable also the caching itself.
	 * 
	 * @return Whether the CacheManager should show cached pages at all.
	 */
	public boolean showCachedPage();
	
	/**
	 * Whether the CacheManager should cache pages.
	 * If this method returns false, also showCachedPage should return false.
	 * 
	 * @return Whether the CacheManager should cache pages.
	 */
	public boolean cachePages();
	
}
