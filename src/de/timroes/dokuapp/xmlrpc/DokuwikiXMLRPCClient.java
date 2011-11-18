package de.timroes.dokuapp.xmlrpc;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;
import de.timroes.dokuapp.content.Page;
import de.timroes.dokuapp.content.PageInfo;
import de.timroes.dokuapp.manager.PasswordManager;
import de.timroes.dokuapp.xmlrpc.callback.ErrorCallback;
import de.timroes.dokuapp.xmlrpc.callback.LoginCallback;
import de.timroes.dokuapp.xmlrpc.callback.PageInfoCallback;
import de.timroes.dokuapp.services.PageLoadedListener;
import de.timroes.dokuapp.xmlrpc.callback.PageHtmlCallback;
import java.net.URL;
import java.util.Date;
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
public final class DokuwikiXMLRPCClient {

	public final static int ERROR_NO_ACCESS = 1;

	private final static String CALL_LOGIN ="dokuwiki.login";
	private final static String CALL_GETPAGEHTML = "wiki.getPageHTML";
	private final static String CALL_PAGE_INFO = "wiki.getPageInfo";

	private final static String KEY_PAGE_NAME = "name";
	private final static String KEY_LAST_MODIFIED = "lastModified";
	private final static String KEY_AUTHOR = "author";
	private final static String KEY_VERSION = "version";

	private XMLRPCClient client;
	private CallbackHandler callbackHandler = new CallbackHandler();
	private final PasswordManager passManager;

	private boolean isLoggedin;

	public DokuwikiXMLRPCClient(URL url, PasswordManager manager, String userAgent) {
		this.passManager = manager;
		client = new XMLRPCClient(url, userAgent, XMLRPCClient.FLAGS_ENABLE_COOKIES);
	}

	public long login(LoginCallback callback, String username, String password) {
		long id = client.callAsync(callbackHandler, CALL_LOGIN, username, password);
		return callbackHandler.addCallback(id, callback, CALL_LOGIN, new Object[]{ username, password });
	}

	public long getPageHTML(PageHtmlCallback callback, String pagename) {
		long id = client.callAsync(callbackHandler, CALL_GETPAGEHTML, pagename);
		return callbackHandler.addCallback(id, callback, CALL_GETPAGEHTML, new Object[]{ pagename });
	}

	public long getPageInfo(PageInfoCallback callback, String pagename) {
		long id = client.callAsync(callbackHandler, CALL_PAGE_INFO, pagename);
		return callbackHandler.addCallback(id, callback, CALL_PAGE_INFO, new Object[]{ pagename });
	}

	public void logout() {
		passManager.clearLoginData();
		client.clearCookies();
		isLoggedin = false;
	}

	/**
	 * This inner class handles all the asynchronous callbacks from the server.
	 * Whenever a call is made to the server, the callback must be registered
	 * at the handler with the id.
	 */
	private class CallbackHandler implements XMLRPCCallback {

		private CallbackHistory history = new CallbackHistory();

		/**
		 * Add a callback to the handler, that will be informed when the
		 * server response or error has been returned.
		 * 
		 * @param id The id for the request.
		 * @param callback The callback to be called.
		 * @return The id is passed through for further usage.
		 */
		public long addCallback(long id, ErrorCallback callback, String methodName, Object[] params) {
			return history.add(new CallbackHistory.Entry(id, callback, methodName, params));
		}

		/**
		 * Will be called when the server responded successfully. The returned
		 * object is passed together with the id generated from the request.
		 * To get the type the result has, the original callback methodname
		 * is checked.
		 * 
		 * @param id The id of the request.
		 * @param result The server response.
		 */
		public void onResponse(long id, Object result) {
			
			CallbackHistory.Entry call = history.remove(id);

			if(CALL_LOGIN.equals(call.methodName)) {
				// dokuwiki.login returned
				isLoggedin = (Boolean)result;
				((LoginCallback)call.callback).onLogin((Boolean)result, id);
			} else if(CALL_GETPAGEHTML.equals(call.methodName)) {
				// getPageHTML returned
				((PageHtmlCallback)call.callback).onPageHtml(
						(String)call.params[0], 
						(String)result, 
						id);
			} else if(CALL_PAGE_INFO.equals(call.methodName)) {
				// getPageInfo returned
				Map<String,Object> infos = (Map<String,Object>)result;
				PageInfo pi = new PageInfo(
						(String)infos.get(KEY_PAGE_NAME),
						(Date)infos.get(KEY_LAST_MODIFIED),
						(String)infos.get(KEY_AUTHOR),
						(Integer)infos.get(KEY_VERSION));
				((PageInfoCallback)call.callback).onPageInfoLoaded(pi, id);
			}

		}

		/**
		 * Will be called whenever an error occurs.
		 * 
		 * @param id The id of the request.
		 * @param error The error occured.
		 */
		public void onError(long id, XMLRPCException error) {
			history.remove(id).callback.onError(error, id);
		}

		/**
		 * Will be called whenever the server returns an error.
		 * If the error is no access and the user has stored a username
		 * and password, the client will try to login with this credentials and
		 * do the original call again. If the call succeeds then, it will instead
		 * of the error send the result. If it fails again it will send the original
		 * error message - that will always be an NO ACCESS error message.
		 * 
		 * @param id The id of the request.
		 * @param error The error returned by the server.
		 */
		public void onServerError(long id, XMLRPCServerException error) {

			// If rights are missing try to login and try the same call again
			if(error.getErrorNr() == ERROR_NO_ACCESS 
					&& passManager.hasLoginData()) {
				
				// Try to login.
				boolean login = false;
				try {
					login = (Boolean)client.call(CALL_LOGIN, passManager.getUsername(), passManager.getPassword());
				} catch (XMLRPCException ex) {
					// Don't do anything. Since login will stay false it will be
					// handled with the next if statement.
				}

				// If login failed due to any reason, send original callback
				if(!login) {
					// Send error to callback
					history.remove(id).callback.onServerError(error, id);
					return;
				}

				try {
					// If login was successfull try the original call again.
					CallbackHistory.Entry entry = history.get(id);
					Object result = client.call(entry.methodName, entry.params);
					onResponse(id, result);
					return;
				} catch (XMLRPCException ex) {
					// If the second call fails just send the original error message
					history.remove(id).callback.onServerError(error, id);
				}

			} else {
					history.remove(id).callback.onServerError(error, id);
			}

		}

	}

}