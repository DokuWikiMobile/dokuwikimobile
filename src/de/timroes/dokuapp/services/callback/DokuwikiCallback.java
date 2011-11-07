package de.timroes.dokuapp.services.callback;

import de.timroes.dokuapp.exceptions.DokuwikiError;
import de.timroes.dokuapp.exceptions.DokuwikiServerError;

/**
 *
 * @author Tim Roes
 */
public interface DokuwikiCallback {

	public void onPageLoaded(String pageHtml, long id);

	public void onError(DokuwikiError error, long id);

	public void onServerError(DokuwikiServerError error, long id);

}