package org.dokuwikimobile.exception;

/**
 *
 * @author Tim Roes <mail@timroes.de>
 */
public class ApplicationContextMissingError extends Error {

	public ApplicationContextMissingError(Throwable throwable) {
		super(throwable);
	}

	public ApplicationContextMissingError(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public ApplicationContextMissingError(String detailMessage) {
		super(detailMessage);
	}

	public ApplicationContextMissingError() {
	}
	
}
