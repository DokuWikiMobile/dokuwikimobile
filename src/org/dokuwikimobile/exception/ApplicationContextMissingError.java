package org.dokuwikimobile.exception;

/**
 * The ApplicationContextMissingError indicates, that some part of the application
 * requested an application context from the DokuwikiApplication, but the application
 * context was not available. This should actually never happen, since the application
 * context is stored when the application is started.
 * 
 * @see org.dokuwikimobile.DokuwikiApplication
 * 
 * @author Tim Roes <mail@timroes.de>
 */
public class ApplicationContextMissingError extends Error {

	public ApplicationContextMissingError(String detailMessage) {
		super(detailMessage);
	}
	
}