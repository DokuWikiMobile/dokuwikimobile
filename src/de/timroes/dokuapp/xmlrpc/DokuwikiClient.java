package de.timroes.dokuapp.xmlrpc;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.dokuapp.content.PageInfo;
import java.net.URL;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author Tim Roes
 */
public class DokuwikiClient {

	public enum CallType { LOGIN, PAGE };

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

	public PageInfo getPageInfo(String pagename) throws XMLRPCException {
		Map<String,Object> infos = (Map<String,Object>)client.call("wiki.getPageInfo", pagename);
		return new PageInfo(
				(String)infos.get("name"),
				(Date)infos.get("lastModified"),
				(String)infos.get("author"),
				(Integer)infos.get("version"));
	}

}