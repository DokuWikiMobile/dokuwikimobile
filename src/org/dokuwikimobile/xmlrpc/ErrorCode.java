package org.dokuwikimobile.xmlrpc;

/**
 *
 * @author Daniel Baelz <daniel.baelz@lysandor.de>
 */
public enum ErrorCode {
	
	/**
	 * Uncategorized server errors.
	 */
	UNCATEGORIZED,
	
	// Dokuwiki creation errors

	/**
	 * The version of the DokuWiki, didn't match the minimum required version of
	 * the application.
	 */
	VERSION_ERROR,
	
	/**
	 * Authentication errors, the user is not authorized or not logged in.
	 */
	NO_AUTH,
	NOT_LOGGED_IN,
	
}