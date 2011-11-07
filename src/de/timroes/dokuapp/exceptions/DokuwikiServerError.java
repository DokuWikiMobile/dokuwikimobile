package de.timroes.dokuapp.exceptions;

/**
 *
 * @author Tim Roes
 */
public class DokuwikiServerError extends Exception {

	private int errornr;

	public DokuwikiServerError(String ex, int errnr) {
		super(ex);
		this.errornr = errnr;
	}

	public int getErrorNr() {
		return errornr;
	}

}