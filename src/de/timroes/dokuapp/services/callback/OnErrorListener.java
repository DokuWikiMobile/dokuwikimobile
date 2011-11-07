package de.timroes.dokuapp.services.callback;

import de.timroes.dokuapp.exceptions.DokuwikiError;
import de.timroes.dokuapp.exceptions.DokuwikiServerError;

/**
 *
 * @author Tim Roes
 */
public interface OnErrorListener {

	public void onError(DokuwikiError error);

	public void onServerError(DokuwikiServerError error);

}