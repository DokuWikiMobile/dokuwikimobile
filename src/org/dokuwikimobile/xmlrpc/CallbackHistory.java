package org.dokuwikimobile.xmlrpc;

import java.util.HashMap;
import java.util.Map;
import org.dokuwikimobile.listener.CancelableListener;

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
		public CancelableListener listener;
		public String methodName;
		public Object[] params;

		public Entry() { }

		public Entry(long id, CancelableListener listener, String methodName, Object[] params) {
			this.id = id;
			this.listener = listener;
			this.methodName = methodName;
			this.params = params;
		}
	}
			
}
