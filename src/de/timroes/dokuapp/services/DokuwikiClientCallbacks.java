/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.timroes.dokuapp.services;

import android.util.Pair;
import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;
import de.timroes.dokuapp.services.callback.DokuwikiCallback;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author timroes
 */
public class DokuwikiClientCallbacks implements XMLRPCCallback {

	public enum CallType { LOGIN, PAGE };
	
	private Map<Long, Pair<DokuwikiCallback, CallType>> callbacks
				= new HashMap<Long, Pair<DokuwikiCallback, CallType>>();

	public void onResponse(long id, Object result) {
		
		Pair<DokuwikiCallback, CallType> response = callbacks.get(id);
		
		switch(response.second) {
			case PAGE:
				response.first.onPageLoaded((String)result, id);
				break;
			case LOGIN:
				response.first.onLogin((Boolean)result, id);
				break;
		}
		
	}

	public void onError(long id, XMLRPCException error) {
		callbacks.remove(id).first.onError(error, id);
	}

	public void onServerError(long id, XMLRPCServerException error) {
		callbacks.remove(id).first.onServerError(error, id);
	}
	
	public long addRequest(long id, CallType type, DokuwikiCallback callback) {
		callbacks.put(id, new Pair<DokuwikiCallback, CallType>(callback, type));
		return id;
	}	

}
