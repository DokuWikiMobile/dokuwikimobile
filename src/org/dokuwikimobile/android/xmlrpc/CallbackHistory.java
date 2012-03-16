package org.dokuwikimobile.android.xmlrpc;

import java.util.HashMap;
import java.util.Map;
import org.dokuwikimobile.android.xmlrpc.callback.ErrorCallback;

/**
 *
 * @author Tim Roes
 */
public class CallbackHistory {
	
	private Map<Long, CallbackHistory.Entry> history = new HashMap<Long, Entry>();

	public long add(Entry entry) {
		history.put(entry.id, entry);
		return entry.id;
	}

	public Entry remove(long id) {
		return history.remove(id);
	}

	public Entry get(long id) {
		return history.get(id);
	}

	public static class Entry {
		public long id;
		public ErrorCallback callback;
		public String methodName;
		public Object[] params;

		public Entry() { }

		public Entry(long id, ErrorCallback callback, String methodName, Object[] params) {
			this.id = id;
			this.callback = callback;
			this.methodName = methodName;
			this.params = params;
		}
	}
			
}
