package de.timroes.dokuapp.xmlrpc;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;
import de.timroes.dokuapp.xmlrpc.callback.ErrorCallback;
import de.timroes.dokuapp.xmlrpc.callback.LoginCallback;
import de.timroes.dokuapp.xmlrpc.callback.PageLoadedCallback;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
public class DokuwikiXMLRPCClient {

	private XMLRPCClient client;
	private CallbackHandler callbackHandler = new CallbackHandler();

	public DokuwikiXMLRPCClient(URL url, String userAgent) {
		client = new XMLRPCClient(url, userAgent, XMLRPCClient.FLAGS_ENABLE_COOKIES);
	}

	public long login(LoginCallback callback, String username, String password) {
		long id = client.callAsync(callbackHandler, "dokuwiki.login", username, password);
		return callbackHandler.addCallback(id, callback);
	}

	public long getPageHTML(PageLoadedCallback callback, String pagename) {
		long id = client.callAsync(callbackHandler, "wiki.getPageHTML", pagename);
		return callbackHandler.addCallback(id, callback);
	}

	/**
	 * This inner class handles all the asynchronous callbacks from the server.
	 * Whenever a call is made to the server, the callback must be registered
	 * at the handler with the id.
	 */
	private class CallbackHandler implements XMLRPCCallback {

		private Map<Long, ErrorCallback> callbacks = new HashMap<Long, ErrorCallback>();

		/**
		 * Add a callback to the handler, that will be informed when the
		 * server response or error has been returned.
		 * 
		 * @param id The id for the request.
		 * @param callback The callback to be called.
		 * @return The id is passed through for further usage.
		 */
		public long addCallback(long id, ErrorCallback callback) {
			callbacks.put(id, callback);
			return id;
		}

		/**
		 * Will be called when the server responded successfully. The returned
		 * object is passed together with the id generated from the request.
		 * 
		 * @param id The id of the request.
		 * @param result The server response.
		 */
		public void onResponse(long id, Object result) {
			
			ErrorCallback callback = callbacks.remove(id);

			if(callback instanceof LoginCallback) {
				((LoginCallback)callback).onLogin((Boolean)result, id);
			} else if(callback instanceof PageLoadedCallback) {
				((PageLoadedCallback)callback).onPageLoaded((String)result, id);
			}

		}

		/**
		 * Will be called whenever an error occurs.
		 * 
		 * @param id The id of the request.
		 * @param error The error occured.
		 */
		public void onError(long id, XMLRPCException error) {
			callbacks.remove(id).onError(error, id);
		}

		/**
		 * Will be called whenever the server returns an error.
		 * 
		 * @param id The id of the request.
		 * @param error The error returned by the server.
		 */
		public void onServerError(long id, XMLRPCServerException error) {
			callbacks.remove(id).onServerError(error, id);
		}

	}

}