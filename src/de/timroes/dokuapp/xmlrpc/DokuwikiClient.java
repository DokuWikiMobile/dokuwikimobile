package de.timroes.dokuapp.xmlrpc;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCClient;
import java.net.URL;

/**
 * This client is responsible for direct communication with the server.
 * It is the only part of the application that has access to the XML-RPC interface 
 * of the server via the aXMLRPC client. 
 * 
 * It has a method for every possible method call the server understand.
 * 
 * The method calls will be handled asynchronously. The methods return an id for
 * the call. When the call finished the XMLRPCCallback listener, that has been
 * passed in the constructor, will be called with either the error message
 * or the response from the server.
 * 
 * @author Tim Roes
 */
public class DokuwikiClient {


	private XMLRPCClient client;
	private XMLRPCCallback listener;

	public DokuwikiClient(URL url, String userAgent, XMLRPCCallback listener) {
		client = new XMLRPCClient(url, userAgent, XMLRPCClient.FLAGS_ENABLE_COOKIES);
		this.listener = listener;
	}

	public long getVersion() {
		return client.callAsync(listener, "dokuwiki.getVersion");
	}

	public long login(String username, String password) {
		return client.callAsync(listener, "dokuwiki.login", username, password);
	}

	public long getTitle() {
		return client.callAsync(listener, "dokuwiki.getTitle");
	}

	public long getPageHTML(String pagename) {
		return client.callAsync(listener, "wiki.getPageHTML", pagename);
	}

}