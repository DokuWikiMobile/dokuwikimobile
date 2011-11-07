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

	private class CallbackHandler implements XMLRPCCallback {

		private Map<Long, ErrorCallback> callbacks = new HashMap<Long, ErrorCallback>();

		public long addCallback(long id, ErrorCallback callback) {
			callbacks.put(id, callback);
			return id;
		}

		public void onResponse(long id, Object result) {
			
			ErrorCallback callback = callbacks.remove(id);

			if(callback instanceof LoginCallback) {
				((LoginCallback)callback).onLogin((Boolean)result, id);
			} else if(callback instanceof PageLoadedCallback) {
				((PageLoadedCallback)callback).onPageLoaded((String)result, id);
			}

		}

		public void onError(long id, XMLRPCException error) {
			callbacks.remove(id).onError(error, id);
		}

		public void onServerError(long id, XMLRPCServerException error) {
			callbacks.remove(id).onServerError(error, id);
		}

	}

}