package org.dokuwikimobile.xmlrpc;

import android.util.Log;
import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;
import de.timroes.base64.Base64;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.dokuwikimobile.DokuwikiApplication;
import org.dokuwikimobile.listener.*;
import org.dokuwikimobile.model.Attachment;
import org.dokuwikimobile.model.LoginData;
import org.dokuwikimobile.model.PageInfo;
import org.dokuwikimobile.model.SearchResult;

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
 * @author Tim Roes <mail@timroes.de>
 */
public final class DokuwikiXMLRPCClient {

	public final int version;

	public final static int ERROR_NO_ACCESS = 1;

	private final static String CALL_LOGIN ="dokuwiki.login";
	private final static String CALL_GETPAGEHTML = "wiki.getPageHTML";
	private final static String CALL_PAGE_INFO = "wiki.getPageInfo";
	private final static String CALL_GETATTACHMENT = "wiki.getAttachment";
	private final static String CALL_SEARCH = "dokuwiki.search";

	private final static String KEY_PAGE_NAME = "name";
	private final static String KEY_LAST_MODIFIED = "lastModified";
	private final static String KEY_AUTHOR = "author";
	private final static String KEY_VERSION = "version";

	private XMLRPCClient client;
	private CallbackHandler callbackHandler = new CallbackHandler();
	private LoginData loginData;

	public DokuwikiXMLRPCClient(URL url) {

		client = new XMLRPCClient(url, "DokuWikiMobile",
				XMLRPCClient.FLAGS_IGNORE_STATUSCODE | XMLRPCClient.FLAGS_FORWARD);
		
		// TODO: Need to be asynchrounous!
		int v = 0;
		try {
			v = (Integer)client.call("dokuwiki.getXMLRPCAPIVersion");
		} catch (XMLRPCException ex) {
			// TODO: What do we do when the xmlrpc version cannot be read
		}
		version = v;
	}

	public void setLoginData(LoginData loginData) {
		this.loginData = loginData;
		client.setLoginData(loginData.Username, loginData.Password);	
	}

	public void clearLoginData() {
		this.loginData = null;
		client.clearLoginData();
	}

	public Canceler login(LoginListener listener, LoginData login) {
		return call(listener, CALL_LOGIN, login.Username, login.Password);
	}
		
	public Canceler getPageHTML(PageHtmlListener listener, String pagename) {
		return call(listener, CALL_GETPAGEHTML, pagename);
	}

	public Canceler getPageInfo(PageInfoListener listener, String pagename) {
		return call(listener, CALL_PAGE_INFO, pagename);
	}

	public Canceler getAttachment(AttachmentListener listener, String aid) {
		return call(listener, CALL_GETATTACHMENT, aid);
	}

	public Canceler search(SearchListener listener, String query) {		
		return call(listener, CALL_SEARCH, query);
	}

	private Canceler call(CancelableListener listener, String methodName, Object... params) {
		long id = client.callAsync(callbackHandler, methodName, params);
		Canceler c = callbackHandler.addCallback(id, listener, methodName, params);
		listener.onStartLoading(c, id);
		return c;
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
		public Canceler addCallback(long id, CancelableListener listener, String methodName, Object[] params) {
			history.add(new CallbackHistory.Entry(id, listener, methodName, params));
			return new Canceler(id);
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

			if(call == null) {
				return;
			} else if(CALL_LOGIN.equals(call.methodName)) {
				// dokuwiki.login returned
				((LoginListener)call.listener).onLogin((Boolean)result, id);
			} else if(CALL_GETPAGEHTML.equals(call.methodName)) {
				// getPageHTML returned
				((PageHtmlListener)call.listener).onPageHtml(
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
				((PageInfoListener)call.listener).onPageInfoLoaded(pi, id);
			} else if(CALL_GETATTACHMENT.equals(call.methodName)) {
				// TODO: CHECK REAL xmlrpc version here when it is implemented
				// getAttachment returned
				byte[] data;
				if(version >= 7) {
					data = (byte[])result;
				} else {
					// Before XMLRPC version 7 the data was encoded as base64 but 
					// transfered as type string, so we need to decode it here.
					data = Base64.decode((String)result);
				}
				Attachment a = new Attachment(call.params[0].toString(), data);
				((AttachmentListener)call.listener).onAttachmentLoaded(a, id);
			} else if(CALL_SEARCH.equals(call.methodName)) {
				List<SearchResult> results = new ArrayList<SearchResult>();
				Map<String,Object> searchResult;
				for(Object o : (Object[])result) {
					searchResult = (Map<String,Object>)o;
					results.add(new SearchResult((Integer)searchResult.get("mtime"), 
							(Integer)searchResult.get("score"),
							(Integer)searchResult.get("rev"),
							(String)searchResult.get("id"),
							(String)searchResult.get("snippet")));
							
				}
				((SearchListener)call.listener).onSearchResults(results, id);
			}
			
			call.listener.onEndLoading(id);
			
		}

		/**
		 * Will be called whenever an error occurs.
		 * 
		 * @param id The id of the request.
		 * @param error The error occurred.
		 */
		public void onError(long id, XMLRPCException error) {
			Log.e(DokuwikiApplication.LOGGER_NAME, error.getCause().getMessage());
			history.remove(id).listener.onError(ErrorCode.UNCATEGORIZED, id);
		}

		/**
		 * Will be called whenever the server returns an error.
		 * If login with basic auth returns 'no access' and the user has stored
		 * a username and password, the client tries to login with xmlrpc to check
		 * if the user is authorized to view the page.
		 * @param id The id of the request.
		 * @param error The error returned by the server.
		 */
		public void onServerError(long id, XMLRPCServerException error) {
			// If login with basic auth failed, try to login with xmlrpc
			if(error.getErrorNr() == ERROR_NO_ACCESS 
					&& loginData != null) {
				
				// Try to login.
				boolean login = false;
				try {
					login = (Boolean)client.call(CALL_LOGIN, loginData.Username, loginData.Password);
				} catch (XMLRPCException ex) {
					// Don't do anything. Since login will stay false it will be
					// handled with the next if statement.
				}
				
				if(login) {
					history.get(id).listener.onError(ErrorCode.NO_AUTH, id);
				} else {
					// Send error to callback
					history.remove(id).listener.onError(ErrorCode.NOT_LOGGED_IN, id);
					clearLoginData();
					client.clearLoginData();
				}
			} else {
				history.remove(id).listener.onError(ErrorCode.NOT_LOGGED_IN, id);
			}
		}

	}

	/**
	 * This inner class provides methods to cancel calls. 
	 */
	public class Canceler {

		private long id;

		public Canceler(long id) {
			this.id = id;
		}
		
		public void cancel() {
			callbackHandler.history.remove(id);
			client.cancel(id);
		}

		public long getId() {
			return id;
		}

	}
	
}
