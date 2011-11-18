package de.timroes.dokuapp.cache;

/**
 *
 * @author Tim Roes
 */
public class NoCacheStrategy implements CacheStrategy {

	public boolean showCachedPage() {
		return true;
	}

	public boolean cachePages() {
		return true;
	}
	
}
