package org.dokuwikimobile.xmlrpc;

import android.util.Log;
import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;
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

	public final static int INVALID_XMLRPC_REQUEST = -32600;
	public final static int METHOD_NOT_EXIST = -32601;
	public final static int WRONG_NR_PARAMETER = -32602;
	public final static int NO_AUTH = -32603;
	public final static int NOT_LOGGED_IN = -32604; 
	public final static int PARSE_ERROR = -32700;
	public final static int RECURSIVE_CALL = -32800;
	public final static int UNKNOWN_SERVER_ERROR = -99999;

	

	private final static String CALL_VERSION = "dokuwiki.getXMLRPCAPIVersion";
	private final static String CALL_LOGIN ="dokuwiki.login";
	private final static String CALL_GETPAGEHTML = "wiki.getPageHTML";
	private final static String CALL_PAGE_INFO = "wiki.getPageInfo";
	private final static String CALL_GETATTACHMENT = "wiki.getAttachment";
	private final static String CALL_SEARCH = "dokuwiki.search";
	private final static String CALL_TITLE = "dokuwiki.getTitle";

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
		
	}

	public URL getURL() {
		return client.getURL();
	}

	public void setLoginData(LoginData loginData) {
		this.loginData = loginData;
		client.setLoginData(loginData.Username, loginData.Password);	
	}

	public void clearLoginData() {
		this.loginData = null;
		client.clearLoginData();
	}

	public Canceler getVersion(VersionListener listener) {
		return call(listener, CALL_VERSION);
	}

	public Canceler getTitel(TitleListener listener) {
		return call(listener, CALL_TITLE);
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
			} else if(CALL_VERSION.equals(call.methodName)) {
				// dokuwiki.version returned
				((VersionListener)call.listener).onVersion((Integer)result, id);
			} else if(CALL_TITLE.equals(call.methodName)) {
				// dokuwiki.getTitle returned
				((TitleListener)call.listener).onTitle((String)result, id);
			} else if(CALL_LOGIN.equals(call.methodName)) {
				// dokuwiki.login returned
				((LoginListener)call.listener).onLogin((Boolean)result, id);
			} else if(CALL_GETPAGEHTML.equals(call.methodName)) {
				// getPageHTML returned
				((PageHtmlListener)call.listener).onPageHtml((String)result, id);
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
				byte[] data = (byte[])result;
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
			Log.e(DokuwikiApplication.LOGGER_NAME, error.toString());
			history.remove(id).listener.onError(ErrorCode.UNCATEGORIZED, id);
		}

		/**
		 * Will be called whenever the server returns an error.
		 * @param id The id of the request.
		 * @param error The error returned by the server.
		 */
		public void onServerError(long id, XMLRPCServerException error) {
			int errorNr = error.getErrorNr(); 
			
			switch (errorNr) {
				case NO_AUTH:
					history.remove(id).listener.onError(ErrorCode.NO_AUTH, id);
					break;
				case NOT_LOGGED_IN:
					history.remove(id).listener.onError(ErrorCode.NOT_LOGGED_IN, id);
					break;
				default:
					history.remove(id).listener.onError(ErrorCode.UNCATEGORIZED, id);
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
